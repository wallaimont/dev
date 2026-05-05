from __future__ import annotations

import io
import json
import os
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from uuid import uuid4

import pandas as pd
import uvicorn
from fastapi import Depends, FastAPI, File, Form, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from pydantic import BaseModel, Field

from .service import (
    get_model_expected_columns,
    predict_dataframe,
    predict_from_model,
    predict_records,
    train_from_config,
    train_from_dict,
)


class TrainRequest(BaseModel):
    config_path: str | None = None
    config: dict[str, Any] | None = None


class PredictCsvRequest(BaseModel):
    model_path: str
    input_path: str
    output_path: str


class PredictRecordsRequest(BaseModel):
    model_path: str
    records: list[dict[str, Any]] = Field(default_factory=list)


class RevokeTokenRequest(BaseModel):
    token: str
    reason: str | None = None
    expires_at: str | None = None


app = FastAPI(title="AI Training System API", version="0.2.0")
security = HTTPBearer(auto_error=False)
_revocation_store_instance = None


def _allowed_origins() -> list[str]:
    raw = os.getenv("AI_TRAINING_ALLOWED_ORIGINS", "*").strip()
    if not raw:
        return ["*"]
    return [origin.strip() for origin in raw.split(",") if origin.strip()]


_origins = _allowed_origins()
app.add_middleware(
    CORSMiddleware,
    allow_origins=_origins,
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


class InMemoryRevocationStore:
    def __init__(self):
        self._items: dict[str, datetime | None] = {}

    def revoke(self, token: str, expires_at: datetime | None = None) -> None:
        self._items[token] = expires_at

    def is_revoked(self, token: str) -> bool:
        expiration = self._items.get(token)
        if expiration is None:
            return token in self._items

        if datetime.now(timezone.utc) >= expiration:
            del self._items[token]
            return False
        return True


class RedisRevocationStore:
    def __init__(self, redis_url: str):
        try:
            import redis
        except ImportError as exc:
            raise RuntimeError("Redis backend requires package 'redis'. Install with 'pip install -e .[redis]'.") from exc

        self._redis = redis.from_url(redis_url, decode_responses=True)
        self._prefix = os.getenv("AI_TRAINING_REDIS_PREFIX", "ai-training:revoked:")

    def _key(self, token: str) -> str:
        return f"{self._prefix}{token}"

    def revoke(self, token: str, expires_at: datetime | None = None) -> None:
        key = self._key(token)
        if expires_at is None:
            self._redis.set(key, "1")
            return

        ttl = int((expires_at - datetime.now(timezone.utc)).total_seconds())
        if ttl <= 0:
            return
        self._redis.setex(key, ttl, "1")

    def is_revoked(self, token: str) -> bool:
        return bool(self._redis.exists(self._key(token)))


def _get_revocation_store():
    global _revocation_store_instance
    if _revocation_store_instance is not None:
        return _revocation_store_instance

    backend = os.getenv("AI_TRAINING_TOKEN_REVOKE_BACKEND", "memory").strip().lower()
    if backend == "redis":
        redis_url = os.getenv("AI_TRAINING_REDIS_URL", "").strip()
        if not redis_url:
            raise RuntimeError("AI_TRAINING_REDIS_URL is required when AI_TRAINING_TOKEN_REVOKE_BACKEND=redis")
        _revocation_store_instance = RedisRevocationStore(redis_url)
        return _revocation_store_instance

    _revocation_store_instance = InMemoryRevocationStore()
    return _revocation_store_instance


def _parse_expiration(raw_value: str) -> datetime | None:
    value = raw_value.strip()
    if not value:
        return None

    if value.isdigit():
        return datetime.fromtimestamp(int(value), tz=timezone.utc)

    if value.endswith("Z"):
        value = value[:-1] + "+00:00"

    parsed = datetime.fromisoformat(value)
    if parsed.tzinfo is None:
        parsed = parsed.replace(tzinfo=timezone.utc)
    return parsed.astimezone(timezone.utc)


def _get_valid_tokens() -> dict[str, datetime | None]:
    token_map: dict[str, datetime | None] = {}

    single_token = os.getenv("AI_TRAINING_API_TOKEN", "").strip()
    if single_token:
        token_map[single_token] = None

    # Format: token or token|expiration(ISO-8601 or epoch seconds), comma-separated.
    multi_raw = os.getenv("AI_TRAINING_API_TOKENS", "").strip()
    if multi_raw:
        entries = [entry.strip() for entry in multi_raw.split(",") if entry.strip()]
        for entry in entries:
            token, sep, expiration_raw = entry.partition("|")
            token = token.strip()
            if not token:
                continue

            expiration = None
            if sep:
                expiration = _parse_expiration(expiration_raw)
            token_map[token] = expiration

    if not token_map:
        token_map["dev-token"] = None

    return token_map


def _max_upload_bytes() -> int:
    raw = os.getenv("AI_TRAINING_MAX_UPLOAD_MB", "10").strip()
    mb = float(raw)
    if mb <= 0:
        raise ValueError("AI_TRAINING_MAX_UPLOAD_MB must be greater than zero")
    return int(mb * 1024 * 1024)


def _validate_upload_size(content: bytes) -> None:
    max_bytes = _max_upload_bytes()
    if len(content) > max_bytes:
        raise HTTPException(status_code=413, detail=f"Upload too large. Max allowed is {max_bytes} bytes")


def _validate_columns(
    df: pd.DataFrame,
    required_columns: list[str],
    *,
    strict: bool = False,
) -> None:
    required_set = set(required_columns)
    incoming_set = set(df.columns.tolist())

    missing = sorted(required_set - incoming_set)
    if missing:
        raise ValueError(f"Missing required columns: {missing}")

    if strict:
        extra = sorted(incoming_set - required_set)
        if extra:
            raise ValueError(f"Unexpected extra columns: {extra}")


def _validate_token(credentials: HTTPAuthorizationCredentials | None) -> None:
    valid_tokens = _get_valid_tokens()

    if credentials is None or credentials.scheme.lower() != "bearer":
        raise HTTPException(status_code=401, detail="Invalid or missing token")

    provided = credentials.credentials
    if provided not in valid_tokens:
        raise HTTPException(status_code=401, detail="Invalid or missing token")

    expiration = valid_tokens[provided]
    if expiration is not None and datetime.now(timezone.utc) >= expiration:
        raise HTTPException(status_code=401, detail="Token expired")

    if _get_revocation_store().is_revoked(provided):
        raise HTTPException(status_code=401, detail="Token revoked")


def _validate_admin_token(credentials: HTTPAuthorizationCredentials | None) -> None:
    expected = os.getenv("AI_TRAINING_ADMIN_TOKEN", "dev-admin-token")
    if credentials is None or credentials.scheme.lower() != "bearer" or credentials.credentials != expected:
        raise HTTPException(status_code=403, detail="Admin token required")


def require_token(credentials: HTTPAuthorizationCredentials | None = Depends(security)) -> None:
    _validate_token(credentials)


def require_admin_token(credentials: HTTPAuthorizationCredentials | None = Depends(security)) -> None:
    _validate_admin_token(credentials)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/admin/tokens/revoke")
def revoke_token(req: RevokeTokenRequest, _: None = Depends(require_admin_token)) -> dict[str, Any]:
    token = req.token.strip()
    if not token:
        raise HTTPException(status_code=400, detail="token is required")

    try:
        expires_at = _parse_expiration(req.expires_at) if req.expires_at else None
        _get_revocation_store().revoke(token=token, expires_at=expires_at)
        return {
            "status": "revoked",
            "token_hint": f"{token[:4]}...{token[-2:]}" if len(token) > 6 else f"{token[:2]}...",
            "revocation_id": str(uuid4()),
            "reason": req.reason,
            "expires_at": expires_at.isoformat() if expires_at is not None else None,
            "backend": os.getenv("AI_TRAINING_TOKEN_REVOKE_BACKEND", "memory").strip().lower() or "memory",
        }
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc


@app.post("/train")
def train(req: TrainRequest, _: None = Depends(require_token)) -> dict[str, Any]:
    try:
        if req.config_path:
            return train_from_config(req.config_path)
        if req.config:
            return train_from_dict(req.config)
        raise ValueError("Provide either config_path or config")
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc


@app.post("/predict-csv")
def predict_csv(req: PredictCsvRequest, _: None = Depends(require_token)) -> dict[str, Any]:
    try:
        return predict_from_model(req.model_path, req.input_path, req.output_path)
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc


@app.post("/predict-records")
def predict_json(req: PredictRecordsRequest, _: None = Depends(require_token)) -> dict[str, Any]:
    try:
        return predict_records(req.model_path, req.records)
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc


@app.post("/train-upload")
async def train_upload(
    config_json: str = Form(...),
    file: UploadFile = File(...),
    _: None = Depends(require_token),
) -> dict[str, Any]:
    try:
        cfg = json.loads(config_json)
        if not isinstance(cfg, dict):
            raise ValueError("config_json must be a JSON object")

        content = await file.read()
        _validate_upload_size(content)
        df = pd.read_csv(io.BytesIO(content))

        data_cfg = dict(cfg.get("data") or {})
        target_column = str(data_cfg.get("target_column", "")).strip()
        if not target_column:
            raise ValueError("config.data.target_column is required")
        if target_column not in df.columns:
            raise ValueError(f"Target column '{target_column}' not found in uploaded CSV")

        expected_feature_columns = data_cfg.get("expected_feature_columns") or []
        if expected_feature_columns:
            if not isinstance(expected_feature_columns, list):
                raise ValueError("config.data.expected_feature_columns must be a list")
            _validate_columns(df, expected_feature_columns + [target_column], strict=False)

        output = cfg.get("output") or {}
        train_path = output.get("uploaded_train_path", "artifacts/uploaded_train_data.csv")

        cfg["data"] = data_cfg
        cfg["data"]["train_path"] = train_path

        Path(train_path).parent.mkdir(parents=True, exist_ok=True)
        df.to_csv(train_path, index=False)
        return train_from_dict(cfg)
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc


@app.post("/predict-upload")
async def predict_upload(
    model_path: str = Form(...),
    file: UploadFile = File(...),
    output_path: str | None = Form(default=None),
    expected_columns_json: str | None = Form(default=None),
    _: None = Depends(require_token),
) -> dict[str, Any]:
    try:
        content = await file.read()
        _validate_upload_size(content)
        df = pd.read_csv(io.BytesIO(content))

        if expected_columns_json:
            expected = json.loads(expected_columns_json)
            if not isinstance(expected, list):
                raise ValueError("expected_columns_json must be a JSON array")
            _validate_columns(df, [str(col) for col in expected], strict=True)
        else:
            model_expected = get_model_expected_columns(model_path)
            if model_expected:
                _validate_columns(df, model_expected, strict=True)

        result = predict_dataframe(model_path=model_path, df=df)

        if output_path:
            out_df = df.copy()
            out_df["prediction"] = result["predictions"]
            Path(output_path).parent.mkdir(parents=True, exist_ok=True)
            out_df.to_csv(output_path, index=False)
            result["output_path"] = output_path

        return result
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc


def run() -> None:
    uvicorn.run("ai_training.api:app", host="0.0.0.0", port=8000, reload=False)
