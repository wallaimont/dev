-- =============================================
-- V1__admin_empresas_filiais_usuarios.sql
-- Módulo: Administração
-- Tabelas: empresas, filiais, perfis, usuarios, permissoes, perfil_permissoes
-- =============================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Empresas
CREATE TABLE empresas (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    codigo      VARCHAR(20) NOT NULL UNIQUE,
    razao_social VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    cnpj        VARCHAR(18) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(20),
    inscricao_municipal VARCHAR(20),
    endereco    VARCHAR(300),
    numero      VARCHAR(20),
    complemento VARCHAR(100),
    bairro      VARCHAR(100),
    cidade      VARCHAR(100),
    uf          CHAR(2),
    cep         VARCHAR(10),
    telefone    VARCHAR(20),
    email       VARCHAR(150),
    website     VARCHAR(200),
    logo_url    VARCHAR(500),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    observacao  TEXT
);

CREATE INDEX idx_empresas_codigo ON empresas(codigo);
CREATE INDEX idx_empresas_cnpj ON empresas(cnpj);
CREATE INDEX idx_empresas_deleted ON empresas(deleted);

-- Filiais
CREATE TABLE filiais (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    razao_social VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    cnpj        VARCHAR(18) NOT NULL,
    inscricao_estadual VARCHAR(20),
    inscricao_municipal VARCHAR(20),
    endereco    VARCHAR(300),
    numero      VARCHAR(20),
    complemento VARCHAR(100),
    bairro      VARCHAR(100),
    cidade      VARCHAR(100),
    uf          CHAR(2),
    cep         VARCHAR(10),
    telefone    VARCHAR(20),
    email       VARCHAR(150),
    matriz      BOOLEAN NOT NULL DEFAULT FALSE,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    observacao  TEXT,
    UNIQUE(empresa_id, codigo)
);

CREATE INDEX idx_filiais_empresa ON filiais(empresa_id);
CREATE INDEX idx_filiais_cnpj ON filiais(cnpj);
CREATE INDEX idx_filiais_deleted ON filiais(deleted);

-- Perfis
CREATE TABLE perfis (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(50) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    descricao   VARCHAR(300),
    admin       BOOLEAN NOT NULL DEFAULT FALSE,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

CREATE INDEX idx_perfis_empresa ON perfis(empresa_id);

-- Usuarios
CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT REFERENCES filiais(id),
    perfil_id       BIGINT NOT NULL REFERENCES perfis(id),
    nome            VARCHAR(200) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    senha_hash      VARCHAR(300) NOT NULL,
    telefone        VARCHAR(20),
    avatar_url      VARCHAR(500),
    ultimo_login    TIMESTAMP,
    tentativas_login INT NOT NULL DEFAULT 0,
    bloqueado       BOOLEAN NOT NULL DEFAULT FALSE,
    bloqueado_ate   TIMESTAMP,
    trocar_senha    BOOLEAN NOT NULL DEFAULT FALSE,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);

CREATE INDEX idx_usuarios_empresa ON usuarios(empresa_id);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_perfil ON usuarios(perfil_id);
CREATE INDEX idx_usuarios_deleted ON usuarios(deleted);

-- Permissoes
CREATE TABLE permissoes (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    recurso     VARCHAR(100) NOT NULL,
    acao        VARCHAR(50) NOT NULL,
    descricao   VARCHAR(300),
    modulo      VARCHAR(50) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(recurso, acao)
);

CREATE INDEX idx_permissoes_recurso ON permissoes(recurso);
CREATE INDEX idx_permissoes_modulo ON permissoes(modulo);

-- Perfil x Permissoes
CREATE TABLE perfil_permissoes (
    id          BIGSERIAL PRIMARY KEY,
    perfil_id   BIGINT NOT NULL REFERENCES perfis(id) ON DELETE CASCADE,
    permissao_id BIGINT NOT NULL REFERENCES permissoes(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(perfil_id, permissao_id)
);

CREATE INDEX idx_pp_perfil ON perfil_permissoes(perfil_id);
CREATE INDEX idx_pp_permissao ON perfil_permissoes(permissao_id);

-- Parâmetros do Sistema
CREATE TABLE parametros_sistema (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT REFERENCES empresas(id),
    filial_id   BIGINT REFERENCES filiais(id),
    chave       VARCHAR(100) NOT NULL,
    valor       TEXT,
    tipo        VARCHAR(30) NOT NULL DEFAULT 'STRING',
    descricao   VARCHAR(300),
    modulo      VARCHAR(50),
    editavel    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE UNIQUE INDEX idx_param_unique ON parametros_sistema(
    COALESCE(empresa_id, 0), COALESCE(filial_id, 0), chave
);

-- Menus
CREATE TABLE menus (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    parent_id   BIGINT REFERENCES menus(id),
    codigo      VARCHAR(50) NOT NULL UNIQUE,
    titulo      VARCHAR(100) NOT NULL,
    icone       VARCHAR(50),
    rota        VARCHAR(200),
    ordem       INT NOT NULL DEFAULT 0,
    modulo      VARCHAR(50),
    permissao_recurso VARCHAR(100),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_menus_parent ON menus(parent_id);
CREATE INDEX idx_menus_modulo ON menus(modulo);

-- Audit Log
CREATE TABLE audit_log (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid(),
    empresa_id  BIGINT,
    filial_id   BIGINT,
    usuario_id  BIGINT,
    usuario_nome VARCHAR(200),
    acao        VARCHAR(20) NOT NULL,
    entidade    VARCHAR(100) NOT NULL,
    entidade_id BIGINT,
    dados_antes JSONB,
    dados_depois JSONB,
    ip          VARCHAR(45),
    user_agent  VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_empresa ON audit_log(empresa_id);
CREATE INDEX idx_audit_entidade ON audit_log(entidade, entidade_id);
CREATE INDEX idx_audit_usuario ON audit_log(usuario_id);
CREATE INDEX idx_audit_created ON audit_log(created_at);

-- Login Log
CREATE TABLE login_log (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT,
    email       VARCHAR(150) NOT NULL,
    sucesso     BOOLEAN NOT NULL,
    ip          VARCHAR(45),
    user_agent  VARCHAR(500),
    motivo_falha VARCHAR(200),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_loginlog_email ON login_log(email);
CREATE INDEX idx_loginlog_created ON login_log(created_at);

-- Refresh Tokens
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token       VARCHAR(500) NOT NULL UNIQUE,
    expira_em   TIMESTAMP NOT NULL,
    revogado    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rtoken_usuario ON refresh_tokens(usuario_id);
CREATE INDEX idx_rtoken_token ON refresh_tokens(token);
