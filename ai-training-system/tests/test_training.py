from __future__ import annotations

from pathlib import Path

from ai_training.service import train_from_dict


def test_train_from_dict_creates_artifacts(tmp_path: Path, sample_csv: Path):
    model_path = tmp_path / "model.joblib"
    metrics_path = tmp_path / "metrics.json"
    cv_path = tmp_path / "cv_results.json"

    config = {
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
            "use_cv": True,
            "cv_folds": 3,
            "search_strategy": "none",
            "scoring": "f1_weighted",
            "n_jobs": 1,
        },
        "output": {
            "model_path": str(model_path),
            "metrics_path": str(metrics_path),
            "cv_results_path": str(cv_path),
        },
    }

    result = train_from_dict(config)

    assert result["task"] == "classification"
    assert "metrics" in result
    assert "cv" in result
    assert model_path.exists()
    assert metrics_path.exists()
    assert cv_path.exists()
