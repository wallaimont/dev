# OrionERP — Documento de Arquitetura

## 1. Visão Geral

OrionERP é um ERP web enterprise com arquitetura de **monolito modular**, projetado para evolução futura em microsserviços. O sistema adota uma organização por módulos de negócio, cada um encapsulando seu próprio domínio, regras, serviços e APIs.

## 2. Decisões Arquiteturais

### 2.1 Monolito Modular
Cada módulo (administração, financeiro, compras, etc.) é um pacote Java independente contendo controller, service, repository, domain, dto e mapper. A comunicação entre módulos acontece via serviços internos injetados, permitindo futura extração para microsserviços.

### 2.2 Camadas
```
┌─────────────────────────────────┐
│         FRONTEND (React)        │
│  SPA + Auth + Módulos + Layout  │
└──────────────┬──────────────────┘
               │ HTTP/REST (JSON)
┌──────────────▼──────────────────┐
│       API GATEWAY (Spring)      │
│  Controllers + Swagger + CORS   │
├─────────────────────────────────┤
│       APPLICATION LAYER         │
│  Services + DTOs + Mappers      │
├─────────────────────────────────┤
│        DOMAIN LAYER             │
│  Entities + Enums + Rules       │
├─────────────────────────────────┤
│      INFRASTRUCTURE LAYER       │
│  Repositories + Config + Audit  │
├─────────────────────────────────┤
│        DATA LAYER               │
│  PostgreSQL + Flyway Migrations │
└─────────────────────────────────┘
```

### 2.3 Multiempresa / Multifilial
- Toda entidade transacional possui `empresa_id` e `filial_id`
- Filtro automático via `@TenantFilter` no nível do serviço
- Usuário autenticado carrega contexto de empresa/filial
- Parâmetros configuráveis por empresa e filial

### 2.4 Segurança
- Autenticação stateless via JWT (access token 30min + refresh token 7d)
- RBAC: Usuário → Perfil → Permissões (recurso + ação)
- BCrypt para hash de senhas
- Rate limiting em endpoints de login
- Auditoria em entidades críticas (quem criou, alterou, quando)
- Proteção contra injection via Bean Validation + parametrized queries
- CORS configurável por ambiente
- Logs de login/falha

### 2.5 Padrões Transversais
- **Response padrão**: `ApiResponse<T>` com data, message, timestamp, errors
- **Exception handler global**: `@ControllerAdvice` mapeando exceções para HTTP codes
- **Paginação padrão**: Spring Pageable com filtros dinâmicos
- **Soft delete**: campo `deleted` (boolean) + `deletedAt`
- **Auditoria**: `createdAt`, `updatedAt`, `createdBy`, `updatedBy` via `@EntityListeners`
- **UUID**: chave pública para exposição externa; `id` (Long) para FK internas

## 3. Fluxo entre Módulos

```
COMPRAS                          VENDAS
  │                                │
  ├─ Pedido aprovado ──► ESTOQUE ◄─┤── Pedido aprovado
  │     (entrada)       (saldo)    │     (saída)
  │                                │
  ├─ Recebimento ──► FINANCEIRO ◄──┤── Faturamento
  │   (conta pagar)   (títulos)    │   (conta receber)
  │                                │
  └── WORKFLOW (aprovações) ◄──────┘
              │
         AUDITORIA + LOGS
```

## 4. Estrutura de Pacotes (Backend)

```
com.orionerp
├── config/           # Configs gerais (CORS, Swagger, Jackson, etc.)
├── security/         # JWT, filtros, AuthService, UserDetails
├── common/           # BaseEntity, ApiResponse, PageResponse
├── shared/           # DTOs/enums compartilhados entre módulos
├── exception/        # Exceções custom + GlobalExceptionHandler
├── audit/            # AuditListener, AuditLog entity/repo
└── modules/
    ├── administration/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── domain/
    │   ├── dto/
    │   └── mapper/
    ├── cadastro/
    ├── financeiro/
    ├── compras/
    ├── estoque/
    ├── vendas/
    ├── fiscal/
    ├── crm/
    ├── rh/
    ├── workflow/
    └── relatorios/
```

## 5. Estrutura Frontend

```
src/
├── app/              # App.tsx, rotas raiz
├── assets/           # Imagens, ícones
├── components/
│   ├── ui/           # Button, Input, Modal, Badge, Card
│   ├── forms/        # FormField, FormSelect, FormDatePicker
│   ├── tables/       # DataTable, Pagination, Filters
│   └── layout/       # Sidebar, Header, Breadcrumb, Footer
├── layouts/          # AdminLayout, AuthLayout
├── routes/           # Definição de rotas por módulo
├── services/         # API client (axios), endpoints
├── hooks/            # useAuth, usePagination, usePermission
├── utils/            # formatters, validators, constants
├── contexts/         # AuthContext, TenantContext
├── types/            # Interfaces globais
└── modules/
    ├── auth/         # Login, esqueci senha
    ├── dashboard/    # Tela principal
    ├── administration/
    ├── cadastro/
    ├── financeiro/
    ├── compras/
    ├── estoque/
    ├── vendas/
    ├── fiscal/
    ├── crm/
    ├── rh/
    ├── workflow/
    └── relatorios/
```

## 6. Roadmap de Desenvolvimento

| Etapa | Escopo                                      | Prioridade |
|-------|---------------------------------------------|------------|
| 1     | Arquitetura + planejamento                  | ✅ Feito    |
| 2     | Modelagem do banco + Flyway                 | Alta       |
| 3     | Backend base (config, security, common)     | Alta       |
| 4     | Módulo Administração                        | Alta       |
| 5     | Módulo Cadastros Gerais                     | Alta       |
| 6     | Módulo Financeiro                           | Alta       |
| 7     | Módulo Compras                              | Média      |
| 8     | Módulo Estoque                              | Média      |
| 9     | Módulo Vendas                               | Média      |
| 10    | Fiscal, CRM, RH, Workflow                   | Média      |
| 11    | Frontend completo                           | Alta       |
| 12    | Relatórios, testes, Docker, docs            | Média      |

## 7. Justificativa da Stack

- **Java 21**: LTS, virtual threads, records, pattern matching
- **Spring Boot 3.3**: Ecossistema maduro, produtividade, comunidade ativa
- **PostgreSQL 16**: Melhor RDBMS open-source, JSONB, CTE, Window Functions
- **React + TypeScript**: Tipagem forte no frontend, componentização
- **Tailwind CSS**: Produtividade visual sem lock-in de componente
- **Flyway**: Versionamento de schema confiável
- **Docker**: Ambiente reproduzível e deploy simplificado
