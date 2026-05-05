-- =====================================================================
-- V10 - Módulos Completos padrão TOTVS Protheus
-- Contabilidade, Faturamento, Patrimônio, PCP, Contratos, RH expandido
-- =====================================================================

-- =====================================================================
-- 1. CONTABILIDADE (SIGACTB)
-- =====================================================================

CREATE TABLE plano_contas (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    codigo VARCHAR(20) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    tipo VARCHAR(1) NOT NULL CHECK (tipo IN ('S', 'A')), -- S=Sintética, A=Analítica
    natureza VARCHAR(1) NOT NULL CHECK (natureza IN ('D', 'C')), -- D=Devedora, C=Credora
    classificacao VARCHAR(30), -- ex: 1.1.01.001
    conta_pai_id BIGINT REFERENCES plano_contas(id),
    nivel INTEGER NOT NULL DEFAULT 1,
    aceita_lancamento BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);
CREATE INDEX idx_plano_contas_empresa ON plano_contas(empresa_id);
CREATE INDEX idx_plano_contas_classificacao ON plano_contas(classificacao);
CREATE INDEX idx_plano_contas_deleted ON plano_contas(deleted);

CREATE TABLE centro_resultado (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    codigo VARCHAR(20) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'RESULTADO' CHECK (tipo IN ('RESULTADO', 'INVESTIMENTO', 'FINANCEIRO')),
    responsavel VARCHAR(100),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);
CREATE INDEX idx_centro_resultado_empresa ON centro_resultado(empresa_id);

CREATE TABLE lancamento_contabil (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    filial_id BIGINT REFERENCES filiais(id),
    lote VARCHAR(20) NOT NULL,
    sublote VARCHAR(20),
    numero INTEGER NOT NULL,
    data_lancamento DATE NOT NULL,
    conta_debito_id BIGINT NOT NULL REFERENCES plano_contas(id),
    conta_credito_id BIGINT NOT NULL REFERENCES plano_contas(id),
    valor NUMERIC(18,2) NOT NULL,
    historico VARCHAR(500) NOT NULL,
    documento VARCHAR(50),
    centro_custo_id BIGINT REFERENCES centros_custo(id),
    centro_resultado_id BIGINT REFERENCES centro_resultado(id),
    tipo VARCHAR(20) NOT NULL DEFAULT 'NORMAL' CHECK (tipo IN ('NORMAL', 'PARTIDA_DOBRADA', 'ESTORNO', 'ENCERRAMENTO', 'ABERTURA')),
    origem VARCHAR(30), -- MANUAL, FINANCEIRO, FATURAMENTO, FOLHA, PATRIMONIO
    origem_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'DIGITADO' CHECK (status IN ('DIGITADO', 'VERIFICADO', 'APROVADO', 'ESTORNADO')),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
CREATE INDEX idx_lancamento_empresa ON lancamento_contabil(empresa_id);
CREATE INDEX idx_lancamento_data ON lancamento_contabil(data_lancamento);
CREATE INDEX idx_lancamento_lote ON lancamento_contabil(lote);
CREATE INDEX idx_lancamento_deleted ON lancamento_contabil(deleted);

CREATE TABLE periodo_contabil (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    ano INTEGER NOT NULL,
    mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTO' CHECK (status IN ('ABERTO', 'FECHADO', 'REABERTO')),
    fechado_por VARCHAR(100),
    fechado_em TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(empresa_id, ano, mes)
);

-- =====================================================================
-- 2. FATURAMENTO / NOTA FISCAL (SIGAFAT)
-- =====================================================================

CREATE TABLE notas_fiscais (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    filial_id BIGINT NOT NULL REFERENCES filiais(id),
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA')),
    serie VARCHAR(5) NOT NULL DEFAULT '1',
    numero VARCHAR(20) NOT NULL,
    chave_acesso VARCHAR(44),
    modelo VARCHAR(2) NOT NULL DEFAULT '55', -- 55=NF-e, 65=NFC-e
    natureza_operacao_id BIGINT REFERENCES naturezas_operacao(id),
    cfop_predominante VARCHAR(10),
    data_emissao DATE NOT NULL,
    data_saida_entrada DATE,
    hora_saida_entrada TIME,
    -- Participante
    cliente_id BIGINT REFERENCES clientes(id),
    fornecedor_id BIGINT REFERENCES fornecedores(id),
    -- Transportadora
    transportadora_id BIGINT REFERENCES transportadoras(id),
    frete_por_conta VARCHAR(10) DEFAULT 'EMITENTE' CHECK (frete_por_conta IN ('EMITENTE', 'DESTINATARIO', 'TERCEIROS', 'SEM_FRETE')),
    -- Valores
    valor_produtos NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_frete NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_seguro NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_desconto NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_outras_despesas NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_ipi NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_icms NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_icms_st NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_pis NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_cofins NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_total NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- Informações complementares
    informacoes_complementares TEXT,
    -- Pedido de origem
    pedido_venda_id BIGINT REFERENCES pedidos_venda(id),
    pedido_compra_id BIGINT REFERENCES pedidos_compra(id),
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'DIGITADA' CHECK (status IN ('DIGITADA', 'VALIDADA', 'AUTORIZADA', 'CANCELADA', 'DENEGADA', 'INUTILIZADA')),
    protocolo_autorizacao VARCHAR(30),
    data_autorizacao TIMESTAMP,
    motivo_cancelamento TEXT,
    -- Padrão
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
CREATE INDEX idx_nf_empresa ON notas_fiscais(empresa_id);
CREATE INDEX idx_nf_filial ON notas_fiscais(filial_id);
CREATE INDEX idx_nf_numero ON notas_fiscais(numero);
CREATE INDEX idx_nf_chave ON notas_fiscais(chave_acesso);
CREATE INDEX idx_nf_status ON notas_fiscais(status);
CREATE INDEX idx_nf_deleted ON notas_fiscais(deleted);

CREATE TABLE nota_fiscal_itens (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    nota_fiscal_id BIGINT NOT NULL REFERENCES notas_fiscais(id) ON DELETE CASCADE,
    numero_item INTEGER NOT NULL,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    descricao VARCHAR(200) NOT NULL,
    ncm VARCHAR(10),
    cfop VARCHAR(10) NOT NULL,
    unidade_medida VARCHAR(10) NOT NULL,
    quantidade NUMERIC(18,4) NOT NULL,
    valor_unitario NUMERIC(18,6) NOT NULL,
    valor_total NUMERIC(18,2) NOT NULL,
    valor_desconto NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- ICMS
    cst_icms VARCHAR(5),
    base_icms NUMERIC(18,2) NOT NULL DEFAULT 0,
    aliquota_icms NUMERIC(5,2) NOT NULL DEFAULT 0,
    valor_icms NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- ICMS-ST
    base_icms_st NUMERIC(18,2) NOT NULL DEFAULT 0,
    aliquota_icms_st NUMERIC(5,2) NOT NULL DEFAULT 0,
    valor_icms_st NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- IPI
    cst_ipi VARCHAR(5),
    base_ipi NUMERIC(18,2) NOT NULL DEFAULT 0,
    aliquota_ipi NUMERIC(5,2) NOT NULL DEFAULT 0,
    valor_ipi NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- PIS
    cst_pis VARCHAR(5),
    base_pis NUMERIC(18,2) NOT NULL DEFAULT 0,
    aliquota_pis NUMERIC(5,2) NOT NULL DEFAULT 0,
    valor_pis NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- COFINS
    cst_cofins VARCHAR(5),
    base_cofins NUMERIC(18,2) NOT NULL DEFAULT 0,
    aliquota_cofins NUMERIC(5,2) NOT NULL DEFAULT 0,
    valor_cofins NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- Pedido item origem
    pedido_venda_item_id BIGINT,
    pedido_compra_item_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_nfi_nota ON nota_fiscal_itens(nota_fiscal_id);
CREATE INDEX idx_nfi_produto ON nota_fiscal_itens(produto_id);

-- =====================================================================
-- 3. PATRIMÔNIO / ATIVO FIXO (SIGAATF)
-- =====================================================================

CREATE TABLE bens_patrimoniais (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    filial_id BIGINT REFERENCES filiais(id),
    codigo VARCHAR(20) NOT NULL,
    descricao VARCHAR(300) NOT NULL,
    grupo_bem VARCHAR(50), -- MOVEIS, VEICULOS, EQUIPAMENTOS, IMOVEIS, SOFTWARE, OUTROS
    tipo_bem VARCHAR(30) NOT NULL DEFAULT 'TANGIVEL' CHECK (tipo_bem IN ('TANGIVEL', 'INTANGIVEL')),
    marca VARCHAR(100),
    modelo VARCHAR(100),
    numero_serie VARCHAR(100),
    fornecedor_id BIGINT REFERENCES fornecedores(id),
    nota_fiscal_id BIGINT REFERENCES notas_fiscais(id),
    data_aquisicao DATE NOT NULL,
    data_inicio_depreciacao DATE,
    valor_aquisicao NUMERIC(18,2) NOT NULL,
    valor_residual NUMERIC(18,2) NOT NULL DEFAULT 0,
    taxa_depreciacao_anual NUMERIC(5,2) NOT NULL DEFAULT 0, -- ex: 10%, 20%, 25%
    metodo_depreciacao VARCHAR(20) NOT NULL DEFAULT 'LINEAR' CHECK (metodo_depreciacao IN ('LINEAR', 'SOMA_DIGITOS', 'SALDO_DECRESCENTE', 'UNIDADES_PRODUZIDAS')),
    vida_util_meses INTEGER NOT NULL DEFAULT 60,
    valor_depreciado_acumulado NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_contabil NUMERIC(18,2) NOT NULL DEFAULT 0, -- valor_aquisicao - depreciado_acumulado
    -- Localização
    localizacao VARCHAR(200),
    centro_custo_id BIGINT REFERENCES centros_custo(id),
    responsavel VARCHAR(100),
    -- Conta contábil
    conta_ativo_id BIGINT REFERENCES plano_contas(id),
    conta_depreciacao_id BIGINT REFERENCES plano_contas(id),
    conta_despesa_depreciacao_id BIGINT REFERENCES plano_contas(id),
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO', 'BAIXADO', 'VENDIDO', 'TRANSFERIDO', 'EXTRAVIADO')),
    data_baixa DATE,
    motivo_baixa TEXT,
    valor_baixa NUMERIC(18,2),
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);
CREATE INDEX idx_bens_empresa ON bens_patrimoniais(empresa_id);
CREATE INDEX idx_bens_grupo ON bens_patrimoniais(grupo_bem);
CREATE INDEX idx_bens_status ON bens_patrimoniais(status);
CREATE INDEX idx_bens_deleted ON bens_patrimoniais(deleted);

CREATE TABLE depreciacao (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    bem_patrimonial_id BIGINT NOT NULL REFERENCES bens_patrimoniais(id),
    ano INTEGER NOT NULL,
    mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    data_calculo DATE NOT NULL,
    valor_base NUMERIC(18,2) NOT NULL,
    taxa_mensal NUMERIC(8,6) NOT NULL,
    valor_depreciacao NUMERIC(18,2) NOT NULL,
    valor_acumulado NUMERIC(18,2) NOT NULL,
    valor_contabil_apos NUMERIC(18,2) NOT NULL,
    lancamento_contabil_id BIGINT REFERENCES lancamento_contabil(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(bem_patrimonial_id, ano, mes)
);
CREATE INDEX idx_depreciacao_bem ON depreciacao(bem_patrimonial_id);
CREATE INDEX idx_depreciacao_periodo ON depreciacao(ano, mes);

-- =====================================================================
-- 4. PCP - PRODUÇÃO (SIGAPCP)
-- =====================================================================

CREATE TABLE estrutura_produto (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    produto_pai_id BIGINT NOT NULL REFERENCES produtos(id),
    produto_componente_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade NUMERIC(18,6) NOT NULL,
    unidade_medida VARCHAR(10),
    perda_percentual NUMERIC(5,2) NOT NULL DEFAULT 0,
    sequencia INTEGER NOT NULL DEFAULT 1,
    observacao VARCHAR(200),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
CREATE INDEX idx_estrutura_pai ON estrutura_produto(produto_pai_id);
CREATE INDEX idx_estrutura_componente ON estrutura_produto(produto_componente_id);

CREATE TABLE ordens_producao (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    filial_id BIGINT REFERENCES filiais(id),
    numero VARCHAR(20) NOT NULL,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade_prevista NUMERIC(18,4) NOT NULL,
    quantidade_produzida NUMERIC(18,4) NOT NULL DEFAULT 0,
    quantidade_perda NUMERIC(18,4) NOT NULL DEFAULT 0,
    data_abertura DATE NOT NULL,
    data_previsao DATE NOT NULL,
    data_inicio DATE,
    data_encerramento DATE,
    armazem_id BIGINT REFERENCES armazens(id),
    prioridade VARCHAR(10) NOT NULL DEFAULT 'NORMAL' CHECK (prioridade IN ('BAIXA', 'NORMAL', 'ALTA', 'URGENTE')),
    centro_custo_id BIGINT REFERENCES centros_custo(id),
    custo_previsto NUMERIC(18,2) NOT NULL DEFAULT 0,
    custo_real NUMERIC(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADA' CHECK (status IN ('PLANEJADA', 'LIBERADA', 'EM_PRODUCAO', 'FINALIZADA', 'CANCELADA')),
    pedido_venda_id BIGINT REFERENCES pedidos_venda(id),
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, numero)
);
CREATE INDEX idx_op_empresa ON ordens_producao(empresa_id);
CREATE INDEX idx_op_produto ON ordens_producao(produto_id);
CREATE INDEX idx_op_status ON ordens_producao(status);
CREATE INDEX idx_op_deleted ON ordens_producao(deleted);

CREATE TABLE ordem_producao_itens (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    ordem_producao_id BIGINT NOT NULL REFERENCES ordens_producao(id) ON DELETE CASCADE,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade_prevista NUMERIC(18,4) NOT NULL,
    quantidade_consumida NUMERIC(18,4) NOT NULL DEFAULT 0,
    unidade_medida VARCHAR(10),
    custo_unitario NUMERIC(18,6) NOT NULL DEFAULT 0,
    armazem_id BIGINT REFERENCES armazens(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_opi_op ON ordem_producao_itens(ordem_producao_id);

CREATE TABLE apontamento_producao (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    ordem_producao_id BIGINT NOT NULL REFERENCES ordens_producao(id),
    data_apontamento DATE NOT NULL,
    quantidade_boa NUMERIC(18,4) NOT NULL DEFAULT 0,
    quantidade_refugo NUMERIC(18,4) NOT NULL DEFAULT 0,
    hora_inicio TIME,
    hora_fim TIME,
    funcionario_id BIGINT REFERENCES funcionarios(id),
    observacao VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100)
);
CREATE INDEX idx_apontamento_op ON apontamento_producao(ordem_producao_id);

-- =====================================================================
-- 5. RH EXPANDIDO (SIGAGPE)
-- =====================================================================

-- cargos already created in V2; add new columns
ALTER TABLE cargos ADD COLUMN IF NOT EXISTS cbo VARCHAR(10);
ALTER TABLE cargos ADD COLUMN IF NOT EXISTS salario_base NUMERIC(18,2);
ALTER TABLE cargos ADD COLUMN IF NOT EXISTS nivel VARCHAR(30);
ALTER TABLE cargos ALTER COLUMN descricao TYPE TEXT;
CREATE INDEX IF NOT EXISTS idx_cargos_empresa ON cargos(empresa_id);

CREATE TABLE departamentos_rh (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    codigo VARCHAR(20) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    centro_custo_id BIGINT REFERENCES centros_custo(id),
    gestor_id BIGINT REFERENCES funcionarios(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);
CREATE INDEX idx_depto_rh_empresa ON departamentos_rh(empresa_id);

-- Adicionar FK no funcionário para cargo_id e departamento_id
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS cargo_id BIGINT REFERENCES cargos(id);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS departamento_id BIGINT REFERENCES departamentos_rh(id);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS centro_custo_id BIGINT REFERENCES centros_custo(id);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS tipo_contrato VARCHAR(20) DEFAULT 'CLT' CHECK (tipo_contrato IN ('CLT', 'PJ', 'ESTAGIO', 'TEMPORARIO', 'APRENDIZ'));
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS jornada_trabalho VARCHAR(20) DEFAULT '44H_SEMANAIS';
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS banco_id BIGINT REFERENCES bancos(id);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS agencia VARCHAR(10);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS conta_corrente VARCHAR(20);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS pis_pasep VARCHAR(20);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS ctps VARCHAR(20);
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS rg VARCHAR(20);

CREATE TABLE beneficios (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    codigo VARCHAR(20) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('VALE_TRANSPORTE', 'VALE_REFEICAO', 'VALE_ALIMENTACAO', 'PLANO_SAUDE', 'PLANO_ODONTOLOGICO', 'SEGURO_VIDA', 'AUXILIO_CRECHE', 'PLR', 'OUTROS')),
    valor_empresa NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_funcionario NUMERIC(18,2) NOT NULL DEFAULT 0,
    desconto_folha BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, codigo)
);
CREATE INDEX idx_beneficios_empresa ON beneficios(empresa_id);

CREATE TABLE funcionario_beneficio (
    id BIGSERIAL PRIMARY KEY,
    funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id),
    beneficio_id BIGINT NOT NULL REFERENCES beneficios(id),
    data_inicio DATE NOT NULL,
    data_fim DATE,
    valor_customizado NUMERIC(18,2),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(funcionario_id, beneficio_id)
);

CREATE TABLE folha_pagamento (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    filial_id BIGINT REFERENCES filiais(id),
    ano INTEGER NOT NULL,
    mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    tipo VARCHAR(20) NOT NULL DEFAULT 'MENSAL' CHECK (tipo IN ('MENSAL', 'ADIANTAMENTO', 'FERIAS', 'DECIMO_TERCEIRO', 'RESCISAO', 'PLR')),
    data_calculo DATE NOT NULL,
    data_pagamento DATE,
    total_proventos NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_descontos NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_liquido NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_encargos NUMERIC(18,2) NOT NULL DEFAULT 0, -- INSS patronal, FGTS, etc.
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA' CHECK (status IN ('ABERTA', 'CALCULADA', 'CONFERIDA', 'FECHADA', 'PAGA')),
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, ano, mes, tipo)
);
CREATE INDEX idx_folha_empresa ON folha_pagamento(empresa_id);
CREATE INDEX idx_folha_periodo ON folha_pagamento(ano, mes);

CREATE TABLE folha_pagamento_item (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    folha_pagamento_id BIGINT NOT NULL REFERENCES folha_pagamento(id) ON DELETE CASCADE,
    funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id),
    salario_base NUMERIC(18,2) NOT NULL,
    -- Proventos
    horas_extras_50 NUMERIC(18,2) NOT NULL DEFAULT 0,
    horas_extras_100 NUMERIC(18,2) NOT NULL DEFAULT 0,
    adicional_noturno NUMERIC(18,2) NOT NULL DEFAULT 0,
    adicional_periculosidade NUMERIC(18,2) NOT NULL DEFAULT 0,
    adicional_insalubridade NUMERIC(18,2) NOT NULL DEFAULT 0,
    comissao NUMERIC(18,2) NOT NULL DEFAULT 0,
    gratificacao NUMERIC(18,2) NOT NULL DEFAULT 0,
    outros_proventos NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_proventos NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- Descontos
    inss NUMERIC(18,2) NOT NULL DEFAULT 0,
    irrf NUMERIC(18,2) NOT NULL DEFAULT 0,
    vale_transporte NUMERIC(18,2) NOT NULL DEFAULT 0,
    vale_refeicao NUMERIC(18,2) NOT NULL DEFAULT 0,
    plano_saude NUMERIC(18,2) NOT NULL DEFAULT 0,
    faltas NUMERIC(18,2) NOT NULL DEFAULT 0,
    adiantamento NUMERIC(18,2) NOT NULL DEFAULT 0,
    outros_descontos NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_descontos NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- Líquido
    salario_liquido NUMERIC(18,2) NOT NULL DEFAULT 0,
    -- Encargos empresa
    fgts NUMERIC(18,2) NOT NULL DEFAULT 0,
    inss_empresa NUMERIC(18,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_folha_item_folha ON folha_pagamento_item(folha_pagamento_id);
CREATE INDEX idx_folha_item_func ON folha_pagamento_item(funcionario_id);

CREATE TABLE ponto_eletronico (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id),
    data DATE NOT NULL,
    entrada1 TIME,
    saida1 TIME,
    entrada2 TIME,
    saida2 TIME,
    entrada3 TIME,
    saida3 TIME,
    horas_trabalhadas NUMERIC(5,2) NOT NULL DEFAULT 0,
    horas_extras NUMERIC(5,2) NOT NULL DEFAULT 0,
    horas_falta NUMERIC(5,2) NOT NULL DEFAULT 0,
    tipo VARCHAR(20) NOT NULL DEFAULT 'NORMAL' CHECK (tipo IN ('NORMAL', 'FERIADO', 'COMPENSACAO', 'ABONO')),
    justificativa VARCHAR(200),
    aprovado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(funcionario_id, data)
);
CREATE INDEX idx_ponto_func ON ponto_eletronico(funcionario_id);
CREATE INDEX idx_ponto_data ON ponto_eletronico(data);

CREATE TABLE ferias (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id),
    periodo_aquisitivo_inicio DATE NOT NULL,
    periodo_aquisitivo_fim DATE NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    dias_gozo INTEGER NOT NULL,
    dias_abono INTEGER NOT NULL DEFAULT 0,
    valor_ferias NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_terco NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_abono NUMERIC(18,2) NOT NULL DEFAULT 0,
    valor_adiantamento_13 NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_bruto NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_descontos NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_liquido NUMERIC(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA' CHECK (status IN ('PROGRAMADA', 'APROVADA', 'EM_GOZO', 'CONCLUIDA', 'CANCELADA')),
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
CREATE INDEX idx_ferias_func ON ferias(funcionario_id);
CREATE INDEX idx_ferias_status ON ferias(status);

-- =====================================================================
-- 6. CONTRATOS (SIGAGCT)
-- =====================================================================

CREATE TABLE contratos (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    filial_id BIGINT REFERENCES filiais(id),
    numero VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CLIENTE', 'FORNECEDOR', 'SERVICO', 'ALUGUEL', 'MANUTENCAO')),
    descricao VARCHAR(300) NOT NULL,
    cliente_id BIGINT REFERENCES clientes(id),
    fornecedor_id BIGINT REFERENCES fornecedores(id),
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    valor_total NUMERIC(18,2) NOT NULL,
    valor_mensal NUMERIC(18,2),
    dia_vencimento INTEGER CHECK (dia_vencimento BETWEEN 1 AND 31),
    indice_reajuste VARCHAR(20), -- IPCA, IGPM, INPC, FIXO
    percentual_reajuste NUMERIC(5,2),
    data_proximo_reajuste DATE,
    centro_custo_id BIGINT REFERENCES centros_custo(id),
    natureza_financeira_id BIGINT REFERENCES naturezas_financeiras(id),
    responsavel VARCHAR(100),
    clausulas TEXT,
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO' CHECK (status IN ('RASCUNHO', 'VIGENTE', 'SUSPENSO', 'ENCERRADO', 'CANCELADO', 'RENOVADO')),
    data_assinatura DATE,
    data_encerramento DATE,
    motivo_encerramento TEXT,
    -- Renovação
    renovacao_automatica BOOLEAN NOT NULL DEFAULT FALSE,
    prazo_renovacao_meses INTEGER,
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE(empresa_id, numero)
);
CREATE INDEX idx_contratos_empresa ON contratos(empresa_id);
CREATE INDEX idx_contratos_status ON contratos(status);
CREATE INDEX idx_contratos_tipo ON contratos(tipo);
CREATE INDEX idx_contratos_deleted ON contratos(deleted);

CREATE TABLE contrato_parcelas (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    contrato_id BIGINT NOT NULL REFERENCES contratos(id) ON DELETE CASCADE,
    numero_parcela INTEGER NOT NULL,
    data_vencimento DATE NOT NULL,
    valor NUMERIC(18,2) NOT NULL,
    valor_pago NUMERIC(18,2) NOT NULL DEFAULT 0,
    data_pagamento DATE,
    titulo_id BIGINT REFERENCES titulos(id), -- Vínculo com o financeiro
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE', 'PAGO', 'ATRASADO', 'CANCELADO')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_contrato_parc_contrato ON contrato_parcelas(contrato_id);

-- =====================================================================
-- 7. TABELA DE PREÇO ITENS (complementar para Vendas)
-- =====================================================================

-- tabela_preco_itens already created in V2; add new columns
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS uuid UUID DEFAULT gen_random_uuid();
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS preco_minimo NUMERIC(18,6);
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS preco_maximo NUMERIC(18,6);
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS percentual_desconto_maximo NUMERIC(5,2) NOT NULL DEFAULT 0;
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE tabela_preco_itens ALTER COLUMN preco TYPE NUMERIC(18,6);
CREATE INDEX IF NOT EXISTS idx_tpi_tabela ON tabela_preco_itens(tabela_preco_id);
CREATE INDEX IF NOT EXISTS idx_tpi_produto ON tabela_preco_itens(produto_id);

-- =====================================================================
-- 8. CONCILIAÇÃO BANCÁRIA (complementar para Financeiro)
-- =====================================================================

CREATE TABLE conciliacao_bancaria (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    conta_bancaria_id BIGINT NOT NULL REFERENCES contas_bancarias(id),
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    saldo_banco NUMERIC(18,2) NOT NULL DEFAULT 0,
    saldo_sistema NUMERIC(18,2) NOT NULL DEFAULT 0,
    diferenca NUMERIC(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO' CHECK (status IN ('EM_ANDAMENTO', 'CONCILIADA', 'DIVERGENTE')),
    observacao TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
CREATE INDEX idx_conciliacao_empresa ON conciliacao_bancaria(empresa_id);
CREATE INDEX idx_conciliacao_conta ON conciliacao_bancaria(conta_bancaria_id);

CREATE TABLE conciliacao_bancaria_item (
    id BIGSERIAL PRIMARY KEY,
    conciliacao_id BIGINT NOT NULL REFERENCES conciliacao_bancaria(id) ON DELETE CASCADE,
    data_movimento DATE NOT NULL,
    descricao VARCHAR(200),
    valor NUMERIC(18,2) NOT NULL,
    tipo VARCHAR(1) NOT NULL CHECK (tipo IN ('D', 'C')), -- D=Débito, C=Crédito
    origem VARCHAR(20) NOT NULL CHECK (origem IN ('BANCO', 'SISTEMA')),
    conciliado BOOLEAN NOT NULL DEFAULT FALSE,
    item_par_id BIGINT REFERENCES conciliacao_bancaria_item(id),
    titulo_baixa_id BIGINT REFERENCES titulo_baixas(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_conc_item_conciliacao ON conciliacao_bancaria_item(conciliacao_id);
