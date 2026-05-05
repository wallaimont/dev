from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.models.company import Company
from app.repositories.company_repository import CompanyRepository
from app.utils.audit import AuditWriter


class CompanyService:
    def __init__(self, db: Session):
        self.db = db
        self.repo = CompanyRepository(db)

    def create_company(self, name: str, segment: str | None, actor_email: str) -> Company:
        if self.repo.get_by_name(name):
            raise HTTPException(status_code=400, detail="Empresa já cadastrada.")

        company = self.repo.create(Company(name=name, segment=segment))
        AuditWriter.write(self.db, "CREATE", "Company", actor_email, company.id, f"Empresa {company.name} criada")
        return company

    def list_companies(self) -> list[Company]:
        return self.repo.get_all()
