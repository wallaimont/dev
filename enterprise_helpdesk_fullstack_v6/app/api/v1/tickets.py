
from fastapi import APIRouter, Depends, File, UploadFile
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.schemas.activity import TicketActivityOut
from app.schemas.attachment import TicketAttachmentOut
from app.schemas.comment import CommentCreate, CommentOut
from app.schemas.ticket import TicketCreate, TicketOut, TicketUpdate
from app.services.ticket_service import TicketService
from app.utils.realtime import manager

router = APIRouter(prefix="/tickets", tags=["Tickets"])


@router.post("", response_model=TicketOut, status_code=201)
async def create_ticket(payload: TicketCreate, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    ticket = TicketService(db).create_ticket(payload, current_user)
    await manager.broadcast('tickets', {"type": "ticket_created", "ticket_id": ticket.id, "title": ticket.title})
    return ticket


@router.get("", response_model=list[TicketOut])
def list_tickets(db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    return TicketService(db).list_tickets(current_user)


@router.get("/{ticket_id}", response_model=TicketOut)
def get_ticket(ticket_id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    return TicketService(db).get_ticket(ticket_id, current_user)


@router.get("/{ticket_id}/activity", response_model=list[TicketActivityOut])
def get_ticket_activity(ticket_id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    return TicketService(db).get_activity(ticket_id, current_user)


@router.put("/{ticket_id}", response_model=TicketOut)
async def update_ticket(ticket_id: int, payload: TicketUpdate, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    ticket = TicketService(db).update_ticket(ticket_id, payload, current_user)
    await manager.broadcast('tickets', {"type": "ticket_updated", "ticket_id": ticket.id, "status": ticket.status})
    return ticket


@router.post("/{ticket_id}/request-close", response_model=TicketOut)
async def request_close(ticket_id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    ticket = TicketService(db).request_closure(ticket_id, current_user)
    await manager.broadcast('tickets', {"type": "ticket_requested_close", "ticket_id": ticket.id, "status": ticket.status})
    return ticket


@router.post("/{ticket_id}/approve-close", response_model=TicketOut)
async def approve_close(ticket_id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    ticket = TicketService(db).approve_closure(ticket_id, current_user)
    await manager.broadcast('tickets', {"type": "ticket_closed", "ticket_id": ticket.id, "status": ticket.status})
    return ticket


@router.post("/{ticket_id}/comments", response_model=CommentOut, status_code=201)
async def create_comment(ticket_id: int, payload: CommentCreate, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    comment = TicketService(db).add_comment(ticket_id, payload.content, current_user)
    await manager.broadcast('tickets', {"type": "ticket_commented", "ticket_id": ticket_id})
    return comment


@router.post("/{ticket_id}/attachments", response_model=TicketAttachmentOut, status_code=201)
async def upload_attachment(ticket_id: int, file: UploadFile = File(...), db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    attachment = TicketService(db).upload_attachment(ticket_id, file, current_user)
    await manager.broadcast('tickets', {"type": "ticket_attachment_uploaded", "ticket_id": ticket_id, "file_name": attachment.original_name})
    return attachment


@router.delete("/{ticket_id}/attachments/{attachment_id}", status_code=204)
async def delete_attachment(ticket_id: int, attachment_id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    TicketService(db).delete_attachment(ticket_id, attachment_id, current_user)
    await manager.broadcast('tickets', {"type": "ticket_attachment_deleted", "ticket_id": ticket_id, "attachment_id": attachment_id})
