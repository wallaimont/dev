from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from app.api.deps import require_roles
from app.core.database import get_db
from app.models.user import UserRole
from app.schemas.user import UserCreate, UserOut
from app.services.user_service import UserService

router = APIRouter(prefix="/users", tags=["Users"])


@router.post("", response_model=UserOut, status_code=201)
def create_user(
    payload: UserCreate,
    db: Session = Depends(get_db),
    current_user=Depends(require_roles(UserRole.admin, UserRole.manager)),
):
    return UserService(db).create_user(payload, current_user.email)


@router.get("", response_model=list[UserOut])
def list_users(
    company_id: int | None = Query(None),
    role: str | None = Query(None),
    db: Session = Depends(get_db),
    current_user=Depends(require_roles(UserRole.admin, UserRole.manager, UserRole.analyst)),
):
    return UserService(db).list_users(company_id, role)
