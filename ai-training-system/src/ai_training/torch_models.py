from __future__ import annotations

from typing import Any

import numpy as np
from scipy import sparse
from sklearn.base import BaseEstimator, ClassifierMixin, RegressorMixin
from sklearn.preprocessing import LabelEncoder

try:
    import torch
    from torch import nn
    from torch.utils.data import DataLoader, TensorDataset
except ImportError:  # pragma: no cover
    torch = None
    nn = None
    DataLoader = None
    TensorDataset = None


class _MLP(nn.Module):
    def __init__(self, input_dim: int, hidden_dims: tuple[int, ...], output_dim: int, dropout: float = 0.0):
        super().__init__()
        layers: list[nn.Module] = []
        in_dim = input_dim

        for hidden_dim in hidden_dims:
            layers.append(nn.Linear(in_dim, hidden_dim))
            layers.append(nn.ReLU())
            if dropout > 0:
                layers.append(nn.Dropout(dropout))
            in_dim = hidden_dim

        layers.append(nn.Linear(in_dim, output_dim))
        self.net = nn.Sequential(*layers)

    def forward(self, x):
        return self.net(x)


class _TorchMLPBase(BaseEstimator):
    def __init__(
        self,
        hidden_dims: tuple[int, ...] = (64, 32),
        epochs: int = 30,
        batch_size: int = 32,
        lr: float = 1e-3,
        dropout: float = 0.0,
        random_state: int = 42,
        device: str = "cpu",
        verbose: bool = False,
    ):
        self.hidden_dims = hidden_dims
        self.epochs = epochs
        self.batch_size = batch_size
        self.lr = lr
        self.dropout = dropout
        self.random_state = random_state
        self.device = device
        self.verbose = verbose

    def _to_numpy(self, x: Any) -> np.ndarray:
        if sparse.issparse(x):
            x = x.toarray()
        if hasattr(x, "to_numpy"):
            x = x.to_numpy()
        return np.asarray(x, dtype=np.float32)

    def _ensure_torch(self) -> None:
        if torch is None or nn is None or DataLoader is None or TensorDataset is None:
            raise ImportError(
                "PyTorch is required for pytorch_* models. Install with 'pip install -e .[torch]'."
            )

    def _train_loop(self, x: np.ndarray, y: np.ndarray, output_dim: int, criterion) -> None:
        self._ensure_torch()
        torch.manual_seed(self.random_state)
        np.random.seed(self.random_state)

        self.model_ = _MLP(
            input_dim=x.shape[1],
            hidden_dims=tuple(self.hidden_dims),
            output_dim=output_dim,
            dropout=self.dropout,
        ).to(self.device)

        x_tensor = torch.tensor(x, dtype=torch.float32)
        y_tensor = torch.tensor(y)
        dataset = TensorDataset(x_tensor, y_tensor)
        loader = DataLoader(dataset, batch_size=self.batch_size, shuffle=True)

        optimizer = torch.optim.Adam(self.model_.parameters(), lr=self.lr)

        self.model_.train()
        for epoch in range(self.epochs):
            total_loss = 0.0
            for batch_x, batch_y in loader:
                batch_x = batch_x.to(self.device)
                batch_y = batch_y.to(self.device)

                optimizer.zero_grad()
                outputs = self.model_(batch_x)
                loss = criterion(outputs, batch_y)
                loss.backward()
                optimizer.step()
                total_loss += float(loss.item())

            if self.verbose and (epoch + 1) % 10 == 0:
                avg = total_loss / max(len(loader), 1)
                print(f"epoch={epoch + 1} loss={avg:.6f}")


class TorchMLPClassifier(_TorchMLPBase, ClassifierMixin):
    def fit(self, x, y):
        x_np = self._to_numpy(x)
        y_np = np.asarray(y)

        self.label_encoder_ = LabelEncoder()
        y_encoded = self.label_encoder_.fit_transform(y_np).astype(np.int64)
        self.classes_ = self.label_encoder_.classes_

        self._train_loop(
            x=x_np,
            y=y_encoded,
            output_dim=len(self.classes_),
            criterion=nn.CrossEntropyLoss(),
        )
        return self

    def predict(self, x):
        x_np = self._to_numpy(x)
        x_tensor = torch.tensor(x_np, dtype=torch.float32).to(self.device)

        self.model_.eval()
        with torch.no_grad():
            logits = self.model_(x_tensor)
            pred_idx = torch.argmax(logits, dim=1).cpu().numpy()

        return self.label_encoder_.inverse_transform(pred_idx)

    def predict_proba(self, x):
        x_np = self._to_numpy(x)
        x_tensor = torch.tensor(x_np, dtype=torch.float32).to(self.device)

        self.model_.eval()
        with torch.no_grad():
            logits = self.model_(x_tensor)
            probs = torch.softmax(logits, dim=1).cpu().numpy()

        return probs


class TorchMLPRegressor(_TorchMLPBase, RegressorMixin):
    def fit(self, x, y):
        x_np = self._to_numpy(x)
        y_np = np.asarray(y, dtype=np.float32).reshape(-1, 1)

        self._train_loop(
            x=x_np,
            y=y_np,
            output_dim=1,
            criterion=nn.MSELoss(),
        )
        return self

    def predict(self, x):
        x_np = self._to_numpy(x)
        x_tensor = torch.tensor(x_np, dtype=torch.float32).to(self.device)

        self.model_.eval()
        with torch.no_grad():
            preds = self.model_(x_tensor).cpu().numpy().reshape(-1)

        return preds
