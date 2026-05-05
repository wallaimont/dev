from fastapi import APIRouter, HTTPException, UploadFile, File
from app.models.schemas import IngestRequest, IngestResponse, StatsResponse
from app.services.rag_service import ingest_pdf, ingest_url, get_collection_stats
import os
import tempfile
import aiofiles

router = APIRouter(prefix="/docs", tags=["Documentos"])

ALLOWED_EXTENSIONS = {".pdf"}
MAX_FILE_SIZE = 50 * 1024 * 1024  # 50 MB


@router.post("/upload", response_model=IngestResponse)
async def upload_document(file: UploadFile = File(...)):
    ext = os.path.splitext(file.filename or "")[-1].lower()
    if ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=400,
            detail=f"Tipo de arquivo não suportado. Use: {', '.join(ALLOWED_EXTENSIONS)}",
        )

    contents = await file.read()
    if len(contents) > MAX_FILE_SIZE:
        raise HTTPException(status_code=413, detail="Arquivo muito grande (máx. 50MB)")

    with tempfile.NamedTemporaryFile(suffix=ext, delete=False) as tmp:
        tmp.write(contents)
        tmp_path = tmp.name

    try:
        count = await ingest_pdf(tmp_path)
    finally:
        os.unlink(tmp_path)

    return IngestResponse(
        status="success",
        documents_added=count,
        message=f"Arquivo '{file.filename}' ingerido com sucesso. {count} chunks adicionados.",
    )


@router.post("/ingest-url", response_model=IngestResponse)
async def ingest_from_url(request: IngestRequest):
    if not request.url.startswith(("http://", "https://")):
        raise HTTPException(status_code=400, detail="URL inválida")
    try:
        count = await ingest_url(request.url)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao processar URL: {e}")

    return IngestResponse(
        status="success",
        documents_added=count,
        message=f"URL ingerida com sucesso. {count} chunks adicionados.",
    )


@router.get("/stats", response_model=StatsResponse)
async def get_stats():
    stats = get_collection_stats()
    return StatsResponse(status="ok", **stats)
