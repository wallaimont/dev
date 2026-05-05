
from fastapi import APIRouter, Depends
from fastapi.responses import Response
from sqlalchemy.orm import Session

from app.api.deps import require_roles
from app.core.database import get_db
from app.models.user import UserRole
from app.schemas.ticket import DashboardOut, ExecutiveDashboardOut, AnalystPerformanceOut
from app.services.dashboard_service import DashboardService

router = APIRouter(prefix="/dashboard", tags=["Dashboard"])


@router.get("", response_model=DashboardOut)
def dashboard(db: Session = Depends(get_db), current_user = Depends(require_roles(UserRole.admin, UserRole.manager, UserRole.analyst))):
    return DashboardService(db).get_metrics()


@router.get("/executive", response_model=ExecutiveDashboardOut)
def executive_dashboard(db: Session = Depends(get_db), current_user = Depends(require_roles(UserRole.admin, UserRole.manager, UserRole.analyst))):
    return DashboardService(db).get_executive_metrics()


@router.get("/analysts", response_model=list[AnalystPerformanceOut])
def analyst_dashboard(db: Session = Depends(get_db), current_user = Depends(require_roles(UserRole.admin, UserRole.manager, UserRole.analyst))):
    return DashboardService(db).get_analyst_performance()


@router.get("/exports/analysts.csv")
def export_analysts_csv(db: Session = Depends(get_db), current_user = Depends(require_roles(UserRole.admin, UserRole.manager))):
    content = DashboardService(db).export_analyst_csv()
    headers = {"Content-Disposition": 'attachment; filename="analyst_performance.csv"'}
    return Response(content=content, media_type="text/csv; charset=utf-8", headers=headers)


@router.get("/exports/analysts.pdf")
def export_analysts_pdf(db: Session = Depends(get_db), current_user = Depends(require_roles(UserRole.admin, UserRole.manager))):
    content = DashboardService(db).export_analyst_pdf()
    headers = {"Content-Disposition": 'attachment; filename="analyst_performance.pdf"'}
    return Response(content=content, media_type="application/pdf", headers=headers)
