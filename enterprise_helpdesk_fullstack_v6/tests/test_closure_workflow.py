
from app.models.ticket import TicketStatus
from app.services.ticket_service import TicketService


def test_default_sla_mapping():
    assert TicketService.default_sla_hours("low") == 72
    assert TicketService.default_sla_hours("medium") == 24
    assert TicketService.default_sla_hours("high") == 8
    assert TicketService.default_sla_hours("critical") == 4


def test_ticket_status_enum_contains_closed():
    assert TicketStatus.closed.value == "closed"
