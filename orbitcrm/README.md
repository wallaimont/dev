# OrbitCRM 🌌

OrbitCRM is an original multi-tenant SaaS CRM starter focused on B2B sales, service, operations and management. It ships with a professional monorepo, modern dashboard UI, NestJS API, Prisma schema, Docker stack and demo seed ready for local evolution.

## Stack

### Frontend
- Next.js 14
- TypeScript
- Tailwind CSS
- shared UI kit inspired by shadcn/ui patterns
- TanStack Query
- React Hook Form + Zod
- Recharts

### Backend
- NestJS
- TypeScript
- Prisma ORM
- PostgreSQL
- Redis + BullMQ-ready architecture
- JWT + Refresh Token
- Swagger/OpenAPI

### Infra
- Docker + Docker Compose
- Nginx reverse proxy
- MinIO (S3-compatible)
- MailHog for local mail testing
- Stripe-ready billing module abstraction

---

## Folder tree

```text
orbitcrm/
  apps/
    api/
    web/
  packages/
    config/
    types/
    ui/
    utils/
  prisma/
  infra/
    docker/
    nginx/
    scripts/
  docs/
  .env.example
  docker-compose.yml
  README.md
```

---

## Core modules delivered

- Authentication (`/auth/login`, `/auth/refresh`, `/auth/register`, `/auth/forgot-password`, `/auth/reset-password`, `/auth/me`)
- Multi-tenant foundation with `tenant_id`
- Users / roles / permissions foundation
- Leads CRUD + conversion endpoint
- Accounts CRUD
- Contacts CRUD
- Opportunities CRUD + stage update
- Activities CRUD
- Tickets CRUD + assign/status routes
- Automation rules CRUD
- Reports endpoints
- Audit logs endpoint
- Billing, uploads and notifications scaffolding
- Landing page, pricing, features and private dashboard UI
- Pipeline kanban page and demo seed data

---

## Demo credentials

| Perfil | E-mail | Senha |
|---|---|---|
| Admin | `admin@orbitcrm.demo` | `Admin@123` |
| Sales | `rafa@orbitcrm.demo` | `Sales@123` |
| Sales | `bia@orbitcrm.demo` | `Sales@123` |
| Sales | `carlos@orbitcrm.demo` | `Sales@123` |

---

## How to run locally

### 1) Copy environment

```bash
cp .env.example .env
```

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### 2) Start infrastructure

```bash
docker compose up -d postgres redis minio mailhog
```

### 3) Install dependencies

```bash
npm install
```

### 4) Prepare the database

```bash
npm run db:generate
npm run db:push
npm run db:seed
```

### 5) Start the apps

```bash
npm run dev
```

### Local URLs

- Web: `http://localhost:3000`
- API: `http://localhost:3001`
- Swagger: `http://localhost:3001/docs`
- MinIO Console: `http://localhost:9001`
- MailHog: `http://localhost:8025`
- Nginx gateway: `http://localhost:8080`

---

## Production next steps

1. Add robust background workers for BullMQ.
2. Implement full Stripe Checkout + webhook persistence.
3. Add MFA, invite flows and advanced RBAC UI.
4. Expand audit logging with interceptors and event bus.
5. Add automated tests (unit, integration, e2e).
6. Add observability, metrics and deployment pipelines.
7. Replace demo UI data with real API mutations everywhere.
8. Harden S3 upload flows with signed URLs and antivirus scanning.

---

## Notes

- The project is intentionally structured as a commercial-ready foundation, not a copied clone.
- Design, naming and code were created with an original OrbitCRM identity.
- Where external integrations are not fully wired, a clean abstraction/mocked preparation layer is already included for professional expansion.
