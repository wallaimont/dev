/**
 * Web Security Auditor — API HTTP
 * Porta: env PORT ou 3001
 * Endpoints:
 *   GET  /                → dashboard HTML
 *   GET  /health          → JSON {ok:true}
 *   POST /api/audit       → { urls: string[] }  → relatorio consolidado JSON
 *   GET  /api/audit?urls= → mesmo, via query string (virgula-separado)
 */

import http from "node:http";
import path from "node:path";
import fs from "node:fs";
import { fileURLToPath } from "node:url";
import { runAudit } from "./checks.js";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const PUBLIC_DIR = path.join(__dirname, "..", "public");
const PORT = Number(process.env.PORT ?? 3001);
const CONCURRENT_LIMIT = 5; // maximo de auditorias em paralelo

// ---------- criticidade ----------

export function classify(riskScore) {
  if (riskScore >= 10) return "Critico";
  if (riskScore >= 6)  return "Alto";
  if (riskScore >= 3)  return "Medio";
  return "Baixo";
}

// ---------- utilitarios ----------

function cors(res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type");
}

function json(res, code, data) {
  cors(res);
  res.writeHead(code, { "Content-Type": "application/json; charset=utf-8" });
  res.end(JSON.stringify(data));
}

function err(res, code, message) {
  json(res, code, { error: message });
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    let raw = "";
    req.on("data", (c) => { raw += c; if (raw.length > 512_000) req.destroy(); });
    req.on("end", () => resolve(raw));
    req.on("error", reject);
  });
}

async function parseUrls(req, searchParams) {
  if (req.method === "POST") {
    const raw = await readBody(req);
    let body;
    try { body = JSON.parse(raw); } catch { return null; }
    if (!Array.isArray(body?.urls)) return null;
    return body.urls;
  }
  const q = searchParams.get("urls");
  if (!q) return null;
  return q.split(",").map((u) => u.trim()).filter(Boolean);
}

// Executa auditorias com limite de concorrencia
async function auditPool(urls, timeoutMs) {
  const results = new Array(urls.length).fill(null);
  const running = new Set();
  let idx = 0;

  function next() {
    if (idx >= urls.length) return;
    const i = idx++;
    const p = runAudit(urls[i], timeoutMs)
      .then((r) => { results[i] = { ...r, criticidade: classify(r.riskScore) }; })
      .catch((e) => { results[i] = { target: urls[i], error: e.message, riskScore: 0, criticidade: "Baixo" }; })
      .finally(() => { running.delete(p); next(); });
    running.add(p);
  }

  for (let s = 0; s < Math.min(CONCURRENT_LIMIT, urls.length); s++) next();
  while (running.size > 0) await Promise.race(running);
  return results;
}

function buildReport(results) {
  const byLevel = { Critico: 0, Alto: 0, Medio: 0, Baixo: 0 };
  for (const r of results) byLevel[r.criticidade ?? "Baixo"]++;

  const totalRisk = results.reduce((s, r) => s + (r.riskScore ?? 0), 0);
  const avgRisk = results.length ? +(totalRisk / results.length).toFixed(1) : 0;

  return {
    geradoEm: new Date().toISOString(),
    total: results.length,
    avgRiskScore: avgRisk,
    porCriticidade: byLevel,
    sites: results
  };
}

// ---------- handler de estaticos ----------

function serveStatic(res, filePath) {
  const ext = path.extname(filePath).slice(1).toLowerCase();
  const types = { html: "text/html; charset=utf-8", js: "application/javascript", css: "text/css", svg: "image/svg+xml" };
  const mime = types[ext] ?? "application/octet-stream";
  try {
    const data = fs.readFileSync(filePath);
    cors(res);
    res.writeHead(200, { "Content-Type": mime });
    res.end(data);
  } catch {
    err(res, 404, "Not found");
  }
}

// ---------- servidor ----------

const server = http.createServer(async (req, res) => {
  const urlObj = new URL(req.url, `http://localhost:${PORT}`);
  const pathname = urlObj.pathname;

  // preflight CORS
  if (req.method === "OPTIONS") { cors(res); res.writeHead(204); res.end(); return; }

  // health
  if (pathname === "/health") { json(res, 200, { ok: true, ts: new Date().toISOString() }); return; }

  // dashboard
  if (pathname === "/" || pathname === "/index.html") {
    serveStatic(res, path.join(PUBLIC_DIR, "index.html"));
    return;
  }

  // estaticos da pasta public/
  if (pathname.startsWith("/public/")) {
    serveStatic(res, path.join(PUBLIC_DIR, pathname.replace(/^\/public\//, "")));
    return;
  }

  // POST /api/audit  ou  GET /api/audit?urls=...
  if (pathname === "/api/audit" && (req.method === "POST" || req.method === "GET")) {
    const timeoutMs = Number(urlObj.searchParams.get("timeout")) || 12000;

    let urls;
    try { urls = await parseUrls(req, urlObj.searchParams); }
    catch { err(res, 400, "Corpo de requisicao invalido"); return; }

    if (!urls || urls.length === 0) { err(res, 400, "Informe ao menos uma URL no campo 'urls'"); return; }
    if (urls.length > 50) { err(res, 400, "Limite de 50 URLs por requisicao"); return; }

    for (const u of urls) {
      try { new URL(/^https?:\/\//i.test(u) ? u : `https://${u}`); }
      catch { err(res, 400, `URL invalida: ${u}`); return; }
    }

    const results = await auditPool(urls, timeoutMs);
    json(res, 200, buildReport(results));
    return;
  }

  err(res, 404, "Endpoint nao encontrado");
});

server.listen(PORT, () => {
  console.log(`\n  Web Security Auditor — API + Dashboard`);
  console.log(`  http://localhost:${PORT}\n`);
});

server.on("error", (e) => {
  if (e.code === "EADDRINUSE") {
    console.error(`[ERRO] Porta ${PORT} ja em uso. Defina outra via: $env:PORT=3002; npm run server`);
  } else {
    console.error("[ERRO] Servidor:", e.message);
  }
  process.exit(1);
});
