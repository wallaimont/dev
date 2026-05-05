from __future__ import annotations

import os
from pathlib import Path

import pandas as pd
import pytest


@pytest.fixture(scope="session")
def api_token() -> str:
    token = "test-token-123"
    os.environ["AI_TRAINING_API_TOKEN"] = token
    return token


@pytest.fixture
def sample_csv(tmp_path: Path) -> Path:
    df = pd.DataFrame(
        [
            {"age": 25, "monthly_spend": 89.5, "contract_months": 3, "support_tickets": 4, "payment_method": "credit_card", "churned": 1},
            {"age": 31, "monthly_spend": 120.0, "contract_months": 12, "support_tickets": 1, "payment_method": "pix", "churned": 0},
            {"age": 42, "monthly_spend": 59.9, "contract_months": 24, "support_tickets": 0, "payment_method": "boleto", "churned": 0},
            {"age": 37, "monthly_spend": 140.2, "contract_months": 6, "support_tickets": 3, "payment_method": "credit_card", "churned": 1},
            {"age": 29, "monthly_spend": 78.0, "contract_months": 18, "support_tickets": 1, "payment_method": "debit_card", "churned": 0},
            {"age": 51, "monthly_spend": 200.0, "contract_months": 2, "support_tickets": 6, "payment_method": "boleto", "churned": 1},
            {"age": 33, "monthly_spend": 110.0, "contract_months": 10, "support_tickets": 2, "payment_method": "pix", "churned": 0},
            {"age": 27, "monthly_spend": 95.5, "contract_months": 4, "support_tickets": 4, "payment_method": "credit_card", "churned": 1},
        ]
    )
    path = tmp_path / "train.csv"
    df.to_csv(path, index=False)
    return path
