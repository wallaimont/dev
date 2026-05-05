-- V16: Adicionar colunas herdadas de BaseEntity/TenantEntity às tabelas de sub-entidades
-- e colunas específicas faltantes para compatibilidade com as entidades JPA

-- =============================================
-- nota_fiscal_itens
-- =============================================
ALTER TABLE nota_fiscal_itens ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE nota_fiscal_itens ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE nota_fiscal_itens ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE nota_fiscal_itens ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE nota_fiscal_itens ADD COLUMN IF NOT EXISTS empresa_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE nota_fiscal_itens ADD COLUMN IF NOT EXISTS filial_id BIGINT;

-- =============================================
-- ordem_producao_itens
-- =============================================
ALTER TABLE ordem_producao_itens ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE ordem_producao_itens ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE ordem_producao_itens ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE ordem_producao_itens ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE ordem_producao_itens ADD COLUMN IF NOT EXISTS empresa_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE ordem_producao_itens ADD COLUMN IF NOT EXISTS filial_id BIGINT;

-- =============================================
-- apontamento_producao (já tem empresa_id, created_by, created_at, updated_at, ativo)
-- =============================================
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS filial_id BIGINT;
-- Colunas específicas que a entidade espera mas não existem no banco
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS data_inicio TIMESTAMP;
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS data_fim TIMESTAMP;
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS quantidade_produzida NUMERIC(18,6);
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS quantidade_rejeitada NUMERIC(18,6);
ALTER TABLE apontamento_producao ADD COLUMN IF NOT EXISTS maquina VARCHAR(50);

-- =============================================
-- folha_pagamento_item (não tem nenhuma coluna herdada)
-- =============================================
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS empresa_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS filial_id BIGINT;
-- Colunas específicas que a entidade espera
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS horas_extras_valor NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS comissoes NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS gratificacoes NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS desconto_inss NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS desconto_irrf NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS desconto_vt NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS desconto_vr NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS desconto_plano_saude NUMERIC(18,2);
ALTER TABLE folha_pagamento_item ADD COLUMN IF NOT EXISTS desconto_sindical NUMERIC(18,2);

-- =============================================
-- depreciacao (já tem empresa_id, created_at, ativo)
-- =============================================
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS filial_id BIGINT;
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
-- Colunas específicas que a entidade espera
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS data_depreciacao DATE;
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS valor_liquido NUMERIC(18,2);
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS mes_referencia INTEGER;
ALTER TABLE depreciacao ADD COLUMN IF NOT EXISTS ano_referencia INTEGER;
