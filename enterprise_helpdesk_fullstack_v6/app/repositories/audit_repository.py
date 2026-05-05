from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.models.audit import AuditLog


class AuditRepository:
    def __init__(self, db: Session):
        self.db = db

    def create(self, log: AuditLog) -> AuditLog:
        self.db.add(log)
        self.db.commit()
        self.db.refresh(log)
        return log

    def list_recent(self, limit: int = 50) -> list[AuditLog]:
        stmt = select(AuditLog).order_by(AuditLog.created_at.desc()).limit(limit)
        return list(self.db.scalars(stmt).all())

    def list_ticket_activity(self, ticket_id: int, limit: int = 100) -> list[AuditLog]:
        stmt = (
            select(AuditLog)
            .where(
                or_(
                    (AuditLog.entity == "TicketActivity") & (AuditLog.entity_id == ticket_id),
                    (AuditLog.entity == "Ticket") & (AuditLog.entity_id == ticket_id),
                )
            )
            .order_by(AuditLog.created_at.desc())
            .limit(limit)
        )
        return list(self.db.scalars(stmt).all())
