from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.core.security import hash_password, is_password_strong
from app.models.user import User, UserRole
from app.repositories.company_repository import CompanyRepository
from app.repositories.user_repository import UserRepository
from app.utils.audit import AuditWriter


class UserService:
    def __init__(self, db: Session):
        self.db = db
        self.user_repo = UserRepository(db)
        self.company_repo = CompanyRepository(db)

    def create_user(self, payload, actor_email: str):
        if self.user_repo.get_by_email(payload.email):
            raise HTTPException(status_code=400, detail="E-mail já cadastrado.")
        if not is_password_strong(payload.password):
            raise HTTPException(status_code=400, detail="Senha deve ter 8+ caracteres, maiúscula, minúscula e número.")

        if payload.company_id and not self.company_repo.get_by_id(payload.company_id):
            raise HTTPException(status_code=404, detail="Empresa não encontrada.")

        user = User(
            full_name=payload.full_name,
            email=payload.email,
            password_hash=hash_password(payload.password),
            role=payload.role,
            company_id=payload.company_id,
        )
        user = self.user_repo.create(user)
        AuditWriter.write(self.db, "CREATE", "User", actor_email, user.id, f"Usuário {user.email} criado")
        return user

    def list_users(self, company_id: int | None = None, role: str | None = None):
        if role == 'analyst':
            return self.user_repo.list_by_roles([UserRole.analyst, UserRole.manager, UserRole.admin], company_id)
        return self.user_repo.list_all(company_id)
