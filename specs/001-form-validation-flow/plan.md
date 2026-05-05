# Implementation Plan: Melhoria do Fluxo de Formularios

**Branch**: `[001-form-validation-flow]` | **Date**: 2026-04-17 | **Spec**: `/specs/001-form-validation-flow/spec.md`
**Input**: Feature specification from `/specs/001-form-validation-flow/spec.md`

## Implementation Context Confirmation

- Feature: `001-form-validation-flow`
- Branch metadata: `001-form-validation-flow`
- Current implementation round scope: Phase 1 (Setup), Phase 2 (Foundational), Phase 3 (US1 MVP)
- Confirmed date: 2026-04-17

## Summary

Padronizar o fluxo de formularios do site da seguradora (cotacao e contato) com validacao ponta a ponta, mensagens de erro consistentes e cobertura de testes anti-regressao. A abordagem combina validacao client-side para feedback imediato, validacao server-side autoritativa com Bean Validation, modelo unico de erros por campo e observabilidade minima por submissao (logs estruturados, metrica de taxa de erro e latencia por formulario), incluindo metas de latencia p95 <= 2s e rollout gradual com fallback.

## Technical Context

**Language/Version**: Java 17, Spring Boot 3.3.5  
**Primary Dependencies**: spring-boot-starter-web, spring-boot-starter-thymeleaf, spring-boot-starter-validation, spring-boot-starter-security, spring-boot-starter-test  
**Storage**: H2 (dev/runtime local), SQL Server (runtime), persistencia via JPA para leads  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc  
**Target Platform**: Aplicacao web server-side (Spring MVC) em ambiente JVM Linux/Windows
**Project Type**: Web application MVC server-side  
**Performance Goals**: p95 de submissao (validacao + resposta) <= 2s; p99 <= 3s em condicoes normais  
**Constraints**: Nao vazar detalhes internos em erros; bloquear submissao invalida/duplicada imediata; manter compatibilidade com templates Thymeleaf existentes  
**Scale/Scope**: Formularios principais em escopo: `/cotacao` e `/contato`; cobertura para camada MVC e API relacionada a leads

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Clean code and low coupling approach documented (camadas: template -> DTO validado -> servico -> persistencia; responsabilidades separadas)
- [x] Minimum automated tests defined per change scope (unitario para regras, MockMvc para fluxos e regressao)
- [x] Security baseline covered (validacao/sanitizacao de entrada, excecoes seguras, sem segredos em codigo)
- [x] Performance expectation and observability baseline defined (latencia alvo, logs estruturados, metricas por formulario)
- [x] Documentation and traceability plan defined (FR/NFR -> cenarios -> tarefas -> evidencias de PR)
- [x] Multi-stack impact assessed for Java, Python, and frontend when applicable (impacto principal em Java MVC + frontend server-side)
- [x] PR quality gates identified (revisao, evidencias de testes e bloqueios de merge)

Re-check pos-design: sem violacoes novas identificadas.

## Validation and Error Strategy

### End-to-End Validation

1. Client-side (UX imediata)
- HTML5 constraints + script discreto de validacao para `blur` e `submit`.
- Exibir mensagens por campo com foco no primeiro erro.
- Prevenir duplo envio com desabilitacao temporaria do botao e idempotency token de curta duracao no formulario.

2. Server-side (autoritativa)
- Manter `@Valid` em `LeadRequest` e complementar com regras de dominio (normalizacao de espacos, formatos e limites) em validador dedicado.
- Rejeitar submissao com `BindingResult` contendo erros e retornar mesma view com mapa padrao de mensagens por campo.

3. Consistencia de mensagens
- Catalogo unico de mensagens por chave de campo/regra (ex.: `lead.nome.required`, `lead.email.invalid`).
- Contrato de exibicao unificado para `/cotacao` e `/contato`, evitando divergencia entre front e back.

### Error Model and Observability

- Modelo de erro por submissao: `submissionId`, `formType`, `status` (`SUCCESS`, `VALIDATION_ERROR`, `INTERNAL_ERROR`), `fieldErrors`, `durationMs`, `timestamp`.
- Logs estruturados em JSON para cada submissao, com mascaramento de PII sensivel (email parcialmente mascarado; sem stack trace para usuario).
- Metricas minimas:
  - `form_submission_total{formType,status}`
  - `form_submission_latency_ms{formType}` (histograma para p95/p99)
  - KPI derivado: taxa de erro por formulario = `VALIDATION_ERROR + INTERNAL_ERROR` / total.

### Security Baseline

- Sanitizacao e normalizacao de entrada em fronteira MVC (trim, collapse de whitespace, validacao de tamanho e formato).
- Saidas escapadas pelo Thymeleaf e proibicao de ecoar payload bruto em erro.
- Tratamento centralizado de excecoes com mensagem amigavel unica e correlacao por `submissionId` nos logs.

## Testing Strategy

1. Unitarios
- Validadores de regras de negocio e normalizacao (`nome`, `email`, `telefone`, `valorBem`, `mensagem`, `origem`).

2. Integracao com MockMvc
- Cenarios de sucesso e falha para `POST /cotacao` e `POST /contato`.
- Assercoes sobre mensagens por campo, resposta amigavel em erro interno e bloqueio de duplo envio imediato.

3. Regressao
- Matriz de obrigatoriedade e formatos invalidos por campo.
- Casos de borda: whitespace-only, payloads longos, repeticao rapida de submit.

## Performance and Rollout

- Metas de latencia: p95 <= 2s e p99 <= 3s para submissao em ambiente de referencia.
- Rollout gradual:
  1. Feature flag para validacao reforcada em `%` de sessoes.
  2. Monitorar erro/latencia por 24h em cada incremento (10% -> 50% -> 100%).
  3. Fallback rapido: desligar feature flag e manter comportamento anterior sem interromper captacao.

## Project Structure

### Documentation (this feature)

```text
specs/001-form-validation-flow/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── form-validation-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
seguradora-java-site/
├── src/main/java/com/seguradora/site/
│   ├── SiteController.java
│   ├── model/LeadRequest.java
│   ├── config/GlobalExceptionHandler.java
│   └── service/
├── src/main/resources/templates/
│   ├── cotacao.html
│   ├── contato.html
│   └── error.html
└── src/test/java/com/seguradora/site/
    └── SiteControllerTest.java
```

**Structure Decision**: Manter arquitetura MVC server-side existente, com evolucao incremental em DTO de entrada, controladores e templates Thymeleaf, apoiada por testes MockMvc no mesmo modulo Maven (`seguradora-java-site`).

## PR Quality Gates and Traceability

- Mapeamento obrigatorio no PR: FR/NFR -> testes unitarios/integracao -> evidencias de execucao.
- Bloqueios de merge: testes falhando, ausencia de casos invalidos, mensagem de erro insegura, ou falta de evidencia de latencia/erros.
- Evidencias minimas: saida do `mvn -B test`, exemplos de log estruturado e snapshot de metricas por formulario.

## Complexity Tracking

Sem violacoes constitucionais que exijam justificativa.
