-- V4: Product variants, attributes, user profiles, seller documents, wishlists

-- Product variants
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS product_id UUID;
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS sku VARCHAR(100);
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS variant_name VARCHAR(200);
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS price NUMERIC(15,2);
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS promotional_price NUMERIC(15,2);
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS stock_quantity INT DEFAULT 0;
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS attributes JSONB DEFAULT '{}';
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS weight_grams INT;
ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS barcode VARCHAR(50);

-- Product attributes (dynamic)
CREATE TABLE IF NOT EXISTS product_attribute_values (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    product_id UUID NOT NULL,
    attribute_id UUID NOT NULL,
    value TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Seller documents
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS document_type VARCHAR(50);
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS document_number VARCHAR(50);
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS file_url TEXT;
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS verification_status VARCHAR(30) DEFAULT 'PENDING';
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS verified_at TIMESTAMPTZ;
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS verified_by UUID;
ALTER TABLE seller_documents ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

-- User profiles
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS user_id UUID UNIQUE;
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS cpf VARCHAR(14);
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS cnpj VARCHAR(18);
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS birth_date DATE;
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS avatar_url TEXT;
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS preferred_language VARCHAR(5) DEFAULT 'pt-BR';
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS preferred_currency VARCHAR(3) DEFAULT 'BRL';
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS bio TEXT;
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS notification_email BOOLEAN DEFAULT TRUE;
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS notification_push BOOLEAN DEFAULT TRUE;
ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS notification_sms BOOLEAN DEFAULT FALSE;

-- User addresses
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS label VARCHAR(50) DEFAULT 'Casa';
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS recipient_name VARCHAR(200);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS street VARCHAR(300);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS number VARCHAR(20);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS complement VARCHAR(100);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS neighborhood VARCHAR(150);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS city VARCHAR(150);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS state VARCHAR(2);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS zip_code VARCHAR(10);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS country VARCHAR(3) DEFAULT 'BRA';
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS is_default BOOLEAN DEFAULT FALSE;
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS latitude NUMERIC(10,7);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS longitude NUMERIC(10,7);

-- Wishlists
CREATE TABLE IF NOT EXISTS wishlists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(tenant_id, user_id, product_id)
);

-- Recently viewed
CREATE TABLE IF NOT EXISTS recently_viewed (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    viewed_at TIMESTAMPTZ DEFAULT now()
);

-- Product Q&A
CREATE TABLE IF NOT EXISTS product_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    question TEXT NOT NULL,
    answer TEXT,
    answered_by UUID,
    answered_at TIMESTAMPTZ,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Expand stores
ALTER TABLE stores ADD COLUMN IF NOT EXISTS banner_url TEXT;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS policies TEXT;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS social_instagram VARCHAR(100);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS social_facebook VARCHAR(100);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS social_website VARCHAR(200);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS rating_avg NUMERIC(3,2) DEFAULT 0;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS rating_count INT DEFAULT 0;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS level VARCHAR(20) DEFAULT 'BRONZE';
ALTER TABLE stores ADD COLUMN IF NOT EXISTS is_premium BOOLEAN DEFAULT FALSE;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS return_policy TEXT;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS shipping_from_city VARCHAR(100);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS shipping_from_state VARCHAR(2);

-- Expand products
ALTER TABLE products ADD COLUMN IF NOT EXISTS brand_id UUID;
ALTER TABLE products ADD COLUMN IF NOT EXISTS weight_grams INT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS dimensions_cm VARCHAR(50);
ALTER TABLE products ADD COLUMN IF NOT EXISTS meta_title VARCHAR(200);
ALTER TABLE products ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500);
ALTER TABLE products ADD COLUMN IF NOT EXISTS slug VARCHAR(300);
ALTER TABLE products ADD COLUMN IF NOT EXISTS tags TEXT[];
ALTER TABLE products ADD COLUMN IF NOT EXISTS rating_avg NUMERIC(3,2) DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS rating_count INT DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS sold_count INT DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Categories expand
ALTER TABLE categories ADD COLUMN IF NOT EXISTS parent_id UUID;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS slug VARCHAR(200);
ALTER TABLE categories ADD COLUMN IF NOT EXISTS icon_url TEXT;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS display_order INT DEFAULT 0;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_product_variants_product_id ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_product_attribute_values_product_id ON product_attribute_values(product_id);
CREATE INDEX IF NOT EXISTS idx_wishlists_user_id ON wishlists(user_id);
CREATE INDEX IF NOT EXISTS idx_recently_viewed_user_id ON recently_viewed(user_id);
CREATE INDEX IF NOT EXISTS idx_product_questions_product_id ON product_questions(product_id);
CREATE INDEX IF NOT EXISTS idx_products_slug ON products(slug);
CREATE INDEX IF NOT EXISTS idx_products_brand_id ON products(brand_id);
CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_categories_slug ON categories(slug);
CREATE INDEX IF NOT EXISTS idx_seller_documents_seller_id ON seller_documents(seller_id);
CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
