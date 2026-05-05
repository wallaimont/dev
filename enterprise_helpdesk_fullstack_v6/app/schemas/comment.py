from datetime import datetime

from pydantic import BaseModel, Field

from app.schemas.user import UserOut


class CommentCreate(BaseModel):
    content: str = Field(..., min_length=2, max_length=5000)


class CommentOut(BaseModel):
    id: int
    content: str
    created_at: datetime
    author: UserOut

    model_config = {"from_attributes": True}
