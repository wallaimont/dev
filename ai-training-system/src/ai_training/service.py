from __future__ import annotations

import json
from pathlib import Path
from typing import Any

import joblib
import pandas as pd
from sklearn.metrics import (
    accuracy_score,
    f1_score,
    mean_absolute_error,
    mean_squared_error,
    r2_score,
)
from sklearn.model_selection import (
    GridSearchCV,
    RandomizedSearchCV,
    cross_val_score,
    train_test_split,
)

from .config import load_config, parse_training_config
from .pipeline import build_training_pipeline


def _compute_metrics(task: str, y_true, y_pred) -> dict[str, Any]:
    if task == "classification":
        return {
            "accuracy": float(accuracy_score(y_true, y_pred)),
            "f1_weighted": float(f1_score(y_true, y_pred, average="weighted")),
        }

    mse = float(mean_squared_error(y_true, y_pred))
    return {
        "mae": float(mean_absolute_error(y_true, y_pred)),
        "mse": mse,
        "rmse": mse ** 0.5,
        "r2": float(r2_score(y_true, y_pred)),
    }


def _default_scoring(task: str) -> str:
    return "f1_weighted" if task == "classification" else "neg_root_mean_squared_error"


def _normalize_search_params(search_params: dict[str, list[Any]]) -> dict[str, list[Any]]:
    normalized: dict[str, list[Any]] = {}
    for key, value in search_params.items():
        if not isinstance(value, list):
            raise ValueError(f"training.search_params['{key}'] must be a list")
        if "__" in key:
            normalized[key] = value
            continue
        normalized[f"model__{key}"] = value
    return normalized


def _build_cv_result(scores: list[float], scoring: str) -> dict[str, Any]:
    score_values = [float(v) for v in scores]
    mean_score = sum(score_values) / len(score_values)
    variance = sum((v - mean_score) ** 2 for v in score_values) / len(score_values)
    return {
        "scoring": scoring,
        "scores": score_values,
        "mean": float(mean_score),
        "std": float(variance ** 0.5),
    }


def train_from_config(config_path: str) -> dict[str, Any]:
    cfg = load_config(config_path)
    return _train_from_parsed_config(cfg)


def train_from_dict(config: dict[str, Any]) -> dict[str, Any]:
    cfg = parse_training_config(config)
    return _train_from_parsed_config(cfg)


def _train_from_parsed_config(cfg) -> dict[str, Any]:

    df = pd.read_csv(cfg.data.train_path)
    if cfg.data.target_column not in df.columns:
        raise ValueError(f"Target column '{cfg.data.target_column}' was not found in dataset")

    x = df.drop(columns=[cfg.data.target_column])
    y = df[cfg.data.target_column]

    numeric_cols = x.select_dtypes(include=["number", "bool"]).columns.tolist()
    categorical_cols = [col for col in x.columns if col not in numeric_cols]

    split_kwargs: dict[str, Any] = {
        "test_size": cfg.data.test_size,
        "random_state": cfg.data.random_state,
    }
    if cfg.task == "classification" and y.nunique() > 1:
        split_kwargs["stratify"] = y

    x_train, x_test, y_train, y_test = train_test_split(x, y, **split_kwargs)

    pipeline = build_training_pipeline(
        task=cfg.task,
        model_name=cfg.model.name,
        model_params=cfg.model.params,
        numeric_cols=numeric_cols,
        categorical_cols=categorical_cols,
    )

    trained_pipeline = pipeline
    cv_payload: dict[str, Any] | None = None

    if cfg.training.use_cv:
        scoring = cfg.training.scoring or _default_scoring(cfg.task)
        strategy = cfg.training.search_strategy
        search_params = _normalize_search_params(cfg.training.search_params)

        if strategy == "grid":
            if not search_params:
                raise ValueError("training.search_params is required for grid search")
            search = GridSearchCV(
                estimator=pipeline,
                param_grid=search_params,
                cv=cfg.training.cv_folds,
                scoring=scoring,
                n_jobs=cfg.training.n_jobs,
            )
            search.fit(x_train, y_train)
            trained_pipeline = search.best_estimator_
            cv_payload = {
                "strategy": "grid",
                "scoring": scoring,
                "best_params": search.best_params_,
                "best_score": float(search.best_score_),
            }
        elif strategy == "random":
            if not search_params:
                raise ValueError("training.search_params is required for random search")
            search = RandomizedSearchCV(
                estimator=pipeline,
                param_distributions=search_params,
                n_iter=cfg.training.n_iter,
                cv=cfg.training.cv_folds,
                scoring=scoring,
                n_jobs=cfg.training.n_jobs,
                random_state=cfg.data.random_state,
            )
            search.fit(x_train, y_train)
            trained_pipeline = search.best_estimator_
            cv_payload = {
                "strategy": "random",
                "scoring": scoring,
                "best_params": search.best_params_,
                "best_score": float(search.best_score_),
                "n_iter": cfg.training.n_iter,
            }
        else:
            scores = cross_val_score(
                pipeline,
                x_train,
                y_train,
                cv=cfg.training.cv_folds,
                scoring=scoring,
                n_jobs=cfg.training.n_jobs,
            )
            cv_payload = {
                "strategy": "none",
                **_build_cv_result(scores.tolist(), scoring),
            }
            trained_pipeline.fit(x_train, y_train)
    else:
        trained_pipeline.fit(x_train, y_train)

    preds = trained_pipeline.predict(x_test)
    metrics = _compute_metrics(cfg.task, y_test, preds)

    model_path = Path(cfg.output.model_path)
    metrics_path = Path(cfg.output.metrics_path)
    model_path.parent.mkdir(parents=True, exist_ok=True)
    metrics_path.parent.mkdir(parents=True, exist_ok=True)

    joblib.dump(trained_pipeline, model_path)

    payload = {
        "task": cfg.task,
        "model": cfg.model.name,
        "data_rows": int(df.shape[0]),
        "feature_count": int(x.shape[1]),
        "numeric_features": numeric_cols,
        "categorical_features": categorical_cols,
        "metrics": metrics,
    }
    if cv_payload is not None:
        payload["cv"] = cv_payload

    metrics_path.write_text(json.dumps(payload, indent=2), encoding="utf-8")
    if cv_payload is not None:
        Path(cfg.output.cv_results_path).parent.mkdir(parents=True, exist_ok=True)
        Path(cfg.output.cv_results_path).write_text(
            json.dumps(cv_payload, indent=2),
            encoding="utf-8",
        )

    return payload


def predict_from_model(model_path: str, input_path: str, output_path: str) -> dict[str, Any]:
    model = joblib.load(model_path)
    df = pd.read_csv(input_path)

    predictions = model.predict(df)
    result = df.copy()
    result["prediction"] = predictions

    out = Path(output_path)
    out.parent.mkdir(parents=True, exist_ok=True)
    result.to_csv(out, index=False)

    return {
        "rows": int(result.shape[0]),
        "output_path": str(out),
    }


def predict_records(model_path: str, records: list[dict[str, Any]]) -> dict[str, Any]:
    if not records:
        raise ValueError("records must contain at least one item")

    model = joblib.load(model_path)
    df = pd.DataFrame(records)
    predictions = model.predict(df)

    return {
        "rows": int(df.shape[0]),
        "predictions": predictions.tolist(),
    }


def predict_dataframe(model_path: str, df: pd.DataFrame) -> dict[str, Any]:
    model = joblib.load(model_path)
    predictions = model.predict(df)
    return {
        "rows": int(df.shape[0]),
        "predictions": predictions.tolist(),
    }


def get_model_expected_columns(model_path: str) -> list[str] | None:
    model = joblib.load(model_path)
    names = getattr(model, "feature_names_in_", None)
    if names is None:
        return None
    return [str(name) for name in names.tolist()]
