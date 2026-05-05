from fastapi import HTTPException
from jose import JWTError
from sqlalchemy.orm import Session

from app.core.security import create_access_token, create_refresh_token, decode_token, verify_password
from app.repositories.user_repository import UserRepository


class AuthService:
    def __init__(self, db: Session):
        self.user_repo = UserRepository(db)

    def login(self, email: str, password: str) -> dict[str, str]:
        user = self.user_repo.get_by_email(email)
        if not user or not verify_password(password, user.password_hash):
            raise HTTPException(status_code=401, detail="E-mail ou senha inválidos.")

        base = {"sub": user.email, "role": user.role.value}
        return {
            "access_token": create_access_token(base),
            "refresh_token": create_refresh_token(base),
            "token_type": "bearer",
        }

    def refresh(self, refresh_token: str) -> dict[str, str]:
        try:
            payload = decode_token(refresh_token)
        except JWTError as exc:
            raise HTTPException(status_code=401, detail="Refresh token inválido.") from exc

        if payload.get("token_type") != "refresh":
            raise HTTPException(status_code=401, detail="Token informado não é um refresh token.")

        email = payload.get("sub")
        user = self.user_repo.get_by_email(email) if email else None
        if not user or not user.is_active:
            raise HTTPException(status_code=401, detail="Usuário inválido para renovação de sessão.")

        base = {"sub": user.email, "role": user.role.value}
        return {
            "access_token": create_access_token(base),
            "refresh_token": create_refresh_token(base),
            "token_type": "bearer",
        }
