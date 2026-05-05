# WhatsApp Chatbot — Empresa de Tecnologia

Chatbot inteligente para WhatsApp usando **WhatsApp Business Cloud API** (Meta) + **OpenAI GPT** para atendimento automatizado.

## Funcionalidades

- Responde mensagens de texto com IA (GPT-4o-mini)
- Memória de conversa (contexto dentro de uma sessão de 30 min)
- Trata diferentes tipos de mensagem (texto, imagem, áudio, localização, etc.)
- Marca mensagens como lidas (✓✓ azul)
- Deduplicação de mensagens
- Logging estruturado com Winston

## Arquitetura

```
src/
├── index.js                  # Entry point + Express server
├── routes/
│   └── webhook.js            # GET (verificação) + POST (mensagens)
├── handlers/
│   └── messageHandler.js     # Lógica de roteamento por tipo
├── services/
│   ├── whatsappService.js    # Envio de mensagens via Graph API
│   └── aiService.js          # OpenAI + histórico de conversa
└── utils/
    └── logger.js             # Winston logger
```

## Pré-requisitos

1. **Conta Meta for Developers** com um app configurado para WhatsApp Business
2. **Número de telefone** vinculado ao WhatsApp Business API
3. **Token de acesso permanente** (System User Token)
4. **Chave da OpenAI** com créditos

## Configuração

```bash
# 1. Clone e instale
cd whatsapp-chatbot
npm install

# 2. Crie o .env a partir do exemplo
cp .env.example .env
# Edite o .env com suas credenciais

# 3. Execute
npm run dev
```

## Configurar Webhook na Meta

1. Acesse [Meta Developers](https://developers.facebook.com) → Seu App → WhatsApp → Configuration
2. Em **Webhook URL**, coloque: `https://seu-dominio.com/webhook`
3. Em **Verify Token**, use o mesmo valor de `WHATSAPP_VERIFY_TOKEN` do seu `.env`
4. Inscreva-se no campo **messages**

> Para desenvolvimento local, use [ngrok](https://ngrok.com): `ngrok http 3000`

## Docker

```bash
docker-compose up -d
```

## Variáveis de Ambiente

| Variável | Descrição |
|---|---|
| `PORT` | Porta do servidor (padrão: 3000) |
| `WHATSAPP_ACCESS_TOKEN` | Token permanente da API do WhatsApp |
| `WHATSAPP_VERIFY_TOKEN` | Token para verificação do webhook |
| `OPENAI_API_KEY` | Chave da API OpenAI |
| `OPENAI_MODEL` | Modelo (padrão: gpt-4o-mini) |
| `COMPANY_NAME` | Nome da empresa exibido pela IA |

## Personalização

Edite o `SYSTEM_PROMPT` em [src/services/aiService.js](src/services/aiService.js) para ajustar:
- Nome e descrição da empresa
- Serviços oferecidos
- Horário de atendimento
- Tom de voz do assistente
- Regras de negócio específicas
