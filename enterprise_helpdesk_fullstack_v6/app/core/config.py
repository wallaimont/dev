from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "Enterprise Helpdesk API"
    app_env: str = "development"
    app_debug: bool = True
    api_v1_prefix: str = "/api/v1"

    database_url: str = "postgresql+psycopg://helpdesk:helpdesk@localhost:5432/helpdesk_db"
    test_database_url: str = "sqlite+pysqlite:///:memory:"

    jwt_secret_key: str = "change-me-in-production"
    jwt_algorithm: str = "HS256"
    jwt_access_expire_minutes: int = 30
    jwt_refresh_expire_days: int = 7

    default_admin_name: str = "Administrador Geral"
    default_admin_email: str = "admin@empresa.com"
    default_admin_password: str = "Admin1234"
    default_company_name: str = "Empresa Demo"
    default_company_segment: str = "Tecnologia"

    default_sla_low_hours: int = 72
    default_sla_medium_hours: int = 24
    default_sla_high_hours: int = 8
    default_sla_critical_hours: int = 4

    cors_origins: str = "http://localhost:3000,http://127.0.0.1:3000,http://frontend:3000"
    uploads_dir: str = "uploads"
    max_upload_size_mb: int = 10

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    @property
    def cors_origin_list(self) -> list[str]:
        return [origin.strip() for origin in self.cors_origins.split(",") if origin.strip()]


@lru_cache
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
