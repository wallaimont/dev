# Orbyt Market

Orbyt Market e uma plataforma enterprise de marketplace multi-tenant, multi-store e multi-seller desenhada para operacao real em escala, com base pronta para evolucao para microservicos, aplicacoes mobile e operacao internacional.

## Etapas Entregues

1. Branding, posicionamento e identidade em [docs/product-blueprint.md](docs/product-blueprint.md)
2. Arquitetura recomendada em [docs/architecture.md](docs/architecture.md)
3. Modelagem de dados em [docs/database.md](docs/database.md)
4. Endpoints e fluxos em [docs/api.md](docs/api.md) e [docs/flows.md](docs/flows.md)
5. Backend Spring Boot modular em [backend](backend)
6. Frontend Next.js com fluxo buyer/seller/admin em [frontend](frontend)
7. App mobile com multiplas telas em [mobile](mobile)
8. Infra local com Docker, Nginx, Prometheus e Grafana em [infra](infra)
9. Roadmap de evolucao em [docs/roadmap.md](docs/roadmap.md)

## Decisao Arquitetural

O projeto adota monolito modular como estrategia principal para MVP enterprise:

- reduz custo e complexidade operacional inicial
- preserva separacao por bounded context
- facilita consistencia transacional em checkout, pedido e pagamento
- prepara cada modulo para futura extracao em microservicos via eventos, outbox e contratos claros

## Como Rodar

Copie `.env.example` para `.env`.

```bash
docker compose -f infra/docker-compose.yml up --build
```

Servicos adicionais de observabilidade:

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001 (admin/admin)

## Observacoes

- A base e comercial e original, pronta para crescimento incremental por squads.
- Integracoes externas estao preparadas por contratos, sem acoplamento a um unico provedor.
- Seed inicial automatico:
  - tenant: `orbyt-demo`
  - usuario vendedor/admin: `admin@orbyt.local`
  - senha: `Admin@123`
- RBAC por permissoes reais:
  - `admin.dashboard.view`
  - `catalog.manage`
  - `users.manage`
  - `platform.tenants.manage`
- Testes de integracao com Testcontainers:
  - rodam com Postgres em container quando Docker esta disponivel
  - sao ignorados automaticamente quando Docker nao esta disponivel
