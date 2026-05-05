
from datetime import datetime, timedelta, timezone
from io import BytesIO
import csv

from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas
from sqlalchemy import case, func
from sqlalchemy.orm import Session

from app.models.ticket import Ticket, TicketPriority, TicketStatus
from app.models.user import User, UserRole


class DashboardService:
    def __init__(self, db: Session):
        self.db = db

    def get_metrics(self):
        now = datetime.now(timezone.utc)
        return {
            "total_tickets": self.db.query(func.count(Ticket.id)).scalar() or 0,
            "open_tickets": self.db.query(func.count(Ticket.id)).filter(Ticket.status == TicketStatus.open).scalar() or 0,
            "in_progress_tickets": self.db.query(func.count(Ticket.id)).filter(Ticket.status == TicketStatus.in_progress).scalar() or 0,
            "resolved_tickets": self.db.query(func.count(Ticket.id)).filter(Ticket.status.in_([TicketStatus.resolved, TicketStatus.closed])).scalar() or 0,
            "overdue_tickets": self.db.query(func.count(Ticket.id)).filter(
                Ticket.due_at.isnot(None),
                Ticket.due_at < now,
                Ticket.status.notin_([TicketStatus.resolved, TicketStatus.closed]),
            ).scalar() or 0,
            "critical_tickets": self.db.query(func.count(Ticket.id)).filter(Ticket.priority == TicketPriority.critical).scalar() or 0,
        }

    def get_analyst_performance(self):
        now = datetime.now(timezone.utc)
        rows = (
            self.db.query(
                User.id.label("analyst_id"),
                User.full_name.label("analyst_name"),
                func.count(Ticket.id).label("assigned_total"),
                func.sum(case((Ticket.status.in_([TicketStatus.resolved, TicketStatus.closed]), 1), else_=0)).label("resolved_total"),
                func.sum(case(((Ticket.due_at < now) & (Ticket.status.notin_([TicketStatus.resolved, TicketStatus.closed])), 1), else_=0)).label("overdue_total"),
                func.avg(
                    case(
                        (Ticket.resolved_at.isnot(None), func.extract('epoch', Ticket.resolved_at - Ticket.created_at) / 3600.0),
                        else_=None,
                    )
                ).label("avg_resolution_hours"),
                func.sum(case((Ticket.status.in_([TicketStatus.open, TicketStatus.in_progress, TicketStatus.waiting_client]), 1), else_=0)).label("active_load"),
            )
            .outerjoin(Ticket, Ticket.assigned_to_id == User.id)
            .filter(User.role == UserRole.analyst)
            .group_by(User.id, User.full_name)
            .order_by(User.full_name.asc())
            .all()
        )
        return [
            {
                "analyst_id": row.analyst_id,
                "analyst_name": row.analyst_name,
                "assigned_total": int(row.assigned_total or 0),
                "resolved_total": int(row.resolved_total or 0),
                "overdue_total": int(row.overdue_total or 0),
                "avg_resolution_hours": round(float(row.avg_resolution_hours or 0), 2),
                "active_load": int(row.active_load or 0),
            }
            for row in rows
        ]

    def get_executive_metrics(self):
        totals = self.get_metrics()
        resolution_hours = self.db.query(
            func.avg(func.extract('epoch', Ticket.resolved_at - Ticket.created_at) / 3600.0)
        ).filter(Ticket.resolved_at.isnot(None)).scalar() or 0
        avg_sla_hours = self.db.query(func.avg(Ticket.sla_hours)).scalar() or 0

        status_rows = self.db.query(Ticket.status, func.count(Ticket.id)).group_by(Ticket.status).all()
        status_breakdown = [
            {"status": status.value if hasattr(status, 'value') else str(status), "total": total}
            for status, total in status_rows
        ]

        trends = []
        today = datetime.now(timezone.utc).date()
        for offset in range(6, -1, -1):
            day = today - timedelta(days=offset)
            next_day = day + timedelta(days=1)
            opened = self.db.query(func.count(Ticket.id)).filter(Ticket.created_at >= day, Ticket.created_at < next_day).scalar() or 0
            resolved = self.db.query(func.count(Ticket.id)).filter(Ticket.resolved_at.isnot(None), Ticket.resolved_at >= day, Ticket.resolved_at < next_day).scalar() or 0
            trends.append({"day": day.isoformat(), "opened": opened, "resolved": resolved})

        return {
            "totals": totals,
            "avg_sla_hours": round(float(avg_sla_hours), 2),
            "avg_resolution_hours": round(float(resolution_hours), 2),
            "status_breakdown": status_breakdown,
            "trends": trends,
            "analyst_performance": self.get_analyst_performance(),
        }

    def export_analyst_csv(self) -> bytes:
        rows = self.get_analyst_performance()
        output = BytesIO()
        text_stream = output
        wrapper = []
        import io
        stream = io.StringIO()
        writer = csv.writer(stream)
        writer.writerow(["Analyst ID", "Analyst Name", "Assigned Total", "Resolved Total", "Overdue Total", "Average Resolution Hours", "Active Load"])
        for row in rows:
            writer.writerow([
                row["analyst_id"],
                row["analyst_name"],
                row["assigned_total"],
                row["resolved_total"],
                row["overdue_total"],
                row["avg_resolution_hours"],
                row["active_load"],
            ])
        return stream.getvalue().encode("utf-8-sig")

    def export_analyst_pdf(self) -> bytes:
        rows = self.get_analyst_performance()
        buffer = BytesIO()
        pdf = canvas.Canvas(buffer, pagesize=A4)
        width, height = A4
        y = height - 50
        pdf.setFont("Helvetica-Bold", 16)
        pdf.drawString(40, y, "Analyst Performance Report")
        y -= 28
        pdf.setFont("Helvetica", 10)
        pdf.drawString(40, y, f"Generated at: {datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M UTC')}")
        y -= 30
        for row in rows:
            if y < 80:
                pdf.showPage()
                y = height - 50
                pdf.setFont("Helvetica", 10)
            pdf.setFont("Helvetica-Bold", 11)
            pdf.drawString(40, y, row["analyst_name"])
            y -= 14
            pdf.setFont("Helvetica", 10)
            pdf.drawString(50, y, f"Assigned: {row['assigned_total']} | Resolved: {row['resolved_total']} | Overdue: {row['overdue_total']} | Active: {row['active_load']} | Avg: {row['avg_resolution_hours']}h")
            y -= 20
        pdf.save()
        return buffer.getvalue()
