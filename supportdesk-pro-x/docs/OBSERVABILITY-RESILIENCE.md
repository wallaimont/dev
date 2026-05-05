# ETAPA 13 - Observabilidade e Resiliencia

## 1. Logs estruturados
- Implementado `logback-spring.xml` com JSON logging.
- Campos principais:
  - timestamp
  - level
  - logger
  - thread
  - correlationId
  - message
  - exception

## 2. Tratamento global de excecoes
- `GlobalExceptionHandler` centraliza erros de negocio, validacao, autenticacao e inesperados.
- Sempre retorna payload padronizado (`ApiError`) com status e contexto da falha.

## 3. Mensagens tecnicas e amigaveis
- `ApiError` agora contem:
  - `friendlyMessage` para UX
  - `technicalMessage` para troubleshooting
  - `message` apontando mensagem de negocio

## 4. Health/readiness conceitualmente
- Actuator com probes habilitados:
  - liveness
  - readiness
- Readiness inclui DB, Redis, Kafka e storage.

## 5. Readiness para metricas futuras
- Micrometer + Prometheus registry adicionado no backend.
- Endpoint `/actuator/prometheus` exposto para scraping.

## 6. Estrategia de monitoramento
- Sinais minimos:
  - latencia p95/p99 por endpoint
  - taxa de erro 4xx/5xx
  - disponibilidade readiness/liveness
  - contadores de outbox (publish/retry/fail)
- Dashboards sugeridos: Grafana + Prometheus.

## 7. Pontos de resiliencia
- Outbox pattern com retries e backoff exponencial.
- Fallback de chave JWT em ambiente dev para nao bloquear startup.
- Healthchecks no compose para dependencia de servicos.

## 8. Tratamento de falhas assincronas
- `AsyncConfig` com:
  - `AsyncUncaughtExceptionHandler`
  - propagacao de MDC para threads assincronas
- `OutboxPollingService` monitora e contabiliza falhas/retries.

## 9. Preocupacao com rastreabilidade
- `CorrelationIdFilter` injeta e propaga `X-Correlation-Id`.
- CorrelationId aparece em logs e respostas de erro.
