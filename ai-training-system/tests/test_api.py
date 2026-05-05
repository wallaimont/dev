from __future__ import annotations

import json
from pathlib import Path

import pandas as pd
from fastapi.testclient import TestClient

from ai_training.api import app


def _auth_headers(api_token: str) -> dict[str, str]:
    return {"Authorization": f"Bearer {api_token}"}


def _train_payload(sample_csv: Path, tmp_path: Path) -> dict:
    return {
        "config": {
            "task": "classification",
            "data": {
                "train_path": str(sample_csv),
                "target_column": "churned",
                "test_size": 0.25,
                "random_state": 42,
            },
            "model": {
                "name": "random_forest",
                "params": {"n_estimators": 20, "max_depth": 5},
            },
            "training": {
                "use_cv": False,
            },
            "output": {
                "model_path": str(tmp_path / "model_http.joblib"),
                "metrics_path": str(tmp_path / "metrics_http.json"),
                "cv_results_path": str(tmp_path / "cv_http.json"),
            },
        }
    }


def test_auth_required(api_token: str, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)
    resp = client.post("/train", json=_train_payload(sample_csv, tmp_path))
    assert resp.status_code == 401


def test_train_and_predict_records(api_token: str, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)

    train_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers=_auth_headers(api_token),
    )
    assert train_resp.status_code == 200

    model_path = str(tmp_path / "model_http.joblib")
    predict_resp = client.post(
        "/predict-records",
        json={
            "model_path": model_path,
            "records": [
                {
                    "age": 35,
                    "monthly_spend": 130.0,
                    "contract_months": 8,
                    "support_tickets": 2,
                    "payment_method": "pix",
                }
            ],
        },
        headers=_auth_headers(api_token),
    )

    assert predict_resp.status_code == 200
    body = predict_resp.json()
    assert body["rows"] == 1
    assert len(body["predictions"]) == 1


def test_train_upload_csv(api_token: str, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)

    upload_config = {
        "task": "classification",
        "data": {
            "target_column": "churned",
            "test_size": 0.25,
            "random_state": 42,
        },
        "model": {
            "name": "random_forest",
            "params": {"n_estimators": 10, "max_depth": 4},
        },
        "training": {
            "use_cv": False,
        },
        "output": {
            "model_path": str(tmp_path / "model_upload.joblib"),
            "metrics_path": str(tmp_path / "metrics_upload.json"),
            "cv_results_path": str(tmp_path / "cv_upload.json"),
            "uploaded_train_path": str(tmp_path / "uploaded_train.csv"),
        },
    }

    with sample_csv.open("rb") as fh:
        resp = client.post(
            "/train-upload",
            headers=_auth_headers(api_token),
            data={"config_json": json.dumps(upload_config)},
            files={"file": ("train.csv", fh, "text/csv")},
        )

    assert resp.status_code == 200
    assert (tmp_path / "model_upload.joblib").exists()


def test_predict_upload_csv(api_token: str, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)

    train_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers=_auth_headers(api_token),
    )
    assert train_resp.status_code == 200

    model_path = str(tmp_path / "model_http.joblib")
    predict_csv = tmp_path / "predict.csv"
    df = pd.read_csv(sample_csv)
    df = df.drop(columns=["churned"])
    df.to_csv(predict_csv, index=False)

    with predict_csv.open("rb") as fh:
        resp = client.post(
            "/predict-upload",
            headers=_auth_headers(api_token),
            data={"model_path": model_path},
            files={"file": ("predict.csv", fh, "text/csv")},
        )

    assert resp.status_code == 200
    body = resp.json()
    assert body["rows"] > 0
    assert len(body["predictions"]) == body["rows"]


def test_multiple_tokens_and_expiration(monkeypatch, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)

    monkeypatch.setenv("AI_TRAINING_API_TOKEN", "")
    monkeypatch.setenv(
        "AI_TRAINING_API_TOKENS",
        "expired-token|2000-01-01T00:00:00Z,valid-token|2999-01-01T00:00:00Z",
    )

    expired_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers={"Authorization": "Bearer expired-token"},
    )
    assert expired_resp.status_code == 401

    valid_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers={"Authorization": "Bearer valid-token"},
    )
    assert valid_resp.status_code == 200


def test_train_upload_respects_size_limit(api_token: str, monkeypatch, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)
    monkeypatch.setenv("AI_TRAINING_MAX_UPLOAD_MB", "0.00001")

    upload_config = {
        "task": "classification",
        "data": {
            "target_column": "churned",
        },
        "model": {
            "name": "random_forest",
            "params": {"n_estimators": 10},
        },
        "training": {
            "use_cv": False,
        },
        "output": {
            "model_path": str(tmp_path / "m.joblib"),
            "metrics_path": str(tmp_path / "m.json"),
            "uploaded_train_path": str(tmp_path / "u.csv"),
        },
    }

    with sample_csv.open("rb") as fh:
        resp = client.post(
            "/train-upload",
            headers=_auth_headers(api_token),
            data={"config_json": json.dumps(upload_config)},
            files={"file": ("train.csv", fh, "text/csv")},
        )

    assert resp.status_code == 413


def test_predict_upload_schema_validation(api_token: str, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)

    train_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers=_auth_headers(api_token),
    )
    assert train_resp.status_code == 200

    missing_col_csv = tmp_path / "predict_missing_col.csv"
    df = pd.read_csv(sample_csv)
    df = df.drop(columns=["payment_method"])
    df.to_csv(missing_col_csv, index=False)

    model_path = str(tmp_path / "model_http.joblib")
    with missing_col_csv.open("rb") as fh:
        resp = client.post(
            "/predict-upload",
            headers=_auth_headers(api_token),
            data={"model_path": model_path},
            files={"file": ("predict.csv", fh, "text/csv")},
        )

    assert resp.status_code == 400
    assert "Missing required columns" in resp.json()["detail"]


def test_admin_revoke_token_runtime(monkeypatch, sample_csv: Path, tmp_path: Path):
    client = TestClient(app)

    monkeypatch.setenv("AI_TRAINING_API_TOKEN", "revokable-token")
    monkeypatch.setenv("AI_TRAINING_ADMIN_TOKEN", "admin-secret")

    train_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers={"Authorization": "Bearer revokable-token"},
    )
    assert train_resp.status_code == 200

    revoke_resp = client.post(
        "/admin/tokens/revoke",
        json={"token": "revokable-token", "reason": "security event"},
        headers={"Authorization": "Bearer admin-secret"},
    )
    assert revoke_resp.status_code == 200
    assert revoke_resp.json()["status"] == "revoked"

    blocked_resp = client.post(
        "/train",
        json=_train_payload(sample_csv, tmp_path),
        headers={"Authorization": "Bearer revokable-token"},
    )
    assert blocked_resp.status_code == 401
    assert blocked_resp.json()["detail"] == "Token revoked"


def test_admin_revoke_requires_admin_token(monkeypatch):
    client = TestClient(app)
    monkeypatch.setenv("AI_TRAINING_ADMIN_TOKEN", "admin-secret")

    resp = client.post(
        "/admin/tokens/revoke",
        json={"token": "some-token"},
        headers={"Authorization": "Bearer not-admin"},
    )
    assert resp.status_code == 403
