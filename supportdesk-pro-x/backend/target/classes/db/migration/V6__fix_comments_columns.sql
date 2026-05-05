-- V6: Align comments table with JPA entity mapping

ALTER TABLE comments
    ADD COLUMN IF NOT EXISTS body TEXT,
    ADD COLUMN IF NOT EXISTS is_internal BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_by UUID;

UPDATE comments
SET body = COALESCE(body, content),
    is_internal = COALESCE(is_internal, internal)
WHERE body IS NULL OR is_internal IS NULL;

ALTER TABLE comments
    ALTER COLUMN body SET NOT NULL,
    ALTER COLUMN is_internal SET NOT NULL;
