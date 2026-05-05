const axios = require("axios");
const { logger } = require("../utils/logger");

const API_VERSION = "v21.0";
const BASE_URL = `https://graph.facebook.com/${API_VERSION}`;

/**
 * Envia uma mensagem de texto via WhatsApp Business Cloud API.
 */
async function sendTextMessage(phoneNumberId, to, text) {
  const url = `${BASE_URL}/${phoneNumberId}/messages`;
  const token = process.env.WHATSAPP_ACCESS_TOKEN;

  try {
    await axios.post(
      url,
      {
        messaging_product: "whatsapp",
        to,
        type: "text",
        text: { body: text },
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );
    logger.info("Mensagem enviada", { to });
  } catch (err) {
    logger.error("Erro ao enviar mensagem", {
      to,
      status: err.response?.status,
      data: err.response?.data,
    });
  }
}

/**
 * Marca a mensagem como lida (double blue tick).
 */
async function markAsRead(phoneNumberId, messageId) {
  const url = `${BASE_URL}/${phoneNumberId}/messages`;
  const token = process.env.WHATSAPP_ACCESS_TOKEN;

  try {
    await axios.post(
      url,
      {
        messaging_product: "whatsapp",
        status: "read",
        message_id: messageId,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );
  } catch {
    // Falha silenciosa — marcar como lido não é crítico
  }
}

module.exports = { sendTextMessage, markAsRead };
