from datetime import datetime

from pydantic import BaseModel


class AuditOut(BaseModel):
    id: int
    action: str
    entity: str
    entity_id: int | None
    performed_by: str
    details: str | None
    created_at: datetime

    model_config = {"from_attributes": True}
