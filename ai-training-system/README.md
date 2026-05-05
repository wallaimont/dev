# AI Training System

A practical system to train, evaluate, and run inference for AI models on tabular data.

## Features

- Train models from a YAML config
- Auto-detect numeric and categorical columns
- Build preprocessing + model pipeline
- Support classic ML and PyTorch neural networks
- Run cross-validation and hyperparameter search (grid/random)
- Save trained model artifact
- Export evaluation metrics as JSON
- Run batch predictions from CSV
- Expose training and inference as REST API with FastAPI

## Supported tasks

- `classification`
- `regression`

## Supported models

Classification:
- `logistic_regression`
- `random_forest`
- `gradient_boosting`
- `pytorch_mlp_classifier`

Regression:
- `linear_regression`
- `random_forest_regressor`
- `gradient_boosting_regressor`
- `pytorch_mlp_regressor`

## Setup

```bash
cd ai-training-system
pip install -e .
```

Install PyTorch support:

```bash
pip install -e .[torch]
```

For tests:

```bash
pip install -e .[dev]
```

## Project structure

```text
ai-training-system/
  config.example.yaml
  data/
    customer_churn_sample.csv
  artifacts/
  src/ai_training/
    cli.py
    config.py
    pipeline.py
    service.py
```

## Train a model

```bash
ai-trainer train --config config.example.yaml
```

This command will:
- train the model
- write `artifacts/model.joblib`
- write `artifacts/metrics.json`
- write `artifacts/cv_results.json` when CV is enabled

PyTorch example:

```bash
ai-trainer train --config config.pytorch.example.yaml
```

## Run prediction

```bash
ai-trainer predict --model artifacts/model.joblib --input data/customer_churn_sample.csv --output artifacts/predictions.csv
```

## API REST (FastAPI)

Token auth:

- Protected endpoints require `Authorization: Bearer <token>`
- Single token: `AI_TRAINING_API_TOKEN`
- Multiple rotating tokens: `AI_TRAINING_API_TOKENS`
- `AI_TRAINING_API_TOKENS` format: `token` or `token|expiration`, comma-separated
- Expiration accepts ISO-8601 (example: `2026-12-31T23:59:59Z`) or epoch seconds
- If no token env var is set, default token is `dev-token`

Upload and schema validation:

- Max upload size controlled by `AI_TRAINING_MAX_UPLOAD_MB` (default `10`)
- `train-upload` validates target column existence in uploaded CSV
- `train-upload` can validate additional features via `config.data.expected_feature_columns`
- `predict-upload` validates schema against model training columns automatically
- `predict-upload` also accepts `expected_columns_json` to enforce an explicit schema
- Browser CORS origins can be controlled by `AI_TRAINING_ALLOWED_ORIGINS`

Runtime token revocation (admin):

- Endpoint: `POST /admin/tokens/revoke`
- Requires admin bearer token from `AI_TRAINING_ADMIN_TOKEN` (default `dev-admin-token`)
- Revocation backend: `AI_TRAINING_TOKEN_REVOKE_BACKEND` (`memory` or `redis`)
- Redis URL: `AI_TRAINING_REDIS_URL` when backend is `redis`
- Optional Redis key prefix: `AI_TRAINING_REDIS_PREFIX`

Start server:

```bash
ai-trainer serve --host 0.0.0.0 --port 8000
```

or

```bash
ai-trainer-api
```

Health check:

```bash
curl http://localhost:8000/health
```

Protected request example:

```bash
curl -X POST http://localhost:8000/train \
  -H "Authorization: Bearer dev-token" \
  -H "Content-Type: application/json" \
  -d "{\"config_path\":\"config.example.yaml\"}"
```

Train from config file path:

```bash
curl -X POST http://localhost:8000/train \
  -H "Content-Type: application/json" \
  -d "{\"config_path\":\"config.example.yaml\"}"
```

Train from inline config JSON:

```bash
curl -X POST http://localhost:8000/train \
  -H "Content-Type: application/json" \
  -d "{\"config\":{\"task\":\"classification\",\"data\":{\"train_path\":\"data/customer_churn_sample.csv\",\"target_column\":\"churned\"},\"model\":{\"name\":\"random_forest\",\"params\":{\"n_estimators\":100}},\"training\":{\"use_cv\":false},\"output\":{\"model_path\":\"artifacts/model_http.joblib\",\"metrics_path\":\"artifacts/metrics_http.json\"}}}"
```

Predict from CSV file:

```bash
curl -X POST http://localhost:8000/predict-csv \
  -H "Content-Type: application/json" \
  -d "{\"model_path\":\"artifacts/model.joblib\",\"input_path\":\"data/customer_churn_sample.csv\",\"output_path\":\"artifacts/predictions_http.csv\"}"
```

Predict from JSON records:

```bash
curl -X POST http://localhost:8000/predict-records \
  -H "Authorization: Bearer dev-token" \
  -H "Content-Type: application/json" \
  -d "{\"model_path\":\"artifacts/model.joblib\",\"records\":[{\"age\":25,\"monthly_spend\":89.5,\"contract_months\":3,\"support_tickets\":4,\"payment_method\":\"credit_card\"}]}"
```

Train from uploaded CSV (no local data path from client):

```bash
curl -X POST http://localhost:8000/train-upload \
  -H "Authorization: Bearer dev-token" \
  -F "config_json={\"task\":\"classification\",\"data\":{\"target_column\":\"churned\",\"expected_feature_columns\":[\"age\",\"monthly_spend\",\"contract_months\",\"support_tickets\",\"payment_method\"]},\"model\":{\"name\":\"random_forest\",\"params\":{\"n_estimators\":50}},\"training\":{\"use_cv\":false},\"output\":{\"model_path\":\"artifacts/model_upload.joblib\",\"metrics_path\":\"artifacts/metrics_upload.json\"}}" \
  -F "file=@data/customer_churn_sample.csv"
```

Predict from uploaded CSV:

```bash
curl -X POST http://localhost:8000/predict-upload \
  -H "Authorization: Bearer dev-token" \
  -F "model_path=artifacts/model.joblib" \
  -F "expected_columns_json=[\"age\",\"monthly_spend\",\"contract_months\",\"support_tickets\",\"payment_method\"]" \
  -F "file=@data/customer_churn_sample.csv"
```

Example token rotation env var:

```bash
export AI_TRAINING_API_TOKENS="token-v1|2026-06-01T00:00:00Z,token-v2|2027-01-01T00:00:00Z"
```

Admin revoke token example:

```bash
curl -X POST http://localhost:8000/admin/tokens/revoke \
  -H "Authorization: Bearer dev-admin-token" \
  -H "Content-Type: application/json" \
  -d '{"token":"token-v1","reason":"incident"}'
```

Redis revocation backend example:

```bash
export AI_TRAINING_TOKEN_REVOKE_BACKEND=redis
export AI_TRAINING_REDIS_URL=redis://localhost:6379/0
pip install -e .[redis]
```

## Tests (pytest)

Run all tests:

```bash
pytest -q
```

CI pipeline:

- GitHub Actions workflow at `.github/workflows/ci.yml`
- Runs on every push and pull request
- Matrix: Python 3.11 and 3.12
- Installs project with `pip install -e .[dev]` and executes `pytest -q`

## Frontend web

Location:

- `frontend/index.html`
- `frontend/styles.css`
- `frontend/app.js`

What it supports:

- Health check
- Train by `config_path`
- Train via CSV upload (`/train-upload`)
- Predict via JSON records (`/predict-records`)
- Predict via CSV upload (`/predict-upload`)

Run frontend locally:

```bash
cd ai-training-system/frontend
python -m http.server 5500
```

Then open:

- `http://127.0.0.1:5500`

Make sure API is running at `http://127.0.0.1:8000` and token is configured in the UI.

## Config example

See `config.example.yaml` for all supported fields.
See `config.pytorch.example.yaml` for PyTorch neural network setup.
