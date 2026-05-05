from sqlalchemy.orm import Session

from app.models.ticket_attachment import TicketAttachment


class AttachmentRepository:
    def __init__(self, db: Session):
        self.db = db

    def create(self, attachment: TicketAttachment):
        self.db.add(attachment)
        self.db.commit()
        self.db.refresh(attachment)
        return attachment

    def get_by_id(self, attachment_id: int):
        return self.db.query(TicketAttachment).filter(TicketAttachment.id == attachment_id).first()

    def list_by_ticket(self, ticket_id: int):
        return self.db.query(TicketAttachment).filter(TicketAttachment.ticket_id == ticket_id).order_by(TicketAttachment.created_at.desc()).all()

    def delete(self, attachment: TicketAttachment):
        self.db.delete(attachment)
        self.db.commit()
