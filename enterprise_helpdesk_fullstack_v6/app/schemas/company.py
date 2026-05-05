from datetime import datetime

from pydantic import BaseModel, Field


class CompanyCreate(BaseModel):
    name: str = Field(..., min_length=2, max_length=120)
    segment: str | None = Field(None, max_length=80)


class CompanyOut(BaseModel):
    id: int
    name: str
    segment: str | None
    active: bool
    created_at: datetime

    model_config = {"from_attributes": True}
