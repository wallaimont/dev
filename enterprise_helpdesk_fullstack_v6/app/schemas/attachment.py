from datetime import datetime

from pydantic import BaseModel


class TicketAttachmentOut(BaseModel):
    id: int
    ticket_id: int
    original_name: str
    stored_name: str
    file_path: str
    content_type: str | None
    size_bytes: int
    uploaded_by_id: int
    created_at: datetime

    model_config = {"from_attributes": True}
