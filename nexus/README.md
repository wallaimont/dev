# NEXUS Marketplace Platform

> Enterprise Multi-tenant Marketplace — Java 21 · Spring Boot 3 · Next.js 14 · PostgreSQL · Kafka · PIX-first

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6db33f)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14-black)](https://nextjs.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)](https://www.postgresql.org)
[![License](https://img.shields.io/badge/license-Commercial-blue)](#)

---

## Visão Geral

NEXUS é uma plataforma de marketplace enterprise completa, pronta para produção, com:

- **Multi-tenant nativo** — cada cliente tem seu domínio, branding e regras de negócio
- **PIX-first** — integração completa com geradores de cobrança PIX (Pagar.me / Gerencianet)
- **Split payment automático** — divisão automática entre vendedores e plataforma
- **Event-driven** — outbox pattern, Kafka, idempotência, DLQ
- **Monólito modular** — 11 bounded contexts, extração em microserviços sem retrabalho
- **LGPD-compliant** — auditoria, consentimento, retenção de dados

---

## Stack

| Camada | Tecnologias |
|--------|-------------|
| Backend | Java 21, Spring Boot 3.3, Spring Security, JPA/Hibernate, Flyway |
| Frontend | Next.js 14, TypeScript, Tailwind CSS, TanStack Query, Zustand |
| Banco | PostgreSQL 16, Redis 7.2 |
| Eventos | Kafka (Confluent), RabbitMQ (opcional) |
| Auth | JWT + Refresh Token (JJWT 0.12), BCrypt |
| Pagamentos | Pagar.me, Gerencianet/EFI (PIX), split automático |
| DevOps | Docker, Docker Compose, Nginx, GitHub Actions |
| Obs. | Actuator, Micrometer, OpenTelemetry, structured logs |

---

## Setup Rápido

### Pré-requisitos
- Docker 24+
- Docker Compose 2.20+
- Java 21 (para desenvolvimento local)
- Node.js 20+ (para desenvolvimento local)

### 1. Clone e configure
```bash
git clone https://github.com/seu-org/nexus-marketplace.git
cd nexus-marketplace
cp .env.example .env
# Edite .env com suas credenciais reais
```

### 2. Suba toda a stack com Docker
```bash
# Com serviços de dev (Kafka UI, MailHog)
docker compose --profile dev up -d

# Produção
docker compose up -d
```

### 3. Acesse
| Serviço | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Kafka UI | http://localhost:8081 |
| MailHog | http://localhost:8025 |

### 4. Desenvolvimento local (sem Docker)

**Backend:**
```bash
cd backend
# Garanta que PostgreSQL e Redis estão rodando
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

---

## Arquitetura

### Estrutura de Pastas

```
nexus/
├── backend/
│   └── src/main/java/com/nexus/
│       ├── config/                 # Security, OpenAPI, Kafka, Redis configs
│       ├── shared/
│       │   ├── domain/             # BaseEntity, auditoria
│       │   ├── events/             # DomainEvent, OutboxEventPublisher
│       │   ├── security/           # JwtService, JwtFilter, NexusPrincipal
│       │   ├── tenant/             # TenantContext, TenantFilter
│       │   └── exceptions/         # GlobalExceptionHandler
│       └── modules/
│           ├── auth/               # Login, JWT, tokens, email verification
│           ├── user/               # Perfis, endereços, preferências
│           ├── seller/             # Cadastro, aprovação, lojas, KYB
│           ├── catalog/            # Produtos, categorias, marcas, estoque
│           ├── cart/               # Carrinho persistente + sessão
│           ├── order/              # Pedidos, subpedidos, status, cancelamento
│           ├── payment/            # PIX, cartão, boleto, split, webhook
│           ├── shipping/           # Frete, rastreamento, transportadoras
│           ├── review/             # Avaliações de produto e vendedor
│           ├── chat/               # WebSocket, histórico, moderação
│           ├── notification/       # Email, push, in-app, WhatsApp
│           ├── coupon/             # Cupons, regras, uso
│           ├── support/            # Tickets, disputas, SLA
│           └── admin/              # KPIs, GMV, gestão de tenants
├── frontend/
│   └── src/
│       ├── app/
│       │   ├── (public)/           # Homepage, catálogo, produto, loja
│       │   ├── (buyer)/            # Pedidos, favoritos, chat, perfil
│       │   ├── (seller)/           # Produtos, financeiro, analytics
│       │   └── (admin)/            # Dashboard, aprovações, configurações
│       ├── components/
│       │   ├── ui/                 # Design system: Button, Input, Card...
│       │   └── features/           # Componentes de domínio
│       ├── lib/
│       │   ├── api/                # Axios client, interceptors, queries
│       │   └── utils/              # formatCurrency, formatDate, cn()
│       └── store/                  # Zustand: cart, auth, ui
├── infra/
│   ├── nginx/                      # nginx.conf, certs
│   └── k8s/                        # Kubernetes manifests (futuro)
├── docs/                           # Documentação adicional
├── docker-compose.yml
└── .env.example
```

---

## Fluxos Principais

### Fluxo de Autenticação

```
POST /api/v1/auth/register
  → validar tenant (X-Tenant-ID header)
  → verificar email único
  → hashear senha (BCrypt 12)
  → criar user + profile + role BUYER
  → enviar e-mail de verificação
  → retornar access_token + refresh_token

POST /api/v1/auth/login
  → validar credenciais
  → verificar status da conta
  → gerar JWT access token (15min)
  → persistir refresh token (hash SHA-256, 30 dias)
  → log de auditoria + evento de segurança
  → retornar tokens + roles + userId

POST /api/v1/auth/refresh
  → validar refresh token hash
  → verificar não revogado + não expirado
  → revogar token atual (rotação)
  → gerar novo par de tokens
  → retornar novos tokens
```

### Fluxo de Checkout

```
POST /api/v1/orders/checkout
  1. Recuperar carrinho ativo do usuário
  2. Validar stock de todos os itens (reserva pessimista)
  3. Agrupar itens por vendedor (order_groups)
  4. Para cada grupo:
     - calcular subtotal
     - calcular frete via ShippingService
     - calcular comissão (tenant_settings.commission_rate)
     - calcular valor do vendedor
  5. Aplicar cupom (se houver)
  6. Criar Order + OrderGroups + OrderItems
  7. Persistir evento OrderCreated no outbox
  8. Converter carrinho para status CONVERTED
  9. Retornar orderId + total
```

### Fluxo PIX (End-to-End)

```
POST /api/v1/orders/{orderId}/payments/pix
  [Header: Idempotency-Key: uuid]
  
  1. Verificar idempotência (já existe?) → retornar existente
  2. Criar Payment record (status=PENDING)
  3. Chamar gateway (Gerencianet/EFI):
     - POST /v2/cob → gerar cobrança PIX
     - receber txid + qrcode + pixCopyPaste
  4. Persistir PixCharge
  5. Retornar QR Code + Copy-Paste + expiry (30min)

[Webhook assíncrono do gateway]
POST /api/v1/webhooks/gerencianet
  1. Validar assinatura HMAC do webhook
  2. Verificar idempotência (evento já processado?)
  3. Extrair txid do payload
  4. Encontrar PixCharge pelo txid
  5. Atualizar PixCharge.status = COMPLETED
  6. Atualizar Payment.status = PAID
  7. Publicar PaymentApprovedEvent no outbox
  
[OrderService escuta PaymentApprovedEvent]
  8. Confirmar pedido (status = CONFIRMED)
  9. Publicar OrderConfirmedEvent
  10. NotificationService envia e-mail + push
  11. SellerService notifica vendedores
```

### Split Payment

```
Ao confirmar pagamento:
  Para cada OrderGroup:
    total_do_grupo = subtotal + frete
    commission_rate = tenant_settings OU seller.commission_rate
    platform_fee = total * commission_rate
    seller_amount = total - platform_fee
    
    PaymentSplit:
      payment_id → referência ao pagamento
      seller_id  → vendedor
      seller_amount → valor a transferir
      platform_fee  → comissão retida
      recipient_key → chave PIX do vendedor
      status = PENDING

[Job de repasse (agendado)]
  → buscar splits PENDING dos últimos 7 dias
  → para cada split: transferir via gateway
  → atualizar status = TRANSFERRED
  → criar SellerPayout record
```

### Arquitetura de Eventos

```
Evento               → Tópico Kafka              → Consumidores
─────────────────────────────────────────────────────────────
OrderCreated         → nexus.orders              → notification, analytics
PaymentApproved      → nexus.payments            → order (confirma), notification
PaymentFailed        → nexus.payments            → order (cancela), notification  
OrderConfirmed       → nexus.orders              → seller notification
OrderShipped         → nexus.orders              → buyer notification, analytics
OrderDelivered       → nexus.orders              → review request, payout unlock
SellerApproved       → nexus.sellers             → notification, email
ProductApproved      → nexus.catalog             → notification, search index
RefundRequested      → nexus.refunds             → admin notification, dispute
ChatMessageReceived  → nexus.chat                → push notification

Padrões implementados:
  ✓ Outbox Pattern — evento persistido junto com a transação de negócio
  ✓ Idempotência — idempotency_key evita duplicatas
  ✓ Retry — até 5 tentativas com backoff exponencial
  ✓ Dead Letter Queue — eventos falhos para análise
  ✓ At-least-once delivery — consumidores idempotentes
```

---

## Multi-tenant

Cada tenant é isolado logicamente via `tenant_id`:

```
Header obrigatório: X-Tenant-ID: {tenant-slug}

TenantFilter (Spring Filter):
  1. Extrai X-Tenant-ID do header (ou subdomínio)
  2. Resolve tenant no banco/cache Redis
  3. Seta TenantContext (ThreadLocal)
  4. JpaAuditing usa TenantContext para filtros automáticos

Configurações por tenant (tenant_settings):
  - commission_rate (ex: 0.10 = 10%)
  - auto_approve_sellers
  - gateway de pagamento
  - logo, cores primárias/secundárias
  - domínio personalizado
  - locale e currency padrão
  - SMTP próprio
```

---

## Segurança

- **JWT** com assinatura HS256, TTL curto (15min) + Refresh Token (30 dias)
- **Refresh Token Rotation** — token invalidado a cada uso, novo gerado
- **BCrypt cost 12** para hashes de senha
- **RBAC granular** — roles: SUPER_ADMIN, TENANT_ADMIN, SELLER, BUYER, SUPPORT
- **Rate Limiting** — via Nginx (auth: 10/min, api: 60/min)
- **Webhook Signature** — validação HMAC de todos webhooks de pagamento
- **Idempotência de pagamentos** — header obrigatório Idempotency-Key
- **Auditoria completa** — toda ação sensível em audit_logs
- **SQL Injection** — apenas JPA/queries parametrizadas, zero SQL raw
- **XSS** — headers de segurança Nginx + sanitização de inputs
- **LGPD** — consentimento, deletação de conta, portabilidade de dados

---

## API Reference (principais endpoints)

### Auth
```
POST   /api/v1/auth/register          Cadastro
POST   /api/v1/auth/login             Login
POST   /api/v1/auth/refresh           Renovar tokens
POST   /api/v1/auth/logout            Revogar refresh token
GET    /api/v1/auth/verify-email      Verificar e-mail
POST   /api/v1/auth/forgot-password   Esqueci a senha
POST   /api/v1/auth/reset-password    Redefinir senha
```

### Catalog (público)
```
GET    /api/v1/products               Busca/listagem com filtros
GET    /api/v1/products/{slug}        Detalhe do produto
GET    /api/v1/categories             Lista de categorias
GET    /api/v1/stores/{slug}          Página pública da loja
```

### Orders
```
POST   /api/v1/orders/checkout        Criar pedido (checkout)
GET    /api/v1/orders                 Meus pedidos (buyer)
GET    /api/v1/orders/{id}            Detalhe do pedido
POST   /api/v1/orders/{id}/cancel     Cancelar pedido
GET    /api/v1/orders/seller          Pedidos como vendedor
POST   /api/v1/orders/groups/{id}/ship Marcar como enviado
```

### Payments
```
POST   /api/v1/orders/{id}/payments/pix    Gerar PIX
GET    /api/v1/payments/pix/{txid}         Status do PIX
POST   /api/v1/orders/{id}/payments/card   Pagar com cartão
POST   /api/v1/orders/{id}/payments/boleto Gerar boleto
POST   /api/v1/payments/{id}/refund        Solicitar reembolso
POST   /api/v1/webhooks/pagarme            Webhook Pagar.me
POST   /api/v1/webhooks/gerencianet        Webhook Gerencianet
```

Documentação interativa completa em: `GET /swagger-ui.html`

---

## Roadmap — Etapa 20

### v1.1 (próximos 3 meses)
- [ ] Elasticsearch para busca avançada
- [ ] App mobile React Native (área do comprador)
- [ ] Dashboard de analytics avançado (vendedor)
- [ ] Integração Melhor Envio completa
- [ ] Programa de fidelidade/pontos

### v1.2 (3-6 meses)
- [ ] Recomendação por IA (collaborative filtering)
- [ ] Live commerce (streaming + vendas)
- [ ] B2B marketplace mode (pedidos mínimos, tabela de preço)
- [ ] Multi-store por vendedor
- [ ] White-label mobile app por tenant

### v2.0 (6-12 meses — extração em microserviços)
- [ ] payment-service → processo de pagamento isolado
- [ ] catalog-service → com Elasticsearch dedicado
- [ ] notification-service → fila dedicada + templates
- [ ] chat-service → WebSocket em serviço próprio
- [ ] API Gateway (Kong ou AWS API Gateway)
- [ ] Kubernetes (Helm charts prontos)

---

## Licença

Uso comercial — contato: contato@nexus.com.br

---

**NEXUS Marketplace Platform** — Construído para escalar do MVP ao unicórnio. 🚀
