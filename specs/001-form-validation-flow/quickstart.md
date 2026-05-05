# Quickstart - 001-form-validation-flow

## Objetivo
Implementar validacao ponta a ponta com mensagens consistentes, observabilidade minima, seguranca de entrada e cobertura de testes para formularios de cotacao e contato.

## Tracking de Implementacao

- Rodada atual: Fase 1 (Setup), Fase 2 (Foundational), Fase 3 (US1 MVP)
- Escopo fora da rodada: US2, US3 e tarefas de polish
- Projeto alvo: `seguradora-java-site`
- Ultima atualizacao: 2026-04-17

## Pre-requisitos
- Java 17 e Maven configurados.
- Projeto: `seguradora-java-site`.
- Branch: `001-form-validation-flow`.

## Passos de implementacao (resumo)
1. Padronizar regras de validacao de entrada em `LeadRequest` e em validador de dominio (normalizacao e regras por formulario).
2. Introduzir contrato unico de mensagens por campo para `/cotacao` e `/contato`.
3. Ajustar templates para exibicao consistente de erros por campo e feedback de submit.
4. Implementar logs estruturados por submissao com `submissionId` e status.
5. Instrumentar metricas minimas por formulario (`total`, `latency`, taxa de erro derivada).
6. Garantir tratamento seguro de excecoes sem vazamento de detalhes internos.

## Testes minimos obrigatorios
1. Unitarios para validadores e normalizacao (obrigatoriedade, formato, limites, whitespace).
2. Integracao MockMvc para sucesso/falha em `POST /cotacao` e `POST /contato`.
3. Regressao para campos obrigatorios, formatos invalidos e erro interno controlado.

## Metas e rollout
- Meta de latencia: p95 <= 2s, p99 <= 3s.
- Rollout gradual por feature flag: 10% -> 50% -> 100% com monitoracao a cada etapa.
- Fallback: desativar feature flag e retornar ao comportamento anterior em caso de degradacao.

## Comando de validacao
```powershell
Set-Location "c:\Users\wallace\Desktop\Nova pasta (3)\seguradora-java-site"; mvn -B test
```
