-- V13: Fix funcionarios.departamento_id FK to departamentos_rh
-- and add missing audit columns to funcionario_beneficio

-- 1. Fix FK: departamento_id should reference departamentos_rh (where data actually lives)
ALTER TABLE funcionarios DROP CONSTRAINT IF EXISTS funcionarios_departamento_id_fkey;
ALTER TABLE funcionarios ADD CONSTRAINT funcionarios_departamento_id_fkey
    FOREIGN KEY (departamento_id) REFERENCES departamentos_rh(id);

-- 2. Add missing BaseEntity audit columns to funcionario_beneficio
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS uuid UUID UNIQUE DEFAULT gen_random_uuid();
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE funcionario_beneficio ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);

-- Fill empresa_id from funcionario's empresa
UPDATE funcionario_beneficio fb
SET empresa_id = f.empresa_id
FROM funcionarios f
WHERE fb.funcionario_id = f.id
  AND fb.empresa_id IS NULL;
