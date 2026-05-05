-- =============================================
-- V7__fiscal_crm_rh_workflow.sql
-- Módulos: Fiscal, CRM, RH, Workflow
-- =============================================

-- ==================== FISCAL ====================

CREATE TABLE cfops (
    id          BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    descricao   VARCHAR(300) NOT NULL,
    tipo        VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA')),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE csts (
    id          BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    descricao   VARCHAR(300) NOT NULL,
    tipo_imposto VARCHAR(20) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ncms (
    id          BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    descricao   VARCHAR(500) NOT NULL,
    aliquota_ipi NUMERIC(7,4),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE naturezas_operacao (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    codigo      VARCHAR(20) NOT NULL,
    nome        VARCHAR(150) NOT NULL,
    tipo        VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA')),
    cfop_id     BIGINT REFERENCES cfops(id),
    gera_financeiro BOOLEAN NOT NULL DEFAULT TRUE,
    movimenta_estoque BOOLEAN NOT NULL DEFAULT TRUE,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);

CREATE TABLE regras_fiscais (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id  BIGINT NOT NULL REFERENCES empresas(id),
    uf_origem   CHAR(2),
    uf_destino  CHAR(2),
    ncm_id      BIGINT REFERENCES ncms(id),
    cfop_id     BIGINT REFERENCES cfops(id),
    cst_icms_id BIGINT REFERENCES csts(id),
    cst_pis_id  BIGINT REFERENCES csts(id),
    cst_cofins_id BIGINT REFERENCES csts(id),
    aliquota_icms NUMERIC(7,4),
    aliquota_pis NUMERIC(7,4),
    aliquota_cofins NUMERIC(7,4),
    aliquota_ipi NUMERIC(7,4),
    reducao_base_icms NUMERIC(7,4),
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==================== CRM ====================

CREATE TABLE leads (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    nome            VARCHAR(200) NOT NULL,
    email           VARCHAR(150),
    telefone        VARCHAR(20),
    empresa_lead    VARCHAR(200),
    cargo           VARCHAR(100),
    origem          VARCHAR(50),
    responsavel_id  BIGINT REFERENCES usuarios(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'NOVO'
                    CHECK (status IN ('NOVO','CONTATADO','QUALIFICADO','CONVERTIDO','PERDIDO')),
    convertido_cliente_id BIGINT REFERENCES clientes(id),
    observacao      TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);

CREATE INDEX idx_leads_empresa ON leads(empresa_id);
CREATE INDEX idx_leads_status ON leads(status);

CREATE TABLE oportunidades (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    titulo          VARCHAR(200) NOT NULL,
    lead_id         BIGINT REFERENCES leads(id),
    cliente_id      BIGINT REFERENCES clientes(id),
    responsavel_id  BIGINT REFERENCES usuarios(id),
    valor_estimado  NUMERIC(18,2),
    probabilidade   INT CHECK (probabilidade BETWEEN 0 AND 100),
    etapa_funil     VARCHAR(30) NOT NULL DEFAULT 'PROSPECCAO'
                    CHECK (etapa_funil IN ('PROSPECCAO','QUALIFICACAO','PROPOSTA','NEGOCIACAO','FECHAMENTO','GANHO','PERDIDO')),
    data_previsao_fechamento DATE,
    motivo_perda    VARCHAR(300),
    observacao      TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);

CREATE INDEX idx_oportunidades_empresa ON oportunidades(empresa_id);
CREATE INDEX idx_oportunidades_etapa ON oportunidades(etapa_funil);

CREATE TABLE atividades_crm (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    tipo            VARCHAR(20) NOT NULL CHECK (tipo IN ('LIGACAO','EMAIL','REUNIAO','TAREFA','VISITA')),
    titulo          VARCHAR(200) NOT NULL,
    descricao       TEXT,
    lead_id         BIGINT REFERENCES leads(id),
    oportunidade_id BIGINT REFERENCES oportunidades(id),
    cliente_id      BIGINT REFERENCES clientes(id),
    responsavel_id  BIGINT REFERENCES usuarios(id),
    data_hora       TIMESTAMP NOT NULL,
    duracao_minutos INT,
    concluida       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE INDEX idx_ativcrm_empresa ON atividades_crm(empresa_id);

-- ==================== RH ====================

CREATE TABLE funcionarios (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT NOT NULL REFERENCES filiais(id),
    codigo          VARCHAR(20) NOT NULL,
    nome            VARCHAR(200) NOT NULL,
    cpf             VARCHAR(14) NOT NULL,
    rg              VARCHAR(20),
    data_nascimento DATE,
    sexo            CHAR(1) CHECK (sexo IN ('M','F')),
    estado_civil    VARCHAR(20),
    endereco        VARCHAR(300),
    cidade          VARCHAR(100),
    uf              CHAR(2),
    cep             VARCHAR(10),
    telefone        VARCHAR(20),
    email           VARCHAR(150),
    departamento_id BIGINT REFERENCES departamentos(id),
    cargo_id        BIGINT REFERENCES cargos(id),
    data_admissao   DATE NOT NULL,
    data_demissao   DATE,
    salario         NUMERIC(18,2),
    situacao        VARCHAR(20) NOT NULL DEFAULT 'ATIVO'
                    CHECK (situacao IN ('ATIVO','FERIAS','AFASTADO','DEMITIDO')),
    usuario_id      BIGINT REFERENCES usuarios(id),
    pis             VARCHAR(20),
    ctps            VARCHAR(20),
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

CREATE INDEX idx_func_empresa ON funcionarios(empresa_id, filial_id);
CREATE INDEX idx_func_cpf ON funcionarios(cpf);
CREATE INDEX idx_func_depto ON funcionarios(departamento_id);

-- ==================== WORKFLOW ====================

CREATE TABLE workflow_definicoes (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    codigo          VARCHAR(50) NOT NULL,
    nome            VARCHAR(150) NOT NULL,
    descricao       VARCHAR(300),
    modulo          VARCHAR(50) NOT NULL,
    entidade        VARCHAR(100) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(empresa_id, codigo)
);

CREATE TABLE workflow_alcadas (
    id                  BIGSERIAL PRIMARY KEY,
    workflow_definicao_id BIGINT NOT NULL REFERENCES workflow_definicoes(id) ON DELETE CASCADE,
    nivel               INT NOT NULL,
    nome                VARCHAR(100) NOT NULL,
    perfil_id           BIGINT REFERENCES perfis(id),
    usuario_id          BIGINT REFERENCES usuarios(id),
    valor_minimo        NUMERIC(18,2),
    valor_maximo        NUMERIC(18,2),
    obrigatorio         BOOLEAN NOT NULL DEFAULT TRUE,
    ordem               INT NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE workflow_aprovacoes (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id          BIGINT NOT NULL REFERENCES empresas(id),
    filial_id           BIGINT NOT NULL REFERENCES filiais(id),
    workflow_definicao_id BIGINT NOT NULL REFERENCES workflow_definicoes(id),
    alcada_id           BIGINT NOT NULL REFERENCES workflow_alcadas(id),
    entidade            VARCHAR(100) NOT NULL,
    entidade_id         BIGINT NOT NULL,
    nivel               INT NOT NULL,
    aprovador_id        BIGINT REFERENCES usuarios(id),
    decisao             VARCHAR(20) CHECK (decisao IN ('APROVADO','REPROVADO')),
    justificativa       TEXT,
    data_decisao        TIMESTAMP,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                        CHECK (status IN ('PENDENTE','APROVADO','REPROVADO','EXPIRADO')),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wfaprov_entidade ON workflow_aprovacoes(entidade, entidade_id);
CREATE INDEX idx_wfaprov_aprovador ON workflow_aprovacoes(aprovador_id);
CREATE INDEX idx_wfaprov_status ON workflow_aprovacoes(status);
