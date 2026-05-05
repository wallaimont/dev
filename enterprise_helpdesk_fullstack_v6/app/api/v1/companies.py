from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.api.deps import require_roles
from app.core.database import get_db
from app.models.user import UserRole
from app.schemas.company import CompanyCreate, CompanyOut
from app.services.company_service import CompanyService

router = APIRouter(prefix="/companies", tags=["Companies"])


@router.post("", response_model=CompanyOut, status_code=201)
def create_company(
    payload: CompanyCreate,
    db: Session = Depends(get_db),
    current_user=Depends(require_roles(UserRole.admin, UserRole.manager)),
):
    return CompanyService(db).create_company(payload.name, payload.segment, current_user.email)


@router.get("", response_model=list[CompanyOut])
def list_companies(
    db: Session = Depends(get_db),
    current_user=Depends(require_roles(UserRole.admin, UserRole.manager, UserRole.analyst)),
):
    return CompanyService(db).list_companies()
