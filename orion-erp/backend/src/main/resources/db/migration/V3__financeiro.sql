-- =============================================
-- V3__financeiro.sql
-- Módulo: Financeiro
-- =============================================

-- Títulos financeiros
CREATE TABLE titulos (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    tipo                VARCHAR(10) NOT NULL CHECK (tipo IN ('PAGAR', 'RECEBER')),
    numero              VARCHAR(30) NOT NULL,
    serie               VARCHAR(10),
    cliente_id          BIGINT REFERENCES clientes(id),
    fornecedor_id       BIGINT REFERENCES fornecedores(id),
    natureza_financeira_id BIGINT REFERENCES naturezas_financeiras(id),
    centro_custo_id     BIGINT REFERENCES centros_custo(id),
    conta_bancaria_id   BIGINT REFERENCES contas_bancarias(id),
    documento_origem    VARCHAR(50),
    documento_origem_id BIGINT,
    data_emissao        DATE NOT NULL,
    valor_original      NUMERIC(18,2) NOT NULL,
    valor_aberto        NUMERIC(18,2) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'ABERTO'
                        CHECK (status IN ('ABERTO','PARCIAL','QUITADO','CANCELADO','RENEGOCIADO')),
    observacao          TEXT,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);

CREATE INDEX idx_titulos_empresa ON titulos(empresa_id, filial_id);
CREATE INDEX idx_titulos_tipo ON titulos(tipo);
CREATE INDEX idx_titulos_status ON titulos(status);
CREATE INDEX idx_titulos_cliente ON titulos(cliente_id);
CREATE INDEX idx_titulos_fornecedor ON titulos(fornecedor_id);
CREATE INDEX idx_titulos_deleted ON titulos(deleted);

-- Parcelas
CREATE TABLE titulo_parcelas (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    titulo_id       BIGINT NOT NULL REFERENCES titulos(id) ON DELETE CASCADE,
    numero_parcela  INT NOT NULL,
    data_vencimento DATE NOT NULL,
    valor           NUMERIC(18,2) NOT NULL,
    valor_pago      NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_juros     NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_multa     NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_desconto  NUMERIC(18,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'ABERTO'
                    CHECK (status IN ('ABERTO','PARCIAL','QUITADO','CANCELADO')),
    data_pagamento  DATE,
    nosso_numero    VARCHAR(30),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(titulo_id, numero_parcela)
);

CREATE INDEX idx_parcelas_titulo ON titulo_parcelas(titulo_id);
CREATE INDEX idx_parcelas_vencimento ON titulo_parcelas(data_vencimento);
CREATE INDEX idx_parcelas_status ON titulo_parcelas(status);

-- Baixas
CREATE TABLE titulo_baixas (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    parcela_id      BIGINT NOT NULL REFERENCES titulo_parcelas(id),
    data_baixa      DATE NOT NULL,
    valor_pago      NUMERIC(18,2) NOT NULL,
    valor_juros     NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_multa     NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_desconto  NUMERIC(18,2) NOT NULL DEFAULT 0,
    conta_bancaria_id BIGINT REFERENCES contas_bancarias(id),
    forma_pagamento VARCHAR(30),
    observacao      TEXT,
    estornado       BOOLEAN NOT NULL DEFAULT FALSE,
    estornado_em    TIMESTAMP,
    estornado_por   VARCHAR(100),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE INDEX idx_baixas_parcela ON titulo_baixas(parcela_id);
CREATE INDEX idx_baixas_data ON titulo_baixas(data_baixa);

-- Fluxo de Caixa (lançamentos)
CREATE TABLE fluxo_caixa (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    conta_bancaria_id   BIGINT NOT NULL REFERENCES contas_bancarias(id),
    tipo                VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA')),
    valor               NUMERIC(18,2) NOT NULL,
    data_lancamento     DATE NOT NULL,
    descricao           VARCHAR(300),
    titulo_id           BIGINT REFERENCES titulos(id),
    baixa_id            BIGINT REFERENCES titulo_baixas(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fluxo_empresa ON fluxo_caixa(empresa_id, filial_id);
CREATE INDEX idx_fluxo_conta ON fluxo_caixa(conta_bancaria_id);
CREATE INDEX idx_fluxo_data ON fluxo_caixa(data_lancamento);
