const OpenAI = require("openai");
const { logger } = require("../utils/logger");

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });

const SYSTEM_PROMPT = `Você é o assistente virtual de uma empresa de tecnologia chamada "${process.env.COMPANY_NAME || "TechCorp"}".

Sobre a empresa:
- Oferecemos desenvolvimento de software sob medida, consultoria em TI, cloud computing, integração de sistemas e suporte técnico.
- Trabalhamos com tecnologias modernas: Java, Node.js, React, React Native, Python, AWS, Azure, Docker, Kubernetes.
- Atendemos desde startups até grandes corporações.
- Horário de atendimento humano: segunda a sexta, 8h às 18h.

Suas diretrizes:
1. Responda sempre em português brasileiro, de forma clara e profissional, mas amigável.
2. Se o cliente perguntar algo que exige análise técnica aprofundada ou orçamento, colete o nome, e-mail e descreva o que ele precisa, e diga que um consultor entrará em contato.
3. Nunca invente informações sobre preços — diga que um consultor fornecerá o orçamento.
4. Para dúvidas de suporte técnico simples, tente ajudar diretamente.
5. Se o assunto fugir completamente da área de tecnologia, redirecione educadamente.
6. Mantenha respostas concisas (máximo 300 palavras).`;

// Cache simples de conversas em memória (para produção, usar Redis/DB)
const conversationCache = new Map();

const MAX_HISTORY = 20;
const CACHE_TTL_MS = 30 * 60 * 1000; // 30 minutos

function getConversation(phone) {
  const entry = conversationCache.get(phone);
  if (!entry) return [];
  // Limpa se expirou
  if (Date.now() - entry.updatedAt > CACHE_TTL_MS) {
    conversationCache.delete(phone);
    return [];
  }
  return entry.messages;
}

function saveConversation(phone, messages) {
  // Mantém apenas as últimas N mensagens
  const trimmed = messages.slice(-MAX_HISTORY);
  conversationCache.set(phone, { messages: trimmed, updatedAt: Date.now() });
}

/**
 * Gera uma resposta usando OpenAI, mantendo contexto da conversa.
 */
async function generateResponse(phone, userMessage) {
  const history = getConversation(phone);

  history.push({ role: "user", content: userMessage });

  try {
    const completion = await openai.chat.completions.create({
      model: process.env.OPENAI_MODEL || "gpt-4o-mini",
      messages: [{ role: "system", content: SYSTEM_PROMPT }, ...history],
      max_tokens: 500,
      temperature: 0.7,
    });

    const reply = completion.choices[0]?.message?.content?.trim();

    if (!reply) {
      throw new Error("Resposta vazia da OpenAI");
    }

    history.push({ role: "assistant", content: reply });
    saveConversation(phone, history);

    return reply;
  } catch (err) {
    logger.error("Erro na IA", { error: err.message });
    return "Desculpe, estou com dificuldades para processar sua mensagem no momento. Por favor, tente novamente em alguns instantes ou entre em contato pelo e-mail suporte@techcorp.com.br.";
  }
}

module.exports = { generateResponse };
