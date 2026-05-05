-- ====================================================================
-- V14 – Adicionar colunas faltantes e relaxar constraints NOT NULL
-- Corrige 11 tabelas que divergiam das entities Java
-- ====================================================================

-- ────────────────────────────────────────────────────────────────────
-- 1. tabela_preco_itens  (TenantEntity: precisa de TODAS as colunas)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS deleted         BOOLEAN       DEFAULT false;
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS deleted_at      TIMESTAMP;
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS created_by      VARCHAR(255);
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS updated_by      VARCHAR(255);
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS empresa_id      BIGINT;
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS filial_id       BIGINT;
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS preco_promocional NUMERIC(18,2);
ALTER TABLE tabela_preco_itens ADD COLUMN IF NOT EXISTS quantidade_minima NUMERIC(18,6);

-- FK empresa / filial
ALTER TABLE tabela_preco_itens DROP CONSTRAINT IF EXISTS fk_tabprecoitem_empresa;
ALTER TABLE tabela_preco_itens ADD CONSTRAINT fk_tabprecoitem_empresa
    FOREIGN KEY (empresa_id) REFERENCES empresas(id);
ALTER TABLE tabela_preco_itens DROP CONSTRAINT IF EXISTS fk_tabprecoitem_filial;
ALTER TABLE tabela_preco_itens ADD CONSTRAINT fk_tabprecoitem_filial
    FOREIGN KEY (filial_id) REFERENCES filiais(id);

-- ────────────────────────────────────────────────────────────────────
-- 2. comissoes  (filial_id NOT NULL → DROP NOT NULL + add missing cols)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE comissoes ALTER COLUMN filial_id DROP NOT NULL;

-- ────────────────────────────────────────────────────────────────────
-- 3. conciliacao_bancaria  (falta filial_id)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE conciliacao_bancaria ADD COLUMN IF NOT EXISTS filial_id BIGINT;
ALTER TABLE conciliacao_bancaria DROP CONSTRAINT IF EXISTS fk_concbancaria_filial;
ALTER TABLE conciliacao_bancaria ADD CONSTRAINT fk_concbancaria_filial
    FOREIGN KEY (filial_id) REFERENCES filiais(id);

-- ────────────────────────────────────────────────────────────────────
-- 4. bens_patrimoniais  (falta numero_patrimonio — entity mapeia)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE bens_patrimoniais ADD COLUMN IF NOT EXISTS numero_patrimonio VARCHAR(30);

-- ────────────────────────────────────────────────────────────────────
-- 5. estrutura_produto  (falta filial_id)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE estrutura_produto ADD COLUMN IF NOT EXISTS filial_id BIGINT;
ALTER TABLE estrutura_produto DROP CONSTRAINT IF EXISTS fk_estproduto_filial;
ALTER TABLE estrutura_produto ADD CONSTRAINT fk_estproduto_filial
    FOREIGN KEY (filial_id) REFERENCES filiais(id);

-- ────────────────────────────────────────────────────────────────────
-- 6. contratos  (falta forma_pagamento)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE contratos ADD COLUMN IF NOT EXISTS forma_pagamento VARCHAR(50);

-- ────────────────────────────────────────────────────────────────────
-- 7. ferias  (falta filial_id)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE ferias ADD COLUMN IF NOT EXISTS filial_id BIGINT;
ALTER TABLE ferias DROP CONSTRAINT IF EXISTS fk_ferias_filial;
ALTER TABLE ferias ADD CONSTRAINT fk_ferias_filial
    FOREIGN KEY (filial_id) REFERENCES filiais(id);

-- ────────────────────────────────────────────────────────────────────
-- 8. ponto_eletronico  (faltam 5 colunas de BaseEntity + filial_id)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE ponto_eletronico ADD COLUMN IF NOT EXISTS deleted     BOOLEAN   DEFAULT false;
ALTER TABLE ponto_eletronico ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP;
ALTER TABLE ponto_eletronico ADD COLUMN IF NOT EXISTS created_by  VARCHAR(255);
ALTER TABLE ponto_eletronico ADD COLUMN IF NOT EXISTS updated_by  VARCHAR(255);
ALTER TABLE ponto_eletronico ADD COLUMN IF NOT EXISTS filial_id   BIGINT;
ALTER TABLE ponto_eletronico DROP CONSTRAINT IF EXISTS fk_pontoeletronico_filial;
ALTER TABLE ponto_eletronico ADD CONSTRAINT fk_pontoeletronico_filial
    FOREIGN KEY (filial_id) REFERENCES filiais(id);

-- ────────────────────────────────────────────────────────────────────
-- 9. folha_pagamento  (data_calculo NOT NULL → relaxar)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE folha_pagamento ALTER COLUMN data_calculo DROP NOT NULL;

-- ────────────────────────────────────────────────────────────────────
-- 10. notas_fiscais  (filial_id NOT NULL → relaxar para não exigir no DTO)
-- ────────────────────────────────────────────────────────────────────
-- A entidade NotaFiscal extends TenantEntity, filial_id existe mas pode ser NOT NULL.
-- Vamos relaxar para permitir inserção sem filialId.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'notas_fiscais' AND column_name = 'filial_id'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE notas_fiscais ALTER COLUMN filial_id DROP NOT NULL;
    END IF;
END $$;

-- ────────────────────────────────────────────────────────────────────
-- 11. workflow_alcadas  (nivel NOT NULL → add default)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE workflow_alcadas ALTER COLUMN nivel SET DEFAULT 1;
ALTER TABLE workflow_alcadas ALTER COLUMN nome  SET DEFAULT '';
