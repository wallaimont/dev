-- ============================================================
-- NEXUS MARKETPLACE — V001: Core Schema
-- Multi-tenant, full marketplace database structure
-- ============================================================

-- ============================================================
-- TENANTS & CONFIGURAÇÃO
-- ============================================================
CREATE TABLE tenants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(100) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    domain          VARCHAR(255),
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','SUSPENDED','TRIAL','CANCELLED')),
    plan            VARCHAR(30) NOT NULL DEFAULT 'STARTER'
                        CHECK (plan IN ('STARTER','GROWTH','ENTERPRISE','CUSTOM')),
    currency_code   CHAR(3) NOT NULL DEFAULT 'BRL',
    default_locale  VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE tenant_settings (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                   UUID NOT NULL REFERENCES tenants(id),
    commission_rate             NUMERIC(5,4) NOT NULL DEFAULT 0.10,
    auto_approve_sellers        BOOLEAN NOT NULL DEFAULT FALSE,
    auto_approve_products       BOOLEAN NOT NULL DEFAULT FALSE,
    pix_key                     VARCHAR(255),
    pix_key_type                VARCHAR(20),
    payment_gateway             VARCHAR(50) DEFAULT 'PAGAR_ME',
    shipping_provider           VARCHAR(50) DEFAULT 'MELHOR_ENVIO',
    max_installments            INTEGER DEFAULT 12,
    min_order_value             NUMERIC(10,2) DEFAULT 0,
    logo_url                    VARCHAR(500),
    favicon_url                 VARCHAR(500),
    primary_color               CHAR(7) DEFAULT '#1A2FE8',
    secondary_color             CHAR(7) DEFAULT '#FF4B26',
    terms_url                   VARCHAR(500),
    privacy_url                 VARCHAR(500),
    support_email               VARCHAR(255),
    smtp_host                   VARCHAR(255),
    smtp_port                   INTEGER DEFAULT 587,
    smtp_user                   VARCHAR(255),
    smtp_password               VARCHAR(255) -- encrypted
);

-- ============================================================
-- USERS & SEGURANÇA
-- ============================================================
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION'
                        CHECK (status IN ('ACTIVE','INACTIVE','PENDING_VERIFICATION',
                                          'SUSPENDED','BANNED')),
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,
    created_by      UUID,
    updated_by      UUID,
    UNIQUE (tenant_id, email)
);

CREATE TABLE roles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID REFERENCES tenants(id),
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    is_system   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE permissions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource    VARCHAR(100) NOT NULL,
    action      VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    UNIQUE(resource, action)
);

CREATE TABLE user_roles (
    user_id     UUID NOT NULL REFERENCES users(id),
    role_id     UUID NOT NULL REFERENCES roles(id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id         UUID NOT NULL REFERENCES roles(id),
    permission_id   UUID NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    device_info     VARCHAR(500),
    ip_address      INET,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE email_verifications (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id),
    token       VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE password_resets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id),
    token_hash  VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- PERFIS DE USUÁRIO
-- ============================================================
CREATE TABLE user_profiles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    cpf             VARCHAR(14),
    phone           VARCHAR(20),
    birth_date      DATE,
    gender          VARCHAR(20),
    avatar_url      VARCHAR(500),
    bio             TEXT,
    preferred_locale    VARCHAR(10) DEFAULT 'pt-BR',
    preferred_currency  CHAR(3) DEFAULT 'BRL',
    marketing_consent   BOOLEAN DEFAULT FALSE,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_addresses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    label           VARCHAR(50),
    recipient_name  VARCHAR(255),
    zip_code        VARCHAR(10) NOT NULL,
    street          VARCHAR(255) NOT NULL,
    number          VARCHAR(20) NOT NULL,
    complement      VARCHAR(100),
    neighborhood    VARCHAR(100),
    city            VARCHAR(100) NOT NULL,
    state           CHAR(2) NOT NULL,
    country         CHAR(2) NOT NULL DEFAULT 'BR',
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- VENDEDORES & LOJAS
-- ============================================================
CREATE TABLE sellers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    user_id         UUID NOT NULL REFERENCES users(id),
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','ACTIVE','SUSPENDED',
                                          'BANNED','REJECTED','UNDER_REVIEW')),
    seller_type     VARCHAR(20) NOT NULL DEFAULT 'PF'
                        CHECK (seller_type IN ('PF','PJ')),
    document        VARCHAR(18) NOT NULL,
    company_name    VARCHAR(255),
    trade_name      VARCHAR(255),
    commission_rate NUMERIC(5,4),
    level           VARCHAR(20) NOT NULL DEFAULT 'BRONZE'
                        CHECK (level IN ('BRONZE','SILVER','GOLD','PLATINUM','DIAMOND')),
    is_premium      BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at     TIMESTAMPTZ,
    approved_by     UUID,
    rejection_reason TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(tenant_id, user_id)
);

CREATE TABLE seller_documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    document_type   VARCHAR(50) NOT NULL,
    file_url        VARCHAR(500) NOT NULL,
    status          VARCHAR(20) DEFAULT 'PENDING',
    reviewed_at     TIMESTAMPTZ,
    reviewed_by     UUID,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE stores (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    seller_id       UUID NOT NULL UNIQUE REFERENCES sellers(id),
    slug            VARCHAR(150) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    logo_url        VARCHAR(500),
    banner_url      VARCHAR(500),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    avg_rating      NUMERIC(3,2) DEFAULT 0,
    total_ratings   INTEGER DEFAULT 0,
    total_sales     INTEGER DEFAULT 0,
    policies        TEXT,
    return_policy   TEXT,
    social_instagram VARCHAR(100),
    social_facebook VARCHAR(100),
    social_website  VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(tenant_id, slug)
);

-- ============================================================
-- CATÁLOGO
-- ============================================================
CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id),
    parent_id   UUID REFERENCES categories(id),
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(150) NOT NULL,
    icon_url    VARCHAR(500),
    sort_order  INTEGER DEFAULT 0,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(tenant_id, slug)
);

CREATE TABLE brands (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id),
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(150) NOT NULL,
    logo_url    VARCHAR(500),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(tenant_id, slug)
);

CREATE TABLE product_attributes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id),
    category_id UUID REFERENCES categories(id),
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(30) NOT NULL DEFAULT 'TEXT',
    is_required BOOLEAN DEFAULT FALSE,
    is_variant  BOOLEAN DEFAULT FALSE
);

CREATE TABLE products (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenants(id),
    seller_id           UUID NOT NULL REFERENCES sellers(id),
    store_id            UUID NOT NULL REFERENCES stores(id),
    category_id         UUID NOT NULL REFERENCES categories(id),
    brand_id            UUID REFERENCES brands(id),
    name                VARCHAR(500) NOT NULL,
    slug                VARCHAR(500) NOT NULL,
    description         TEXT,
    short_description   VARCHAR(500),
    status              VARCHAR(30) NOT NULL DEFAULT 'DRAFT'
                            CHECK (status IN ('DRAFT','PENDING_REVIEW','ACTIVE',
                                              'INACTIVE','REJECTED','SUSPENDED')),
    condition           VARCHAR(20) DEFAULT 'NEW',
    base_price          NUMERIC(12,2) NOT NULL,
    promotional_price   NUMERIC(12,2),
    promo_starts_at     TIMESTAMPTZ,
    promo_ends_at       TIMESTAMPTZ,
    currency_code       CHAR(3) NOT NULL DEFAULT 'BRL',
    weight_grams        INTEGER,
    width_cm            NUMERIC(8,2),
    height_cm           NUMERIC(8,2),
    length_cm           NUMERIC(8,2),
    meta_title          VARCHAR(255),
    meta_description    VARCHAR(500),
    meta_keywords       VARCHAR(500),
    avg_rating          NUMERIC(3,2) DEFAULT 0,
    total_reviews       INTEGER DEFAULT 0,
    total_sold          INTEGER DEFAULT 0,
    is_digital          BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured         BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at         TIMESTAMPTZ,
    approved_by         UUID,
    rejection_reason    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ,
    UNIQUE(tenant_id, slug)
);

CREATE TABLE product_images (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    alt_text    VARCHAR(255),
    sort_order  INTEGER DEFAULT 0,
    is_main     BOOLEAN DEFAULT FALSE
);

CREATE TABLE product_variants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku             VARCHAR(100) NOT NULL,
    name            VARCHAR(255),
    price           NUMERIC(12,2) NOT NULL,
    promotional_price NUMERIC(12,2),
    attributes      JSONB,
    image_url       VARCHAR(500),
    barcode         VARCHAR(50),
    weight_grams    INTEGER,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    UNIQUE(product_id, sku)
);

CREATE TABLE stock_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    variant_id      UUID NOT NULL UNIQUE REFERENCES product_variants(id),
    quantity        INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reserved        INTEGER NOT NULL DEFAULT 0 CHECK (reserved >= 0),
    min_quantity    INTEGER DEFAULT 0,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- CARRINHO
-- ============================================================
CREATE TABLE carts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    user_id         UUID REFERENCES users(id),
    session_id      VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','ABANDONED','CONVERTED')),
    coupon_code     VARCHAR(50),
    coupon_discount NUMERIC(10,2) DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id         UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    variant_id      UUID NOT NULL REFERENCES product_variants(id),
    product_id      UUID NOT NULL REFERENCES products(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(12,2) NOT NULL,
    added_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- PEDIDOS
-- ============================================================
CREATE TABLE orders (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenants(id),
    buyer_id            UUID NOT NULL REFERENCES users(id),
    order_number        VARCHAR(50) NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING','CONFIRMED','PROCESSING',
                                              'PARTIALLY_SHIPPED','SHIPPED',
                                              'DELIVERED','CANCELLED','REFUNDING','REFUNDED')),
    currency_code       CHAR(3) NOT NULL DEFAULT 'BRL',
    subtotal            NUMERIC(12,2) NOT NULL,
    shipping_total      NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_total      NUMERIC(12,2) NOT NULL DEFAULT 0,
    total               NUMERIC(12,2) NOT NULL,
    coupon_code         VARCHAR(50),
    shipping_address_id UUID REFERENCES user_addresses(id),
    notes               TEXT,
    cancelled_at        TIMESTAMPTZ,
    cancel_reason       TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(tenant_id, order_number)
);

CREATE TABLE order_groups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL REFERENCES orders(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    store_id        UUID NOT NULL REFERENCES stores(id),
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    subtotal        NUMERIC(12,2) NOT NULL,
    shipping_cost   NUMERIC(12,2) DEFAULT 0,
    commission_rate NUMERIC(5,4) NOT NULL,
    commission_amt  NUMERIC(12,2) NOT NULL,
    seller_amount   NUMERIC(12,2) NOT NULL,
    payout_id       UUID,
    payout_at       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_group_id  UUID NOT NULL REFERENCES order_groups(id),
    order_id        UUID NOT NULL REFERENCES orders(id),
    variant_id      UUID NOT NULL REFERENCES product_variants(id),
    product_id      UUID NOT NULL REFERENCES products(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    product_name    VARCHAR(500) NOT NULL,
    sku             VARCHAR(100) NOT NULL,
    attributes      JSONB,
    quantity        INTEGER NOT NULL,
    unit_price      NUMERIC(12,2) NOT NULL,
    total_price     NUMERIC(12,2) NOT NULL,
    review_id       UUID
);

CREATE TABLE order_status_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID NOT NULL REFERENCES orders(id),
    group_id    UUID REFERENCES order_groups(id),
    status      VARCHAR(30) NOT NULL,
    comment     TEXT,
    changed_by  UUID REFERENCES users(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- PAGAMENTOS
-- ============================================================
CREATE TABLE payments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenants(id),
    order_id            UUID NOT NULL REFERENCES orders(id),
    idempotency_key     VARCHAR(100) NOT NULL UNIQUE,
    method              VARCHAR(30) NOT NULL
                            CHECK (method IN ('PIX','CREDIT_CARD','DEBIT_CARD',
                                              'BOLETO','WALLET','BALANCE')),
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING','PROCESSING','PAID',
                                              'FAILED','CANCELLED','REFUNDED','DISPUTED')),
    amount              NUMERIC(12,2) NOT NULL,
    currency_code       CHAR(3) NOT NULL DEFAULT 'BRL',
    gateway             VARCHAR(50),
    gateway_txn_id      VARCHAR(255),
    gateway_response    JSONB,
    installments        INTEGER DEFAULT 1,
    paid_at             TIMESTAMPTZ,
    expires_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE pix_charges (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id      UUID NOT NULL REFERENCES payments(id),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    txid            VARCHAR(100) NOT NULL UNIQUE,
    e2e_id          VARCHAR(100),
    qr_code         TEXT NOT NULL,
    qr_code_url     VARCHAR(500),
    pix_copy_paste  TEXT NOT NULL,
    amount          NUMERIC(12,2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','COMPLETED','EXPIRED','CANCELLED')),
    expires_at      TIMESTAMPTZ NOT NULL,
    paid_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE payment_splits (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id      UUID NOT NULL REFERENCES payments(id),
    order_group_id  UUID NOT NULL REFERENCES order_groups(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    recipient_key   VARCHAR(255),
    total_amount    NUMERIC(12,2) NOT NULL,
    seller_amount   NUMERIC(12,2) NOT NULL,
    platform_fee    NUMERIC(12,2) NOT NULL,
    status          VARCHAR(20) DEFAULT 'PENDING',
    gateway_split_id VARCHAR(255),
    transferred_at  TIMESTAMPTZ
);

CREATE TABLE payment_webhooks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    gateway         VARCHAR(50) NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         JSONB NOT NULL,
    signature       VARCHAR(500),
    processed       BOOLEAN DEFAULT FALSE,
    processed_at    TIMESTAMPTZ,
    error_message   TEXT,
    retry_count     INTEGER DEFAULT 0,
    received_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE refunds (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    payment_id      UUID NOT NULL REFERENCES payments(id),
    order_id        UUID NOT NULL REFERENCES orders(id),
    requested_by    UUID NOT NULL REFERENCES users(id),
    reason          VARCHAR(500),
    amount          NUMERIC(12,2) NOT NULL,
    status          VARCHAR(20) DEFAULT 'REQUESTED'
                        CHECK (status IN ('REQUESTED','APPROVED','PROCESSING',
                                          'COMPLETED','REJECTED')),
    gateway_refund_id VARCHAR(255),
    approved_by     UUID,
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE seller_payouts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    period_start    DATE NOT NULL,
    period_end      DATE NOT NULL,
    gross_amount    NUMERIC(12,2) NOT NULL,
    fees_amount     NUMERIC(12,2) NOT NULL,
    net_amount      NUMERIC(12,2) NOT NULL,
    status          VARCHAR(20) DEFAULT 'PENDING',
    pix_key         VARCHAR(255),
    transfer_id     VARCHAR(255),
    transferred_at  TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- FRETE
-- ============================================================
CREATE TABLE shipments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_group_id  UUID NOT NULL REFERENCES order_groups(id),
    carrier         VARCHAR(50),
    service         VARCHAR(100),
    tracking_code   VARCHAR(100),
    tracking_url    VARCHAR(500),
    estimated_days  INTEGER,
    shipped_at      TIMESTAMPTZ,
    delivered_at    TIMESTAMPTZ,
    status          VARCHAR(30) DEFAULT 'PENDING',
    label_url       VARCHAR(500),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE shipment_tracking (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id UUID NOT NULL REFERENCES shipments(id),
    status      VARCHAR(100) NOT NULL,
    description TEXT,
    location    VARCHAR(255),
    occurred_at TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- CUPONS
-- ============================================================
CREATE TABLE coupons (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    seller_id       UUID REFERENCES sellers(id),
    code            VARCHAR(50) NOT NULL,
    type            VARCHAR(30) NOT NULL
                        CHECK (type IN ('PERCENTAGE','FIXED','FREE_SHIPPING')),
    scope           VARCHAR(30) DEFAULT 'GLOBAL'
                        CHECK (scope IN ('GLOBAL','SELLER','CATEGORY','PRODUCT','FIRST_ORDER')),
    value           NUMERIC(10,2) NOT NULL,
    min_order_value NUMERIC(10,2) DEFAULT 0,
    max_discount    NUMERIC(10,2),
    max_uses        INTEGER,
    used_count      INTEGER DEFAULT 0,
    max_per_user    INTEGER DEFAULT 1,
    starts_at       TIMESTAMPTZ,
    expires_at      TIMESTAMPTZ,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(tenant_id, code)
);

CREATE TABLE coupon_usages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id   UUID NOT NULL REFERENCES coupons(id),
    user_id     UUID NOT NULL REFERENCES users(id),
    order_id    UUID NOT NULL REFERENCES orders(id),
    discount    NUMERIC(10,2) NOT NULL,
    used_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- AVALIAÇÕES
-- ============================================================
CREATE TABLE reviews (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    product_id      UUID NOT NULL REFERENCES products(id),
    order_item_id   UUID REFERENCES order_items(id),
    reviewer_id     UUID NOT NULL REFERENCES users(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    rating          INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    title           VARCHAR(200),
    comment         TEXT,
    pros            TEXT,
    cons            TEXT,
    status          VARCHAR(20) DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','APPROVED','REJECTED','FLAGGED')),
    helpful_count   INTEGER DEFAULT 0,
    images          JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE seller_ratings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id           UUID NOT NULL REFERENCES sellers(id),
    order_id            UUID NOT NULL REFERENCES orders(id),
    reviewer_id         UUID NOT NULL REFERENCES users(id),
    overall_rating      INTEGER NOT NULL CHECK (overall_rating BETWEEN 1 AND 5),
    shipping_rating     INTEGER CHECK (shipping_rating BETWEEN 1 AND 5),
    communication_rating INTEGER CHECK (communication_rating BETWEEN 1 AND 5),
    comment             TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- CHAT
-- ============================================================
CREATE TABLE chats (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    buyer_id        UUID NOT NULL REFERENCES users(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    product_id      UUID REFERENCES products(id),
    order_id        UUID REFERENCES orders(id),
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    last_message_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_messages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_id     UUID NOT NULL REFERENCES chats(id),
    sender_id   UUID NOT NULL REFERENCES users(id),
    content     TEXT,
    type        VARCHAR(20) DEFAULT 'TEXT'
                    CHECK (type IN ('TEXT','IMAGE','FILE','SYSTEM')),
    file_url    VARCHAR(500),
    read_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ
);

-- ============================================================
-- NOTIFICAÇÕES
-- ============================================================
CREATE TABLE notifications (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id),
    user_id     UUID NOT NULL REFERENCES users(id),
    type        VARCHAR(50) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    body        TEXT,
    data        JSONB,
    channel     VARCHAR(20) DEFAULT 'IN_APP'
                    CHECK (channel IN ('IN_APP','EMAIL','PUSH','SMS','WHATSAPP')),
    read_at     TIMESTAMPTZ,
    sent_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- SUPORTE & DISPUTAS
-- ============================================================
CREATE TABLE support_tickets (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    requester_id    UUID NOT NULL REFERENCES users(id),
    order_id        UUID REFERENCES orders(id),
    category        VARCHAR(50) NOT NULL,
    priority        VARCHAR(20) DEFAULT 'NORMAL',
    status          VARCHAR(30) DEFAULT 'OPEN'
                        CHECK (status IN ('OPEN','IN_PROGRESS','WAITING_CUSTOMER',
                                          'WAITING_SELLER','RESOLVED','CLOSED')),
    subject         VARCHAR(500) NOT NULL,
    assigned_to     UUID REFERENCES users(id),
    resolved_at     TIMESTAMPTZ,
    sla_deadline    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE support_messages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID NOT NULL REFERENCES support_tickets(id),
    sender_id   UUID NOT NULL REFERENCES users(id),
    content     TEXT NOT NULL,
    attachments JSONB,
    is_internal BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE disputes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    order_id        UUID NOT NULL REFERENCES orders(id),
    opened_by       UUID NOT NULL REFERENCES users(id),
    seller_id       UUID NOT NULL REFERENCES sellers(id),
    reason          VARCHAR(100) NOT NULL,
    description     TEXT,
    status          VARCHAR(30) DEFAULT 'OPEN'
                        CHECK (status IN ('OPEN','UNDER_REVIEW','RESOLVED_BUYER',
                                          'RESOLVED_SELLER','ESCALATED','CLOSED')),
    resolution      TEXT,
    resolved_by     UUID REFERENCES users(id),
    resolved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- ANTIFRAUDE
-- ============================================================
CREATE TABLE fraud_analysis (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    order_id        UUID REFERENCES orders(id),
    user_id         UUID REFERENCES users(id),
    ip_address      INET,
    device_id       VARCHAR(255),
    risk_score      NUMERIC(5,2),
    decision        VARCHAR(20) CHECK (decision IN ('APPROVE','REVIEW','REJECT')),
    triggers        JSONB,
    gateway_id      VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE security_blacklist (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID REFERENCES tenants(id),
    type        VARCHAR(20) NOT NULL CHECK (type IN ('IP','EMAIL','CPF','DEVICE')),
    value       VARCHAR(255) NOT NULL,
    reason      TEXT,
    expires_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- OUTBOX EVENTS (Event-driven / Saga pattern)
-- ============================================================
CREATE TABLE outbox_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type  VARCHAR(100) NOT NULL,
    aggregate_id    UUID NOT NULL,
    event_type      VARCHAR(150) NOT NULL,
    payload         JSONB NOT NULL,
    tenant_id       UUID REFERENCES tenants(id),
    idempotency_key VARCHAR(200) UNIQUE,
    status          VARCHAR(20) DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','PROCESSING','SENT','FAILED','DEAD_LETTER')),
    retry_count     INTEGER DEFAULT 0,
    max_retries     INTEGER DEFAULT 5,
    last_error      TEXT,
    scheduled_at    TIMESTAMPTZ DEFAULT NOW(),
    processed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- AUDITORIA
-- ============================================================
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID REFERENCES tenants(id),
    user_id     UUID REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    resource    VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100),
    old_value   JSONB,
    new_value   JSONB,
    ip_address  INET,
    user_agent  TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE security_events (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID REFERENCES tenants(id),
    user_id     UUID REFERENCES users(id),
    event_type  VARCHAR(100) NOT NULL,
    severity    VARCHAR(20) NOT NULL DEFAULT 'INFO',
    description TEXT,
    ip_address  INET,
    user_agent  TEXT,
    metadata    JSONB,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- CMS
-- ============================================================
CREATE TABLE banners (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id),
    title       VARCHAR(255),
    image_url   VARCHAR(500) NOT NULL,
    link_url    VARCHAR(500),
    position    VARCHAR(50) DEFAULT 'HOME_TOP',
    starts_at   TIMESTAMPTZ,
    ends_at     TIMESTAMPTZ,
    sort_order  INTEGER DEFAULT 0,
    is_active   BOOLEAN DEFAULT TRUE,
    locale      VARCHAR(10) DEFAULT 'pt-BR',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE cms_pages (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id),
    slug        VARCHAR(200) NOT NULL,
    title       VARCHAR(500) NOT NULL,
    content     TEXT,
    locale      VARCHAR(10) DEFAULT 'pt-BR',
    is_active   BOOLEAN DEFAULT TRUE,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(tenant_id, slug, locale)
);

-- ============================================================
-- WISHLIST / FAVORITOS
-- ============================================================
CREATE TABLE wishlists (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id),
    product_id  UUID NOT NULL REFERENCES products(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, product_id)
);

-- ============================================================
-- ÍNDICES CRÍTICOS
-- ============================================================
CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_users_email ON users(tenant_id, email);
CREATE INDEX idx_products_tenant_status ON products(tenant_id, status);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_seller ON products(seller_id);
CREATE INDEX idx_products_slug ON products(tenant_id, slug);
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_tenant_status ON orders(tenant_id, status);
CREATE INDEX idx_order_groups_seller ON order_groups(seller_id);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_pix_charges_txid ON pix_charges(txid);
CREATE INDEX idx_outbox_status ON outbox_events(status, scheduled_at);
CREATE INDEX idx_notifications_user ON notifications(user_id, read_at);
CREATE INDEX idx_chats_buyer ON chats(buyer_id);
CREATE INDEX idx_chats_seller ON chats(seller_id);
CREATE INDEX idx_audit_logs_tenant ON audit_logs(tenant_id, created_at);
CREATE INDEX idx_products_fts ON products USING gin(to_tsvector('portuguese', name || ' ' || COALESCE(description, '')));

-- ============================================================
-- SEED: Roles e Permissions padrão
-- ============================================================
INSERT INTO roles (id, name, description, is_system) VALUES
  (gen_random_uuid(), 'SUPER_ADMIN', 'Administrador global da plataforma', TRUE),
  (gen_random_uuid(), 'TENANT_ADMIN', 'Administrador do tenant', TRUE),
  (gen_random_uuid(), 'SELLER', 'Vendedor', TRUE),
  (gen_random_uuid(), 'BUYER', 'Comprador', TRUE),
  (gen_random_uuid(), 'SUPPORT', 'Operador de suporte', TRUE);
