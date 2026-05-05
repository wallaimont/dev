const express = require("express");
const router = express.Router();
const { logger } = require("../utils/logger");
const { handleIncomingMessage } = require("../handlers/messageHandler");

const VERIFY_TOKEN = process.env.WHATSAPP_VERIFY_TOKEN;

// Verificação do webhook (GET) — Meta envia isso ao cadastrar o webhook
router.get("/", (req, res) => {
  const mode = req.query["hub.mode"];
  const token = req.query["hub.verify_token"];
  const challenge = req.query["hub.challenge"];

  if (mode === "subscribe" && token === VERIFY_TOKEN) {
    logger.info("Webhook verificado com sucesso");
    return res.status(200).send(challenge);
  }

  logger.warn("Falha na verificação do webhook");
  return res.sendStatus(403);
});

// Recebimento de mensagens (POST)
router.post("/", async (req, res) => {
  // Responde 200 imediatamente para a Meta não reenviar
  res.sendStatus(200);

  try {
    const body = req.body;

    if (
      body.object !== "whatsapp_business_account" ||
      !body.entry?.length
    ) {
      return;
    }

    for (const entry of body.entry) {
      const changes = entry.changes || [];
      for (const change of changes) {
        const value = change.value;
        if (!value?.messages?.length) continue;

        for (const message of value.messages) {
          await handleIncomingMessage(message, value.metadata);
        }
      }
    }
  } catch (err) {
    logger.error("Erro ao processar webhook", { error: err.message });
  }
});

module.exports = router;
