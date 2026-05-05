from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.user import User, UserRole


class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_by_email(self, email: str) -> User | None:
        return self.db.scalar(select(User).where(User.email == email))

    def get_by_id(self, user_id: int) -> User | None:
        return self.db.get(User, user_id)

    def list_all(self, company_id: int | None = None):
        stmt = select(User).order_by(User.full_name.asc())
        if company_id:
            stmt = stmt.where(User.company_id == company_id)
        return list(self.db.scalars(stmt).all())

    def list_by_roles(self, roles: list[UserRole], company_id: int | None = None):
        stmt = select(User).where(User.role.in_(roles)).order_by(User.full_name.asc())
        if company_id:
            stmt = stmt.where(User.company_id == company_id)
        return list(self.db.scalars(stmt).all())

    def create(self, user: User) -> User:
        self.db.add(user)
        self.db.commit()
        self.db.refresh(user)
        return user
