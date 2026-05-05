import { execSync, spawnSync } from "node:child_process";
import fs from "node:fs";
import path from "node:path";
import os from "node:os";

// ------------------------------------------------------------------
// Modos de scan OWASP ZAP (via Docker, imagem ghcr.io/zaproxy/zaproxy)
//
//  baseline : spidering passivo rapido (~2 min). Nao envia payloads.
//  full     : Active Scan completo (XSS, SQLi, RCE, etc). Pode demorar.
//
// REQUISITO: Docker instalado e em execucao na maquina local.
// USE APENAS EM SISTEMAS COM AUTORIZACAO EXPLICITA DO PROPRIETARIO.
// ------------------------------------------------------------------

const ZAP_IMAGE = "ghcr.io/zaproxy/zaproxy:stable";

function ensureDocker() {
  const result = spawnSync("docker", ["info"], { encoding: "utf8", shell: true });
  if (result.status !== 0) {
    throw new Error(
      "Docker nao encontrado ou nao esta em execucao.\n" +
      "Instale o Docker Desktop: https://www.docker.com/products/docker-desktop/"
    );
  }
}

function pullZapImage() {
  console.log(`[ZAP] Baixando imagem ${ZAP_IMAGE} (caso necessario)...`);
  const result = spawnSync("docker", ["pull", ZAP_IMAGE], {
    stdio: "inherit",
    shell: true
  });
  if (result.status !== 0) {
    throw new Error("Falha ao baixar imagem do OWASP ZAP.");
  }
}

/**
 * Executa o OWASP ZAP via Docker.
 *
 * @param {Object} opts
 * @param {string} opts.target       - URL alvo com autorizacao
 * @param {"baseline"|"full"} opts.mode  - Modo de scan
 * @param {string} opts.outDir       - Pasta local para salvar relatorios
 * @param {number} [opts.minutes]    - Minutos do active scan (-m). Default: 5
 * @param {string} [opts.context]    - Caminho para contexto ZAP (.context) opcional
 * @returns {{ reportHtml: string, reportJson: string, exitCode: number }}
 */
export async function runZapScan({ target, mode, outDir, minutes = 5, context }) {
  ensureDocker();
  pullZapImage();

  fs.mkdirSync(outDir, { recursive: true });

  const absOut = path.resolve(outDir);
  const timestamp = new Date().toISOString().replace(/[:.]/g, "-");
  const slug = new URL(target).hostname.replace(/\W+/g, "_");
  const htmlFile = `zap-${slug}-${timestamp}.html`;
  const jsonFile = `zap-${slug}-${timestamp}.json`;

  const containerOut = "/zap/reports";

  // Argumentos comuns
  const zapScript = mode === "full" ? "zap-full-scan.py" : "zap-baseline.py";

  const dockerArgs = [
    "run",
    "--rm",
    "-v", `${absOut}:${containerOut}`,
    ZAP_IMAGE,
    "python", `/zap/${zapScript}`,
    "-t", target,
    "-r", `${containerOut}/${htmlFile}`,
    "-J", `${containerOut}/${jsonFile}`,
    "-I"  // ignora falhas de alertas (nao encerra com exit 2)
  ];

  if (mode === "full") {
    dockerArgs.push("-m", String(minutes));
  }

  if (context) {
    const absContext = path.resolve(context);
    dockerArgs.splice(4, 0, "-v", `${absContext}:/zap/context.context`);
    dockerArgs.push("-n", "/zap/context.context");
  }

  console.log(`\n[ZAP] Iniciando scan ${mode.toUpperCase()} em: ${target}`);
  console.log(`[ZAP] Imagem: ${ZAP_IMAGE}`);
  console.log(`[ZAP] Relatorios serao salvos em: ${absOut}\n`);

  const result = spawnSync("docker", dockerArgs, {
    stdio: "inherit",
    shell: true
  });

  const reportHtml = path.join(absOut, htmlFile);
  const reportJson = path.join(absOut, jsonFile);

  if (!fs.existsSync(reportHtml) && !fs.existsSync(reportJson)) {
    throw new Error("Relatorio nao gerado. Verifique se o alvo esta acessivel e o Docker funciona.");
  }

  return {
    reportHtml: fs.existsSync(reportHtml) ? reportHtml : null,
    reportJson: fs.existsSync(reportJson) ? reportJson : null,
    exitCode: result.status
  };
}

/**
 * Le o relatorio JSON do ZAP e retorna um resumo estruturado.
 */
export function summarizeZapReport(jsonPath) {
  const raw = fs.readFileSync(jsonPath, "utf8");
  const data = JSON.parse(raw);

  const alerts = data.site?.flatMap((s) => s.alerts ?? []) ?? [];

  const bySeverity = { High: [], Medium: [], Low: [], Informational: [] };
  for (const alert of alerts) {
    const key = alert.riskdesc?.split(" ")[0];
    if (bySeverity[key]) {
      bySeverity[key].push({
        name: alert.name,
        description: alert.desc,
        solution: alert.solution,
        reference: alert.reference,
        cweid: alert.cweid,
        instances: alert.instances?.length ?? 0
      });
    }
  }

  return {
    scannedAt: new Date().toISOString(),
    totalAlerts: alerts.length,
    summary: {
      high: bySeverity.High.length,
      medium: bySeverity.Medium.length,
      low: bySeverity.Low.length,
      info: bySeverity.Informational.length
    },
    alerts: bySeverity
  };
}
