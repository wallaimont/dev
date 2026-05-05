from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from app.api.deps import require_roles
from app.core.database import get_db
from app.models.user import UserRole
from app.repositories.audit_repository import AuditRepository
from app.schemas.audit import AuditOut

router = APIRouter(prefix="/audit", tags=["Audit"])


@router.get("", response_model=list[AuditOut])
def list_audit(
    limit: int = Query(50, ge=1, le=500),
    db: Session = Depends(get_db),
    current_user=Depends(require_roles(UserRole.admin)),
):
    return AuditRepository(db).list_recent(limit)
