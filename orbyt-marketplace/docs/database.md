# Modelagem de Banco

## Diretrizes

- chaves primarias `uuid`
- `tenant_id` em entidades multi-tenant
- `status`, `created_at`, `updated_at`, `created_by`, `updated_by`
- `deleted_at` quando houver soft delete

## Tabelas Principais

- `tenants`
- `tenant_settings`
- `users`
- `roles`
- `permissions`
- `user_roles`
- `role_permissions`
- `user_profiles`
- `user_addresses`
- `sellers`
- `seller_documents`
- `stores`
- `categories`
- `brands`
- `product_attributes`
- `product_attribute_values`
- `products`
- `product_images`
- `product_videos`
- `product_variants`
- `stock_items`
- `carts`
- `cart_items`
- `order_groups`
- `orders`
- `order_items`
- `order_status_history`
- `shipments`
- `shipment_tracking`
- `payments`
- `payment_transactions`
- `payment_webhooks`
- `pix_charges`
- `refunds`
- `seller_payouts`
- `commissions`
- `coupons`
- `coupon_rules`
- `coupon_usages`
- `reviews`
- `seller_ratings`
- `chats`
- `chat_messages`
- `notifications`
- `support_tickets`
- `support_messages`
- `disputes`
- `banners`
- `cms_pages`
- `audit_logs`
- `security_events`
- `fraud_analysis`
- `integrations`
- `webhooks_outbox`
- `outbox_events`
