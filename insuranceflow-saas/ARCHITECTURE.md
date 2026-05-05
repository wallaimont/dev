# InsuranceFlow SaaS — Arquitetura da Plataforma

> Documentação técnica da estrutura SaaS, arquitetura multiempresa e modelagem do banco de dados.

---

## Sumário

1. [Visão Geral da Plataforma](#1-visão-geral-da-plataforma)
2. [Stack Tecnológico](#2-stack-tecnológico)
3. [Arquitetura em Camadas](#3-arquitetura-em-camadas)
4. [Arquitetura Multi-Tenant](#4-arquitetura-multi-tenant)
5. [Segurança e Autenticação](#5-segurança-e-autenticação)
6. [Perfis e Permissões](#6-perfis-e-permissões)
7. [Modelagem do Banco de Dados](#7-modelagem-do-banco-de-dados)
8. [Estrutura de Pacotes Java](#8-estrutura-de-pacotes-java)
9. [Endpoints da API](#9-endpoints-da-api)
10. [Planos e Limites SaaS](#10-planos-e-limites-saas)

---

## 1. Visão Geral da Plataforma

O **InsuranceFlow SaaS** é uma plataforma multi-tenant completa para gestão de administradoras de seguros. Cada empresa (tenant) opera isoladamente com seus dados de clientes, propostas, apólices, sinistros e financeiro, enquanto um painel master controla toda a operação SaaS.

### Modelo de Negócio

```
┌─────────────────────────────────────────────────────────────────┐
│                    InsuranceFlow SaaS (Master)                  │
│    Planos: Starter │ Professional │ Business │ Enterprise       │
├─────────────┬──────────────┬─────────────┬──────────────────────┤
│  Empresa A  │  Empresa B   │  Empresa C  │    Empresa N...      │
│  (Tenant)   │  (Tenant)    │  (Tenant)   │    (Tenant)          │
│             │              │             │                      │
│ • Clientes  │ • Clientes   │ • Clientes  │ • Clientes           │
│ • Propostas │ • Propostas  │ • Propostas │ • Propostas          │
│ • Apólices  │ • Apólices   │ • Apólices  │ • Apólices           │
│ • Sinistros │ • Sinistros  │ • Sinistros │ • Sinistros          │
│ • Financ.   │ • Financ.    │ • Financ.   │ • Financ.            │
└─────────────┴──────────────┴─────────────┴──────────────────────┘
```

---

## 2. Stack Tecnológico

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| **Linguagem** | Java (OpenJDK) | 25 |
| **Framework** | Spring Boot | 3.5.0 |
| **ORM** | Hibernate / JPA | 6.6.15 |
| **Banco de dados** | PostgreSQL | 16 |
| **Migrações** | Flyway | 10.x |
| **Segurança** | Spring Security + JWT (HMAC-SHA512) | 6.5.0 |
| **Frontend** | Thymeleaf + JavaScript (fetch API) | 3.x |
| **Build** | Maven | 3.9.14 |
| **Containers** | Docker + Docker Compose | 29.x |
| **Documentação API** | SpringDoc OpenAPI (Swagger) | 2.x |
| **Monitoramento** | Spring Actuator | — |

---

## 3. Arquitetura em Camadas

```
┌───────────────────────────────────────────────────────┐
│                   📄 WEB LAYER                        │
│  Thymeleaf Templates (landing, login, master,         │
│  admin, portal) + Static Assets                       │
├───────────────────────────────────────────────────────┤
│                   🔌 API LAYER                        │
│  AuthController   MasterController  TenantController  │
│  PublicController  PortalClienteController             │
├───────────────────────────────────────────────────────┤
│                   🔒 SECURITY LAYER                   │
│  JwtAuthFilter → JwtTokenProvider → TenantContext     │
│  SecurityConfig (Role-Based Access Control)           │
├───────────────────────────────────────────────────────┤
│                   ⚙️ SERVICE LAYER                    │
│  AuthService    MasterService    TenantService        │
│  (Regras de negócio + TenantContext filtering)        │
├───────────────────────────────────────────────────────┤
│                   💾 DATA LAYER                       │
│  20+ JPA Repositories (Spring Data)                   │
│  BaseEntity / TenantBaseEntity (UUID, audit fields)   │
├───────────────────────────────────────────────────────┤
│                   🐘 POSTGRESQL 16                    │
│  25 tabelas | UUID PKs | JSONB audit | Flyway         │
└───────────────────────────────────────────────────────┘
```

### Fluxo de uma Requisição

```
Browser → Tomcat :8080 → JwtAuthFilter → SecurityConfig
    → Controller → Service → Repository → PostgreSQL
    → Response JSON (ApiResponse<T>)
```

---

## 4. Arquitetura Multi-Tenant

### Estratégia: Banco Compartilhado com Coluna Discriminadora

O InsuranceFlow adota a estratégia **Shared Database, Shared Schema** com isolamento por coluna `empresa_id` (UUID). Todas as empresas compartilham o mesmo banco PostgreSQL, mas cada entidade operacional carrega o `empresa_id` como discriminador obrigatório.

### Componentes do Multi-Tenancy

#### 4.1 TenantContext (ThreadLocal)

```java
// security/TenantContext.java
public class TenantContext {
    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    public static UUID getCurrentTenant() { return currentTenant.get(); }
    public static void setCurrentTenant(UUID empresaId) { currentTenant.set(empresaId); }
    public static void clear() { currentTenant.remove(); }
}
```

O `TenantContext` armazena o `empresaId` no ThreadLocal da thread corrente. É preenchido pelo filtro JWT e limpo ao final de cada requisição para evitar vazamento de contexto.

#### 4.2 TenantBaseEntity (Coluna Discriminadora)

```java
// common/model/TenantBaseEntity.java
@MappedSuperclass
public abstract class TenantBaseEntity extends BaseEntity {
    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;
}
```

Todas as 13 entidades operacionais (Cliente, Proposta, Apólice...) estendem `TenantBaseEntity`, garantindo que o campo `empresa_id` é **NOT NULL** em toda tabela.

#### 4.3 Fluxo de Isolamento

```
1. Usuário faz login → JWT gerado com claim "empresaId"
2. Requisição autenticada chega ao JwtAuthFilter
3. Filter extrai empresaId do JWT → TenantContext.set(empresaId)
4. Service chama Repository com filtro: findByEmpresaId(empresaId)
5. SQL gerado: SELECT * FROM tabela WHERE empresa_id = ?
6. Somente dados daquele tenant são retornados
7. Finally: TenantContext.clear()
```

#### 4.4 Garantias de Isolamento

| Camada | Mecanismo |
|--------|-----------|
| **JWT** | `empresaId` embutido no token, não manipulável pelo cliente |
| **Filter** | `JwtAuthFilter` injeta o tenant a cada request |
| **Service** | Toda query usa `TenantContext.getCurrentTenant()` |
| **Entity** | `empresa_id NOT NULL` no banco — impossível inserir sem tenant |
| **Cleanup** | `TenantContext.clear()` no finally do filter |

#### 4.5 Separação Master vs Tenant

```
┌─────────────────────────┐  ┌──────────────────────────────┐
│    MASTER ENTITIES      │  │     TENANT ENTITIES           │
│   (extends BaseEntity)  │  │  (extends TenantBaseEntity)   │
│                         │  │                               │
│ • Plano                 │  │ • Cliente                     │
│ • Empresa               │  │ • Seguradora                  │
│ • AssinaturaEmpresa     │  │ • Corretora                   │
│ • PagamentoAssinatura   │  │ • RamoSeguro                  │
│ • ConfigWhiteLabel      │  │ • Proposta                    │
│ • LeadSaas              │  │ • Apólice                     │
│ • TicketSuporte         │  │ • Renovação                   │
│ • Usuário               │  │ • Sinistro                    │
│                         │  │ • LancamentoFinanceiro        │
│ Acessíveis somente      │  │ • Boleto                      │
│ pelo ADMIN_MASTER       │  │ • Comissão                    │
│                         │  │ • Documento                   │
│                         │  │ • Notificação                 │
│                         │  │ • Auditoria                   │
│                         │  │                               │
│                         │  │ Filtradas por empresa_id      │
└─────────────────────────┘  └──────────────────────────────┘
```

---

## 5. Segurança e Autenticação

### 5.1 Fluxo de Login

```
POST /api/auth/login { email, senha }
    │
    ▼
AuthService.login()
    │
    ├── AuthenticationManager.authenticate()
    │       └── CustomUserDetailsService.loadUserByUsername(email)
    │               └── UsuarioRepository.findByEmailAndActiveTrue(email)
    │
    ├── Verifica: empresa.bloqueada == false
    │
    ├── JwtTokenProvider.generateToken(userPrincipal)
    │       ├── Access Token  (1h) → claims: userId, email, role, empresaId, nome
    │       └── Refresh Token (7d) → claims: userId, type=refresh
    │
    └── Return: LoginResponse { token, refreshToken, tipo, nome, email, perfil, empresaId }
```

### 5.2 Estrutura do JWT

```
Header:  { "alg": "HS512" }
Payload: {
    "sub": "c0000000-0000-0000-0000-000000000001",     // userId
    "email": "admin@insuranceflow.com.br",
    "role": "ADMIN_MASTER",
    "empresaId": "b0000000-0000-0000-0000-000000000001",
    "nome": "Administrador Master",
    "iat": 1774794008,
    "exp": 1774797608
}
Signature: HMAC-SHA512(secret)
```

### 5.3 Matriz de Autorização dos Endpoints

| Padrão de URL | Acesso | Descrição |
|---------------|--------|-----------|
| `/`, `/login`, `/master`, `/admin`, `/portal` | PUBLIC | Páginas HTML (SPAs autenticam via JS) |
| `/api/auth/**` | PUBLIC | Login e refresh token |
| `/api/public/**` | PUBLIC | Dados públicos (planos, etc) |
| `/api/onboarding/**` | PUBLIC | Cadastro de novas empresas |
| `/api/leads-saas` | PUBLIC | Captura de leads (landing page) |
| `/api/master/**` | `ADMIN_MASTER` | Gestão de toda a plataforma SaaS |
| `/api/tenant/**` | `AUTHENTICATED` | Dados operacionais (filtrados por tenant) |
| `/api/portal/**` | `CLIENTE` | Portal do segurado |
| `/swagger-ui/**`, `/v3/api-docs/**` | PUBLIC | Documentação da API |
| `/actuator/health`, `/actuator/info` | PUBLIC | Health checks |

---

## 6. Perfis e Permissões

### Hierarquia de Perfis

```
🏆 ADMIN_MASTER ─── Dono da plataforma SaaS
│   Acessa: /api/master/** (empresas, planos, assinaturas, leads, tickets)
│   Visão: Dashboard SaaS (MRR, ARR, empresas, churn)
│
├── 🏢 ADMIN_EMPRESA ─── Gestor da administradora de seguros
│   │   Acessa: /api/tenant/** (todos os módulos da empresa)
│   │   Visão: Dashboard operacional + White Label + Equipe
│   │
│   ├── 👔 GERENTE ─── Gerente comercial
│   │       Acessa: /api/tenant/** (clientes, propostas, apólices)
│   │       Visão: Dashboard operacional (leitura + escrita parcial)
│   │
│   └── 👷 OPERADOR ─── Analista de seguros
│           Acessa: /api/tenant/** (cadastros + financeiro)
│           Visão: Operacional (leitura + escrita limitada)
│
└── 👤 CLIENTE ─── Segurado final
        Acessa: /api/portal/** (somente seus dados)
        Visão: Portal do cliente (apólices, sinistros, boletos)
```

### Detalhamento por Perfil

| Perfil | Painel | Módulos Acessíveis | Escopo de Dados |
|--------|--------|--------------------|-----------------|
| `ADMIN_MASTER` | `/master` | Empresas, Planos, Assinaturas, Pagamentos, Leads, Tickets, Dashboard SaaS | Toda a plataforma |
| `ADMIN_EMPRESA` | `/admin` | Clientes, Propostas, Apólices, Sinistros, Renovações, Financeiro, Comissões, White Label, Equipe, Dashboard | Somente sua empresa |
| `GERENTE` | `/admin` | Clientes, Propostas, Apólices, Sinistros, Dashboard | Somente sua empresa |
| `OPERADOR` | `/admin` | Clientes, Propostas, Financeiro | Somente sua empresa |
| `CLIENTE` | `/portal` | Minhas Apólices, Meus Sinistros, Meus Boletos, Notificações | Somente seus dados pessoais |

---

## 7. Modelagem do Banco de Dados

### 7.1 Visão Geral

- **25 tabelas** no schema `public`
- **UUID** como chave primária em todas as tabelas (`gen_random_uuid()`)
- **Audit fields** em todas tabelas: `created_at`, `updated_at`, `created_by`, `updated_by`, `active`
- **Soft delete** via campo `active` (boolean, default true)
- **JSONB** para campos de auditoria (dados anteriores/novos)
- **Flyway** gerencia versionamento do schema

### 7.2 Tabelas Master (Plataforma SaaS)

#### `plano` — Planos de Assinatura
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `nome` | VARCHAR(100) | Starter, Professional, Business, Enterprise |
| `descricao` | TEXT | Descrição do plano |
| `preco_mensal` | DECIMAL(10,2) | Valor mensal |
| `preco_anual` | DECIMAL(10,2) | Valor anual (desconto) |
| `limite_usuarios` | INT | Máx. usuários permitidos |
| `limite_clientes` | INT | Máx. clientes cadastrados |
| `limite_propostas_mes` | INT | Máx. propostas/mês |
| `limite_apolices` | INT | Máx. apólices ativas |
| `limite_armazenamento_gb` | INT | Armazenamento de documentos |
| `portal_cliente` | BOOLEAN | Portal do segurado habilitado |
| `whatsapp_integrado` | BOOLEAN | Integração WhatsApp |
| `relatorios_avancados` | BOOLEAN | Relatórios detalhados |
| `white_label` | BOOLEAN | Personalização de marca |
| `acesso_api` | BOOLEAN | API REST externa |
| `suporte_prioritario` | BOOLEAN | Suporte dedicado |

#### `empresa` — Tenants (Administradoras de Seguros)
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `plano_id` | UUID FK → plano | Plano contratado |
| `razao_social` | VARCHAR(255) | Razão social |
| `nome_fantasia` | VARCHAR(255) | Nome fantasia |
| `cnpj` | VARCHAR(20) UNIQUE | CNPJ da empresa |
| `email` | VARCHAR(255) | Email principal |
| `telefone` / `celular` | VARCHAR(20) | Contato |
| `cep`, `logradouro`, `numero`, `bairro`, `cidade`, `estado` | VARCHAR | Endereço |
| `status` | VARCHAR(20) | ATIVA, TRIAL, SUSPENSA, CANCELADA |
| `data_inicio_trial` / `data_fim_trial` | TIMESTAMP | Período trial |
| `bloqueada` | BOOLEAN | Empresa bloqueada (inadimplência etc) |
| `motivo_bloqueio` | TEXT | Motivo do bloqueio |
| `slug` | VARCHAR(100) UNIQUE | Identificador URL (white-label) |

#### `usuario` — Usuários de Todos os Perfis
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK → empresa | Empresa do usuário |
| `nome` | VARCHAR(255) | Nome completo |
| `email` | VARCHAR(255) UNIQUE | Email (login) |
| `senha` | VARCHAR(255) | Hash BCrypt |
| `perfil` | VARCHAR(20) | ADMIN_MASTER, ADMIN_EMPRESA, GERENTE, OPERADOR, CLIENTE |
| `telefone` | VARCHAR(20) | Telefone |
| `cargo` | VARCHAR(100) | Cargo na empresa |
| `avatar_url` | VARCHAR(500) | URL do avatar |
| `ultimo_acesso` | TIMESTAMP | Último login |

#### `assinatura_empresa` — Assinaturas Ativas
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK → empresa | Empresa assinante |
| `plano_id` | UUID FK → plano | Plano assinado |
| `ciclo` | VARCHAR(20) | MENSAL, ANUAL |
| `valor` | DECIMAL(10,2) | Valor da assinatura |
| `status` | VARCHAR(20) | ATIVA, SUSPENSA, CANCELADA |
| `data_inicio` | TIMESTAMP | Início da assinatura |
| `data_vencimento` | TIMESTAMP | Próximo vencimento |
| `data_cancelamento` | TIMESTAMP | Data do cancelamento |
| `id_assinatura_externa` | VARCHAR(255) | ID no gateway (Stripe/PagSeguro) |
| `gateway_pagamento` | VARCHAR(50) | Gateway utilizado |

#### `pagamento_assinatura` — Histórico de Pagamentos SaaS
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `assinatura_id` | UUID FK | Assinatura relacionada |
| `empresa_id` | UUID FK | Empresa pagadora |
| `valor` | DECIMAL(10,2) | Valor pago |
| `data_vencimento` | TIMESTAMP | Vencimento |
| `data_pagamento` | TIMESTAMP | Data efetiva do pagamento |
| `status` | VARCHAR(20) | PENDENTE, PAGO, ATRASADO, CANCELADO |
| `metodo_pagamento` | VARCHAR(50) | PIX, Cartão, Boleto |
| `id_transacao_externa` | VARCHAR(255) | ID da transação no gateway |

#### `configuracao_white_label` — Personalização por Empresa
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK UNIQUE | Uma config por empresa |
| `logo_url` / `favicon_url` | VARCHAR(500) | Imagens da marca |
| `nome_sistema` | VARCHAR(255) | Nome exibido no sistema |
| `cor_primaria` / `cor_secundaria` / `cor_acento` / `cor_fundo` | VARCHAR(7) | Cores HEX |
| `nome_portal_cliente` | VARCHAR(255) | Nome do portal público |
| `rodape` | TEXT | Texto do rodapé personalizado |
| `email_suporte` / `telefone_suporte` | VARCHAR | Suporte da empresa |
| `dominio_personalizado` / `subdominio` | VARCHAR | DNS personalizado |

#### `lead_saas` — Leads Captados pela Landing Page
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `nome` | VARCHAR(255) | Nome do contato |
| `empresa_nome` | VARCHAR(255) | Empresa do lead |
| `email` | VARCHAR(255) | Email |
| `telefone` | VARCHAR(20) | Telefone |
| `origem` | VARCHAR(100) | Landing Page, Google Ads, Indicação |
| `interesse` | VARCHAR(100) | Plano de interesse |
| `status` | VARCHAR(20) | NOVO, CONTATADO, DEMO_AGENDADA, NEGOCIANDO, CONVERTIDO, PERDIDO |
| `observacoes` | TEXT | Anotações |

#### `ticket_suporte` — Tickets de Suporte
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Empresa solicitante |
| `usuario_id` | UUID FK | Usuário que abriu |
| `assunto` | VARCHAR(255) | Assunto do ticket |
| `descricao` | TEXT | Descrição detalhada |
| `prioridade` | VARCHAR(20) | BAIXA, MEDIA, ALTA, URGENTE |
| `status` | VARCHAR(20) | ABERTO, EM_ANDAMENTO, RESOLVIDO, FECHADO |
| `categoria` | VARCHAR(100) | Categoria do problema |
| `data_resolucao` | TIMESTAMP | Quando foi resolvido |

### 7.3 Tabelas Tenant (Operacional por Empresa)

> Todas possuem `empresa_id NOT NULL` (FK → empresa) para isolamento multi-tenant.

#### `cliente` — Clientes/Segurados
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `tipo_pessoa` | VARCHAR(2) | PF ou PJ |
| `nome` | VARCHAR(255) | Nome/Razão Social |
| `cpf_cnpj` | VARCHAR(20) UNIQUE | CPF ou CNPJ |
| `rg` | VARCHAR(20) | RG (PF) |
| `data_nascimento` | DATE | Data de nascimento |
| `sexo` | VARCHAR(1) | M/F |
| `estado_civil` | VARCHAR(20) | Estado civil |
| `profissao` | VARCHAR(100) | Profissão |
| `email` / `telefone` / `celular` | VARCHAR | Contato |
| `cep`, `logradouro`, ..., `estado` | VARCHAR | Endereço |
| `observacoes` | TEXT | Observações |
| `origem` | VARCHAR(100) | Origem do cadastro |

#### `seguradora` — Seguradoras Parceiras
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `nome` | VARCHAR(255) | Nome da seguradora |
| `cnpj` | VARCHAR(20) | CNPJ |
| `codigo_susep` | VARCHAR(20) | Código SUSEP |
| `email` / `telefone` / `website` | VARCHAR | Contato |
| `logo_url` | VARCHAR(500) | Logo |
| `contato_nome`, `contato_email`, `contato_telefone` | VARCHAR | Contato comercial |

#### `corretora` — Corretoras Parceiras
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `nome` | VARCHAR(255) | Nome da corretora |
| `cnpj` / `susep` | VARCHAR | Documentos |
| `email` / `telefone` | VARCHAR | Contato |
| `responsavel` | VARCHAR(255) | Responsável |
| `comissao_padrao` | DECIMAL(5,2) | % comissão padrão |

#### `ramo_seguro` — Ramos/Linhas de Negócio
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `codigo` | VARCHAR(10) | Código SUSEP (0531, 0114...) |
| `nome` | VARCHAR(100) | Automóvel, Vida, Incêndio... |
| `descricao` | TEXT | Descrição |

#### `proposta` — Propostas/Cotações de Seguro
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `numero` | VARCHAR(50) | Número sequencial (PROP-2025-001) |
| `cliente_id` | UUID FK → cliente | Proponente |
| `seguradora_id` | UUID FK → seguradora | Seguradora cotada |
| `corretora_id` | UUID FK → corretora | Corretora intermediária |
| `ramo_id` | UUID FK → ramo_seguro | Ramo do seguro |
| `tipo_seguro` | VARCHAR(100) | Tipo descritivo |
| `status` | VARCHAR(20) | RASCUNHO, EM_ANALISE, ACEITA, RECUSADA, CANCELADA |
| `data_proposta` | DATE | Data da proposta |
| `data_inicio_vigencia` / `data_fim_vigencia` | DATE | Vigência |
| `valor_importancia_segurada` | DECIMAL(15,2) | IS |
| `valor_premio` | DECIMAL(15,2) | Prêmio total |
| `valor_premio_liquido` | DECIMAL(15,2) | Prêmio líquido |
| `valor_iof` | DECIMAL(15,2) | IOF |
| `forma_pagamento` | VARCHAR(50) | Forma de pagamento |
| `numero_parcelas` | INT | Parcelas |
| `observacoes` / `motivo_recusa` | TEXT | Notas |

#### `apolice` — Apólices Emitidas
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `numero_apolice` | VARCHAR(50) | Número da apólice |
| `proposta_id` | UUID FK → proposta | Proposta de origem |
| `cliente_id` | UUID FK → cliente | Segurado |
| `seguradora_id` | UUID FK → seguradora | Seguradora emissora |
| `corretora_id` | UUID FK → corretora | Corretora |
| `ramo_id` | UUID FK → ramo_seguro | Ramo |
| `tipo_seguro` | VARCHAR(100) | Tipo |
| `status` | VARCHAR(20) | ATIVA, CANCELADA, SUSPENSA, EXPIRADA |
| `data_emissao` | DATE | Data de emissão |
| `data_inicio_vigencia` / `data_fim_vigencia` | DATE | Vigência |
| `valor_importancia_segurada` | DECIMAL(15,2) | IS |
| `valor_premio` | DECIMAL(15,2) | Prêmio |
| `numero_parcelas` | INT | Parcelas |
| `renovacao_automatica` | BOOLEAN | Flag auto-renovar |

#### `renovacao` — Renovações de Apólices
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `apolice_id` | UUID FK → apolice | Apólice original |
| `cliente_id` | UUID FK → cliente | Segurado |
| `status` | VARCHAR(20) | PENDENTE, RENOVADA, NAO_RENOVADA |
| `data_vencimento_original` | DATE | Vencimento original |
| `data_nova_vigencia_inicio` / `..._fim` | DATE | Nova vigência |
| `valor_premio_anterior` / `valor_premio_novo` | DECIMAL(15,2) | Comparativo |

#### `sinistro` — Sinistros/Ocorrências
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `numero_sinistro` | VARCHAR(50) | Número (SIN-2025-001) |
| `apolice_id` | UUID FK → apolice | Apólice vinculada |
| `cliente_id` | UUID FK → cliente | Segurado |
| `data_ocorrencia` / `data_aviso` | DATE | Datas |
| `tipo` | VARCHAR(100) | Tipo de ocorrência |
| `descricao` | TEXT | Descrição detalhada |
| `status` | VARCHAR(20) | ABERTO, EM_ANALISE, APROVADO, NEGADO, PAGO |
| `valor_estimado` | DECIMAL(15,2) | Estimativa de dano |
| `valor_indenizado` | DECIMAL(15,2) | Valor efetivamente pago |
| `data_pagamento_indenizacao` | TIMESTAMP | Data do pagamento |
| `local_ocorrencia` | VARCHAR(255) | Local |
| `boletim_ocorrencia` | VARCHAR(100) | Nº do B.O. |

#### `lancamento_financeiro` — Contas a Pagar/Receber
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `tipo` | VARCHAR(20) | RECEITA, DESPESA |
| `categoria` | VARCHAR(100) | Prêmio, Comissão, Sinistro... |
| `descricao` | VARCHAR(500) | Descrição |
| `valor` | DECIMAL(15,2) | Valor |
| `data_vencimento` / `data_pagamento` | TIMESTAMP | Datas |
| `status` | VARCHAR(20) | PENDENTE, PAGO, ATRASADO, CANCELADO |
| `forma_pagamento` | VARCHAR(50) | Forma |
| `cliente_id` | UUID FK | Cliente relacionado |
| `apolice_id` | UUID FK | Apólice relacionada |
| `proposta_id` | UUID FK | Proposta relacionada |

#### `boleto` — Boletos de Cobrança
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `lancamento_id` | UUID FK → lancamento_financeiro | Lançamento pai |
| `apolice_id` | UUID FK → apolice | Apólice |
| `cliente_id` | UUID FK → cliente | Pagador |
| `numero_parcela` | INT | Número da parcela |
| `valor` | DECIMAL(15,2) | Valor |
| `data_vencimento` / `data_pagamento` | TIMESTAMP | Datas |
| `status` | VARCHAR(20) | PENDENTE, PAGO, VENCIDO, CANCELADO |
| `linha_digitavel` / `codigo_barras` / `url_boleto` | VARCHAR | Dados do boleto |

#### `comissao` — Comissões de Corretoras/Seguradoras
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `apolice_id` | UUID FK → apolice | Apólice base |
| `corretora_id` | UUID FK → corretora | Corretora comissionada |
| `seguradora_id` | UUID FK → seguradora | Seguradora pagante |
| `tipo` | VARCHAR(50) | CORRETAGEM, AGENCIAMENTO, PRO_LABORE |
| `percentual` | DECIMAL(5,2) | % da comissão |
| `valor` | DECIMAL(15,2) | Valor calculado |
| `data_referencia` / `data_pagamento` | TIMESTAMP | Datas |
| `status` | VARCHAR(20) | PENDENTE, PAGA, CANCELADA |

#### `documento` — Armazenamento de Documentos
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `entidade_tipo` | VARCHAR(50) | CLIENTE, PROPOSTA, APOLICE, SINISTRO... |
| `entidade_id` | UUID | ID da entidade relacionada (polimórfico) |
| `nome` | VARCHAR(255) | Nome exibição |
| `nome_arquivo` | VARCHAR(255) | Nome original do arquivo |
| `tipo_arquivo` | VARCHAR(50) | MIME type |
| `tamanho_bytes` | BIGINT | Tamanho |
| `url` | VARCHAR(500) | URL de armazenamento |
| `descricao` | TEXT | Descrição |

#### `notificacao` — Notificações In-App
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `usuario_id` | UUID FK → usuario | Destinatário |
| `titulo` | VARCHAR(255) | Título |
| `mensagem` | TEXT | Conteúdo |
| `tipo` | VARCHAR(50) | INFO, ALERTA, URGENTE, SUCESSO |
| `lida` | BOOLEAN | Lida ou não |
| `link` | VARCHAR(500) | Link para ação |

#### `auditoria` — Log de Auditoria (JSONB)
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Tenant |
| `usuario_id` | UUID FK → usuario | Quem executou |
| `acao` | VARCHAR(50) | CRIAR, EDITAR, EXCLUIR, LOGIN... |
| `entidade` | VARCHAR(100) | Nome da entidade afetada |
| `entidade_id` | UUID | ID da entidade |
| `dados_anteriores` | JSONB | Snapshot antes da alteração |
| `dados_novos` | JSONB | Snapshot depois da alteração |
| `ip` | VARCHAR(50) | IP do usuário |
| `user_agent` | VARCHAR(500) | User-Agent do browser |

### 7.4 Configurações Adicionais

#### `configuracao_empresa` — Configurações Gerais
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK UNIQUE | Uma config por empresa |

#### `onboarding_progress` — Progresso de Onboarding
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | UUID PK | Identificador |
| `empresa_id` | UUID FK | Empresa |
| `passo_atual` | INT | Passo atual do wizard |
| `empresa_configurada` | BOOLEAN | Step 1 concluído |
| `equipe_cadastrada` | BOOLEAN | Step 2 concluído |
| `marca_personalizada` | BOOLEAN | Step 3 concluído |
| `primeiro_cliente` | BOOLEAN | Step 4 concluído |
| `primeira_proposta` | BOOLEAN | Step 5 concluído |
| `concluido` | BOOLEAN | Onboarding finalizado |
| `data_conclusao` | TIMESTAMP | Data de conclusão |

### 7.5 Índices e Constraints

```sql
-- Índices de busca por tenant (performance multi-tenant)
CREATE INDEX idx_cliente_empresa ON cliente(empresa_id);
CREATE INDEX idx_proposta_empresa ON proposta(empresa_id);
CREATE INDEX idx_apolice_empresa ON apolice(empresa_id);
CREATE INDEX idx_sinistro_empresa ON sinistro(empresa_id);

-- Índices compostos para queries frequentes
CREATE INDEX idx_proposta_empresa_status ON proposta(empresa_id, status);
CREATE INDEX idx_apolice_empresa_status ON apolice(empresa_id, status);
CREATE INDEX idx_apolice_vigencia ON apolice(data_fim_vigencia);
CREATE INDEX idx_boleto_vencimento ON boleto(data_vencimento, status);
CREATE INDEX idx_lancamento_vencimento ON lancamento_financeiro(data_vencimento, status);

-- Unique Constraints
ALTER TABLE empresa ADD CONSTRAINT uk_empresa_cnpj UNIQUE (cnpj);
ALTER TABLE empresa ADD CONSTRAINT uk_empresa_slug UNIQUE (slug);
ALTER TABLE usuario ADD CONSTRAINT uk_usuario_email UNIQUE (email);
ALTER TABLE cliente ADD CONSTRAINT uk_cliente_cpf_cnpj UNIQUE (cpf_cnpj);
ALTER TABLE configuracao_white_label ADD CONSTRAINT uk_white_label_empresa UNIQUE (empresa_id);
```

---

## 8. Estrutura de Pacotes Java

```
com.insuranceflow/
│
├── InsuranceFlowApplication.java          # Main class
│
├── auth/                                  # Autenticação
│   ├── controller/AuthController.java     # POST /api/auth/login, /refresh
│   ├── dto/LoginRequest.java             # { email, senha }
│   ├── dto/LoginResponse.java            # { token, refreshToken, perfil, empresaId }
│   ├── model/Usuario.java                # @Entity usuario
│   ├── model/Perfil.java                 # Enum: ADMIN_MASTER, ADMIN_EMPRESA, GERENTE, OPERADOR, CLIENTE
│   ├── repository/UsuarioRepository.java
│   └── service/AuthService.java           # login(), refreshToken()
│
├── master/                                # Gestão SaaS (Admin Master)
│   ├── controller/MasterController.java   # /api/master/**
│   ├── controller/PublicController.java   # /api/public/**
│   ├── dto/                              # DTOs: PlanoRequest, EmpresaRequest, DashboardMasterResponse...
│   ├── model/                            # Plano, Empresa, AssinaturaEmpresa, PagamentoAssinatura,
│   │                                     # ConfiguracaoWhiteLabel, LeadSaas, TicketSuporte
│   ├── repository/                       # 7 repositories
│   └── service/MasterService.java         # Dashboard SaaS, CRUD empresas/planos
│
├── tenant/                                # Operacional Multi-Tenant
│   ├── controller/TenantController.java   # /api/tenant/**
│   ├── dto/                              # DTOs: ClienteRequest, PropostaRequest, DashboardTenantResponse...
│   ├── model/                            # Cliente, Seguradora, Corretora, RamoSeguro, Proposta,
│   │                                     # Apolice, Renovacao, Sinistro, LancamentoFinanceiro,
│   │                                     # Boleto, Comissao, Documento, Notificacao, Auditoria
│   ├── repository/                       # 14 repositories
│   └── service/TenantService.java         # CRUD com TenantContext filtering
│
├── portal/                                # Portal do Cliente
│   └── controller/PortalClienteController.java  # /api/portal/**
│
├── web/                                   # Páginas HTML
│   └── WebPageController.java             # /, /login, /master, /admin, /portal
│
├── security/                              # Segurança & JWT
│   ├── JwtAuthenticationFilter.java       # OncePerRequestFilter
│   ├── JwtTokenProvider.java              # HMAC-SHA512
│   ├── UserPrincipal.java                 # implements UserDetails
│   ├── CustomUserDetailsService.java      # implements UserDetailsService
│   └── TenantContext.java                 # ThreadLocal<UUID>
│
├── config/                                # Configurações Spring
│   ├── SecurityConfig.java                # SecurityFilterChain, BCryptEncoder
│   ├── WebConfig.java                     # CORS
│   ├── SwaggerConfig.java                # OpenAPI
│   └── AuditConfig.java                  # Spring Data Auditing
│
└── common/                                # Classes Base Compartilhadas
    ├── model/BaseEntity.java              # UUID id, timestamps, audit
    ├── model/TenantBaseEntity.java        # BaseEntity + empresaId
    ├── dto/ApiResponse.java               # { success, message, data, timestamp }
    └── exception/
        ├── BusinessException.java
        ├── ResourceNotFoundException.java
        ├── AccessDeniedException.java
        ├── PlanLimitExceededException.java
        └── GlobalExceptionHandler.java    # @RestControllerAdvice
```

---

## 9. Endpoints da API

### 9.1 Autenticação (`/api/auth`)

| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| POST | `/api/auth/login` | Login (email + senha) → JWT | Público |
| POST | `/api/auth/refresh` | Renovar access token | Público |

### 9.2 Master SaaS (`/api/master`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/master/dashboard` | Dashboard SaaS (MRR, ARR, totais) |
| GET | `/api/master/planos` | Listar planos |
| POST | `/api/master/planos` | Criar plano |
| GET | `/api/master/empresas` | Listar empresas (tenants) |
| POST | `/api/master/empresas` | Criar empresa |
| PUT | `/api/master/empresas/{id}/bloquear` | Bloquear empresa |
| GET | `/api/master/assinaturas` | Listar assinaturas |
| GET | `/api/master/pagamentos` | Listar pagamentos |
| GET | `/api/master/leads` | Listar leads SaaS |
| GET | `/api/master/tickets` | Listar tickets de suporte |

### 9.3 Tenant Operacional (`/api/tenant`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/tenant/dashboard` | Dashboard operacional da empresa |
| GET | `/api/tenant/clientes` | Listar clientes |
| POST | `/api/tenant/clientes` | Criar cliente |
| GET | `/api/tenant/seguradoras` | Listar seguradoras |
| GET | `/api/tenant/corretoras` | Listar corretoras |
| GET | `/api/tenant/propostas` | Listar propostas |
| POST | `/api/tenant/propostas` | Criar proposta |
| GET | `/api/tenant/apolices` | Listar apólices |
| GET | `/api/tenant/sinistros` | Listar sinistros |
| GET | `/api/tenant/renovacoes` | Listar renovações |
| GET | `/api/tenant/financeiro` | Listar lançamentos financeiros |

### 9.4 Portal do Cliente (`/api/portal`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/portal/apolices` | Minhas apólices |
| GET | `/api/portal/sinistros` | Meus sinistros |
| GET | `/api/portal/boletos` | Meus boletos |

### 9.5 Público

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/public/**` | Dados públicos |
| POST | `/api/leads-saas` | Captura de leads |
| POST | `/api/onboarding/**` | Cadastro de empresa |

---

## 10. Planos e Limites SaaS

| Feature | Starter (R$197/mês) | Professional (R$497/mês) | Business (R$997/mês) | Enterprise (R$2.497/mês) |
|---------|---------------------|--------------------------|----------------------|--------------------------|
| Usuários | 3 | 10 | 25 | Ilimitado |
| Clientes | 100 | 500 | 2.000 | Ilimitado |
| Propostas/mês | 30 | 150 | 500 | Ilimitado |
| Apólices | 100 | 500 | 2.000 | Ilimitado |
| Armazenamento | 2 GB | 10 GB | 50 GB | 500 GB |
| Portal do Cliente | ❌ | ✅ | ✅ | ✅ |
| WhatsApp | ❌ | ❌ | ✅ | ✅ |
| Relatórios Avançados | ❌ | ✅ | ✅ | ✅ |
| White Label | ❌ | ❌ | ✅ | ✅ |
| Acesso API | ❌ | ❌ | ✅ | ✅ |
| Suporte Prioritário | ❌ | ❌ | ✅ | ✅ |

---

> **InsuranceFlow SaaS** — Plataforma completa de gestão para administradoras de seguros.
> Arquitetura multi-tenant, segurança enterprise-grade, pronta para escalar.
