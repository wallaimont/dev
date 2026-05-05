from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import get_settings
from app.routes import chat, codegen, docs, analysis

settings = get_settings()

app = FastAPI(
    title="ProtheusAI",
    description="Sistema de Inteligência Artificial para TOTVS Protheus",
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat.router, prefix="/api")
app.include_router(codegen.router, prefix="/api")
app.include_router(docs.router, prefix="/api")
app.include_router(analysis.router, prefix="/api")


@app.get("/api/health")
async def health():
    return {"status": "ok", "service": "ProtheusAI"}
