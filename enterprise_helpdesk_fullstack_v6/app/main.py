from contextlib import asynccontextmanager
from pathlib import Path

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from app.api.v1.audit import router as audit_router
from app.api.v1.auth import router as auth_router
from app.api.v1.companies import router as companies_router
from app.api.v1.dashboard import router as dashboard_router
from app.api.v1.realtime import router as realtime_router
from app.api.v1.tickets import router as tickets_router
from app.api.v1.users import router as users_router
from app.core.config import settings
from app.core.database import Base, SessionLocal, engine
from app.core.security import hash_password
from app.models import ticket_attachment  # noqa: F401
from app.models.company import Company
from app.models.user import User, UserRole


@asynccontextmanager
async def lifespan(_: FastAPI):
    Path(settings.uploads_dir).mkdir(parents=True, exist_ok=True)
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()
    try:
        company = db.query(Company).filter(Company.name == settings.default_company_name).first()
        if not company:
            company = Company(name=settings.default_company_name, segment=settings.default_company_segment)
            db.add(company)
            db.commit()
            db.refresh(company)

        admin = db.query(User).filter(User.email == settings.default_admin_email).first()
        if not admin:
            admin = User(
                full_name=settings.default_admin_name,
                email=settings.default_admin_email,
                password_hash=hash_password(settings.default_admin_password),
                role=UserRole.admin,
                company_id=company.id,
            )
            db.add(admin)
            db.commit()
    finally:
        db.close()
    yield


app = FastAPI(title=settings.app_name, version="6.0.0", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origin_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router, prefix=settings.api_v1_prefix)
app.include_router(companies_router, prefix=settings.api_v1_prefix)
app.include_router(users_router, prefix=settings.api_v1_prefix)
app.include_router(tickets_router, prefix=settings.api_v1_prefix)
app.include_router(dashboard_router, prefix=settings.api_v1_prefix)
app.include_router(audit_router, prefix=settings.api_v1_prefix)
app.include_router(realtime_router)
app.mount('/uploads', StaticFiles(directory=settings.uploads_dir), name='uploads')


@app.get("/")
def root():
    return {
        "message": "Enterprise Helpdesk API online",
        "docs": "/docs",
        "api_base": settings.api_v1_prefix,
        "websocket": "/ws/events?channel=tickets",
        "default_admin": f"{settings.default_admin_email} / {settings.default_admin_password}",
    }
