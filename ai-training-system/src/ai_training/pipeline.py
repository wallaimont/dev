from __future__ import annotations

from typing import Any

from sklearn.compose import ColumnTransformer
from sklearn.ensemble import (
    GradientBoostingClassifier,
    GradientBoostingRegressor,
    RandomForestClassifier,
    RandomForestRegressor,
)
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler


def build_model(task: str, model_name: str, params: dict[str, Any]):
    if task == "classification":
        models = {
            "logistic_regression": LogisticRegression,
            "random_forest": RandomForestClassifier,
            "gradient_boosting": GradientBoostingClassifier,
        }

        if model_name == "pytorch_mlp_classifier":
            from .torch_models import TorchMLPClassifier

            return TorchMLPClassifier(**params)
    else:
        models = {
            "linear_regression": LinearRegression,
            "random_forest_regressor": RandomForestRegressor,
            "gradient_boosting_regressor": GradientBoostingRegressor,
        }

        if model_name == "pytorch_mlp_regressor":
            from .torch_models import TorchMLPRegressor

            return TorchMLPRegressor(**params)

    if model_name not in models:
        options = ", ".join(sorted(models.keys()))
        raise ValueError(f"Unsupported model '{model_name}' for task '{task}'. Options: {options}")

    return models[model_name](**params)


def build_preprocessor(numeric_cols: list[str], categorical_cols: list[str]) -> ColumnTransformer:
    numeric_pipeline = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="median")),
            ("scaler", StandardScaler()),
        ]
    )
    categorical_pipeline = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="most_frequent")),
            ("onehot", OneHotEncoder(handle_unknown="ignore")),
        ]
    )

    return ColumnTransformer(
        transformers=[
            ("num", numeric_pipeline, numeric_cols),
            ("cat", categorical_pipeline, categorical_cols),
        ]
    )


def build_training_pipeline(
    task: str,
    model_name: str,
    model_params: dict[str, Any],
    numeric_cols: list[str],
    categorical_cols: list[str],
) -> Pipeline:
    preprocessor = build_preprocessor(numeric_cols=numeric_cols, categorical_cols=categorical_cols)
    model = build_model(task=task, model_name=model_name, params=model_params)

    return Pipeline(
        steps=[
            ("preprocessor", preprocessor),
            ("model", model),
        ]
    )
