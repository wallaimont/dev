from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.company import Company


class CompanyRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all(self) -> list[Company]:
        return list(self.db.scalars(select(Company).order_by(Company.name.asc())).all())

    def get_by_id(self, company_id: int) -> Company | None:
        return self.db.get(Company, company_id)

    def get_by_name(self, name: str) -> Company | None:
        stmt = select(Company).where(func.lower(Company.name) == name.lower())
        return self.db.scalar(stmt)

    def create(self, company: Company) -> Company:
        self.db.add(company)
        self.db.commit()
        self.db.refresh(company)
        return company
