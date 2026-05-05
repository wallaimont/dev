# PR Assistant AI

Assistente inteligente de revisão de Pull Requests com análise automatizada por regras e IA.

## Arquitetura

```
┌──────────────────┐        ┌──────────────────────────────┐
│  Angular 17 SPA  │──API──▶│  Spring Boot 3.3 (Java 21)   │
│  Material Design │        │  REST + WebFlux WebClient     │
│  Signals + OnPush│        ├──────────────────────────────┤
└──────────────────┘        │ SecurityConfig (JWT HS512)    │
                            │ Rule Engine → Critical Files  │
                            │ AI Provider → OpenAI / Mock   │
                            │ GitHub Client (Webhooks)      │
                            ├──────────┬───────────────────┤
                            │PostgreSQL│      Redis         │
                            │  16      │      7 (cache)    │
                            └──────────┴───────────────────┘
```

## Stack Tecnológica

| Camada      | Tecnologia                                        |
|-------------|---------------------------------------------------|
| Frontend    | Angular 17.3, Material 17.3, chart.js, ng2-charts |
| Backend     | Java 21, Spring Boot 3.3.5, Spring Security, JPA  |
| Auth        | JWT (jjwt 0.12.6) — stateless, HS512              |
| Banco       | PostgreSQL 16, Redis 7 (cache opcional)            |
| AI          | OpenAI GPT-4 (plugável) + Mock provider            |
| Infra       | Docker Compose, GitHub Actions CI/CD               |

## Funcionalidades

- **Webhook GitHub**: Recebe eventos de PR e dispara análise automática
- **Motor de Regras**: Detecta arquivos críticos (CI/CD, Dockerfile, secrets, migrations)
- **Análise por IA**: Envia diff para GPT-4 e recebe avaliação de risco/score
- **Análise Final**: Combina regras + IA em score final 0-100 e nível de risco
- **Dashboard**: Cards de estatísticas + gráfico doughnut de distribuição
- **RBAC**: Três papéis (ADMIN, REVIEWER, VIEWER) com `@PreAuthorize`
- **Audit Trail**: Log de todas as ações com IP e correlação

## Pré-requisitos

- Docker & Docker Compose **ou**
- Java 21, Node 20, PostgreSQL 16, Redis 7

## Quick Start (Docker)

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/pr-assistant-ai.git
cd pr-assistant-ai

# 2. Configure variáveis de ambiente
cp .env.example .env
# Edite .env com suas credenciais

# 3. Suba tudo
docker compose up -d

# 4. Acesse
# Frontend:  http://localhost
# Backend:   http://localhost:8080
# Swagger:   http://localhost:8080/swagger-ui.html (se habilitado)
```

## Desenvolvimento Local

### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# API em http://localhost:8080
```

### Frontend

```bash
cd frontend
npm install
npx ng serve
# SPA em http://localhost:4200 (proxy /api → :8080)
```

## Endpoints Principais

| Método | Path                              | Acesso   | Descrição                      |
|--------|-----------------------------------|----------|--------------------------------|
| POST   | `/api/auth/login`                 | Público  | Login — retorna JWT            |
| POST   | `/api/webhooks/github`            | Webhook  | Recebe evento de PR            |
| GET    | `/api/pull-requests`              | AUTH     | Lista PRs (paginado)           |
| GET    | `/api/pull-requests/{id}`         | AUTH     | Detalhe de um PR               |
| GET    | `/api/pull-requests/{id}/analyses`| AUTH     | Histórico de análises do PR    |
| GET    | `/api/dashboard`                  | AUTH     | Métricas agregadas             |

## Estrutura de Pastas

```
pr-assistant-ai/
├── backend/
│   ├── src/main/java/dev/prassistant/
│   │   ├── config/          # Security, WebClient, CORS, Audit
│   │   ├── controller/      # Auth, PR, Dashboard, Webhook
│   │   ├── domain/
│   │   │   ├── entity/      # JPA entities (UUID PKs)
│   │   │   └── enums/       # RiskLevel, PrState, Role...
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Global handler + custom exceptions
│   │   ├── repository/      # Spring Data JPA repos
│   │   ├── rules/           # Rule engine + critical file detector
│   │   ├── security/        # JWT provider, filter, UserDetails
│   │   └── service/         # Business logic + AI providers
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/app/
│   │   ├── core/            # Services, guards, interceptors, models
│   │   └── features/        # Login, Dashboard, PR list/detail
│   ├── Dockerfile
│   ├── nginx.conf
│   └── angular.json
├── database/
│   └── init.sql
├── .github/workflows/ci.yml
├── docker-compose.yml
└── .env.example
```

## CI/CD (GitHub Actions)

O pipeline em `.github/workflows/ci.yml`:

1. **backend-test** — Maven build + testes com PostgreSQL service container
2. **frontend-test** — `npm ci` + `ng build --production`
3. **docker-build** — Build das imagens Docker (apenas na `main`)

## Decisões de Design

| Decisão | Justificativa |
|---------|---------------|
| Standalone Components | Padrão Angular 17+, tree-shakeable, sem NgModules |
| Signals + OnPush | Reatividade granular, melhor performance de CD |
| JWT Stateless | Escalabilidade horizontal sem session store |
| AI Provider Interface | Strategy pattern — troca OpenAI/Mock sem alterar lógica |
| UUID como PK | Segurança (não sequencial), compatível com sistemas distribuídos |
| Multi-stage Docker | Imagens < 200MB, sem código-fonte no runtime |
| Health checks | compose `depends_on: condition` garante ordem de startup |

## Licença

MIT
