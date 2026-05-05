from __future__ import annotations

from dataclasses import dataclass, field
from pathlib import Path
from typing import Any

import yaml


@dataclass
class DataConfig:
    train_path: str
    target_column: str
    test_size: float = 0.2
    random_state: int = 42


@dataclass
class ModelConfig:
    name: str
    params: dict[str, Any] = field(default_factory=dict)


@dataclass
class OutputConfig:
    model_path: str = "artifacts/model.joblib"
    metrics_path: str = "artifacts/metrics.json"
    cv_results_path: str = "artifacts/cv_results.json"


@dataclass
class TrainingOptions:
    use_cv: bool = False
    cv_folds: int = 5
    search_strategy: str = "none"
    search_params: dict[str, list[Any]] = field(default_factory=dict)
    n_iter: int = 10
    scoring: str | None = None
    n_jobs: int = -1


@dataclass
class TrainingConfig:
    task: str
    data: DataConfig
    model: ModelConfig
    output: OutputConfig
    training: TrainingOptions


def load_config(config_path: str | Path) -> TrainingConfig:
    path = Path(config_path)
    if not path.exists():
        raise FileNotFoundError(f"Config file not found: {path}")

    raw = yaml.safe_load(path.read_text(encoding="utf-8"))
    return parse_training_config(raw)


def parse_training_config(raw: dict[str, Any]) -> TrainingConfig:
    if not isinstance(raw, dict):
        raise ValueError("Config root must be a mapping")

    task = str(raw.get("task", "")).strip().lower()
    if task not in {"classification", "regression"}:
        raise ValueError("task must be 'classification' or 'regression'")

    data_raw = raw.get("data") or {}
    model_raw = raw.get("model") or {}
    output_raw = raw.get("output") or {}
    training_raw = raw.get("training") or {}

    data = DataConfig(
        train_path=str(data_raw.get("train_path", "")).strip(),
        target_column=str(data_raw.get("target_column", "")).strip(),
        test_size=float(data_raw.get("test_size", 0.2)),
        random_state=int(data_raw.get("random_state", 42)),
    )
    if not data.train_path:
        raise ValueError("data.train_path is required")
    if not data.target_column:
        raise ValueError("data.target_column is required")

    model = ModelConfig(
        name=str(model_raw.get("name", "")).strip().lower(),
        params=dict(model_raw.get("params") or {}),
    )
    if not model.name:
        raise ValueError("model.name is required")

    output = OutputConfig(
        model_path=str(output_raw.get("model_path", "artifacts/model.joblib")),
        metrics_path=str(output_raw.get("metrics_path", "artifacts/metrics.json")),
        cv_results_path=str(output_raw.get("cv_results_path", "artifacts/cv_results.json")),
    )

    strategy = str(training_raw.get("search_strategy", "none")).strip().lower()
    if strategy not in {"none", "grid", "random"}:
        raise ValueError("training.search_strategy must be 'none', 'grid' or 'random'")

    training = TrainingOptions(
        use_cv=bool(training_raw.get("use_cv", False)),
        cv_folds=int(training_raw.get("cv_folds", 5)),
        search_strategy=strategy,
        search_params=dict(training_raw.get("search_params") or {}),
        n_iter=int(training_raw.get("n_iter", 10)),
        scoring=training_raw.get("scoring"),
        n_jobs=int(training_raw.get("n_jobs", -1)),
    )
    if training.cv_folds < 2:
        raise ValueError("training.cv_folds must be >= 2")
    if training.n_iter < 1:
        raise ValueError("training.n_iter must be >= 1")

    return TrainingConfig(task=task, data=data, model=model, output=output, training=training)
