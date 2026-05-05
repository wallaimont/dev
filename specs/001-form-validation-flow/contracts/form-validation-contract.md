# Contract - Form Validation Flow

## Scope
Formularios server-side em:
- `POST /cotacao`
- `POST /contato`

## Request contract
Content-Type: `application/x-www-form-urlencoded`

Campos comuns:
- `nome` (obrigatorio)
- `email` (obrigatorio)
- `telefone` (opcional)
- `tipoSeguro` (obrigatorio em cotacao)
- `mensagem` (obrigatorio em contato)
- `origem` (`cotacao` ou `contato`)

Campos especificos de cotacao:
- `cobertura` (obrigatorio)
- `valorBem` (obrigatorio, > 0)

## Validation response contract (MVC)
Em erro de validacao:
- HTTP: `200` (mantendo fluxo MVC atual)
- View: mesma pagina de origem (`cotacao` ou `contato`)
- Modelo inclui:
  - `fieldErrors`: mapa por campo
  - `errorSummary`: lista ordenada de erros
  - `submissionId`: identificador para suporte

Em sucesso:
- HTTP: `200`
- View: mesma pagina de origem
- Modelo inclui `successMessage` e, no caso de cotacao, `quoteResult`

Em erro interno:
- HTTP: `200` para fluxo de tela
- Mensagem amigavel generica sem detalhes internos
- Log interno com `submissionId` para rastreio

## Error model
Representacao logica:

```json
{
  "submissionId": "uuid",
  "formType": "cotacao|contato",
  "status": "VALIDATION_ERROR|INTERNAL_ERROR|SUCCESS",
  "fieldErrors": {
    "email": ["E-mail invalido"],
    "nome": ["Nome e obrigatorio"]
  },
  "durationMs": 142,
  "timestamp": "2026-04-17T12:00:00Z"
}
```

## Observability contract
Metricas:
- `form_submission_total{formType,status}`
- `form_submission_latency_ms{formType}`

Logs estruturados obrigatorios:
- `submissionId`
- `formType`
- `status`
- `durationMs`
- `httpStatus`

## Security contract
- Sanitizar e normalizar entrada antes de validacao de negocio.
- Nao retornar stack trace, SQL, classe Java ou payload sensivel para usuario.
- Aplicar mascaramento minimo de PII em logs de erro.
