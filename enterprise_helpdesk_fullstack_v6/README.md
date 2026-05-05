# Enterprise Helpdesk Full Stack v6

Projeto full stack corporativo com:
- FastAPI + PostgreSQL
- React + TypeScript + Vite
- JWT
- Dashboard executivo
- WebSocket para eventos em tempo real
- Upload real de anexos no back-end
- Docker Compose
- Alembic

## Subir o ambiente

```bash
cp .env.example .env
docker compose up --build
```

## URLs
- Front-end: http://localhost:3000
- API: http://localhost:8000
- Swagger: http://localhost:8000/docs
- Uploads estáticos: http://localhost:8000/uploads
- WebSocket: ws://localhost:8000/ws/events?channel=tickets

## Login padrão
- admin@empresa.com
- Admin1234

## Novidades da v4
- anexos reais com armazenamento em `uploads/`
- eventos em tempo real por WebSocket
- dashboard executivo com tendências e métricas médias
- front-end integrado ao upload real
- docker com volume persistente para uploads

## Observações
- O volume `uploads_data` mantém os anexos do ambiente Docker.
- O front recebe eventos em tempo real de criação, atualização, comentário e upload.
- Para iniciar com banco limpo, remova os volumes do Compose antes de subir novamente.


## Novidades da v5

- refresh token com renovação automática da sessão no front-end
- histórico de atividades por ticket
- SLA padrão por prioridade
- atribuição de analistas na criação e edição do chamado
- endpoint `/api/v1/tickets/{id}/activity`
- endpoint `/api/v1/auth/refresh`

SLA padrão:
- low: 72h
- medium: 24h
- high: 8h
- critical: 4h


## Novidades da v6

- RBAC mais detalhado para atribuição, exportação e aprovação de encerramento
- fluxo de encerramento com solicitação e aprovação
- metadata de aprovação registrada no ticket
- indicadores por analista no dashboard executivo
- exportação gerencial em CSV e PDF
- endpoint `/api/v1/dashboard/analysts`
- endpoints `/api/v1/dashboard/exports/analysts.csv` e `/api/v1/dashboard/exports/analysts.pdf`
- endpoints `/api/v1/tickets/{id}/request-close` e `/api/v1/tickets/{id}/approve-close`

Regras principais:
- cliente pode solicitar encerramento do próprio chamado, mas não aprovar
- admin e manager podem aprovar encerramento
- exportações gerenciais ficam restritas a admin e manager
- atribuição de analistas fica restrita a admin, manager e analyst
