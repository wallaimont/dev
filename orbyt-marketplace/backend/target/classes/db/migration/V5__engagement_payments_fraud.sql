-- V5: Payment, Fraud, Notifications, Support, Chat, Reviews expansion

-- Expand payments
ALTER TABLE payments ADD COLUMN IF NOT EXISTS order_group_id UUID;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS buyer_id UUID;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS payment_method VARCHAR(30);
ALTER TABLE payments ADD COLUMN IF NOT EXISTS currency_code VARCHAR(3) DEFAULT 'BRL';
ALTER TABLE payments ADD COLUMN IF NOT EXISTS amount NUMERIC(15,2);
ALTER TABLE payments ADD COLUMN IF NOT EXISTS gateway_provider VARCHAR(50);
ALTER TABLE payments ADD COLUMN IF NOT EXISTS gateway_transaction_id VARCHAR(200);
ALTER TABLE payments ADD COLUMN IF NOT EXISTS paid_at TIMESTAMPTZ;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS failure_reason TEXT;

-- Expand payment_transactions
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS payment_id UUID;
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(30);
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS amount NUMERIC(15,2);
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS gateway_response JSONB;
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(200) UNIQUE;

-- Expand pix_charges
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS payment_id UUID;
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS txid VARCHAR(200);
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS qr_code TEXT;
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS qr_code_image TEXT;
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS copy_paste_code TEXT;
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS amount NUMERIC(15,2);
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ;
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS paid_at TIMESTAMPTZ;
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS payer_cpf VARCHAR(14);
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS payer_name VARCHAR(200);
ALTER TABLE pix_charges ADD COLUMN IF NOT EXISTS end_to_end_id VARCHAR(100);

-- Expand refunds
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS payment_id UUID;
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS reason TEXT;
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS amount NUMERIC(15,2);
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS refund_method VARCHAR(30);
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS processed_at TIMESTAMPTZ;
ALTER TABLE refunds ADD COLUMN IF NOT EXISTS processed_by UUID;

-- Expand commissions
ALTER TABLE commissions ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE commissions ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE commissions ADD COLUMN IF NOT EXISTS order_amount NUMERIC(15,2);
ALTER TABLE commissions ADD COLUMN IF NOT EXISTS commission_rate NUMERIC(5,4);
ALTER TABLE commissions ADD COLUMN IF NOT EXISTS commission_amount NUMERIC(15,2);
ALTER TABLE commissions ADD COLUMN IF NOT EXISTS currency_code VARCHAR(3) DEFAULT 'BRL';

-- Expand seller_payouts
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS amount NUMERIC(15,2);
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS currency_code VARCHAR(3) DEFAULT 'BRL';
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS payout_method VARCHAR(30);
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS bank_account JSONB;
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS pix_key VARCHAR(100);
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS paid_at TIMESTAMPTZ;
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS period_start DATE;
ALTER TABLE seller_payouts ADD COLUMN IF NOT EXISTS period_end DATE;

-- Expand fraud_analysis
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS order_group_id UUID;
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS buyer_id UUID;
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS risk_score INT DEFAULT 0;
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS risk_level VARCHAR(20) DEFAULT 'LOW';
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS user_agent TEXT;
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS device_fingerprint VARCHAR(200);
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS rules_triggered JSONB DEFAULT '[]';
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS decision VARCHAR(20) DEFAULT 'APPROVE';
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS reviewed_by UUID;
ALTER TABLE fraud_analysis ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMPTZ;

-- Blacklist for antifraude
CREATE TABLE IF NOT EXISTS fraud_blacklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    blacklist_type VARCHAR(30) NOT NULL,
    value VARCHAR(200) NOT NULL,
    reason TEXT,
    created_by UUID,
    created_at TIMESTAMPTZ DEFAULT now(),
    expires_at TIMESTAMPTZ,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Expand reviews
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS product_id UUID;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS rating INT NOT NULL DEFAULT 5;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS title VARCHAR(200);
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS comment TEXT;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS images TEXT[];
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS is_verified_purchase BOOLEAN DEFAULT FALSE;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS helpful_count INT DEFAULT 0;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS reply TEXT;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS replied_by UUID;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS replied_at TIMESTAMPTZ;

-- Expand seller_ratings
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS rating INT DEFAULT 5;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS comment TEXT;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS delivery_time_rating INT;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS communication_rating INT;
ALTER TABLE seller_ratings ADD COLUMN IF NOT EXISTS product_quality_rating INT;

-- Expand chats
ALTER TABLE chats ADD COLUMN IF NOT EXISTS buyer_id UUID;
ALTER TABLE chats ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE chats ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE chats ADD COLUMN IF NOT EXISTS subject VARCHAR(200);
ALTER TABLE chats ADD COLUMN IF NOT EXISTS last_message_at TIMESTAMPTZ;
ALTER TABLE chats ADD COLUMN IF NOT EXISTS buyer_unread INT DEFAULT 0;
ALTER TABLE chats ADD COLUMN IF NOT EXISTS seller_unread INT DEFAULT 0;

-- Expand chat_messages
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS chat_id UUID;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS sender_id UUID;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS message_type VARCHAR(20) DEFAULT 'TEXT';
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS content TEXT;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS attachment_url TEXT;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS is_read BOOLEAN DEFAULT FALSE;
ALTER TABLE chat_messages ADD COLUMN IF NOT EXISTS read_at TIMESTAMPTZ;

-- Expand notifications
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS notification_type VARCHAR(50);
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS channel VARCHAR(20) DEFAULT 'INTERNAL';
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS title VARCHAR(300);
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS body TEXT;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS data JSONB DEFAULT '{}';
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS is_read BOOLEAN DEFAULT FALSE;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS read_at TIMESTAMPTZ;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS sent_at TIMESTAMPTZ;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS delivery_status VARCHAR(20) DEFAULT 'PENDING';

-- Expand support_tickets
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS category VARCHAR(50);
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS priority VARCHAR(20) DEFAULT 'MEDIUM';
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS subject VARCHAR(300);
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS assigned_to UUID;
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS resolved_at TIMESTAMPTZ;
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS sla_deadline TIMESTAMPTZ;

-- Expand support_messages
ALTER TABLE support_messages ADD COLUMN IF NOT EXISTS ticket_id UUID;
ALTER TABLE support_messages ADD COLUMN IF NOT EXISTS sender_id UUID;
ALTER TABLE support_messages ADD COLUMN IF NOT EXISTS message TEXT;
ALTER TABLE support_messages ADD COLUMN IF NOT EXISTS attachment_url TEXT;
ALTER TABLE support_messages ADD COLUMN IF NOT EXISTS is_internal BOOLEAN DEFAULT FALSE;

-- Expand disputes
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS buyer_id UUID;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS reason VARCHAR(100);
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS resolution TEXT;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS resolved_by UUID;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS resolved_at TIMESTAMPTZ;
ALTER TABLE disputes ADD COLUMN IF NOT EXISTS refund_amount NUMERIC(15,2);

-- Expand banners
ALTER TABLE banners ADD COLUMN IF NOT EXISTS title VARCHAR(200);
ALTER TABLE banners ADD COLUMN IF NOT EXISTS subtitle VARCHAR(300);
ALTER TABLE banners ADD COLUMN IF NOT EXISTS image_url TEXT;
ALTER TABLE banners ADD COLUMN IF NOT EXISTS link_url TEXT;
ALTER TABLE banners ADD COLUMN IF NOT EXISTS position VARCHAR(50);
ALTER TABLE banners ADD COLUMN IF NOT EXISTS display_order INT DEFAULT 0;
ALTER TABLE banners ADD COLUMN IF NOT EXISTS starts_at TIMESTAMPTZ;
ALTER TABLE banners ADD COLUMN IF NOT EXISTS ends_at TIMESTAMPTZ;
ALTER TABLE banners ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE banners ADD COLUMN IF NOT EXISTS language VARCHAR(5) DEFAULT 'pt-BR';

-- Expand cms_pages
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS slug VARCHAR(200);
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS title VARCHAR(300);
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS content TEXT;
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS page_type VARCHAR(50);
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS language VARCHAR(5) DEFAULT 'pt-BR';
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS is_published BOOLEAN DEFAULT FALSE;
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS published_at TIMESTAMPTZ;
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS meta_title VARCHAR(200);
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500);

-- Expand coupons
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS code VARCHAR(50);
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS coupon_type VARCHAR(20);
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS discount_type VARCHAR(20);
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS discount_value NUMERIC(15,2);
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS min_order_value NUMERIC(15,2);
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS max_discount NUMERIC(15,2);
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS usage_limit INT;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS usage_count INT DEFAULT 0;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS per_user_limit INT DEFAULT 1;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS category_id UUID;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS first_purchase_only BOOLEAN DEFAULT FALSE;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS starts_at TIMESTAMPTZ;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Expand security_events
ALTER TABLE security_events ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE security_events ADD COLUMN IF NOT EXISTS event_type VARCHAR(50);
ALTER TABLE security_events ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
ALTER TABLE security_events ADD COLUMN IF NOT EXISTS user_agent TEXT;
ALTER TABLE security_events ADD COLUMN IF NOT EXISTS details JSONB DEFAULT '{}';
ALTER TABLE security_events ADD COLUMN IF NOT EXISTS severity VARCHAR(20) DEFAULT 'INFO';

-- Expand audit_logs
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS action VARCHAR(100);
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS entity_type VARCHAR(100);
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS entity_id UUID;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS old_values JSONB;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS new_values JSONB;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);

-- Expand outbox_events
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS event_type VARCHAR(100);
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS aggregate_type VARCHAR(100);
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS aggregate_id UUID;
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS payload JSONB;
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS published BOOLEAN DEFAULT FALSE;
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS published_at TIMESTAMPTZ;
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS retry_count INT DEFAULT 0;
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS next_retry_at TIMESTAMPTZ;

-- Rate limiting table
CREATE TABLE IF NOT EXISTS rate_limits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identifier VARCHAR(200) NOT NULL,
    action VARCHAR(50) NOT NULL,
    attempt_count INT DEFAULT 1,
    window_start TIMESTAMPTZ DEFAULT now(),
    blocked_until TIMESTAMPTZ,
    UNIQUE(identifier, action)
);

-- Shipping carriers
CREATE TABLE IF NOT EXISTS shipping_carriers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    tracking_url_template TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Favorite stores
CREATE TABLE IF NOT EXISTS favorite_stores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    store_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(tenant_id, user_id, store_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_fraud_blacklist_type_value ON fraud_blacklist(blacklist_type, value);
CREATE INDEX IF NOT EXISTS idx_reviews_product_id ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_seller_ratings_seller_id ON seller_ratings(seller_id);
CREATE INDEX IF NOT EXISTS idx_chats_buyer_id ON chats(buyer_id);
CREATE INDEX IF NOT EXISTS idx_chats_seller_id ON chats(seller_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_chat_id ON chat_messages(chat_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_support_tickets_user_id ON support_tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_disputes_order_id ON disputes(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_group_id ON payments(order_group_id);
CREATE INDEX IF NOT EXISTS idx_pix_charges_payment_id ON pix_charges(payment_id);
CREATE INDEX IF NOT EXISTS idx_refunds_payment_id ON refunds(payment_id);
CREATE INDEX IF NOT EXISTS idx_commissions_order_id ON commissions(order_id);
CREATE INDEX IF NOT EXISTS idx_seller_payouts_seller_id ON seller_payouts(seller_id);
CREATE INDEX IF NOT EXISTS idx_outbox_events_published ON outbox_events(published);
CREATE INDEX IF NOT EXISTS idx_security_events_user_id ON security_events(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_banners_position ON banners(position);
CREATE INDEX IF NOT EXISTS idx_cms_pages_slug ON cms_pages(slug);
CREATE INDEX IF NOT EXISTS idx_coupons_code ON coupons(code);
CREATE INDEX IF NOT EXISTS idx_rate_limits_identifier ON rate_limits(identifier, action);
