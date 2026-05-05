-- =============================================
-- V6__vendas.sql
-- Módulo: Vendas
-- =============================================

-- Orçamentos
CREATE TABLE orcamentos (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    numero              VARCHAR(20) NOT NULL,
    cliente_id          BIGINT NOT NULL REFERENCES clientes(id),
    vendedor_id         BIGINT REFERENCES usuarios(id),
    data_orcamento      DATE NOT NULL DEFAULT CURRENT_DATE,
    data_validade       DATE,
    condicao_pagamento_id BIGINT REFERENCES condicoes_pagamento(id),
    tabela_preco_id     BIGINT REFERENCES tabelas_preco(id),
    valor_produtos      NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_desconto      NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_frete         NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_total         NUMERIC(18,2) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'ABERTO'
                        CHECK (status IN ('ABERTO','ENVIADO','APROVADO','REPROVADO','CONVERTIDO','CANCELADO')),
    observacao          TEXT,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE TABLE orcamento_itens (
    id              BIGSERIAL PRIMARY KEY,
    orcamento_id    BIGINT NOT NULL REFERENCES orcamentos(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    quantidade      NUMERIC(14,4) NOT NULL,
    preco_unitario  NUMERIC(18,4) NOT NULL,
    percentual_desconto NUMERIC(7,4) NOT NULL DEFAULT 0,
    valor_desconto  NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_total     NUMERIC(18,2) NOT NULL,
    unidade_medida_id BIGINT REFERENCES unidades_medida(id),
    observacao      VARCHAR(300),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Pedidos de Venda
CREATE TABLE pedidos_venda (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    numero              VARCHAR(20) NOT NULL,
    orcamento_id        BIGINT REFERENCES orcamentos(id),
    cliente_id          BIGINT NOT NULL REFERENCES clientes(id),
    vendedor_id         BIGINT REFERENCES usuarios(id),
    transportadora_id   BIGINT REFERENCES transportadoras(id),
    data_pedido         DATE NOT NULL DEFAULT CURRENT_DATE,
    data_previsao_entrega DATE,
    condicao_pagamento_id BIGINT REFERENCES condicoes_pagamento(id),
    tabela_preco_id     BIGINT REFERENCES tabelas_preco(id),
    valor_produtos      NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_desconto      NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_frete         NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_total         NUMERIC(18,2) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                        CHECK (status IN ('PENDENTE','APROVADO','REPROVADO','BLOQUEADO',
                                         'SEPARACAO','EXPEDIDO','FATURADO','CANCELADO')),
    aprovado_por        BIGINT REFERENCES usuarios(id),
    aprovado_em         TIMESTAMP,
    bloqueio_motivo     VARCHAR(300),
    observacao          TEXT,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    UNIQUE(empresa_id, filial_id, numero)
);

CREATE INDEX idx_pedvenda_empresa ON pedidos_venda(empresa_id, filial_id);
CREATE INDEX idx_pedvenda_cliente ON pedidos_venda(cliente_id);
CREATE INDEX idx_pedvenda_status ON pedidos_venda(status);
CREATE INDEX idx_pedvenda_deleted ON pedidos_venda(deleted);

CREATE TABLE pedido_venda_itens (
    id              BIGSERIAL PRIMARY KEY,
    pedido_venda_id BIGINT NOT NULL REFERENCES pedidos_venda(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    quantidade      NUMERIC(14,4) NOT NULL,
    quantidade_entregue NUMERIC(14,4) NOT NULL DEFAULT 0,
    preco_unitario  NUMERIC(18,4) NOT NULL,
    percentual_desconto NUMERIC(7,4) NOT NULL DEFAULT 0,
    valor_desconto  NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_total     NUMERIC(18,2) NOT NULL,
    unidade_medida_id BIGINT REFERENCES unidades_medida(id),
    armazem_id      BIGINT REFERENCES armazens(id),
    observacao      VARCHAR(300),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Comissões
CREATE TABLE comissoes (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    vendedor_id     BIGINT NOT NULL REFERENCES usuarios(id),
    pedido_venda_id BIGINT NOT NULL REFERENCES pedidos_venda(id),
    percentual      NUMERIC(7,4) NOT NULL,
    valor_base      NUMERIC(18,2) NOT NULL,
    valor_comissao  NUMERIC(18,2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                    CHECK (status IN ('PENDENTE','APROVADA','PAGA','CANCELADA')),
    data_pagamento  DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comissoes_vendedor ON comissoes(vendedor_id);
CREATE INDEX idx_comissoes_pedido ON comissoes(pedido_venda_id);
