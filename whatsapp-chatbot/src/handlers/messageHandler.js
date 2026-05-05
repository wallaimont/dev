const { logger } = require("../utils/logger");
const { sendTextMessage, markAsRead } = require("../services/whatsappService");
const { generateResponse } = require("../services/aiService");

// Set para evitar processar mensagens duplicadas
const processedMessages = new Set();
const MAX_PROCESSED = 10000;

function deduplicateMessage(messageId) {
  if (processedMessages.has(messageId)) return true;
  processedMessages.add(messageId);
  // Limpa periodicamente para não estourar memória
  if (processedMessages.size > MAX_PROCESSED) {
    const arr = [...processedMessages];
    arr.splice(0, arr.length - MAX_PROCESSED / 2);
    processedMessages.clear();
    arr.forEach((id) => processedMessages.add(id));
  }
  return false;
}

/**
 * Processa uma mensagem recebida do WhatsApp.
 */
async function handleIncomingMessage(message, metadata) {
  const messageId = message.id;
  const from = message.from; // número do remetente
  const phoneNumberId = metadata.phone_number_id;

  // Evita mensagens duplicadas
  if (deduplicateMessage(messageId)) {
    return;
  }

  logger.info("Mensagem recebida", { from, type: message.type, messageId });

  // Marca como lida
  await markAsRead(phoneNumberId, messageId);

  let userText;

  switch (message.type) {
    case "text":
      userText = message.text?.body;
      break;

    case "image":
    case "video":
    case "document":
    case "audio":
      userText = message.image?.caption
        || message.video?.caption
        || message.document?.caption
        || null;

      if (!userText) {
        await sendTextMessage(
          phoneNumberId,
          from,
          "Recebi seu arquivo! No momento, consigo responder apenas mensagens de texto. Por favor, descreva como posso te ajudar. 😊"
        );
        return;
      }
      break;

    case "location":
      await sendTextMessage(
        phoneNumberId,
        from,
        "Recebi sua localização! Nosso escritório fica na Av. Paulista, 1000 — São Paulo/SP. Precisa de mais alguma informação?"
      );
      return;

    case "contacts":
      await sendTextMessage(
        phoneNumberId,
        from,
        "Obrigado por compartilhar o contato! Um consultor pode entrar em contato em breve. Como posso ajudar?"
      );
      return;

    case "interactive":
      userText =
        message.interactive?.button_reply?.title ||
        message.interactive?.list_reply?.title;
      break;

    default:
      logger.info("Tipo de mensagem não suportado", { type: message.type });
      return;
  }

  if (!userText || !userText.trim()) {
    return;
  }

  // Gera resposta com IA
  const reply = await generateResponse(from, userText.trim());

  // Envia resposta
  await sendTextMessage(phoneNumberId, from, reply);
}

module.exports = { handleIncomingMessage };
