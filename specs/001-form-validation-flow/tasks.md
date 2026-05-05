# Tasks: Melhoria do Fluxo de Formularios

**Input**: Design documents from `/specs/001-form-validation-flow/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Minimum automated tests are REQUIRED for every functional change. Include test tasks for each user story.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Single project structure under `seguradora-java-site/`
- Backend code in `seguradora-java-site/src/main/java/`
- Templates and static assets in `seguradora-java-site/src/main/resources/`
- Tests in `seguradora-java-site/src/test/java/`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar baseline de feature, dependencias e estrutura de suporte para implementacao incremental.

- [X] T001 Confirm feature context and branch metadata in specs/001-form-validation-flow/plan.md
- [X] T002 Add implementation tracking section for this feature in specs/001-form-validation-flow/quickstart.md
- [X] T003 [P] Prepare test package structure for validation flow in seguradora-java-site/src/test/java/com/seguradora/site/validation/
- [X] T004 [P] Prepare frontend asset structure for form UX enhancements in seguradora-java-site/src/main/resources/static/js/

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestrutura base obrigatoria para todas as historias (validacao central, observabilidade, feature flag e fallback).

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [X] T005 Add metrics dependency and validate build config in seguradora-java-site/pom.xml
- [X] T006 Create feature-flag properties model for form validation rollout in seguradora-java-site/src/main/java/com/seguradora/site/config/FormValidationProperties.java
- [X] T007 Create central form validation toggle service with fallback policy in seguradora-java-site/src/main/java/com/seguradora/site/service/FormValidationFeatureFlagService.java
- [X] T008 [P] Create structured submission event model in seguradora-java-site/src/main/java/com/seguradora/site/model/FormSubmissionEvent.java
- [X] T009 Create telemetry service for logs and metrics emission in seguradora-java-site/src/main/java/com/seguradora/site/service/FormSubmissionTelemetryService.java
- [X] T010 [P] Create centralized validation error catalog in seguradora-java-site/src/main/resources/messages.properties
- [X] T011 Configure feature-flag defaults and observability keys in seguradora-java-site/src/main/resources/application.properties

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel.

---

## Phase 3: User Story 1 - Enviar formulario sem bloqueios evitaveis (Priority: P1) 🎯 MVP

**Goal**: Garantir envio consistente de cotacao e contato com validacao server-side autoritativa e bloqueio de submits invalidos/duplicados.

**Independent Test**: Executar testes de validacao e MockMvc para `POST /cotacao` e `POST /contato`, comprovando sucesso com payload valido e bloqueio com payload invalido.

### Tests for User Story 1 (REQUIRED) ⚠️

- [X] T012 [P] [US1] Add unit tests for LeadRequest required and format rules in seguradora-java-site/src/test/java/com/seguradora/site/validation/LeadRequestValidationTest.java
- [X] T013 [P] [US1] Add unit tests for whitespace normalization and trimming rules in seguradora-java-site/src/test/java/com/seguradora/site/validation/FormInputNormalizationTest.java
- [X] T014 [US1] Add MockMvc tests for success and validation-block scenarios on /cotacao and /contato in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerFormFlowTest.java

### Implementation for User Story 1

- [X] T015 [US1] Strengthen Bean Validation constraints and per-form requirements in seguradora-java-site/src/main/java/com/seguradora/site/model/LeadRequest.java
- [X] T016 [P] [US1] Implement input normalization service for trim/collapse whitespace in seguradora-java-site/src/main/java/com/seguradora/site/service/FormInputNormalizationService.java
- [X] T017 [US1] Integrate normalization and origin-aware validation into form POST handlers in seguradora-java-site/src/main/java/com/seguradora/site/SiteController.java
- [X] T018 [US1] Implement duplicate immediate submit guard (token/window) for MVC flow in seguradora-java-site/src/main/java/com/seguradora/site/service/FormDuplicateSubmissionGuardService.java
- [X] T019 [US1] Wire duplicate submit guard into cotacao and contato handlers in seguradora-java-site/src/main/java/com/seguradora/site/SiteController.java
- [X] T020 [US1] Add hidden idempotency token fields and submission binding updates in seguradora-java-site/src/main/resources/templates/cotacao.html
- [X] T021 [US1] Add hidden idempotency token fields and submission binding updates in seguradora-java-site/src/main/resources/templates/contato.html

**Checkpoint**: User Story 1 deve estar funcional e validavel isoladamente com testes automatizados e fluxo MVP completo.

---

## Phase 4: User Story 2 - Corrigir erros com feedback claro (Priority: P2)

**Goal**: Padronizar mensagens e UX de erro/sucesso para reduzir friccao na correcao de dados.

**Independent Test**: Simular multiplos erros por formulario e validar que mensagens sao claras, consistentes e posicionadas corretamente no front.

### Tests for User Story 2 (REQUIRED) ⚠️

- [ ] T022 [P] [US2] Add MockMvc tests for multi-field error summary consistency in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerFeedbackTest.java
- [ ] T023 [P] [US2] Add MockMvc tests for user-friendly internal error message behavior in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerInternalErrorFeedbackTest.java
- [ ] T024 [US2] Add template rendering assertions for field highlight and error banner in seguradora-java-site/src/test/java/com/seguradora/site/TemplateFeedbackRenderingTest.java

### Implementation for User Story 2

- [ ] T025 [US2] Implement reusable fieldErrors and errorSummary mapper for MVC model in seguradora-java-site/src/main/java/com/seguradora/site/service/FormErrorPresentationService.java
- [ ] T026 [US2] Apply standardized error mapping and success feedback contract in cotacao/contact handlers in seguradora-java-site/src/main/java/com/seguradora/site/SiteController.java
- [ ] T027 [P] [US2] Add progressive client-side validation and first-error focus behavior in seguradora-java-site/src/main/resources/static/js/form-validation.js
- [ ] T028 [US2] Integrate client-side validation script and ARIA messaging hooks in seguradora-java-site/src/main/resources/templates/cotacao.html
- [ ] T029 [US2] Integrate client-side validation script and ARIA messaging hooks in seguradora-java-site/src/main/resources/templates/contato.html
- [ ] T030 [US2] Add consistent visual treatment for field error, summary and success states in seguradora-java-site/src/main/resources/static/css/styles.css

**Checkpoint**: US1 e US2 funcionam de forma independente, com mensagens coerentes entre formularios e UX previsivel.

---

## Phase 5: User Story 3 - Evoluir sem regressao (Priority: P3)

**Goal**: Garantir observabilidade e rollout seguro da nova validacao com testes de regressao e fallback imediato.

**Independent Test**: Executar suite completa e validar emissao de logs/metricas com feature flag em ON/OFF sem quebra de fluxo.

### Tests for User Story 3 (REQUIRED) ⚠️

- [ ] T031 [P] [US3] Add telemetry unit tests for SUCCESS/VALIDATION_ERROR/INTERNAL_ERROR event emission in seguradora-java-site/src/test/java/com/seguradora/site/service/FormSubmissionTelemetryServiceTest.java
- [ ] T032 [P] [US3] Add MockMvc tests for feature-flag ON/OFF behavior and fallback path in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerFeatureFlagFallbackTest.java
- [ ] T033 [US3] Add regression test matrix for edge cases (whitespace-only, long payload, rapid resubmit) in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerRegressionMatrixTest.java

### Implementation for User Story 3

- [ ] T034 [US3] Instrument structured submission logging with submissionId, formType, status and duration in seguradora-java-site/src/main/java/com/seguradora/site/service/FormSubmissionTelemetryService.java
- [ ] T035 [US3] Instrument metrics counters and latency timer for form submissions in seguradora-java-site/src/main/java/com/seguradora/site/service/FormSubmissionTelemetryService.java
- [ ] T036 [US3] Integrate telemetry lifecycle (received/validated/success/error) in seguradora-java-site/src/main/java/com/seguradora/site/SiteController.java
- [ ] T037 [US3] Ensure safe generic error handling with submissionId correlation in seguradora-java-site/src/main/java/com/seguradora/site/config/GlobalExceptionHandler.java
- [ ] T038 [US3] Implement runtime rollout controls (10%-50%-100%) with kill-switch fallback in seguradora-java-site/src/main/java/com/seguradora/site/service/FormValidationFeatureFlagService.java
- [ ] T039 [US3] Add rollout and fallback operational settings in seguradora-java-site/src/main/resources/application.properties

**Checkpoint**: Todas as historias estao cobertas por testes, telemetria e mecanismos de rollout/fallback.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Fechar documentacao minima, rastreabilidade e checklist de aceite para entrega segura.

- [ ] T040 [P] Update feature quickstart with execution order and verification commands in specs/001-form-validation-flow/quickstart.md
- [ ] T041 [P] Add FR/NFR to test mapping table in specs/001-form-validation-flow/requirements-traceability.md
- [ ] T042 [P] Document rollout strategy, kill-switch and fallback runbook in seguradora-java-site/README.md
- [ ] T043 Create acceptance checklist for manual and automated validation in specs/001-form-validation-flow/acceptance-checklist.md
- [ ] T044 Record evidence template for test runs, logs and metrics snapshots in specs/001-form-validation-flow/pr-evidence-template.md
- [ ] T045 Run full validation command sequence and annotate results in specs/001-form-validation-flow/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories.
- **User Stories (Phase 3+)**: Depend on Foundational completion.
- **Polish (Phase 6)**: Depends on all selected user stories complete.

### User Story Dependencies

- **US1 (P1)**: Starts after Phase 2; no dependency on US2/US3.
- **US2 (P2)**: Starts after Phase 2; can reuse US1 handlers but remains independently testable.
- **US3 (P3)**: Starts after Phase 2; validates regressions and rollout over US1/US2 behavior.

### Within Each User Story

- Tests first (at least one failing assertion before implementation for bug-fix paths).
- Model/normalization before controller integration.
- Controller integration before template/UX finalization.
- Telemetry and rollout wiring before release readiness checks.

### Parallel Opportunities

- Phase 1 tasks marked `[P]` can run together (`T003`, `T004`).
- In Phase 2, `T008` and `T010` can run in parallel.
- In US1, `T012` and `T013` can run in parallel before `T014`; `T016` can run in parallel with `T015`.
- In US2, `T022` and `T023` can run in parallel; `T027` can run in parallel with `T025`.
- In US3, `T031` and `T032` can run in parallel before `T033`.
- In Phase 6, `T040`, `T041`, `T042` can run in parallel.

---

## Parallel Example: User Story 1

```bash
# Parallel test authoring for US1
Task: "T012 Add unit tests for LeadRequest required and format rules in seguradora-java-site/src/test/java/com/seguradora/site/validation/LeadRequestValidationTest.java"
Task: "T013 Add unit tests for whitespace normalization and trimming rules in seguradora-java-site/src/test/java/com/seguradora/site/validation/FormInputNormalizationTest.java"

# Parallel implementation preparation for US1
Task: "T015 Strengthen Bean Validation constraints in seguradora-java-site/src/main/java/com/seguradora/site/model/LeadRequest.java"
Task: "T016 Implement input normalization service in seguradora-java-site/src/main/java/com/seguradora/site/service/FormInputNormalizationService.java"
```

---

## Parallel Example: User Story 2

```bash
# Parallel feedback tests
Task: "T022 Add MockMvc tests for multi-field error summary consistency in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerFeedbackTest.java"
Task: "T023 Add MockMvc tests for internal error friendly message in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerInternalErrorFeedbackTest.java"

# Parallel implementation
Task: "T025 Implement FormErrorPresentationService in seguradora-java-site/src/main/java/com/seguradora/site/service/FormErrorPresentationService.java"
Task: "T027 Add client-side validation script in seguradora-java-site/src/main/resources/static/js/form-validation.js"
```

---

## Parallel Example: User Story 3

```bash
# Parallel regression and rollout tests
Task: "T031 Add telemetry unit tests in seguradora-java-site/src/test/java/com/seguradora/site/service/FormSubmissionTelemetryServiceTest.java"
Task: "T032 Add feature-flag fallback MockMvc tests in seguradora-java-site/src/test/java/com/seguradora/site/SiteControllerFeatureFlagFallbackTest.java"

# Parallel operational hardening
Task: "T038 Implement rollout controls in seguradora-java-site/src/main/java/com/seguradora/site/service/FormValidationFeatureFlagService.java"
Task: "T039 Add rollout properties in seguradora-java-site/src/main/resources/application.properties"
```

---

## Implementation Strategy

### MVP First (US1)

1. Complete Phase 1 and Phase 2.
2. Deliver Phase 3 (US1) end-to-end.
3. Validate independently with `mvn -B test` focused on US1 classes.
4. Demonstrate blocked invalid submit + successful valid submit before moving on.

### Incremental Delivery

1. Foundation complete -> baseline observability and feature flag available.
2. Deliver US1 -> stable conversion path.
3. Deliver US2 -> improved correction UX and message consistency.
4. Deliver US3 -> regression safety + controlled rollout.
5. Finish polish docs and acceptance checklist.

### Continuous Verification

1. Execute tests after each completed task group (`unit` then `MockMvc`).
2. Verify logs and metrics snapshots at each rollout stage.
3. If degradation appears, disable feature flag and keep fallback path active.

---

## Notes

- `[P]` tasks indicate no direct file conflict and can be delegated.
- Keep commits granular by phase/checkpoint for easier rollback.
- Preserve MVC contract (`HTTP 200` + same view for validation/internal failures) while improving feedback quality.
- Rollout must always keep kill-switch fallback operational.
