import os

os.environ["DATABASE_URL"] = "sqlite+pysqlite:///./test.db"
os.environ["TEST_DATABASE_URL"] = "sqlite+pysqlite:///./test.db"
os.environ["JWT_SECRET_KEY"] = "test-secret"
os.environ["DEFAULT_ADMIN_PASSWORD"] = "Admin1234"

import pytest
from fastapi.testclient import TestClient

from app.core.database import Base, SessionLocal, engine
from app.main import app


@pytest.fixture(scope="session", autouse=True)
def setup_database():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)


@pytest.fixture()
def client():
    with TestClient(app) as test_client:
        yield test_client
