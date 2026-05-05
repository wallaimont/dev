-- =============================================
-- V2__cadastros_gerais.sql
-- Módulo: Cadastros Gerais
-- =============================================

-- Departamentos
CREATE TABLE departamentos (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Cargos
CREATE TABLE cargos (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    descricao   VARCHAR(300),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Bancos
CREATE TABLE bancos (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    codigo_banco VARCHAR(10) NOT NULL UNIQUE,
    nome        VARCHAR(150) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Contas Bancárias
CREATE TABLE contas_bancarias (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    filial_id   BIGINT REFERENCES filiais(id),
    banco_id    BIGINT NOT NULL REFERENCES bancos(id),
    agencia     VARCHAR(20) NOT NULL,
    conta       VARCHAR(30) NOT NULL,
    digito      VARCHAR(5),
    tipo        VARCHAR(20) NOT NULL DEFAULT 'CORRENTE',
    descricao   VARCHAR(200),
    saldo_inicial NUMERIC(18,2) NOT NULL DEFAULT 0,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE INDEX idx_contabanc_empresa ON contas_bancarias(empresa_id);

-- Centros de Custo
CREATE TABLE centros_custo (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(30) NOT NULL,
    nome        VARCHAR(150) NOT NULL,
    parent_id   BIGINT REFERENCES centros_custo(id),
    nivel       INT NOT NULL DEFAULT 1,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Naturezas Financeiras
CREATE TABLE naturezas_financeiras (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(30) NOT NULL,
    nome        VARCHAR(150) NOT NULL,
    tipo        VARCHAR(10) NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    parent_id   BIGINT REFERENCES naturezas_financeiras(id),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Condições de Pagamento
CREATE TABLE condicoes_pagamento (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    tipo        VARCHAR(30) NOT NULL DEFAULT 'A_PRAZO',
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

CREATE TABLE condicao_pagamento_parcelas (
    id                      BIGSERIAL PRIMARY KEY,
    condicao_pagamento_id   BIGINT NOT NULL REFERENCES condicoes_pagamento(id) ON DELETE CASCADE,
    numero_parcela          INT NOT NULL,
    dias                    INT NOT NULL,
    percentual              NUMERIC(7,4) NOT NULL,
    UNIQUE(condicao_pagamento_id, numero_parcela)
);

-- Unidades de Medida
CREATE TABLE unidades_medida (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    nome        VARCHAR(50) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Grupos de Produto
CREATE TABLE grupos_produto (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Subgrupos de Produto
CREATE TABLE subgrupos_produto (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    grupo_id        BIGINT NOT NULL REFERENCES grupos_produto(id),
    codigo          VARCHAR(20) NOT NULL,
    nome            VARCHAR(100) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, grupo_id, codigo)
);

-- Marcas
CREATE TABLE marcas (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Categorias
CREATE TABLE categorias (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    parent_id   BIGINT REFERENCES categorias(id),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Produtos
CREATE TABLE produtos (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    codigo              VARCHAR(30) NOT NULL,
    nome                VARCHAR(200) NOT NULL,
    descricao           TEXT,
    grupo_id            BIGINT REFERENCES grupos_produto(id),
    subgrupo_id         BIGINT REFERENCES subgrupos_produto(id),
    marca_id            BIGINT REFERENCES marcas(id),
    categoria_id        BIGINT REFERENCES categorias(id),
    unidade_medida_id   BIGINT REFERENCES unidades_medida(id),
    codigo_barras       VARCHAR(50),
    ncm                 VARCHAR(10),
    peso_bruto          NUMERIC(12,4),
    peso_liquido        NUMERIC(12,4),
    preco_custo         NUMERIC(18,4) NOT NULL DEFAULT 0,
    preco_venda         NUMERIC(18,4) NOT NULL DEFAULT 0,
    estoque_minimo      NUMERIC(14,4) NOT NULL DEFAULT 0,
    estoque_maximo      NUMERIC(14,4) NOT NULL DEFAULT 0,
    controla_estoque    BOOLEAN NOT NULL DEFAULT TRUE,
    controla_lote       BOOLEAN NOT NULL DEFAULT FALSE,
    controla_validade   BOOLEAN NOT NULL DEFAULT FALSE,
    tipo                VARCHAR(20) NOT NULL DEFAULT 'PRODUTO',
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    observacao          TEXT,
    UNIQUE(empresa_id, codigo)
);

CREATE INDEX idx_produtos_empresa ON produtos(empresa_id);
CREATE INDEX idx_produtos_grupo ON produtos(grupo_id);
CREATE INDEX idx_produtos_nome ON produtos(empresa_id, nome);
CREATE INDEX idx_produtos_barras ON produtos(codigo_barras);
CREATE INDEX idx_produtos_deleted ON produtos(deleted);

-- Tabelas de Preço
CREATE TABLE tabelas_preco (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    vigencia_inicio DATE,
    vigencia_fim    DATE,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

CREATE TABLE tabela_preco_itens (
    id              BIGSERIAL PRIMARY KEY,
    tabela_preco_id BIGINT NOT NULL REFERENCES tabelas_preco(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produtos(id),
    preco           NUMERIC(18,4) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(tabela_preco_id, produto_id)
);

-- Clientes
CREATE TABLE clientes (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    codigo          VARCHAR(20) NOT NULL,
    tipo_pessoa     VARCHAR(1) NOT NULL DEFAULT 'J' CHECK (tipo_pessoa IN ('F', 'J')),
    razao_social    VARCHAR(200) NOT NULL,
    nome_fantasia   VARCHAR(200),
    cpf_cnpj        VARCHAR(18) NOT NULL,
    inscricao_estadual VARCHAR(20),
    endereco        VARCHAR(300),
    numero          VARCHAR(20),
    complemento     VARCHAR(100),
    bairro          VARCHAR(100),
    cidade          VARCHAR(100),
    uf              CHAR(2),
    cep             VARCHAR(10),
    telefone        VARCHAR(20),
    celular         VARCHAR(20),
    email           VARCHAR(150),
    website         VARCHAR(200),
    limite_credito  NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_devedor   NUMERIC(18,2) NOT NULL DEFAULT 0,
    bloqueio_financeiro BOOLEAN NOT NULL DEFAULT FALSE,
    bloqueio_comercial  BOOLEAN NOT NULL DEFAULT FALSE,
    condicao_pagamento_id BIGINT REFERENCES condicoes_pagamento(id),
    tabela_preco_id     BIGINT REFERENCES tabelas_preco(id),
    vendedor_id         BIGINT,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    observacao      TEXT,
    UNIQUE(empresa_id, codigo)
);

CREATE INDEX idx_clientes_empresa ON clientes(empresa_id);
CREATE INDEX idx_clientes_cpfcnpj ON clientes(cpf_cnpj);
CREATE INDEX idx_clientes_nome ON clientes(empresa_id, razao_social);
CREATE INDEX idx_clientes_deleted ON clientes(deleted);

-- Fornecedores
CREATE TABLE fornecedores (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    codigo          VARCHAR(20) NOT NULL,
    tipo_pessoa     VARCHAR(1) NOT NULL DEFAULT 'J' CHECK (tipo_pessoa IN ('F', 'J')),
    razao_social    VARCHAR(200) NOT NULL,
    nome_fantasia   VARCHAR(200),
    cpf_cnpj        VARCHAR(18) NOT NULL,
    inscricao_estadual VARCHAR(20),
    endereco        VARCHAR(300),
    numero          VARCHAR(20),
    complemento     VARCHAR(100),
    bairro          VARCHAR(100),
    cidade          VARCHAR(100),
    uf              CHAR(2),
    cep             VARCHAR(10),
    telefone        VARCHAR(20),
    email           VARCHAR(150),
    website         VARCHAR(200),
    condicao_pagamento_id BIGINT REFERENCES condicoes_pagamento(id),
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    observacao      TEXT,
    UNIQUE(empresa_id, codigo)
);

CREATE INDEX idx_fornecedores_empresa ON fornecedores(empresa_id);
CREATE INDEX idx_fornecedores_cpfcnpj ON fornecedores(cpf_cnpj);
CREATE INDEX idx_fornecedores_deleted ON fornecedores(deleted);

-- Transportadoras
CREATE TABLE transportadoras (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    codigo          VARCHAR(20) NOT NULL,
    razao_social    VARCHAR(200) NOT NULL,
    nome_fantasia   VARCHAR(200),
    cpf_cnpj        VARCHAR(18) NOT NULL,
    endereco        VARCHAR(300),
    cidade          VARCHAR(100),
    uf              CHAR(2),
    telefone        VARCHAR(20),
    email           VARCHAR(150),
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

-- Contatos (genérico para clientes, fornecedores, transportadoras)
CREATE TABLE contatos (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    entidade_tipo   VARCHAR(30) NOT NULL,
    entidade_id     BIGINT NOT NULL,
    nome            VARCHAR(200) NOT NULL,
    cargo           VARCHAR(100),
    telefone        VARCHAR(20),
    celular         VARCHAR(20),
    email           VARCHAR(150),
    principal       BOOLEAN NOT NULL DEFAULT FALSE,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contatos_entidade ON contatos(entidade_tipo, entidade_id);
