import uvicorn
from app.config import get_settings
from app.services.rag_service import ingest_text
import asyncio
import os

settings = get_settings()


async def load_base_knowledge():
    kb_path = os.path.join(os.path.dirname(__file__), "knowledge_base", "protheus_knowledge.md")
    if os.path.exists(kb_path):
        try:
            with open(kb_path, "r", encoding="utf-8") as f:
                content = f.read()
            count = await ingest_text(content, {"source": "Base de Conhecimento Protheus Interna", "type": "base"})
            print(f"[ProtheusAI] Base de conhecimento carregada: {count} chunks")
        except Exception as e:
            print(f"[ProtheusAI] Aviso: não foi possível carregar base de conhecimento ({e}). Continuando sem ela.")
    else:
        print("[ProtheusAI] Arquivo de base de conhecimento não encontrado. Continuando sem ele.")


if __name__ == "__main__":
    print("[ProtheusAI] Iniciando servidor...")
    asyncio.run(load_base_knowledge())
    uvicorn.run(
        "app:app",
        host=settings.host,
        port=settings.port,
        reload=True,
        log_level="info",
    )
