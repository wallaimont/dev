from sqlalchemy.orm import Session

from app.models.audit import AuditLog
from app.repositories.audit_repository import AuditRepository


class AuditWriter:
    @staticmethod
    def write(
        db: Session,
        action: str,
        entity: str,
        performed_by: str,
        entity_id: int | None = None,
        details: str | None = None,
    ) -> AuditLog:
        repo = AuditRepository(db)
        return repo.create(
            AuditLog(
                action=action,
                entity=entity,
                entity_id=entity_id,
                performed_by=performed_by,
                details=details,
            )
        )
