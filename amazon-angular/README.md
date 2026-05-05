# AmazonAngular

Loja em Angular + API Express com persistencia local de carrinho.

## Comandos do dia a dia

Instalar dependencias:

```bash
npm install
```

Rodar em desenvolvimento (frontend + API juntos):

```bash
npm run run:dev
```

- Frontend: porta automatica (4200, 4201, 4202 ou 4300)
- API dev: http://localhost:4001

Rodar em producao SSR (build + server):

```bash
npm run run:prod
```

- SSR padrao: http://localhost:4000

Para subir SSR em porta especifica no PowerShell (exemplo 4301):

```powershell
$env:PORT=4301; npm run run:prod
```

## Observacoes

- Nao usar `npx ng serve --ssr` neste projeto; use `npm run run:prod` para fluxo SSR.
- O carrinho persiste em arquivo local: `data/cart-store.json`.
