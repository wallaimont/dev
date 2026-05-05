from fastapi import APIRouter, HTTPException
from app.models.schemas import CodeGenRequest, CodeGenResponse
from app.services.codegen_service import generate_code

router = APIRouter(prefix="/codegen", tags=["Geração de Código"])


@router.post("", response_model=CodeGenResponse)
async def codegen_endpoint(request: CodeGenRequest):
    try:
        result = await generate_code(
            description=request.description,
            language=request.language,
            module=request.module,
            context=request.context,
        )
        return CodeGenResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
