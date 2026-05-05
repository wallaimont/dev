-- =============================================
-- V4__compras.sql
-- Módulo: Compras
-- =============================================

-- Solicitações de Compra
CREATE TABLE solicitacoes_compra (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    numero          VARCHAR(20) NOT NULL,
    data_solicitacao DATE NOT NULL DEFAULT CURRENT_DATE,
    solicitante_id  BIGINT NOT NULL REFERENCES usuarios(id),
    departamento_id BIGINT REFERENCES departamentos(id),
    centro_custo_id BIGINT REFERENCES centros_custo(id),
    prioridade      VARCHAR(10) NOT NULL DEFAULT 'MEDIA'
                    CHECK (prioridade IN ('BAIXA','MEDIA','ALTA','URGENTE')),
    justificativa   TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                    CHECK (status IN ('PENDENTE','APROVADA','REPROVADA','COTACAO','ATENDIDA','CANCELADA')),
    observacao      TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE TABLE solicitacao_compra_itens (
    id                      BIGSERIAL PRIMARY KEY,
    solicitacao_compra_id   BIGINT NOT NULL REFERENCES solicitacoes_compra(id) ON DELETE CASCADE,
    produto_id              BIGINT NOT NULL REFERENCES produtos(id),
    quantidade              NUMERIC(14,4) NOT NULL,
    unidade_medida_id       BIGINT REFERENCES unidades_medida(id),
    observacao              VARCHAR(300),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Cotações
CREATE TABLE cotacoes (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    numero          VARCHAR(20) NOT NULL,
    data_cotacao    DATE NOT NULL DEFAULT CURRENT_DATE,
    data_validade   DATE,
    solicitacao_compra_id BIGINT REFERENCES solicitacoes_compra(id),
    comprador_id    BIGINT REFERENCES usuarios(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'ABERTA'
                    CHECK (status IN ('ABERTA','EM_ANALISE','APROVADA','CANCELADA')),
    observacao      TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE TABLE cotacao_itens (
    id              BIGSERIAL PRIMARY KEY,
    cotacao_id      BIGINT NOT NULL REFERENCES cotacoes(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    quantidade      NUMERIC(14,4) NOT NULL,
    unidade_medida_id BIGINT REFERENCES unidades_medida(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cotacao_fornecedores (
    id              BIGSERIAL PRIMARY KEY,
    cotacao_item_id BIGINT NOT NULL REFERENCES cotacao_itens(id) ON DELETE CASCADE,
    fornecedor_id   BIGINT NOT NULL REFERENCES fornecedores(id),
    preco_unitario  NUMERIC(18,4) NOT NULL,
    prazo_entrega   INT,
    condicao_pagamento VARCHAR(100),
    selecionado     BOOLEAN NOT NULL DEFAULT FALSE,
    observacao      VARCHAR(300),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Pedidos de Compra
CREATE TABLE pedidos_compra (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    numero              VARCHAR(20) NOT NULL,
    fornecedor_id       BIGINT NOT NULL REFERENCES fornecedores(id),
    cotacao_id          BIGINT REFERENCES cotacoes(id),
    data_pedido         DATE NOT NULL DEFAULT CURRENT_DATE,
    data_previsao_entrega DATE,
    condicao_pagamento_id BIGINT REFERENCES condicoes_pagamento(id),
    comprador_id        BIGINT REFERENCES usuarios(id),
    valor_total         NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_frete         NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_desconto      NUMERIC(18,2) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                        CHECK (status IN ('PENDENTE','APROVADO','REPROVADO','PARCIAL','RECEBIDO','CANCELADO')),
    observacao          TEXT,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE INDEX idx_pedcomp_empresa ON pedidos_compra(empresa_id, filial_id);
CREATE INDEX idx_pedcomp_fornecedor ON pedidos_compra(fornecedor_id);
CREATE INDEX idx_pedcomp_status ON pedidos_compra(status);

CREATE TABLE pedido_compra_itens (
    id              BIGSERIAL PRIMARY KEY,
    pedido_compra_id BIGINT NOT NULL REFERENCES pedidos_compra(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    quantidade      NUMERIC(14,4) NOT NULL,
    quantidade_recebida NUMERIC(14,4) NOT NULL DEFAULT 0,
    preco_unitario  NUMERIC(18,4) NOT NULL,
    valor_total     NUMERIC(18,2) NOT NULL,
    unidade_medida_id BIGINT REFERENCES unidades_medida(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Recebimentos
CREATE TABLE recebimentos (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    numero              VARCHAR(20) NOT NULL,
    pedido_compra_id    BIGINT NOT NULL REFERENCES pedidos_compra(id),
    fornecedor_id       BIGINT NOT NULL REFERENCES fornecedores(id),
    data_recebimento    DATE NOT NULL DEFAULT CURRENT_DATE,
    numero_nf           VARCHAR(30),
    serie_nf            VARCHAR(10),
    chave_nfe           VARCHAR(50),
    valor_total         NUMERIC(18,2) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                        CHECK (status IN ('PENDENTE','CONFERIDO','FINALIZADO','CANCELADO')),
    observacao          TEXT,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE TABLE recebimento_itens (
    id              BIGSERIAL PRIMARY KEY,
    recebimento_id  BIGINT NOT NULL REFERENCES recebimentos(id) ON DELETE CASCADE,
    pedido_compra_item_id BIGINT REFERENCES pedido_compra_itens(id),
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    quantidade      NUMERIC(14,4) NOT NULL,
    preco_unitario  NUMERIC(18,4) NOT NULL,
    valor_total     NUMERIC(18,2) NOT NULL,
    lote            VARCHAR(50),
    validade        DATE,
    armazem_id      BIGINT,
    localizacao_id  BIGINT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
