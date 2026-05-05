-- =============================================
-- V5__estoque.sql
-- Módulo: Estoque
-- =============================================

-- Armazéns
CREATE TABLE armazens (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    filial_id   BIGINT NOT NULL REFERENCES filiais(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    tipo        VARCHAR(20) NOT NULL DEFAULT 'PRINCIPAL',
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, filial_id, codigo)
);

-- Localizações dentro de armazéns
CREATE TABLE localizacoes (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    armazem_id  BIGINT NOT NULL REFERENCES armazens(id),
    codigo      VARCHAR(30) NOT NULL,
    descricao   VARCHAR(100),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(armazem_id, codigo)
);

-- Saldos de Estoque
CREATE TABLE saldos_estoque (
    id              BIGSERIAL PRIMARY KEY,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    armazem_id      BIGINT NOT NULL REFERENCES armazens(id),
    localizacao_id  BIGINT REFERENCES localizacoes(id),
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    lote            VARCHAR(50),
    validade        DATE,
    quantidade      NUMERIC(14,4) NOT NULL DEFAULT 0,
    custo_medio     NUMERIC(18,4) NOT NULL DEFAULT 0,
    reservado       NUMERIC(14,4) NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uq_saldoest_natural ON saldos_estoque(empresa_id, filial_id, armazem_id, COALESCE(localizacao_id, 0), produto_id, COALESCE(lote, ''));

CREATE INDEX idx_saldoest_produto ON saldos_estoque(produto_id);
CREATE INDEX idx_saldoest_armazem ON saldos_estoque(armazem_id);
CREATE INDEX idx_saldoest_empresa ON saldos_estoque(empresa_id, filial_id);

-- Movimentações de Estoque (Kardex)
CREATE TABLE movimentacoes_estoque (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    armazem_id      BIGINT NOT NULL REFERENCES armazens(id),
    localizacao_id  BIGINT REFERENCES localizacoes(id),
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    tipo            VARCHAR(15) NOT NULL
                    CHECK (tipo IN ('ENTRADA','SAIDA','AJUSTE','TRANSFERENCIA')),
    quantidade      NUMERIC(14,4) NOT NULL,
    custo_unitario  NUMERIC(18,4) NOT NULL DEFAULT 0,
    custo_total     NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_anterior  NUMERIC(14,4) NOT NULL DEFAULT 0,
    saldo_posterior NUMERIC(14,4) NOT NULL DEFAULT 0,
    lote            VARCHAR(50),
    validade        DATE,
    documento_tipo  VARCHAR(30),
    documento_id    BIGINT,
    documento_numero VARCHAR(30),
    observacao      VARCHAR(300),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE INDEX idx_movest_produto ON movimentacoes_estoque(produto_id);
CREATE INDEX idx_movest_empresa ON movimentacoes_estoque(empresa_id, filial_id);
CREATE INDEX idx_movest_data ON movimentacoes_estoque(created_at);
CREATE INDEX idx_movest_tipo ON movimentacoes_estoque(tipo);

-- Inventários
CREATE TABLE inventarios (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    armazem_id      BIGINT NOT NULL REFERENCES armazens(id),
    numero          VARCHAR(20) NOT NULL,
    data_inventario DATE NOT NULL DEFAULT CURRENT_DATE,
    responsavel_id  BIGINT REFERENCES usuarios(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'ABERTO'
                    CHECK (status IN ('ABERTO','EM_CONTAGEM','FINALIZADO','CANCELADO')),
    observacao      TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE TABLE inventario_itens (
    id              BIGSERIAL PRIMARY KEY,
    inventario_id   BIGINT NOT NULL REFERENCES inventarios(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    lote            VARCHAR(50),
    quantidade_sistema NUMERIC(14,4) NOT NULL DEFAULT 0,
    quantidade_contada NUMERIC(14,4),
    diferenca       NUMERIC(14,4),
    ajustado        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
