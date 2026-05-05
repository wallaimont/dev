ALTER TABLE coupons
    ADD COLUMN IF NOT EXISTS minimum_order_value NUMERIC(12,2),
    ADD COLUMN IF NOT EXISTS maximum_discount NUMERIC(12,2),
    ADD COLUMN IF NOT EXISTS max_usages INTEGER,
    ADD COLUMN IF NOT EXISTS usage_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS max_usages_per_user INTEGER,
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE coupons
SET max_usages = COALESCE(max_usages, max_uses),
    expires_at = COALESCE(expires_at, ends_at),
    active = COALESCE(active, status = 'ACTIVE');