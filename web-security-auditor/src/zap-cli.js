import { runZapScan, summarizeZapReport } from "./zap.js";
import path from "node:path";

function printUsage() {
  console.log(`
Uso: npm run zap -- <url> [opcoes]

Opcoes:
  --mode baseline|full   Tipo de scan (padrao: baseline)
  --out  <pasta>         Pasta de saida para relatorios (padrao: ./relatorios-zap)
  --minutes <n>          Duracao maxima do active scan em minutos (full apenas, padrao: 5)
  --context <arquivo>    Arquivo de contexto ZAP .context (opcional)
  --summary              Imprime resumo do JSON no terminal apos o scan

Exemplos:
  npm run zap -- https://staging.meuapp.com
  npm run zap -- https://staging.meuapp.com --mode full --minutes 10 --summary
  npm run zap -- https://staging.meuapp.com --out C:\\relatorios --context C:\\app.context

ATENCAO: Use SOMENTE em sistemas em que voce possui autorizacao escrita do proprietario.
`);
}

function parseArgs(argv) {
  const opts = {
    target: "",
    mode: "baseline",
    outDir: path.join(process.cwd(), "relatorios-zap"),
    minutes: 5,
    context: "",
    summary: false
  };

  for (let i = 0; i < argv.length; i++) {
    const t = argv[i];
    if (t === "--mode") { opts.mode = argv[++i] ?? "baseline"; continue; }
    if (t === "--out") { opts.outDir = argv[++i] ?? opts.outDir; continue; }
    if (t === "--minutes") { opts.minutes = Number(argv[++i]) || 5; continue; }
    if (t === "--context") { opts.context = argv[++i] ?? ""; continue; }
    if (t === "--summary") { opts.summary = true; continue; }
    if (!t.startsWith("--")) { opts.target = t; }
  }

  return opts;
}

async function main() {
  const argv = process.argv.slice(2);

  if (argv.length === 0 || argv.includes("--help") || argv.includes("-h")) {
    printUsage();
    return;
  }

  const opts = parseArgs(argv);

  if (!opts.target) {
    console.error("[ERRO] Informe a URL alvo.");
    printUsage();
    process.exitCode = 1;
    return;
  }

  if (!["baseline", "full"].includes(opts.mode)) {
    console.error(`[ERRO] Modo invalido: ${opts.mode}. Use baseline ou full.`);
    process.exitCode = 1;
    return;
  }

  console.log("=".repeat(60));
  console.log("  OWASP ZAP - DAST (Teste Autorizado)");
  console.log("=".repeat(60));
  console.log(`  Alvo    : ${opts.target}`);
  console.log(`  Modo    : ${opts.mode}`);
  console.log(`  Saida   : ${opts.outDir}`);
  if (opts.mode === "full") console.log(`  Duracao : ate ${opts.minutes} min`);
  console.log("=".repeat(60));
  console.log("\n  UTILIZE APENAS COM AUTORIZACAO EXPLICITA DO PROPRIETARIO DO SISTEMA\n");

  try {
    const { reportHtml, reportJson } = await runZapScan({
      target: opts.target,
      mode: opts.mode,
      outDir: opts.outDir,
      minutes: opts.minutes,
      context: opts.context || undefined
    });

    console.log("\n[ZAP] Scan concluido.");
    if (reportHtml) console.log(`[ZAP] Relatorio HTML : ${reportHtml}`);
    if (reportJson) console.log(`[ZAP] Relatorio JSON : ${reportJson}`);

    if (opts.summary && reportJson) {
      const summary = summarizeZapReport(reportJson);
      console.log("\n=== RESUMO DO SCAN ===");
      console.log(`Total de alertas : ${summary.totalAlerts}`);
      console.log(`  High           : ${summary.summary.high}`);
      console.log(`  Medium         : ${summary.summary.medium}`);
      console.log(`  Low            : ${summary.summary.low}`);
      console.log(`  Informacional  : ${summary.summary.info}`);

      if (summary.alerts.High.length > 0) {
        console.log("\n--- ALERTAS HIGH ---");
        for (const a of summary.alerts.High) {
          console.log(`  [CWE-${a.cweid}] ${a.name} (${a.instances} ocorrencias)`);
          console.log(`  Solucao: ${a.solution?.replace(/<[^>]+>/g, "").trim()}`);
        }
      }
      if (summary.alerts.Medium.length > 0) {
        console.log("\n--- ALERTAS MEDIUM ---");
        for (const a of summary.alerts.Medium) {
          console.log(`  [CWE-${a.cweid}] ${a.name} (${a.instances} ocorrencias)`);
        }
      }
    }
  } catch (err) {
    console.error(`\n[ERRO] ${err.message}`);
    process.exitCode = 1;
  }
}

main();
