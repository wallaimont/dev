# InsuranceFlow SaaS

**Plataforma SaaS completa para Administradoras de Seguros**

Sistema multi-tenant com painel master (dono do SaaS), painel administrativo (administradora de seguros) e portal do cliente (segurado).

---

## Tecnologias

- **Backend:** Java 25, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate
- **Banco:** PostgreSQL 16 + Flyway (migrations automáticas)
- **Auth:** JWT (access + refresh token)
- **Docs:** Swagger/OpenAPI 3
- **Frontend:** HTML5, CSS3, JavaScript (SPA-like com Thymeleaf)
- **Infra:** Docker, Docker Compose

## Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                   Landing Page                       │
│              (captura de leads)                       │
├─────────────────────────────────────────────────────┤
│          Login → JWT Auth → Role-based               │
├──────────┬──────────────┬───────────────┬───────────┤
│  Painel  │    Painel    │    Portal     │  API      │
│  Master  │    Admin     │   Cliente     │  Pública  │
│ (SaaS)   │  (Empresa)   │ (Segurado)   │           │
├──────────┴──────────────┴───────────────┴───────────┤
│              Spring Boot 3 + Security                │
│          Multi-tenant (empresa_id isolation)          │
├─────────────────────────────────────────────────────┤
│              PostgreSQL + Flyway                     │
└─────────────────────────────────────────────────────┘
```

## Módulos

| Módulo | Descrição |
|--------|-----------|
| Clientes PF/PJ | Cadastro completo de clientes |
| Propostas | Workflow de propostas com status |
| Apólices | Controle de ciclo de vida completo |
| Sinistros | Registro e acompanhamento |
| Renovações | Gestão de renovações |
| Financeiro | Lançamentos, receitas e despesas |
| Boletos | Emissão e controle |
| Comissões | Cálculo e controle por corretora |
| Seguradoras | Cadastro de seguradoras |
| Corretoras | Cadastro de corretoras parceiras |
| White-Label | Personalização por empresa |
| Portal do Cliente | Área do segurado |
| Dashboard | Métricas em tempo real |
| Auditoria | Log completo de operações |

## Quick Start com Docker

```bash
docker compose up -d
```

Acesse: http://localhost:8080

## Quick Start Local

### Pré-requisitos
- Java 25+
- Maven 3.9+
- PostgreSQL 16+

### Banco de dados
```sql
CREATE DATABASE insuranceflow;
CREATE USER insuranceflow WITH PASSWORD 'insuranceflow2025';
GRANT ALL PRIVILEGES ON DATABASE insuranceflow TO insuranceflow;
```

### Executar
```bash
mvn spring-boot:run
```

## Publicação Estável em Produção

Este projeto já está preparado para deploy com URL fixa e HTTPS em:

- Render: arquivo `render.yaml`
- Railway: arquivo `railway.json`

### Opção 1: Render (recomendado)

1. Suba o repositório para GitHub.
2. No Render, escolha **Blueprint** e selecione o repositório.
3. O Render criará automaticamente:
	- Serviço web `insuranceflow-saas`
	- Banco PostgreSQL `insuranceflow-db`
4. Configure o domínio customizado (ex: `app.seudominio.com.br`).
5. Atualize as variáveis no serviço:
	- `CORS_ORIGINS=https://app.seudominio.com.br`
	- `PLATFORM_URL=https://app.seudominio.com.br`
6. Deploy automático com HTTPS ativado.

### Opção 2: Railway

1. Crie projeto no Railway e conecte o repositório.
2. Adicione plugin PostgreSQL no mesmo projeto.
3. Configure variáveis de ambiente no serviço:
	- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`
	- `JWT_SECRET` (base64 forte)
	- `CORS_ORIGINS`, `PLATFORM_URL`
	- `SERVER_COOKIE_SECURE=true`
	- `SWAGGER_ENABLED=false`
4. Configure domínio customizado no Railway e aponte DNS.

### Checklist de produção

- `JWT_SECRET` forte e exclusivo por ambiente
- Swagger desabilitado em produção (`SWAGGER_ENABLED=false`)
- Cookie seguro (`SERVER_COOKIE_SECURE=true`)
- Origem CORS apenas do domínio oficial
- Banco gerenciado com backup automático

## Credenciais de Demonstração

| Perfil | Email | Senha |
|--------|-------|-------|
| Admin Master (SaaS) | admin@insuranceflow.com.br | admin123 |
| Admin Empresa | admin@demo.com.br | demo123 |

## Endpoints da API

### Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /api/auth/login | Login |
| POST | /api/auth/refresh | Refresh token |
| GET | /api/auth/me | Dados do usuário logado |

### Master (SaaS)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | /api/master/dashboard | Dashboard do SaaS |
| GET/POST | /api/master/empresas | CRUD de empresas |
| GET/POST | /api/master/planos | CRUD de planos |
| GET | /api/master/leads | Listar leads |
| POST | /api/master/onboarding | Onboard nova empresa |

### Tenant (Empresa)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | /api/tenant/dashboard | Dashboard da empresa |
| GET/POST | /api/tenant/clientes | CRUD clientes |
| GET/POST | /api/tenant/propostas | CRUD propostas |
| GET | /api/tenant/apolices | Listar apólices |
| GET | /api/tenant/sinistros | Listar sinistros |
| GET | /api/tenant/financeiro | Lançamentos |
| GET | /api/tenant/comissoes | Comissões |

### Portal do Cliente
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | /api/portal/apolices | Apólices do cliente |
| GET | /api/portal/sinistros | Sinistros do cliente |
| GET | /api/portal/boletos | Boletos do cliente |
| GET | /api/portal/notificacoes | Notificações |

### Documentação Swagger
Acesse: http://localhost:8080/swagger-ui.html

## Planos

| Plano | Preço | Usuários | Clientes | Apólices |
|-------|-------|----------|----------|----------|
| Starter | R$ 197/mês | 3 | 100 | 100 |
| Professional | R$ 497/mês | 10 | 500 | 500 |
| Business | R$ 997/mês | 25 | 2.000 | 2.000 |
| Enterprise | R$ 2.497/mês | Ilimitado | Ilimitado | Ilimitado |

## Estrutura do Projeto

```
src/main/java/com/insuranceflow/
├── InsuranceFlowApplication.java
├── auth/          # Autenticação (JWT, login, usuários)
├── common/        # Base entities, DTOs, exceções
├── config/        # Security, CORS, Swagger, Audit
├── master/        # Módulo Master SaaS (planos, empresas, leads)
├── security/      # JWT, filtros, tenant context
└── tenant/        # Módulo Tenant (clientes, propostas, apólices...)

src/main/resources/
├── application.yml
├── db/migration/  # Flyway migrations
└── templates/     # Frontend (landing, login, master, admin, portal)
```

## Licença

Proprietário — Todos os direitos reservados.
