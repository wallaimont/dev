from pydantic import BaseModel, Field
from typing import Optional, Literal


class ChatMessage(BaseModel):
    role: Literal["user", "assistant", "system"]
    content: str


class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, max_length=4000)
    history: list[ChatMessage] = Field(default_factory=list)
    session_id: Optional[str] = None


class ChatResponse(BaseModel):
    answer: str
    sources: list[str] = []
    session_id: Optional[str] = None


class CodeGenRequest(BaseModel):
    description: str = Field(..., min_length=5, max_length=2000)
    language: Literal["advpl", "tlpp"] = "tlpp"
    module: Optional[str] = None
    context: Optional[str] = None


class CodeGenResponse(BaseModel):
    code: str
    explanation: str
    language: str


class IngestRequest(BaseModel):
    url: str = Field(..., description="URL para fazer scraping")


class IngestResponse(BaseModel):
    status: str
    documents_added: int
    message: str


class AnalysisRequest(BaseModel):
    question: str = Field(..., min_length=5, max_length=2000)
    module: Optional[str] = None


class AnalysisResponse(BaseModel):
    analysis: str
    recommendations: list[str] = []
    sources: list[str] = []


class StatsResponse(BaseModel):
    total_documents: int
    collection_name: str
    status: str
