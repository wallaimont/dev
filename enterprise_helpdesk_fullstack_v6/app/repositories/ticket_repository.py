from sqlalchemy import select
from sqlalchemy.orm import Session, selectinload

from app.models.comment import TicketComment
from app.models.ticket import Ticket


class TicketRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_by_id(self, ticket_id: int) -> Ticket | None:
        stmt = (
            select(Ticket)
            .options(selectinload(Ticket.comments).selectinload(TicketComment.author))
            .where(Ticket.id == ticket_id)
        )
        return self.db.scalar(stmt)

    def list_all(self) -> list[Ticket]:
        stmt = select(Ticket).order_by(Ticket.created_at.desc())
        return list(self.db.scalars(stmt).all())

    def create(self, ticket: Ticket) -> Ticket:
        self.db.add(ticket)
        self.db.commit()
        self.db.refresh(ticket)
        return ticket

    def save(self, ticket: Ticket) -> Ticket:
        self.db.commit()
        self.db.refresh(ticket)
        return ticket
