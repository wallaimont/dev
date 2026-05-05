-- =============================================================
-- InsuranceFlow SaaS - Migration V3 - Billing Infrastructure
-- =============================================================

-- ========== TABELA EVENTO_BILLING ==========
CREATE TABLE IF NOT EXISTS evento_billing (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID REFERENCES empresa(id),
    assinatura_id UUID REFERENCES assinatura_empresa(id),
    pagamento_id UUID REFERENCES pagamento_assinatura(id),
    tipo VARCHAR(50) NOT NULL,
    gateway VARCHAR(30),
    id_evento_externo VARCHAR(255),
    payload TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'RECEBIDO',
    mensagem_erro TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(150),
    updated_by VARCHAR(150)
);

-- ========== PERMITIR plano_novo_id NULL em historico_plano (para cancelamentos) ==========
ALTER TABLE historico_plano ALTER COLUMN plano_novo_id DROP NOT NULL;

-- ========== ÍNDICES PARA BILLING ==========
CREATE INDEX IF NOT EXISTS idx_evento_billing_empresa ON evento_billing(empresa_id);
CREATE INDEX IF NOT EXISTS idx_evento_billing_tipo ON evento_billing(tipo);
CREATE INDEX IF NOT EXISTS idx_evento_billing_gateway ON evento_billing(gateway);
CREATE INDEX IF NOT EXISTS idx_evento_billing_id_externo ON evento_billing(id_evento_externo);
CREATE INDEX IF NOT EXISTS idx_assinatura_status ON assinatura_empresa(status);
CREATE INDEX IF NOT EXISTS idx_assinatura_vencimento ON assinatura_empresa(data_vencimento);
CREATE INDEX IF NOT EXISTS idx_pagamento_status ON pagamento_assinatura(status);
CREATE INDEX IF NOT EXISTS idx_pagamento_vencimento ON pagamento_assinatura(data_vencimento);
CREATE INDEX IF NOT EXISTS idx_pagamento_assinatura ON pagamento_assinatura(assinatura_id);
CREATE INDEX IF NOT EXISTS idx_historico_plano_empresa ON historico_plano(empresa_id);
