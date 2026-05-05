# OrbitCRM Architecture

## Overview
OrbitCRM is a multi-tenant SaaS CRM monorepo organized for commercial evolution.

### Layers
- `apps/web`: Next.js frontend with protected routes, landing page and CRM workspace UI.
- `apps/api`: NestJS REST API with JWT auth, RBAC hooks, Swagger and tenant-aware services.
- `packages/*`: shared UI, types, config and helpers.
- `prisma/`: canonical data model and seed script.
- `infra/`: Docker, Nginx and local scripts.

## Multi-tenant strategy
- All core entities carry `tenant_id`.
- JWT payload stores `tenantId` and permission scopes.
- Controllers and services always scope database queries by tenant.
- Plans, billing and settings are linked to the tenant.

## Security baseline
- Password hashing with `bcryptjs`
- Access + refresh tokens
- Route protection via `JwtAuthGuard`
- Permission metadata via `Permissions()` decorator
- Soft delete on critical records
- Helmet + CORS + validation pipe in API bootstrap

## Async/readiness
- Redis/BullMQ dependencies are included for future email, notification and automation workers.
- S3-compatible uploads are abstracted for MinIO or production object storage.
- Stripe billing module is scaffolded for checkout/webhook expansion.
