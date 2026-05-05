-- =============================================================
-- InsuranceFlow SaaS - Migration V1 - Estrutura Completa
-- =============================================================

-- ========== PLANOS ==========
CREATE TABLE plano (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco_mensal DECIMAL(12,2) NOT NULL DEFAULT 0,
    preco_anual DECIMAL(12,2) NOT NULL DEFAULT 0,
    limite_usuarios INT NOT NULL DEFAULT 5,
    limite_clientes INT NOT NULL DEFAULT 100,
    limite_propostas_mes INT NOT NULL DEFAULT 50,
    limite_apolices INT NOT NULL DEFAULT 200,
    limite_armazenamento_gb INT NOT NULL DEFAULT 5,
    portal_cliente BOOLEAN DEFAULT FALSE,
    whatsapp_integrado BOOLEAN DEFAULT FALSE,
    relatorios_avancados BOOLEAN DEFAULT FALSE,
    white_label BOOLEAN DEFAULT FALSE,
    acesso_api BOOLEAN DEFAULT FALSE,
    suporte_prioritario BOOLEAN DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

-- ========== EMPRESA (Tenant / Administradora) ==========
CREATE TABLE empresa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    razao_social VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200) NOT NULL,
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    inscricao_estadual VARCHAR(30),
    email VARCHAR(150) NOT NULL,
    telefone VARCHAR(20),
    celular VARCHAR(20),
    website VARCHAR(200),
    cep VARCHAR(10),
    logradouro VARCHAR(200),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    plano_id UUID REFERENCES plano(id),
    status VARCHAR(30) NOT NULL DEFAULT 'TRIAL',
    data_inicio_trial TIMESTAMP,
    data_fim_trial TIMESTAMP,
    bloqueada BOOLEAN DEFAULT FALSE,
    motivo_bloqueio TEXT,
    slug VARCHAR(100) UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_empresa_cnpj ON empresa(cnpj);
CREATE INDEX idx_empresa_status ON empresa(status);
CREATE INDEX idx_empresa_slug ON empresa(slug);

-- ========== ASSINATURA EMPRESA ==========
CREATE TABLE assinatura_empresa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    plano_id UUID NOT NULL REFERENCES plano(id),
    ciclo VARCHAR(20) NOT NULL DEFAULT 'MENSAL',
    valor DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'TRIAL',
    data_inicio TIMESTAMP NOT NULL DEFAULT NOW(),
    data_vencimento TIMESTAMP,
    data_cancelamento TIMESTAMP,
    id_assinatura_externa VARCHAR(200),
    gateway_pagamento VARCHAR(50),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_assinatura_empresa ON assinatura_empresa(empresa_id);
CREATE INDEX idx_assinatura_status ON assinatura_empresa(status);

-- ========== PAGAMENTO ASSINATURA ==========
CREATE TABLE pagamento_assinatura (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assinatura_id UUID NOT NULL REFERENCES assinatura_empresa(id),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    valor DECIMAL(12,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    metodo_pagamento VARCHAR(50),
    id_transacao_externa VARCHAR(200),
    link_cobranca VARCHAR(500),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_pagamento_assinatura ON pagamento_assinatura(assinatura_id);
CREATE INDEX idx_pagamento_empresa ON pagamento_assinatura(empresa_id);
CREATE INDEX idx_pagamento_status ON pagamento_assinatura(status);

-- ========== CONFIGURAÇÃO WHITE LABEL ==========
CREATE TABLE configuracao_white_label (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID UNIQUE NOT NULL REFERENCES empresa(id),
    logo_url VARCHAR(500),
    favicon_url VARCHAR(500),
    nome_sistema VARCHAR(200),
    cor_primaria VARCHAR(10) DEFAULT '#1A365D',
    cor_secundaria VARCHAR(10) DEFAULT '#2B6CB0',
    cor_acento VARCHAR(10) DEFAULT '#38A169',
    cor_fundo VARCHAR(10) DEFAULT '#F7FAFC',
    nome_portal_cliente VARCHAR(200),
    rodape TEXT,
    email_suporte VARCHAR(150),
    telefone_suporte VARCHAR(20),
    texto_institucional TEXT,
    dominio_personalizado VARCHAR(200),
    subdominio VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

-- ========== USUARIO ==========
CREATE TABLE usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID REFERENCES empresa(id),
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL DEFAULT 'OPERADOR',
    telefone VARCHAR(20),
    cargo VARCHAR(100),
    avatar_url VARCHAR(500),
    ultimo_acesso TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_empresa ON usuario(empresa_id);
CREATE INDEX idx_usuario_perfil ON usuario(perfil);

-- ========== LEAD SaaS ==========
CREATE TABLE lead_saas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    empresa_nome VARCHAR(200),
    email VARCHAR(150) NOT NULL,
    telefone VARCHAR(20),
    origem VARCHAR(100),
    interesse TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'NOVO',
    observacoes TEXT,
    data_ultimo_contato TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_lead_status ON lead_saas(status);

-- ========== TICKET SUPORTE ==========
CREATE TABLE ticket_suporte (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID REFERENCES empresa(id),
    usuario_id UUID REFERENCES usuario(id),
    assunto VARCHAR(300) NOT NULL,
    descricao TEXT NOT NULL,
    prioridade VARCHAR(20) NOT NULL DEFAULT 'MEDIA',
    status VARCHAR(30) NOT NULL DEFAULT 'ABERTO',
    categoria VARCHAR(50),
    data_resolucao TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE TABLE ticket_mensagem (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL REFERENCES ticket_suporte(id),
    usuario_id UUID REFERENCES usuario(id),
    mensagem TEXT NOT NULL,
    is_suporte BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ticket_empresa ON ticket_suporte(empresa_id);
CREATE INDEX idx_ticket_status ON ticket_suporte(status);

-- ========== HISTORICO PLANO ==========
CREATE TABLE historico_plano (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    plano_anterior_id UUID REFERENCES plano(id),
    plano_novo_id UUID NOT NULL REFERENCES plano(id),
    tipo_alteracao VARCHAR(30) NOT NULL,
    observacoes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(150)
);

-- ===================================================================
-- MÓDULOS DA ADMINISTRADORA (TENANT)
-- ===================================================================

-- ========== CLIENTE (PF/PJ) ==========
CREATE TABLE cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    tipo_pessoa VARCHAR(2) NOT NULL DEFAULT 'PF',
    nome VARCHAR(200) NOT NULL,
    cpf_cnpj VARCHAR(18) UNIQUE,
    rg VARCHAR(20),
    data_nascimento DATE,
    sexo VARCHAR(1),
    estado_civil VARCHAR(20),
    profissao VARCHAR(100),
    email VARCHAR(150),
    telefone VARCHAR(20),
    celular VARCHAR(20),
    cep VARCHAR(10),
    logradouro VARCHAR(200),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    observacoes TEXT,
    origem VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_cliente_empresa ON cliente(empresa_id);
CREATE INDEX idx_cliente_cpf_cnpj ON cliente(cpf_cnpj);

-- ========== SEGURADORA ==========
CREATE TABLE seguradora (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    nome VARCHAR(200) NOT NULL,
    cnpj VARCHAR(18),
    codigo_susep VARCHAR(50),
    email VARCHAR(150),
    telefone VARCHAR(20),
    website VARCHAR(200),
    logo_url VARCHAR(500),
    contato_nome VARCHAR(200),
    contato_email VARCHAR(150),
    contato_telefone VARCHAR(20),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_seguradora_empresa ON seguradora(empresa_id);

-- ========== CORRETORA ==========
CREATE TABLE corretora (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    nome VARCHAR(200) NOT NULL,
    cnpj VARCHAR(18),
    susep VARCHAR(50),
    email VARCHAR(150),
    telefone VARCHAR(20),
    responsavel VARCHAR(200),
    comissao_padrao DECIMAL(5,2),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_corretora_empresa ON corretora(empresa_id);

-- ========== RAMO DE SEGURO ==========
CREATE TABLE ramo_seguro (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    codigo VARCHAR(20),
    nome VARCHAR(200) NOT NULL,
    descricao TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_ramo_empresa ON ramo_seguro(empresa_id);

-- ========== LEAD INTERNO ==========
CREATE TABLE lead_interno (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(150),
    telefone VARCHAR(20),
    tipo_seguro VARCHAR(100),
    origem VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'NOVO',
    valor_estimado DECIMAL(12,2),
    responsavel_id UUID REFERENCES usuario(id),
    observacoes TEXT,
    data_ultimo_contato TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_lead_interno_empresa ON lead_interno(empresa_id);
CREATE INDEX idx_lead_interno_status ON lead_interno(status);

-- ========== PROPOSTA ==========
CREATE TABLE proposta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    numero VARCHAR(30),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    seguradora_id UUID REFERENCES seguradora(id),
    corretora_id UUID REFERENCES corretora(id),
    ramo_id UUID REFERENCES ramo_seguro(id),
    tipo_seguro VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'RASCUNHO',
    data_proposta DATE NOT NULL DEFAULT CURRENT_DATE,
    data_inicio_vigencia DATE,
    data_fim_vigencia DATE,
    valor_importancia_segurada DECIMAL(14,2),
    valor_premio DECIMAL(12,2),
    valor_premio_liquido DECIMAL(12,2),
    valor_iof DECIMAL(12,2),
    forma_pagamento VARCHAR(50),
    numero_parcelas INT DEFAULT 1,
    observacoes TEXT,
    motivo_recusa TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_proposta_empresa ON proposta(empresa_id);
CREATE INDEX idx_proposta_cliente ON proposta(cliente_id);
CREATE INDEX idx_proposta_status ON proposta(status);

-- ========== APÓLICE ==========
CREATE TABLE apolice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    numero_apolice VARCHAR(50),
    proposta_id UUID REFERENCES proposta(id),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    seguradora_id UUID REFERENCES seguradora(id),
    corretora_id UUID REFERENCES corretora(id),
    ramo_id UUID REFERENCES ramo_seguro(id),
    tipo_seguro VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'ATIVA',
    data_emissao DATE,
    data_inicio_vigencia DATE NOT NULL,
    data_fim_vigencia DATE NOT NULL,
    valor_importancia_segurada DECIMAL(14,2),
    valor_premio DECIMAL(12,2),
    valor_premio_liquido DECIMAL(12,2),
    valor_iof DECIMAL(12,2),
    forma_pagamento VARCHAR(50),
    numero_parcelas INT DEFAULT 1,
    renovacao_automatica BOOLEAN DEFAULT FALSE,
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_apolice_empresa ON apolice(empresa_id);
CREATE INDEX idx_apolice_cliente ON apolice(cliente_id);
CREATE INDEX idx_apolice_numero ON apolice(numero_apolice);
CREATE INDEX idx_apolice_status ON apolice(status);
CREATE INDEX idx_apolice_vigencia ON apolice(data_fim_vigencia);

-- ========== RENOVAÇÃO ==========
CREATE TABLE renovacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    apolice_id UUID NOT NULL REFERENCES apolice(id),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    data_vencimento_original DATE NOT NULL,
    data_nova_vigencia_inicio DATE,
    data_nova_vigencia_fim DATE,
    valor_premio_anterior DECIMAL(12,2),
    valor_premio_novo DECIMAL(12,2),
    numero_nova_apolice VARCHAR(50),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_renovacao_empresa ON renovacao(empresa_id);
CREATE INDEX idx_renovacao_status ON renovacao(status);

-- ========== SINISTRO ==========
CREATE TABLE sinistro (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    numero_sinistro VARCHAR(50),
    apolice_id UUID NOT NULL REFERENCES apolice(id),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    data_ocorrencia DATE NOT NULL,
    data_aviso DATE,
    tipo VARCHAR(100),
    descricao TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ABERTO',
    valor_estimado DECIMAL(14,2),
    valor_indenizado DECIMAL(14,2),
    data_pagamento_indenizacao DATE,
    local_ocorrencia VARCHAR(300),
    boletim_ocorrencia VARCHAR(50),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_sinistro_empresa ON sinistro(empresa_id);
CREATE INDEX idx_sinistro_apolice ON sinistro(apolice_id);
CREATE INDEX idx_sinistro_status ON sinistro(status);

-- ========== DOCUMENTO ==========
CREATE TABLE documento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    entidade_tipo VARCHAR(50) NOT NULL,
    entidade_id UUID NOT NULL,
    nome VARCHAR(200) NOT NULL,
    nome_arquivo VARCHAR(300) NOT NULL,
    tipo_arquivo VARCHAR(50),
    tamanho_bytes BIGINT,
    url VARCHAR(1000),
    descricao TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_documento_empresa ON documento(empresa_id);
CREATE INDEX idx_documento_entidade ON documento(entidade_tipo, entidade_id);

-- ========== LANÇAMENTO FINANCEIRO ==========
CREATE TABLE lancamento_financeiro (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    tipo VARCHAR(20) NOT NULL,
    categoria VARCHAR(50),
    descricao VARCHAR(300) NOT NULL,
    valor DECIMAL(12,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    forma_pagamento VARCHAR(50),
    cliente_id UUID REFERENCES cliente(id),
    apolice_id UUID REFERENCES apolice(id),
    proposta_id UUID REFERENCES proposta(id),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_financeiro_empresa ON lancamento_financeiro(empresa_id);
CREATE INDEX idx_financeiro_status ON lancamento_financeiro(status);
CREATE INDEX idx_financeiro_tipo ON lancamento_financeiro(tipo);
CREATE INDEX idx_financeiro_vencimento ON lancamento_financeiro(data_vencimento);

-- ========== BOLETO ==========
CREATE TABLE boleto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    lancamento_id UUID REFERENCES lancamento_financeiro(id),
    apolice_id UUID REFERENCES apolice(id),
    cliente_id UUID REFERENCES cliente(id),
    numero_parcela INT,
    valor DECIMAL(12,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    linha_digitavel VARCHAR(100),
    codigo_barras VARCHAR(100),
    url_boleto VARCHAR(500),
    nosso_numero VARCHAR(50),
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_boleto_empresa ON boleto(empresa_id);
CREATE INDEX idx_boleto_cliente ON boleto(cliente_id);
CREATE INDEX idx_boleto_status ON boleto(status);
CREATE INDEX idx_boleto_vencimento ON boleto(data_vencimento);

-- ========== COMISSÃO ==========
CREATE TABLE comissao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    apolice_id UUID REFERENCES apolice(id),
    corretora_id UUID REFERENCES corretora(id),
    seguradora_id UUID REFERENCES seguradora(id),
    tipo VARCHAR(30) NOT NULL,
    percentual DECIMAL(5,2),
    valor DECIMAL(12,2) NOT NULL,
    data_referencia DATE,
    data_pagamento DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    observacoes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_comissao_empresa ON comissao(empresa_id);
CREATE INDEX idx_comissao_status ON comissao(status);

-- ========== NOTIFICAÇÃO ==========
CREATE TABLE notificacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID REFERENCES empresa(id),
    usuario_id UUID REFERENCES usuario(id),
    titulo VARCHAR(200) NOT NULL,
    mensagem TEXT NOT NULL,
    tipo VARCHAR(50),
    lida BOOLEAN DEFAULT FALSE,
    link VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notificacao_usuario ON notificacao(usuario_id, lida);

-- ========== MENSAGEM WHATSAPP ==========
CREATE TABLE mensagem_whatsapp (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    cliente_id UUID REFERENCES cliente(id),
    telefone_destino VARCHAR(20) NOT NULL,
    mensagem TEXT NOT NULL,
    tipo VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    data_envio TIMESTAMP,
    id_mensagem_externa VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_whatsapp_empresa ON mensagem_whatsapp(empresa_id);

-- ========== ASSINATURA DIGITAL ==========
CREATE TABLE assinatura_digital (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL REFERENCES empresa(id),
    documento_id UUID REFERENCES documento(id),
    entidade_tipo VARCHAR(50),
    entidade_id UUID,
    signatario_nome VARCHAR(200) NOT NULL,
    signatario_email VARCHAR(150) NOT NULL,
    signatario_cpf VARCHAR(14),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    data_assinatura TIMESTAMP,
    ip_assinatura VARCHAR(50),
    hash_documento VARCHAR(500),
    id_externo VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

CREATE INDEX idx_assinatura_digital_empresa ON assinatura_digital(empresa_id);

-- ========== AUDITORIA ==========
CREATE TABLE auditoria (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID REFERENCES empresa(id),
    usuario_id UUID REFERENCES usuario(id),
    usuario_email VARCHAR(150),
    acao VARCHAR(30) NOT NULL,
    entidade VARCHAR(100) NOT NULL,
    entidade_id UUID,
    dados_anteriores JSONB,
    dados_novos JSONB,
    ip VARCHAR(50),
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_auditoria_empresa ON auditoria(empresa_id);
CREATE INDEX idx_auditoria_entidade ON auditoria(entidade, entidade_id);
CREATE INDEX idx_auditoria_data ON auditoria(created_at);

-- ========== CONFIGURAÇÃO EMPRESA ==========
CREATE TABLE configuracao_empresa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID UNIQUE NOT NULL REFERENCES empresa(id),
    moeda VARCHAR(5) DEFAULT 'BRL',
    fuso_horario VARCHAR(50) DEFAULT 'America/Sao_Paulo',
    formato_data VARCHAR(20) DEFAULT 'dd/MM/yyyy',
    notificacao_email BOOLEAN DEFAULT TRUE,
    notificacao_whatsapp BOOLEAN DEFAULT FALSE,
    renovacao_automatica_dias INT DEFAULT 30,
    alerta_vencimento_dias INT DEFAULT 15,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

-- ========== ONBOARDING ==========
CREATE TABLE onboarding_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID UNIQUE NOT NULL REFERENCES empresa(id),
    passo_atual INT DEFAULT 1,
    empresa_configurada BOOLEAN DEFAULT FALSE,
    equipe_cadastrada BOOLEAN DEFAULT FALSE,
    marca_personalizada BOOLEAN DEFAULT FALSE,
    primeiro_cliente BOOLEAN DEFAULT FALSE,
    primeira_proposta BOOLEAN DEFAULT FALSE,
    concluido BOOLEAN DEFAULT FALSE,
    data_conclusao TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
