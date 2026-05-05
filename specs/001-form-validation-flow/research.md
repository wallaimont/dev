# Phase 0 Research - 001-form-validation-flow

## Decision 1: Estrategia de validacao em duas camadas
Rationale: Client-side reduz friccao e corrige erros simples imediatamente; server-side garante autoridade, seguranca e consistencia de regra.
Alternatives considered: Apenas client-side (rejeitado por ser contornavel); apenas server-side (rejeitado por UX inferior e maior taxa de abandono).

## Decision 2: Modelo unico de mensagens por campo
Rationale: Um catalogo centralizado de mensagens evita divergencia entre `/cotacao` e `/contato` e facilita manutencao de linguagem clara para usuario final.
Alternatives considered: Mensagens hardcoded em cada template/controlador (rejeitado por duplicidade e risco de inconsistencias).

## Decision 3: Modelo de erro observavel por submissao
Rationale: Usar `submissionId`, `formType`, `status`, `fieldErrors` e `durationMs` permite diagnostico rapido e medicao objetiva de qualidade do fluxo.
Alternatives considered: Logs livres sem estrutura (rejeitado por baixa rastreabilidade e dificuldade de metricas).

## Decision 4: Metricas minimas padronizadas
Rationale: `form_submission_total{formType,status}` e histograma de latencia por formulario cobrem NFR de erro e tempo de resposta com baixo custo de instrumentacao.
Alternatives considered: Monitorar somente erros internos (rejeitado por ignorar erro de validacao e impacto de UX).

## Decision 5: Tratamento seguro de excecoes
Rationale: Resposta amigavel e generica ao usuario, com detalhes tecnicos somente em log interno correlacionado por `submissionId`, cumpre seguranca sem perder capacidade de suporte.
Alternatives considered: Expor mensagens tecnicas para acelerar debug (rejeitado por risco de vazamento de informacao).

## Decision 6: Estrategia de testes para regressao
Rationale: Unitarios para regras + MockMvc para fluxo completo oferece cobertura funcional e previne regressao em obrigatoriedade e formato invalido.
Alternatives considered: Somente testes de integracao (rejeitado por baixa precisao no diagnostico); somente unitarios (rejeitado por nao cobrir comportamento MVC/template).

## Decision 7: Rollout gradual com feature flag e fallback
Rationale: Liberacao progressiva (10% -> 50% -> 100%) reduz risco operacional e permite rollback imediato sem deploy emergencial.
Alternatives considered: Big bang em 100% (rejeitado por risco maior de impacto em conversao e suporte).
