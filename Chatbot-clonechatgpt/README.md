# Chaat IA

Frontend em React/Vite + autenticação Firebase + proxy seguro em Firebase Functions para chamadas de IA.

## Desenvolvimento

1. Instale dependências do frontend:

```bash
npm install
```

2. Copie variáveis locais:

```bash
cp .env.example .env.local
```

3. Rode o app:

```bash
npm run dev
```

## Backend seguro (Functions)

O endpoint seguro é exposto em /api/chat (rewrite de Hosting para a function api).
O histórico remoto é exposto em /api/history e salvo por usuário no Firestore.

1. Instale dependências das Functions:

```bash
npm --prefix functions install
```

2. Defina segredos no Firebase (apenas os provedores que você usa):

```bash
firebase functions:secrets:set OPENROUTER_API_KEY
firebase functions:secrets:set OPENAI_API_KEY
firebase functions:secrets:set ANTHROPIC_API_KEY
firebase functions:secrets:set GOOGLE_API_KEY
firebase functions:secrets:set XAI_API_KEY
firebase functions:secrets:set DEEPSEEK_API_KEY
firebase functions:secrets:set MOONSHOT_API_KEY
firebase functions:secrets:set ALIBABA_API_KEY
firebase functions:secrets:set ZHIPU_API_KEY
firebase functions:secrets:set BAIDU_API_KEY
firebase functions:secrets:set MINIMAX_API_KEY
```

3. Deploy:

```bash
npm run build
firebase deploy --only functions,hosting
```

## Observações de segurança

- Chaves de API no cliente ficam apenas em sessionStorage como fallback temporário.
- Com o proxy seguro ativo, o frontend envia somente o token do Firebase; as chaves reais permanecem no servidor.
- O fallback legado é mantido para não quebrar ambientes sem backend já publicado.
- O histórico permanece em fallback local, mas quando o backend está disponível passa a sincronizar automaticamente no Firestore por usuário.
