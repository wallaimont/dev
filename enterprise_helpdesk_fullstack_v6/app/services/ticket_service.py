
from datetime import datetime, timedelta, timezone
from pathlib import Path
from uuid import uuid4

from fastapi import HTTPException, UploadFile
from sqlalchemy import select
from sqlalchemy.orm import Session, selectinload

from app.core.config import settings
from app.models.comment import TicketComment
from app.models.ticket import Ticket, TicketPriority, TicketStatus
from app.models.ticket_attachment import TicketAttachment
from app.models.user import UserRole
from app.repositories.attachment_repository import AttachmentRepository
from app.repositories.audit_repository import AuditRepository
from app.repositories.company_repository import CompanyRepository
from app.repositories.ticket_repository import TicketRepository
from app.repositories.user_repository import UserRepository
from app.utils.audit import AuditWriter


class TicketService:
    def __init__(self, db: Session):
        self.db = db
        self.ticket_repo = TicketRepository(db)
        self.company_repo = CompanyRepository(db)
        self.user_repo = UserRepository(db)
        self.attachment_repo = AttachmentRepository(db)
        self.audit_repo = AuditRepository(db)

    @staticmethod
    def calculate_due_date(sla_hours: int):
        return datetime.now(timezone.utc) + timedelta(hours=sla_hours)

    @staticmethod
    def default_sla_hours(priority: TicketPriority | str) -> int:
        mapping = {
            TicketPriority.low: settings.default_sla_low_hours,
            TicketPriority.medium: settings.default_sla_medium_hours,
            TicketPriority.high: settings.default_sla_high_hours,
            TicketPriority.critical: settings.default_sla_critical_hours,
            "low": settings.default_sla_low_hours,
            "medium": settings.default_sla_medium_hours,
            "high": settings.default_sla_high_hours,
            "critical": settings.default_sla_critical_hours,
        }
        return mapping[priority]

    def _ensure_can_assign(self, current_user):
        if current_user.role not in [UserRole.admin, UserRole.manager, UserRole.analyst]:
            raise HTTPException(status_code=403, detail="Seu perfil não pode atribuir chamados.")

    def _ensure_can_approve(self, current_user):
        if current_user.role not in [UserRole.admin, UserRole.manager]:
            raise HTTPException(status_code=403, detail="Seu perfil não pode aprovar encerramento.")

    def create_ticket(self, payload, current_user) -> Ticket:
        company = self.company_repo.get_by_id(payload.company_id)
        if not company or not company.active:
            raise HTTPException(status_code=404, detail="Empresa não encontrada ou inativa.")

        if current_user.role == UserRole.client and current_user.company_id != payload.company_id:
            raise HTTPException(status_code=403, detail="Você não pode abrir chamado para esta empresa.")

        if payload.assigned_to_id:
            self._ensure_can_assign(current_user)
            assignee = self.user_repo.get_by_id(payload.assigned_to_id)
            if not assignee:
                raise HTTPException(status_code=404, detail="Usuário responsável não encontrado.")

        sla_hours = payload.sla_hours or self.default_sla_hours(payload.priority)
        ticket = Ticket(
            title=payload.title,
            description=payload.description,
            priority=payload.priority,
            company_id=payload.company_id,
            created_by_id=current_user.id,
            assigned_to_id=payload.assigned_to_id,
            sla_hours=sla_hours,
            due_at=self.calculate_due_date(sla_hours),
        )
        ticket = self.ticket_repo.create(ticket)
        AuditWriter.write(self.db, "CREATE", "TicketActivity", current_user.email, ticket.id, f"Chamado criado: {ticket.title}")
        return self.get_ticket(ticket.id, current_user)

    def get_ticket(self, ticket_id: int, current_user=None) -> Ticket:
        stmt = (
            select(Ticket)
            .where(Ticket.id == ticket_id)
            .options(
                selectinload(Ticket.comments).selectinload(TicketComment.author),
                selectinload(Ticket.attachments),
            )
        )
        ticket = self.db.scalars(stmt).first()
        if not ticket:
            raise HTTPException(status_code=404, detail="Chamado não encontrado.")
        if current_user and current_user.role == UserRole.client and ticket.company_id != current_user.company_id:
            raise HTTPException(status_code=403, detail="Acesso negado ao chamado.")
        return ticket

    def list_tickets(self, current_user=None) -> list[Ticket]:
        stmt = select(Ticket).options(
            selectinload(Ticket.comments).selectinload(TicketComment.author),
            selectinload(Ticket.attachments),
        ).order_by(Ticket.created_at.desc())
        if current_user and current_user.role == UserRole.client:
            stmt = stmt.where(Ticket.company_id == current_user.company_id)
        return list(self.db.scalars(stmt).unique().all())

    def update_ticket(self, ticket_id: int, payload, current_user) -> Ticket:
        ticket = self.get_ticket(ticket_id, current_user)

        if current_user.role == UserRole.client and ticket.created_by_id != current_user.id:
            raise HTTPException(status_code=403, detail="Cliente só pode alterar o próprio chamado.")

        if payload.title is not None:
            ticket.title = payload.title
        if payload.description is not None:
            ticket.description = payload.description
        priority_changed = False
        if payload.priority is not None:
            ticket.priority = payload.priority
            priority_changed = True
        if payload.assigned_to_id is not None:
            self._ensure_can_assign(current_user)
            if payload.assigned_to_id and not self.user_repo.get_by_id(payload.assigned_to_id):
                raise HTTPException(status_code=404, detail="Responsável não encontrado.")
            ticket.assigned_to_id = payload.assigned_to_id
        if payload.sla_hours is not None:
            if current_user.role == UserRole.client:
                raise HTTPException(status_code=403, detail="Cliente não pode alterar SLA.")
            ticket.sla_hours = payload.sla_hours
            ticket.due_at = self.calculate_due_date(payload.sla_hours)
        elif priority_changed:
            ticket.sla_hours = self.default_sla_hours(ticket.priority)
            ticket.due_at = self.calculate_due_date(ticket.sla_hours)
        if payload.status is not None:
            if current_user.role == UserRole.client and payload.status not in [TicketStatus.waiting_client]:
                raise HTTPException(status_code=403, detail="Cliente não pode alterar esse status.")
            ticket.status = payload.status
            if payload.status == TicketStatus.resolved:
                ticket.resolved_at = datetime.now(timezone.utc)

        ticket = self.ticket_repo.save(ticket)
        AuditWriter.write(self.db, "UPDATE", "TicketActivity", current_user.email, ticket.id, f"Chamado atualizado: {ticket.title}")
        return self.get_ticket(ticket.id, current_user)

    def request_closure(self, ticket_id: int, current_user) -> Ticket:
        ticket = self.get_ticket(ticket_id, current_user)
        if current_user.role == UserRole.client and ticket.created_by_id != current_user.id:
            raise HTTPException(status_code=403, detail="Cliente só pode solicitar fechamento do próprio chamado.")
        ticket.status = TicketStatus.resolved
        ticket.resolved_at = datetime.now(timezone.utc)
        ticket.closure_requested_at = datetime.now(timezone.utc)
        ticket.closure_requested_by_id = current_user.id
        ticket = self.ticket_repo.save(ticket)
        AuditWriter.write(self.db, "REQUEST_CLOSE", "TicketActivity", current_user.email, ticket.id, "Solicitação de encerramento registrada")
        return self.get_ticket(ticket.id, current_user)

    def approve_closure(self, ticket_id: int, current_user) -> Ticket:
        self._ensure_can_approve(current_user)
        ticket = self.get_ticket(ticket_id, current_user)
        if not ticket.closure_requested_at:
            raise HTTPException(status_code=400, detail="Chamado ainda não possui solicitação de encerramento.")
        ticket.status = TicketStatus.closed
        ticket.closure_approved_at = datetime.now(timezone.utc)
        ticket.closure_approved_by_id = current_user.id
        ticket = self.ticket_repo.save(ticket)
        AuditWriter.write(self.db, "APPROVE_CLOSE", "TicketActivity", current_user.email, ticket.id, "Encerramento aprovado")
        return self.get_ticket(ticket.id, current_user)

    def add_comment(self, ticket_id: int, content: str, current_user) -> TicketComment:
        ticket = self.get_ticket(ticket_id, current_user)
        comment = TicketComment(ticket_id=ticket.id, author_id=current_user.id, content=content)
        self.db.add(comment)
        self.db.commit()
        self.db.refresh(comment)
        AuditWriter.write(self.db, "COMMENT", "TicketActivity", current_user.email, ticket.id, "Comentário inserido")
        return comment

    def upload_attachment(self, ticket_id: int, file: UploadFile, current_user) -> TicketAttachment:
        ticket = self.get_ticket(ticket_id, current_user)
        if current_user.role == UserRole.client and ticket.created_by_id != current_user.id:
            raise HTTPException(status_code=403, detail="Cliente só pode anexar arquivos ao próprio chamado.")

        content = file.file.read()
        max_size = settings.max_upload_size_mb * 1024 * 1024
        if len(content) > max_size:
            raise HTTPException(status_code=413, detail=f"Arquivo excede {settings.max_upload_size_mb} MB.")

        upload_dir = Path(settings.uploads_dir) / 'tickets' / str(ticket.id)
        upload_dir.mkdir(parents=True, exist_ok=True)
        suffix = Path(file.filename or 'arquivo').suffix
        stored_name = f"{uuid4().hex}{suffix}"
        path = upload_dir / stored_name
        path.write_bytes(content)

        attachment = TicketAttachment(
            ticket_id=ticket.id,
            original_name=file.filename or stored_name,
            stored_name=stored_name,
            file_path=str(path).replace('\\', '/'),
            content_type=file.content_type,
            size_bytes=len(content),
            uploaded_by_id=current_user.id,
        )
        attachment = self.attachment_repo.create(attachment)
        AuditWriter.write(self.db, "UPLOAD", "TicketActivity", current_user.email, ticket.id, f"Anexo {attachment.original_name} enviado")
        return attachment

    def delete_attachment(self, ticket_id: int, attachment_id: int, current_user):
        ticket = self.get_ticket(ticket_id, current_user)
        attachment = self.attachment_repo.get_by_id(attachment_id)
        if not attachment or attachment.ticket_id != ticket.id:
            raise HTTPException(status_code=404, detail="Anexo não encontrado.")
        if current_user.role == UserRole.client and ticket.created_by_id != current_user.id:
            raise HTTPException(status_code=403, detail="Cliente só pode remover arquivos do próprio chamado.")

        file_path = Path(attachment.file_path)
        if file_path.exists():
            file_path.unlink()
        self.attachment_repo.delete(attachment)
        AuditWriter.write(self.db, "DELETE", "TicketActivity", current_user.email, ticket.id, f"Anexo removido: {attachment.original_name}")

    def get_activity(self, ticket_id: int, current_user) -> list[dict]:
        self.get_ticket(ticket_id, current_user)
        activity = self.audit_repo.list_ticket_activity(ticket_id)
        return [
            {
                "id": item.id,
                "action": item.action,
                "description": item.details,
                "performed_by": item.performed_by,
                "created_at": item.created_at,
            }
            for item in activity
        ]
