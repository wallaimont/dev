# Orbyt Market — Product Blueprint

## Etapa 1 — Nome da Plataforma e Branding

**Nome**: Orbyt Market
**Tagline**: "O marketplace enterprise que orbita o seu negócio."

### Identidade Visual
- **Paleta principal**: Night (#0f172a), Ember (#f59e0b), Ocean (#0891b2), Mist (#e2e8f0)
- **Paleta complementar**: Emerald (#10b981) para sucesso, Rose (#f43f5e) para alertas
- **Tipografia**: Inter para UI, JetBrains Mono para código/dados
- **Ícones**: Lucide React (consistente, MIT)
- **Bordas**: Arredondadas (28px panels, 12px buttons, 8px inputs)
- **Efeitos**: Glassmorphism sutil, shadow-float, gradientes radiais

### Posicionamento
- Plataforma enterprise para operadores de marketplace, grupos varejistas e ecossistemas B2B2C
- Identidade premium, tecnológica e confiável
- Linguagem visual inspirada em órbita, fluxo financeiro e conexão entre empresas, vendedores e compradores
- Design system próprio sem copiar identidade de concorrentes
- Visual pronto para investidores e pitch decks

---

## Etapa 2 — Proposta de Valor e Diferenciais

### Proposta de Valor
Orbyt Market é uma plataforma marketplace completa, multi-tenant, multi-seller e multi-moeda que permite a qualquer empresa operar seu próprio ecossistema de vendas com qualidade de startup unicórnio.

### Diferenciais Competitivos
1. **Multi-tenant nativo**: cada cliente opera seu marketplace isolado, com identidade própria
2. **Split payment de primeira classe**: PIX, cartão e boleto com divisão automática
3. **Antifraude integrado**: score de risco, blacklist, detecção de padrões
4. **Arquitetura event-driven**: Kafka + outbox pattern para desacoplamento total
5. **Observabilidade desde o dia 1**: logging estruturado, métricas, auditoria, health checks
6. **Multi-idioma e multi-moeda**: pt-BR, en-US, es-ES com BRL, USD, EUR
7. **Dashboards especializados**: comprador, vendedor, admin, super admin
8. **Mobile-ready**: React Native/Expo consumindo a mesma API
9. **RBAC granular**: permissões por recurso e ação
10. **Preparado para escala**: monólito modular com caminho claro para microserviços

### Modelo de Receita
- Comissão por venda (configurável por tenant, entre 5% e 20%)
- Planos de assinatura para tenants (Starter, Growth, Enterprise)
- Taxas de antecipação de recebíveis
- Anúncios e vitrines patrocinadas
- Integração premium (APIs, webhooks)

---

## Etapa 3 — Arquitetura Geral Recomendada

### Decisão: Monólito Modular Enterprise

**Motivos**:
- Acelera go-to-market (time-to-production 3-6 meses vs 12+ em microserviços)
- Simplifica transações distribuídas em checkout + pagamento + estoque
- Custo de operação inicial baixo (1 JVM, 1 deploy)
- Cada módulo é um bounded context com contratos claros
- Kafka events + outbox pattern preparam extração futura

### Diagrama de Módulos

```
┌─────────────────────────────────────────────────────────┐
│                    ORBYT MARKET CORE                     │
├─────────┬──────────┬──────────┬───────────┬─────────────┤
│ platform│ identity │ catalog  │  cart     │   order     │
│ (tenant)│ (auth)   │ (product)│ (basket)  │  (checkout) │
├─────────┼──────────┼──────────┼───────────┼─────────────┤
│ payment │ shipping │ engage   │   cms     │ operations  │
│ (pix)   │ (frete)  │ (review) │ (banner)  │  (admin)    │
├─────────┴──────────┴──────────┴───────────┴─────────────┤
│                    SHARED KERNEL                         │
│  BaseEntity · TenantContext · Events · Security · DTOs   │
├─────────────────────────────────────────────────────────┤
│     PostgreSQL  ·  Redis  ·  Kafka  ·  S3 (futuro)      │
└─────────────────────────────────────────────────────────┘
```

### Caminho para Microserviços (Ordem de Extração)

1. **notification-service** — menos acoplado, consome eventos
2. **chat-service** — WebSocket isolado, alta carga independente
3. **payment-service** — regulatório/compliance separado
4. **catalog-service** — leitura intensa, cacheable
5. **order-service** — orquestração central, por último

---

## Etapa 4 — Stack Tecnológica Definitiva

### Backend
| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 (LTS) | Runtime |
| Spring Boot | 3.3.x | Framework |
| Spring Security | 6.x | Auth/RBAC |
| Spring Data JPA | 3.x | ORM |
| Hibernate | 6.x | Persistence |
| PostgreSQL | 16 | Database |
| Redis | 7 | Cache/Sessions |
| Kafka (Confluent) | 7.6 | Events |
| Flyway | 10.x | Migrations |
| Lombok | 1.18.x | Code gen |
| MapStruct | 1.5.x | DTO mapping |
| SpringDoc OpenAPI | 2.x | API docs |
| JUnit 5 | 5.10.x | Testing |
| Mockito | 5.x | Mocking |
| Testcontainers | 1.19.x | Integration tests |

### Frontend Web
| Tecnologia | Versão | Uso |
|---|---|---|
| Next.js | 14.x | Framework SSR/SSG |
| React | 18.x | UI Library |
| TypeScript | 5.x | Type safety |
| Tailwind CSS | 3.x | Styling |
| TanStack Query | 5.x | Data fetching |
| Zustand | 4.x | State management |
| React Hook Form | 7.x | Forms |
| Zod | 3.x | Validation |
| next-intl | 3.x | i18n |
| Axios | 1.x | HTTP client |
| Lucide React | latest | Icons |
| Recharts | 2.x | Charts |

### Mobile
| Tecnologia | Versão | Uso |
|---|---|---|
| Expo | SDK 51 | Framework |
| React Native | 0.74.x | Runtime |
| Expo Router | 3.x | Navigation |
| Zustand | 4.x | State |
| Axios | 1.x | HTTP |

### DevOps
| Tecnologia | Uso |
|---|---|
| Docker | Containerização |
| Docker Compose | Orquestração local |
| Nginx | Reverse proxy |
| GitHub Actions | CI/CD |
| profiles (dev/staging/prod) | Ambientes |

---

## Etapas 5 a 20

Materializadas nos arquivos do repositório:
- **Etapa 5**: `backend/src/main/resources/db/migration/` (V1 a V5)
- **Etapa 6-8**: `backend/src/main/java/com/orbyt/marketplace/` (todos os módulos)
- **Etapa 9**: Endpoints documentados em `docs/api.md`
- **Etapa 10-14**: Fluxos em `docs/flows.md`
- **Etapa 15**: Código backend completo
- **Etapa 16**: Código frontend em `frontend/`
- **Etapa 17-18**: Telas e dashboards
- **Etapa 19**: README e Docker setup
- **Etapa 20**: Roadmap em `docs/roadmap.md`
