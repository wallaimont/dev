from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline
import re


app = FastAPI(title="Transformers Sentiment API", version="1.0.0")

# Carrega o pipeline uma vez no startup para evitar custo em cada requisicao.
classifier = pipeline(
    task="sentiment-analysis",
    model="nlptown/bert-base-multilingual-uncased-sentiment",
)


class PredictRequest(BaseModel):
    text: str


class PredictResponse(BaseModel):
    label: str
    original_label: str
    stars: int
    score: float


def _map_label_to_pt(label: str) -> str:
    if label.startswith("1") or label.startswith("2"):
        return "negativo"
    if label.startswith("3"):
        return "neutro"
    return "positivo"


def _extract_stars(label: str) -> int:
    match = re.search(r"(\d+)", label)
    if not match:
        return 0

    stars = int(match.group(1))
    return max(1, min(5, stars))


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/predict", response_model=PredictResponse)
def predict(payload: PredictRequest) -> PredictResponse:
    result = classifier(payload.text)[0]
    label_pt = _map_label_to_pt(result["label"])
    stars = _extract_stars(result["label"])
    return PredictResponse(
        label=label_pt,
        original_label=result["label"],
        stars=stars,
        score=float(result["score"]),
    )
