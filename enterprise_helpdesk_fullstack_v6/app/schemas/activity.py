from datetime import datetime
from pydantic import BaseModel


class TicketActivityOut(BaseModel):
    id: int
    action: str
    description: str | None
    performed_by: str
    created_at: datetime

    model_config = {"from_attributes": True}
