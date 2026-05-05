-- =============================================================
-- InsuranceFlow SaaS - Migration V4 - Security Hardening
-- =============================================================

-- ========== CAMPOS DE ACCOUNT LOCKOUT NO USUARIO ==========
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS login_attempts INT NOT NULL DEFAULT 0;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMP;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT FALSE;

-- ========== TOKEN BLACKLIST (para logout / revogação) ==========
CREATE TABLE IF NOT EXISTS token_blacklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_jti VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL REFERENCES usuario(id),
    tipo VARCHAR(20) NOT NULL DEFAULT 'ACCESS',
    expira_em TIMESTAMP NOT NULL,
    revogado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    motivo VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_token_blacklist_jti ON token_blacklist(token_jti);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_user ON token_blacklist(user_id);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_expira ON token_blacklist(expira_em);

-- ========== AUDIT LOG DE SEGURANÇA ==========
CREATE TABLE IF NOT EXISTS security_audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento VARCHAR(100) NOT NULL,
    usuario_id UUID,
    usuario_email VARCHAR(150),
    empresa_id UUID,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    detalhes TEXT,
    sucesso BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_security_audit_evento ON security_audit_log(evento);
CREATE INDEX IF NOT EXISTS idx_security_audit_usuario ON security_audit_log(usuario_id);
CREATE INDEX IF NOT EXISTS idx_security_audit_empresa ON security_audit_log(empresa_id);
CREATE INDEX IF NOT EXISTS idx_security_audit_created ON security_audit_log(created_at);
CREATE INDEX IF NOT EXISTS idx_security_audit_ip ON security_audit_log(ip_address);

-- ========== LIMPEZA AUTOMATICA DE TOKENS EXPIRADOS ==========
-- (Executada por scheduled task no Spring)
