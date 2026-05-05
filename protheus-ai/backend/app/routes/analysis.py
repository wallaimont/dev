from fastapi import APIRouter, HTTPException
from app.models.schemas import AnalysisRequest, AnalysisResponse
from app.services.analysis_service import analyze

router = APIRouter(prefix="/analysis", tags=["Análise"])


@router.post("", response_model=AnalysisResponse)
async def analysis_endpoint(request: AnalysisRequest):
    try:
        result = await analyze(
            question=request.question,
            module=request.module,
        )
        return AnalysisResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
