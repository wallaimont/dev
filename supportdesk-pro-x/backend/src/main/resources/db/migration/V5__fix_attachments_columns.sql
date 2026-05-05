-- V5: Align attachments table with JPA entity mapping

ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS original_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS stored_name VARCHAR(255);

UPDATE attachments
SET original_name = COALESCE(original_name, file_name),
    stored_name = COALESCE(stored_name, file_name)
WHERE original_name IS NULL OR stored_name IS NULL;

ALTER TABLE attachments
    ALTER COLUMN original_name SET NOT NULL,
    ALTER COLUMN stored_name SET NOT NULL;
