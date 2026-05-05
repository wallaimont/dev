-- =============================================
-- V9__seguros.sql
-- Módulo: Seguros (baseado no Protheus SIGA Seguros)
-- Tabelas: seguradoras, corretoras, propostas_seguro, apolices, notificacoes_seguro
-- =============================================

-- ==================== SEGURADORAS ====================

CREATE TABLE seguradoras (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    codigo          VARCHAR(20) NOT NULL,
    nome            VARCHAR(200) NOT NULL,
    cnpj            VARCHAR(18) NOT NULL,
    registro_susep  VARCHAR(30),
    email           VARCHAR(150),
    telefone        VARCHAR(20),
    endereco        VARCHAR(300),
    cidade          VARCHAR(100),
    uf              CHAR(2),
    cep             VARCHAR(10),
    contato         VARCHAR(100),
    observacao      TEXT,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, codigo),
    UNIQUE(empresa_id, cnpj)
);

CREATE INDEX idx_seguradoras_empresa ON seguradoras(empresa_id);
CREATE INDEX idx_seguradoras_nome ON seguradoras(empresa_id, nome);

-- ==================== CORRETORAS ====================

CREATE TABLE corretoras (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    codigo          VARCHAR(20) NOT NULL,
    nome            VARCHAR(200) NOT NULL,
    cnpj            VARCHAR(18) NOT NULL,
    responsavel     VARCHAR(100),
    email           VARCHAR(150),
    telefone        VARCHAR(20),
    endereco        VARCHAR(300),
    cidade          VARCHAR(100),
    uf              CHAR(2),
    cep             VARCHAR(10),
    percentual_comissao NUMERIC(5,2) NOT NULL DEFAULT 0,
    observacao      TEXT,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, codigo),
    UNIQUE(empresa_id, cnpj)
);

CREATE INDEX idx_corretoras_empresa ON corretoras(empresa_id);
CREATE INDEX idx_corretoras_nome ON corretoras(empresa_id, nome);

-- ==================== PROPOSTAS DE SEGURO ====================

CREATE TABLE propostas_seguro (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT REFERENCES filiais(id),
    numero          VARCHAR(20) NOT NULL,
    cliente_id      BIGINT NOT NULL REFERENCES clientes(id),
    seguradora_id   BIGINT NOT NULL REFERENCES seguradoras(id),
    corretora_id    BIGINT REFERENCES corretoras(id),
    ramo            VARCHAR(50) NOT NULL,
    vigencia_inicio DATE NOT NULL,
    vigencia_fim    DATE NOT NULL,
    premio_liquido  NUMERIC(18,2) NOT NULL DEFAULT 0,
    premio_total    NUMERIC(18,2) NOT NULL DEFAULT 0,
    percentual_comissao NUMERIC(5,2) NOT NULL DEFAULT 0,
    responsavel     VARCHAR(100),
    observacao      TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'COTACAO'
                    CHECK (status IN ('COTACAO','ENVIADA','APROVADA','RECUSADA','CANCELADA')),
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, numero)
);

CREATE INDEX idx_propostas_empresa ON propostas_seguro(empresa_id);
CREATE INDEX idx_propostas_cliente ON propostas_seguro(cliente_id);
CREATE INDEX idx_propostas_status ON propostas_seguro(status);
CREATE INDEX idx_propostas_seguradora ON propostas_seguro(seguradora_id);

-- ==================== APÓLICES ====================

CREATE TABLE apolices (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    filial_id       BIGINT REFERENCES filiais(id),
    numero          VARCHAR(30) NOT NULL,
    proposta_id     BIGINT REFERENCES propostas_seguro(id),
    cliente_id      BIGINT NOT NULL REFERENCES clientes(id),
    seguradora_id   BIGINT NOT NULL REFERENCES seguradoras(id),
    corretora_id    BIGINT REFERENCES corretoras(id),
    ramo            VARCHAR(50) NOT NULL,
    vigencia_inicio DATE NOT NULL,
    vigencia_fim    DATE NOT NULL,
    premio_total    NUMERIC(18,2) NOT NULL DEFAULT 0,
    importancia_segurada NUMERIC(18,2) NOT NULL DEFAULT 0,
    franquia        NUMERIC(18,2) NOT NULL DEFAULT 0,
    percentual_comissao NUMERIC(5,2) NOT NULL DEFAULT 0,
    certificado_inclusao VARCHAR(50),
    observacao      TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'ATIVA'
                    CHECK (status IN ('ATIVA','VENCIDA','CANCELADA','SUSPENSA')),
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE(empresa_id, numero)
);

CREATE INDEX idx_apolices_empresa ON apolices(empresa_id);
CREATE INDEX idx_apolices_cliente ON apolices(cliente_id);
CREATE INDEX idx_apolices_status ON apolices(status);
CREATE INDEX idx_apolices_vigencia ON apolices(vigencia_fim);
CREATE INDEX idx_apolices_proposta ON apolices(proposta_id);

-- ==================== NOTIFICAÇÕES DE SEGURO ====================

CREATE TABLE notificacoes_seguro (
    id              BIGSERIAL PRIMARY KEY,
    uuid            UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id      BIGINT NOT NULL REFERENCES empresas(id),
    tipo            VARCHAR(20) NOT NULL
                    CHECK (tipo IN ('VENCIMENTO','STATUS','ALERTA')),
    titulo          VARCHAR(200) NOT NULL,
    descricao       TEXT,
    destinatario    VARCHAR(100),
    tabela_origem   VARCHAR(50),
    registro_id     BIGINT,
    lida            BOOLEAN NOT NULL DEFAULT FALSE,
    data_leitura    TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_seg_empresa ON notificacoes_seguro(empresa_id);
CREATE INDEX idx_notif_seg_lida ON notificacoes_seguro(empresa_id, lida);

-- ==================== PERMISSÕES DO MÓDULO SEGUROS ====================

INSERT INTO permissoes (recurso, acao, descricao, modulo) VALUES
('seguradoras', 'listar', 'Listar seguradoras', 'SEGUROS'),
('seguradoras', 'criar', 'Criar seguradoras', 'SEGUROS'),
('seguradoras', 'editar', 'Editar seguradoras', 'SEGUROS'),
('seguradoras', 'excluir', 'Excluir seguradoras', 'SEGUROS'),
('corretoras', 'listar', 'Listar corretoras', 'SEGUROS'),
('corretoras', 'criar', 'Criar corretoras', 'SEGUROS'),
('corretoras', 'editar', 'Editar corretoras', 'SEGUROS'),
('corretoras', 'excluir', 'Excluir corretoras', 'SEGUROS'),
('propostas_seguro', 'listar', 'Listar propostas de seguro', 'SEGUROS'),
('propostas_seguro', 'criar', 'Criar propostas de seguro', 'SEGUROS'),
('propostas_seguro', 'editar', 'Editar propostas de seguro', 'SEGUROS'),
('propostas_seguro', 'excluir', 'Excluir propostas de seguro', 'SEGUROS'),
('apolices', 'listar', 'Listar apólices', 'SEGUROS'),
('apolices', 'criar', 'Criar apólices', 'SEGUROS'),
('apolices', 'editar', 'Editar apólices', 'SEGUROS'),
('apolices', 'excluir', 'Excluir apólices', 'SEGUROS'),
('notificacoes_seguro', 'listar', 'Listar notificações de seguro', 'SEGUROS'),
('notificacoes_seguro', 'editar', 'Editar notificações de seguro', 'SEGUROS');

-- Vincular permissões de Seguros ao perfil Admin
INSERT INTO perfil_permissoes (perfil_id, permissao_id)
SELECT 1, id FROM permissoes WHERE modulo = 'SEGUROS';

-- ==================== MENUS DO MÓDULO SEGUROS ====================

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('seguros', 'Seguros', 'Shield', 11, 'SEGUROS');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.seguradoras', 'Seguradoras', 'Building', '/seguros/seguradoras', 1, 'SEGUROS', 'seguradoras'),
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.corretoras', 'Corretoras', 'Handshake', '/seguros/corretoras', 2, 'SEGUROS', 'corretoras'),
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.propostas', 'Propostas', 'FileText', '/seguros/propostas', 3, 'SEGUROS', 'propostas_seguro'),
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.apolices', 'Apólices', 'FileCheck', '/seguros/apolices', 4, 'SEGUROS', 'apolices'),
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.notificacoes', 'Notificações', 'Bell', '/seguros/notificacoes', 5, 'SEGUROS', 'notificacoes_seguro');

-- Parâmetros do módulo Seguros
INSERT INTO parametros_sistema (empresa_id, chave, valor, tipo, descricao, modulo) VALUES
(1, 'SEGUROS_ALERTA_VENCIMENTO_DIAS', '30', 'INTEGER', 'Dias de antecedência para alertar vencimento de apólice', 'SEGUROS'),
(1, 'SEGUROS_COMISSAO_PADRAO', '10.00', 'DECIMAL', 'Percentual de comissão padrão para corretoras', 'SEGUROS');
