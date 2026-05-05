# Endpoints Principais

## Auth

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

## Catalogo

- `GET /api/v1/catalog/products`
- `POST /api/v1/catalog/products`
- `GET /api/v1/catalog/products/{productId}`
- `PATCH /api/v1/catalog/products/{productId}`
- `DELETE /api/v1/catalog/products/{productId}`
- `GET /api/v1/catalog/stores`
- `GET /api/v1/catalog/stores/{storeId}`
- `POST /api/v1/catalog/stores`
- `PATCH /api/v1/catalog/stores/{storeId}`
- `DELETE /api/v1/catalog/stores/{storeId}`

## Usuarios

- `GET /api/v1/users`
- `GET /api/v1/users/{userId}`
- `POST /api/v1/users`
- `PATCH /api/v1/users/{userId}`
- `DELETE /api/v1/users/{userId}`

## Tenant / Plataforma

- `GET /api/v1/platform/tenants`
- `GET /api/v1/platform/tenants/{tenantId}`
- `POST /api/v1/platform/tenants`
- `PATCH /api/v1/platform/tenants/{tenantId}`
- `DELETE /api/v1/platform/tenants/{tenantId}`

## Checkout

- `POST /api/v1/checkout/preview`
- `POST /api/v1/checkout`

## Pedidos

- `GET /api/v1/orders/my?buyerId={buyerId}`
- `GET /api/v1/orders/seller?sellerId={sellerId}`
- `GET /api/v1/orders/{orderId}`
- `GET /api/v1/orders/groups/{orderGroupId}`
- `PATCH /api/v1/orders/{orderId}/status`
- `POST /api/v1/orders/{orderId}/cancel`
- `GET /api/v1/orders/{orderId}/history`

## CMS

- `GET /api/v1/cms/banners?position=HOME_HERO`
- `POST /api/v1/cms/banners`
- `GET /api/v1/cms/pages/{slug}?lang=pt-BR`
- `POST /api/v1/cms/pages`
- `PUT /api/v1/cms/pages/{pageId}`

## Pagamentos

- `POST /api/v1/payments/pix/charges`
- `POST /api/v1/payments/webhooks/{provider}`

## Admin

- `GET /api/v1/admin/dashboard`

## Chat

- `GET /api/v1/chats`
- `POST /api/v1/chats/{chatId}/messages`
