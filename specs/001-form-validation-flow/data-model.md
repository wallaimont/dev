# Data Model - 001-form-validation-flow

## Entity: LeadRequest (Input Model)
Description: Modelo de entrada de formularios do site para cotacao e contato.

Fields:
- nome: string, obrigatorio, trim, 3..120 caracteres
- email: string, obrigatorio, formato email valido
- telefone: string, opcional, max 30, normalizado para apenas caracteres permitidos
- tipoSeguro: string, opcional em contato, obrigatorio em cotacao conforme regra de tela
- cobertura: string, obrigatorio em cotacao
- valorBem: decimal, obrigatorio em cotacao, > 0
- mensagem: string, opcional em cotacao e obrigatoria em contato conforme regra de tela, max 1000
- origem: enum string (`cotacao`, `contato`), obrigatorio para telemetria e roteamento

Validation rules:
- Espacos em branco isolados sao tratados como vazio para regras de obrigatoriedade.
- Campos textuais passam por normalizacao (trim e colapso de whitespace quando aplicavel).
- Campos fora de limite geram erro por campo sem interromper acumulacao de outros erros.

## Entity: FormSubmissionEvent (Observability Model)
Description: Evento estruturado emitido a cada tentativa de submissao.

Fields:
- submissionId: string (UUID)
- formType: enum string (`cotacao`, `contato`)
- status: enum string (`SUCCESS`, `VALIDATION_ERROR`, `INTERNAL_ERROR`)
- fieldErrors: map<string, list<string>>
- durationMs: long
- occurredAt: timestamp
- httpStatus: integer
- clientFingerprint: string opcional e anonimizado

State transitions:
1. RECEIVED -> VALIDATED
2. VALIDATED -> REJECTED_VALIDATION (quando ha erros)
3. VALIDATED -> PROCESSED_SUCCESS (quando persistencia/processamento ok)
4. VALIDATED -> FAILED_INTERNAL (quando excecao interna ocorre)

## Entity: ValidationErrorCatalog (Contract Model)
Description: Catalogo logico de chaves de erro e mensagens padronizadas.

Fields:
- code: string (ex.: `lead.email.invalid`)
- field: string (ex.: `email`)
- message: string amigavel ao usuario
- severity: enum string (`ERROR`, `WARNING`)
- appliesToForms: list enum string (`cotacao`, `contato`)

Relationships:
- Um `LeadRequest` pode gerar zero ou muitos erros no `ValidationErrorCatalog`.
- Cada submissao gera exatamente um `FormSubmissionEvent`.
