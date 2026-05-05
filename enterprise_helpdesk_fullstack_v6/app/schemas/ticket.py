
from datetime import datetime

from pydantic import BaseModel, Field

from app.models.ticket import TicketPriority, TicketStatus
from app.schemas.attachment import TicketAttachmentOut
from app.schemas.comment import CommentOut


class TicketCreate(BaseModel):
    title: str = Field(..., min_length=5, max_length=200)
    description: str = Field(..., min_length=10)
    priority: TicketPriority = TicketPriority.medium
    company_id: int
    assigned_to_id: int | None = None
    sla_hours: int | None = Field(None, ge=1, le=720)


class TicketUpdate(BaseModel):
    title: str | None = Field(None, min_length=5, max_length=200)
    description: str | None = Field(None, min_length=10)
    status: TicketStatus | None = None
    priority: TicketPriority | None = None
    assigned_to_id: int | None = None
    sla_hours: int | None = Field(None, ge=1, le=720)


class TicketOut(BaseModel):
    id: int
    title: str
    description: str
    status: TicketStatus
    priority: TicketPriority
    company_id: int
    created_by_id: int
    assigned_to_id: int | None
    closure_requested_by_id: int | None
    closure_approved_by_id: int | None
    sla_hours: int
    due_at: datetime | None
    resolved_at: datetime | None
    closure_requested_at: datetime | None
    closure_approved_at: datetime | None
    created_at: datetime
    updated_at: datetime
    comments: list[CommentOut] = []
    attachments: list[TicketAttachmentOut] = []

    model_config = {"from_attributes": True}


class DashboardOut(BaseModel):
    total_tickets: int
    open_tickets: int
    in_progress_tickets: int
    resolved_tickets: int
    overdue_tickets: int
    critical_tickets: int


class TicketStatusBreakdownOut(BaseModel):
    status: TicketStatus
    total: int


class TicketTrendPointOut(BaseModel):
    day: str
    opened: int
    resolved: int


class AnalystPerformanceOut(BaseModel):
    analyst_id: int
    analyst_name: str
    assigned_total: int
    resolved_total: int
    overdue_total: int
    avg_resolution_hours: float
    active_load: int


class ExecutiveDashboardOut(BaseModel):
    totals: DashboardOut
    avg_sla_hours: float
    avg_resolution_hours: float
    status_breakdown: list[TicketStatusBreakdownOut]
    trends: list[TicketTrendPointOut]
    analyst_performance: list[AnalystPerformanceOut]
