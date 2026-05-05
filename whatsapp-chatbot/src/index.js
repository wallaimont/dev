require("dotenv").config();
const express = require("express");
const { logger } = require("./utils/logger");
const webhookRouter = require("./routes/webhook");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

// Health check
app.get("/health", (_req, res) => {
  res.json({ status: "ok", timestamp: new Date().toISOString() });
});

// WhatsApp webhook
app.use("/webhook", webhookRouter);

app.listen(PORT, () => {
  logger.info(`Chatbot WhatsApp rodando na porta ${PORT}`);
});
