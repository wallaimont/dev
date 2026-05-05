/**
 * Testes para a lógica de validação do backend (functions/index.js).
 * As funções são reproduzidas aqui como puras para permitir teste isolado,
 * sem depender do ambiente Firebase.
 */

import { describe, it, expect, beforeEach } from 'vitest';

// --- Constantes duplicadas do backend (mantidas em sincronia) ---
const ALLOWED_PROVIDERS = new Set([
  'openrouter', 'openai', 'anthropic', 'google', 'xai',
  'deepseek', 'moonshot', 'alibaba', 'zhipu', 'baidu', 'minimax',
]);
const MODEL_ID_RE = /^[\w.:/-]{1,200}$/;
const MAX_MESSAGES = 100;
const MAX_CONTENT_LENGTH = 50_000;
const MAX_CONVERSATIONS = 200;
const MAX_MESSAGES_PER_CONV = 500;

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
    if (typeof msg.content !== 'string') return 'content deve ser string';
    if (msg.content.length > MAX_CONTENT_LENGTH) {
      return `content excede ${MAX_CONTENT_LENGTH} caracteres`;
    }
  }
  return null;
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

// --- Rate limiting ---
const RATE_LIMIT_MAX = 60;
const RATE_LIMIT_WINDOW_MS = 60_000;

function makeRateLimiter() {
  const map = new Map();
  return function checkRateLimit(uid, now = Date.now()) {
    const entry = map.get(uid);
    if (!entry || now > entry.resetAt) {
      map.set(uid, { count: 1, resetAt: now + RATE_LIMIT_WINDOW_MS });
      return true;
    }
    if (entry.count >= RATE_LIMIT_MAX) return false;
    entry.count++;
    return true;
  };
}

// ============================================================

describe('validateMessages', () => {
  it('retorna erro para array vazio', () => {
    expect(validateMessages([])).toBeTruthy();
  });

  it('retorna erro para não-array', () => {
    expect(validateMessages(null)).toBeTruthy();
    expect(validateMessages('texto')).toBeTruthy();
  });

  it('retorna erro quando excede MAX_MESSAGES', () => {
    const msgs = Array.from({ length: MAX_MESSAGES + 1 }, (_, i) => ({
      role: 'user',
      content: `msg ${i}`,
    }));
    expect(validateMessages(msgs)).toMatch(/excede/);
  });

  it('retorna erro para role inválido', () => {
    const msgs = [{ role: 'hacker', content: 'olá' }];
    expect(validateMessages(msgs)).toMatch(/role inválido/);
  });

  it('retorna erro para content não-string', () => {
    const msgs = [{ role: 'user', content: 42 }];
    expect(validateMessages(msgs)).toMatch(/content/);
  });

  it('retorna erro para content muito longo', () => {
    const msgs = [{ role: 'user', content: 'x'.repeat(MAX_CONTENT_LENGTH + 1) }];
    expect(validateMessages(msgs)).toMatch(/excede/);
  });

  it('retorna null para payload válido', () => {
    const msgs = [
      { role: 'system', content: 'Você é um assistente.' },
      { role: 'user', content: 'Olá!' },
      { role: 'assistant', content: 'Olá, posso ajudar?' },
    ];
    expect(validateMessages(msgs)).toBeNull();
  });
});

describe('ALLOWED_PROVIDERS / MODEL_ID_RE', () => {
  it('aceita provedores válidos', () => {
    expect(ALLOWED_PROVIDERS.has('openai')).toBe(true);
    expect(ALLOWED_PROVIDERS.has('anthropic')).toBe(true);
  });

  it('rejeita provedor desconhecido', () => {
    expect(ALLOWED_PROVIDERS.has('evil-provider')).toBe(false);
  });

  it('aceita model IDs válidos', () => {
    expect(MODEL_ID_RE.test('gpt-4o')).toBe(true);
    expect(MODEL_ID_RE.test('claude-3-5-sonnet-20241022')).toBe(true);
    expect(MODEL_ID_RE.test('google/gemini-2.5-flash')).toBe(true);
  });

  it('rejeita model IDs com caracteres perigosos', () => {
    expect(MODEL_ID_RE.test('')).toBe(false);
    expect(MODEL_ID_RE.test('model; DROP TABLE users')).toBe(false);
    expect(MODEL_ID_RE.test('model\n')).toBe(false);
    expect(MODEL_ID_RE.test('a'.repeat(201))).toBe(false);
  });
});

describe('sanitizeConversations', () => {
  it('retorna array vazio para input inválido', () => {
    expect(sanitizeConversations(null)).toEqual([]);
    expect(sanitizeConversations('texto')).toEqual([]);
  });

  it('filtra conversas sem id string', () => {
    const invalid = [{ id: 123, messages: [] }];
    expect(sanitizeConversations(invalid)).toHaveLength(0);
  });

  it('filtra conversas sem messages array', () => {
    const invalid = [{ id: 'c1', messages: 'não é array' }];
    expect(sanitizeConversations(invalid)).toHaveLength(0);
  });

  it('limita conversas a MAX_CONVERSATIONS', () => {
    const many = Array.from({ length: MAX_CONVERSATIONS + 10 }, (_, i) => ({
      id: `c${i}`,
      messages: [],
    }));
    expect(sanitizeConversations(many)).toHaveLength(MAX_CONVERSATIONS);
  });

  it('limita mensagens por conversa a MAX_MESSAGES_PER_CONV', () => {
    const conv = {
      id: 'c1',
      messages: Array.from({ length: MAX_MESSAGES_PER_CONV + 50 }, (_, i) => ({
        role: 'user',
        content: `msg ${i}`,
      })),
    };
    const result = sanitizeConversations([conv]);
    expect(result[0].messages).toHaveLength(MAX_MESSAGES_PER_CONV);
  });

  it('rejeita id com mais de 128 caracteres', () => {
    const conv = { id: 'x'.repeat(129), messages: [] };
    expect(sanitizeConversations([conv])).toHaveLength(0);
  });
});

describe('checkRateLimit', () => {
  it('permite as primeiras RATE_LIMIT_MAX requisições', () => {
    const check = makeRateLimiter();
    for (let i = 0; i < RATE_LIMIT_MAX; i++) {
      expect(check('uid1', 1000)).toBe(true);
    }
  });

  it('bloqueia após RATE_LIMIT_MAX requisições na mesma janela', () => {
    const check = makeRateLimiter();
    for (let i = 0; i < RATE_LIMIT_MAX; i++) check('uid1', 1000);
    expect(check('uid1', 1000)).toBe(false);
  });

  it('reseta após a janela expirar', () => {
    const check = makeRateLimiter();
    for (let i = 0; i < RATE_LIMIT_MAX; i++) check('uid1', 1000);
    // avança além da janela de 60s
    expect(check('uid1', 1000 + RATE_LIMIT_WINDOW_MS + 1)).toBe(true);
  });

  it('UIDs diferentes têm limites independentes', () => {
    const check = makeRateLimiter();
    for (let i = 0; i < RATE_LIMIT_MAX; i++) check('uid1', 1000);
    expect(check('uid1', 1000)).toBe(false);
    expect(check('uid2', 1000)).toBe(true);
  });
});
