# ETAPA 19 - Roteiro de Apresentacao Senior

## 1) Problema de negocio
Empresas perdem eficiencia com atendimento fragmentado, falta de rastreabilidade e baixa previsibilidade de SLA.

## 2) Contexto corporativo
Ambiente com multiplas areas de suporte, necessidade de compliance, auditoria e controle de acesso por perfil.

## 3) Escolha da stack
- Java/Spring Boot para robustez transacional e ecossistema enterprise
- Angular para UI corporativa estruturada
- PostgreSQL para consistencia e consultas ricas
- Redis para performance e controles de sessao
- Kafka para eventos e desacoplamento

## 4) Arquitetura
Monolito modular orientado a dominio, preparado para extracao incremental de servicos.

## 5) Separacao de responsabilidades
- Controller: contrato HTTP
- Service: regra de negocio
- Repository: acesso a dados
- Security: autenticacao/autorizacao
- Messaging: eventos/outbox
- Exception/Observability: operacao

## 6) JWT + Refresh Token
- access token curto para reduzir risco
- refresh token rotativo para sessao longa com controle
- blacklist no Redis em logout

## 7) RBAC
- roles e permissoes no banco
- endpoint protection com `@PreAuthorize`
- testes de permissao para sucesso/falha

## 8) PostgreSQL
- modelo relacional com Flyway
- versionamento de schema
- indices e constraints para consistencia

## 9) Redis
- blacklist JWT
- cache de consultas
- base para lock/distributed coordination futura

## 10) Kafka
- eventos de negocio via outbox
- retries e backoff para resiliencia
- base para notificacoes e integrações futuras

## 11) Angular consumindo Spring Boot
- camada de services
- interceptador JWT
- guards de autenticacao
- componentes criticos cobertos por testes

## 12) Auditoria
- trilhas de alteracao de entidades
- contexto de usuario e acao
- possibilidade de sink para SIEM

## 13) Notificacoes
- eventos para pedido de notificacao
- preparo para consumer dedicado (email/push/sms)

## 14) Anexos
- fluxo de upload com validacao de mime/limite
- ponto de evolucao para S3

## 15) Observabilidade
- logs estruturados JSON
- correlation id
- actuator health/readiness
- metricas outbox

## 16) CI/CD
- pipeline com build/test/quality
- versionamento por tag e SHA
- docker push para registry
- estrategia de deploy por ambiente

## 17) AWS
- frontend: S3+CloudFront
- backend: ECS Fargate+ALB
- dados: RDS + ElastiCache
- stream: MSK
- segredos: Secrets Manager

## 18) Preparacao para microsservicos
Candidatos naturais de split:
- auth service
- ticket service
- notification service
- audit service
- user management service

Mantido simples no inicio com monolito modular e eventos desacopladores.

## 19) Trade-offs
- monolito reduz custo operacional inicial
- eventos no outbox aumentam robustez, mas adicionam complexidade
- fallback de chave JWT facilita dev, mas em producao exige chave gerenciada

## 20) Como o projeto demonstra senioridade
- decisoes com equilibrio entre curto e longo prazo
- foco em operabilidade (logs/health/metricas)
- seguranca por design (JWT, RBAC, revogacao)
- arquitetura evolutiva para crescimento sem reescrever tudo
