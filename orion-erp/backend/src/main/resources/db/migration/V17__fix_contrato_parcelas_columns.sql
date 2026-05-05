-- V17: Adicionar colunas BaseEntity/TenantEntity faltantes na tabela contrato_parcelas
ALTER TABLE contrato_parcelas ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE contrato_parcelas ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE contrato_parcelas ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE contrato_parcelas ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE contrato_parcelas ADD COLUMN IF NOT EXISTS empresa_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE contrato_parcelas ADD COLUMN IF NOT EXISTS filial_id BIGINT;

-- FK para empresa
ALTER TABLE contrato_parcelas ADD CONSTRAINT fk_contrato_parcelas_empresa
    FOREIGN KEY (empresa_id) REFERENCES empresas(id);
