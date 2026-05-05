# SupportDesk Pro X

Plataforma enterprise para gestao de chamados, com arquitetura preparada para evoluir de monolito modular para microsservicos.

## 1. Visao geral
O projeto resolve o problema de atendimento e acompanhamento de chamados em ambientes corporativos, com foco em:
- autenticacao segura
- controle de acesso por papeis
- rastreabilidade
- auditoria
- notificacoes/eventos
- operacao observavel

## 2. Stack
### Backend
- Java 21
- Spring Boot 3.3.5
- Spring Security + JWT (access + refresh)
- Spring Data JPA
- PostgreSQL
- Redis
- Kafka
- Flyway
- Actuator + Micrometer + Prometheus

### Frontend
- Angular 18 (standalone components)
- Angular Material
- RxJS

### Plataforma
- Docker + Docker Compose
- GitHub Actions CI/CD

## 3. Arquitetura
Arquitetura em monolito modular com separacao por dominios e responsabilidades:
- `controller`: interfaces HTTP
- `service`: regras de negocio
- `repository`: persistencia
- `security`: autenticacao/autorizacao
- `messaging`: eventos e outbox
- `exception`: tratamento de falhas
- `observability/health`: rastreabilidade e probes

## 4. Modulos principais
- Auth
- Tickets
- Comments
- Categories
- Attachments
- Audit
- Notification/event-driven

## 5. Diferenciais tecnicos
- JWT com access token curto + refresh token rotativo
- RBAC com `@PreAuthorize`
- Outbox Pattern para eventos
- Logs estruturados (JSON)
- Correlation ID fim a fim
- Readiness/Liveness completos
- Testes backend + testes Angular para componentes criticos

## 6. Execucao local (sem Docker)
### Backend
```bash
cd backend
mvn -B clean test
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

Frontend em `http://localhost:4200` e backend em `http://localhost:8080`.

## 7. Execucao com Docker
```bash
cp .env.example .env
docker compose up -d --build
```

Servicos:
- frontend: `http://localhost:4200`
- backend: `http://localhost:8080`
- kafka-ui: `http://localhost:8090`
- postgres: `localhost:5432`
- redis: `localhost:6379`
- kafka: `localhost:9092`

## 8. Acesso ao Swagger
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/api-docs`

## 9. Credenciais iniciais
As roles e permissoes sao semeadas por Flyway (`V4__seed_roles_permissions.sql`).
Para ambiente local, crie usuarios via endpoint de registro:
- `POST /api/v1/auth/register`

## 10. Fluxo de autenticacao
1. `POST /api/v1/auth/login` -> recebe access e refresh
2. Access token no header `Authorization: Bearer ...`
3. Em expiracao, `POST /api/v1/auth/refresh`
4. `POST /api/v1/auth/logout` revoga refresh e blacklista JWT

## 11. Uso de Redis
- blacklist de JWT (`jwt:blacklist:{jti}`)
- cache de respostas/consultas (configuravel)

## 12. Uso de Kafka
- eventos de dominio publicados via outbox
- retries com backoff
- pronto para consumers de notificacao/auditoria desacoplados

## 13. Pipeline CI/CD
Arquivo: `.github/workflows/ci-cd.yml`
Fluxo:
1. build e testes do backend
2. build, typecheck e testes do frontend
3. analise basica (checkstyle + cobertura)
4. build/push de imagens docker no GHCR
5. estrategia de deploy com ambientes

## 14. Visao AWS
Resumo da arquitetura alvo em `docs/AWS-ARCHITECTURE.md`:
- frontend: S3 + CloudFront
- API: ECS Fargate + ALB + WAF
- banco: RDS PostgreSQL
- cache: ElastiCache Redis
- anexos: S3
- stream: MSK
- segredos: Secrets Manager + SSM
- observabilidade: CloudWatch

## 15. Roadmap
- implementar consumidores de notificacao dedicados
- completar testes de contrato e e2e
- politicas de rate limiting e circuit breaker
- assinatura de eventos com schema registry
- multi-tenant readiness

## 16. Melhorias futuras
- extracao gradual de microsservicos por dominio
- observabilidade distribuida com OpenTelemetry
- SLOs automatizados com alertas por erro/latencia
- deploy canario automatizado

## Execucao de testes
### Backend
```bash
cd backend
mvn -B test
```

### Frontend
```bash
cd frontend
npm install
npm run test:ci
```

## Observabilidade e resiliencia
Detalhes completos em:
- `docs/OBSERVABILITY-RESILIENCE.md`

## Preparacao para evolucao em microsservicos
A estrutura atual foi pensada para separar futuramente:
- auth service
- ticket service
- notification service
- audit service
- user management service

Sem quebrar simplicidade inicial porque:
- regras estao encapsuladas por servico
- contratos via DTOs e eventos ja definidos
- fronteiras de dominio estao explicitas
- outbox reduz acoplamento para extracao futura
