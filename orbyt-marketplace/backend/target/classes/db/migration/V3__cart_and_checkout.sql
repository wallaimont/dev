-- V3: Cart, Checkout, Order Groups, Order Items, Shipments, Coupons

-- Expand carts with session support
ALTER TABLE carts ADD COLUMN IF NOT EXISTS session_id VARCHAR(128);
ALTER TABLE carts ADD COLUMN IF NOT EXISTS currency_code VARCHAR(3) DEFAULT 'BRL';
ALTER TABLE carts ADD COLUMN IF NOT EXISTS coupon_id UUID;
ALTER TABLE carts ADD COLUMN IF NOT EXISTS subtotal NUMERIC(15,2) DEFAULT 0;
ALTER TABLE carts ADD COLUMN IF NOT EXISTS discount NUMERIC(15,2) DEFAULT 0;
ALTER TABLE carts ADD COLUMN IF NOT EXISTS shipping_total NUMERIC(15,2) DEFAULT 0;
ALTER TABLE carts ADD COLUMN IF NOT EXISTS total NUMERIC(15,2) DEFAULT 0;

-- Expand cart_items
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS variant_id UUID;
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS unit_price NUMERIC(15,2);
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS subtotal NUMERIC(15,2);

-- Order groups (one purchase = one order group)
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS buyer_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS currency_code VARCHAR(3) DEFAULT 'BRL';
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS subtotal NUMERIC(15,2) DEFAULT 0;
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS shipping_total NUMERIC(15,2) DEFAULT 0;
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS discount_total NUMERIC(15,2) DEFAULT 0;
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS grand_total NUMERIC(15,2) DEFAULT 0;
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS coupon_id UUID;
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS shipping_address_id UUID;
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS payment_method VARCHAR(30);
ALTER TABLE order_groups ADD COLUMN IF NOT EXISTS notes TEXT;

-- Expand orders (suborder per seller)
ALTER TABLE orders ADD COLUMN IF NOT EXISTS order_group_id UUID;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS seller_id UUID;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS store_id UUID;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS buyer_id UUID;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS currency_code VARCHAR(3) DEFAULT 'BRL';
ALTER TABLE orders ADD COLUMN IF NOT EXISTS subtotal NUMERIC(15,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_cost NUMERIC(15,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount NUMERIC(15,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS total NUMERIC(15,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS commission_rate NUMERIC(5,4) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS commission_amount NUMERIC(15,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS seller_net NUMERIC(15,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_method VARCHAR(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS tracking_code VARCHAR(100);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS estimated_delivery DATE;

-- Order items
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS order_id UUID;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS product_id UUID;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS variant_id UUID;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS sku VARCHAR(100);
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS product_name VARCHAR(500);
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS quantity INT DEFAULT 1;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS unit_price NUMERIC(15,2);
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS subtotal NUMERIC(15,2);

-- Expand coupon rules
ALTER TABLE coupon_rules ADD COLUMN IF NOT EXISTS rule_type VARCHAR(50);
ALTER TABLE coupon_rules ADD COLUMN IF NOT EXISTS rule_value TEXT;

-- Coupon usages
ALTER TABLE coupon_usages ADD COLUMN IF NOT EXISTS coupon_id UUID;
ALTER TABLE coupon_usages ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE coupon_usages ADD COLUMN IF NOT EXISTS order_group_id UUID;
ALTER TABLE coupon_usages ADD COLUMN IF NOT EXISTS used_at TIMESTAMPTZ DEFAULT now();

-- Shipment tracking
ALTER TABLE shipment_tracking ADD COLUMN IF NOT EXISTS shipment_id UUID;
ALTER TABLE shipment_tracking ADD COLUMN IF NOT EXISTS tracking_status VARCHAR(50);
ALTER TABLE shipment_tracking ADD COLUMN IF NOT EXISTS location VARCHAR(200);
ALTER TABLE shipment_tracking ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE shipment_tracking ADD COLUMN IF NOT EXISTS occurred_at TIMESTAMPTZ;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_carts_user_id ON carts(user_id);
CREATE INDEX IF NOT EXISTS idx_carts_session_id ON carts(session_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_order_groups_buyer_id ON order_groups(buyer_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_group_id ON orders(order_group_id);
CREATE INDEX IF NOT EXISTS idx_orders_seller_id ON orders(seller_id);
CREATE INDEX IF NOT EXISTS idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_coupon_usages_coupon_id ON coupon_usages(coupon_id);
