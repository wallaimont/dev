import { onRequest } from 'firebase-functions/v2/https';
import { defineSecret } from 'firebase-functions/params';
import admin from 'firebase-admin';

if (!admin.apps.length) {
  admin.initializeApp();
}

const OPENROUTER_API_KEY = defineSecret('OPENROUTER_API_KEY');
const OPENAI_API_KEY = defineSecret('OPENAI_API_KEY');
const ANTHROPIC_API_KEY = defineSecret('ANTHROPIC_API_KEY');
const GOOGLE_API_KEY = defineSecret('GOOGLE_API_KEY');
const XAI_API_KEY = defineSecret('XAI_API_KEY');
const DEEPSEEK_API_KEY = defineSecret('DEEPSEEK_API_KEY');
const MOONSHOT_API_KEY = defineSecret('MOONSHOT_API_KEY');
const ALIBABA_API_KEY = defineSecret('ALIBABA_API_KEY');
const ZHIPU_API_KEY = defineSecret('ZHIPU_API_KEY');
const BAIDU_API_KEY = defineSecret('BAIDU_API_KEY');
const MINIMAX_API_KEY = defineSecret('MINIMAX_API_KEY');

const PROVIDERS = {
  openrouter: { url: 'https://openrouter.ai/api/v1', key: OPENROUTER_API_KEY },
  openai: { url: 'https://api.openai.com/v1', key: OPENAI_API_KEY },
  anthropic: { url: 'https://api.anthropic.com', key: ANTHROPIC_API_KEY },
  google: { url: 'https://generativelanguage.googleapis.com/v1beta/openai', key: GOOGLE_API_KEY },
  xai: { url: 'https://api.x.ai/v1', key: XAI_API_KEY },
  deepseek: { url: 'https://api.deepseek.com/v1', key: DEEPSEEK_API_KEY },
  moonshot: { url: 'https://api.moonshot.cn/v1', key: MOONSHOT_API_KEY },
  alibaba: { url: 'https://dashscope.aliyuncs.com/compatible-mode/v1', key: ALIBABA_API_KEY },
  zhipu: { url: 'https://open.bigmodel.cn/api/paas/v4', key: ZHIPU_API_KEY },
  baidu: { url: 'https://qianfan.baidubce.com/v2', key: BAIDU_API_KEY },
  minimax: { url: 'https://api.minimax.chat/v1', key: MINIMAX_API_KEY },
};

// --- Validation helpers ---
const ALLOWED_PROVIDERS = new Set(Object.keys(PROVIDERS));
const MODEL_ID_RE = /^[\w.:/-]{1,200}$/; // alphanumeric, dots, colons, slashes, hyphens
const MAX_MESSAGES = 100;
const MAX_CONTENT_LENGTH = 50_000;
const MAX_PROMPT_LENGTH = 8_000;
const MAX_CONVERSATIONS = 200;
const MAX_MESSAGES_PER_CONV = 500;

function apiError(res, status, message, code) {
  return res.status(status).json({ error: message, code });
}

function validateMessages(messages) {
  if (!Array.isArray(messages) || messages.length === 0) {
    return 'messages deve ser um array não vazio';
  }
  if (messages.length > MAX_MESSAGES) {
    return `messages excede o limite de ${MAX_MESSAGES} itens`;
  }
  for (const msg of messages) {
    if (!msg || typeof msg !== 'object') return 'mensagem inválida';
    if (!['system', 'user', 'assistant'].includes(msg.role)) {
      return `role inválido: ${msg.role}`;
    }
    const isStringContent = typeof msg.content === 'string';
    const isMultimodalContent = Array.isArray(msg.content);
    if (!isStringContent && !isMultimodalContent) {
      return 'content deve ser string ou array multimodal';
    }
    if (isStringContent && msg.content.length > MAX_CONTENT_LENGTH) {
      return `content excede ${MAX_CONTENT_LENGTH} caracteres`;
    }
  }
  return null;
}

function validatePrompt(prompt) {
  if (!prompt || typeof prompt !== 'string') return 'prompt é obrigatório';
  if (prompt.length > MAX_PROMPT_LENGTH) {
    return `prompt excede ${MAX_PROMPT_LENGTH} caracteres`;
  }
  return null;
}

const ALLOWED_ORIGINS = new Set([
  'https://chaat-ia.web.app',
  'https://chaat-ia.firebaseapp.com',
]);

function cors(req, res) {
  const origin = req.headers.origin || '';
  const allowedOrigin = ALLOWED_ORIGINS.has(origin) ? origin : 'https://chaat-ia.web.app';
  res.set('Access-Control-Allow-Origin', allowedOrigin);
  res.set('Vary', 'Origin');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  res.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
}

async function verifyFirebaseToken(req) {
  const authHeader = req.headers.authorization || '';
  if (!authHeader.startsWith('Bearer ')) {
    throw new Error('UNAUTHORIZED');
  }
  const idToken = authHeader.slice(7);
  return admin.auth().verifyIdToken(idToken);
}

function getProviderKey(provider) {
  const p = PROVIDERS[provider];
  if (!p) return '';
  return p.key.value();
}

function isValidConversation(conv) {
  if (!conv || typeof conv !== 'object') return false;
  if (typeof conv.id !== 'string' || conv.id.length > 128) return false;
  if (!Array.isArray(conv.messages)) return false;
  return true;
}

function sanitizeConversations(value) {
  if (!Array.isArray(value)) return [];
  return value
    .filter(isValidConversation)
    .slice(0, MAX_CONVERSATIONS)
    .map((conv) => ({
      ...conv,
      messages: conv.messages.slice(0, MAX_MESSAGES_PER_CONV),
    }));
}

export const api = onRequest(
  {
    region: 'us-central1',
    secrets: [
      OPENROUTER_API_KEY,
      OPENAI_API_KEY,
      ANTHROPIC_API_KEY,
      GOOGLE_API_KEY,
      XAI_API_KEY,
      DEEPSEEK_API_KEY,
      MOONSHOT_API_KEY,
      ALIBABA_API_KEY,
      ZHIPU_API_KEY,
      BAIDU_API_KEY,
      MINIMAX_API_KEY,
    ],
    timeoutSeconds: 120,
    memory: '512MiB',
  },
  async (req, res) => {
    cors(req, res);
    if (req.method === 'OPTIONS') {
      return res.status(204).send('');
    }

    try {
      const decoded = await verifyFirebaseToken(req);
      const uid = decoded.uid;

      const path = req.path || '';

      if (path.endsWith('/history')) {
        const historyRef = admin
          .firestore()
          .collection('users')
          .doc(uid)
          .collection('private')
          .doc('history');

        if (req.method === 'GET') {
          const snap = await historyRef.get();
          const data = snap.exists ? snap.data() : null;
          const conversations = sanitizeConversations(data?.conversations);
          return res.json({ conversations });
        }

        if (req.method === 'POST') {
          const conversations = sanitizeConversations(req.body?.conversations);
          await historyRef.set(
            {
              conversations,
              updatedAt: admin.firestore.FieldValue.serverTimestamp(),
            },
            { merge: true }
          );
          return res.json({ ok: true, count: conversations.length });
        }

        return apiError(res, 405, 'Método não permitido', 'METHOD_NOT_ALLOWED');
      }

      if (!path.endsWith('/chat')) {
        return apiError(res, 404, 'Rota não encontrada', 'NOT_FOUND');
      }

      if (req.method !== 'POST') {
        return apiError(res, 405, 'Método não permitido', 'METHOD_NOT_ALLOWED');
      }

      const { provider, model, messages, mode = 'chat', prompt } = req.body || {};

      if (!provider || typeof provider !== 'string') {
        return apiError(res, 400, 'Campo provider é obrigatório', 'INVALID_PROVIDER');
      }
      if (!ALLOWED_PROVIDERS.has(provider)) {
        return apiError(res, 400, `Provedor não suportado: ${provider}`, 'INVALID_PROVIDER');
      }

      if (!model || typeof model !== 'string' || !MODEL_ID_RE.test(model)) {
        return apiError(res, 400, 'Campo model inválido', 'INVALID_MODEL');
      }

      if (!['chat', 'image'].includes(mode)) {
        return apiError(res, 400, 'Modo inválido', 'INVALID_MODE');
      }

      const conf = PROVIDERS[provider];
      const key = getProviderKey(provider);
      if (!key) {
        return apiError(res, 500, `Segredo ausente para ${provider}`, 'MISSING_SECRET');
      }

      const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${key}`,
      };

      if (provider === 'openrouter') {
        headers['HTTP-Referer'] = 'https://chaat-ia.web.app';
        headers['X-Title'] = 'Chaat IA';
      }

      if (mode === 'image') {
        const promptError = validatePrompt(prompt);
        if (promptError) {
          return apiError(res, 400, promptError, 'INVALID_PROMPT');
        }

        const upstream = await fetch(`${conf.url}/images/generations`, {
          method: 'POST',
          headers,
          body: JSON.stringify({
            model,
            prompt,
            size: '1024x1024',
          }),
        });

        const text = await upstream.text();
        if (!upstream.ok) {
          return res.status(upstream.status).send(text);
        }

        let parsed;
        try {
          parsed = JSON.parse(text);
        } catch {
          return res.status(502).json({ error: 'Resposta inválida do provedor' });
        }

        const data = parsed?.data?.[0] || {};
        const imageUrl = data.url || (data.b64_json ? `data:image/png;base64,${data.b64_json}` : '');
        if (!imageUrl) {
          return res.status(502).json({ error: 'Resposta sem imagem do provedor' });
        }

        return res.json({
          imageUrl,
          content: data.revised_prompt ? `Imagem gerada com prompt revisado: ${data.revised_prompt}` : '',
        });
      }

      const messagesError = validateMessages(messages);
      if (messagesError) {
        return apiError(res, 400, messagesError, 'INVALID_MESSAGES');
      }

      if (provider === 'anthropic') {
        const upstream = await fetch('https://api.anthropic.com/v1/messages', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'x-api-key': key,
            'anthropic-version': '2023-06-01',
          },
          body: JSON.stringify({
            model,
            max_tokens: 8096,
            messages,
            stream: false,
          }),
        });

        const text = await upstream.text();
        if (!upstream.ok) {
          return res.status(upstream.status).send(text);
        }
        let parsed;
        try {
          parsed = JSON.parse(text);
        } catch {
          return res.status(502).json({ error: 'Resposta inválida do provedor' });
        }
        const content = Array.isArray(parsed.content)
          ? parsed.content.filter((p) => p.type === 'text').map((p) => p.text).join('')
          : '';
        return res.json({ content });
      }

      const upstream = await fetch(`${conf.url}/chat/completions`, {
        method: 'POST',
        headers,
        body: JSON.stringify({
          model,
          messages,
          stream: false,
        }),
      });

      const text = await upstream.text();
      if (!upstream.ok) {
        return res.status(upstream.status).send(text);
      }

      let parsed;
      try {
        parsed = JSON.parse(text);
      } catch {
        return res.status(502).json({ error: 'Resposta inválida do provedor' });
      }

      const content = parsed?.choices?.[0]?.message?.content || '';
      return res.json({ content });
    } catch (err) {
      if (err?.message === 'UNAUTHORIZED') {
        return apiError(res, 401, 'Não autenticado', 'UNAUTHORIZED');
      }
      console.error('[api] erro', err);
      return res.status(500).json({ error: 'Erro interno' });
    }
  }
);
