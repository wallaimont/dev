import fs from "node:fs/promises";
import { runAudit } from "./checks.js";

function parseArgs(argv) {
  const args = {
    targets: [],
    json: false,
    out: "",
    timeout: 10000
  };

  for (let i = 0; i < argv.length; i += 1) {
    const token = argv[i];
    if (token === "--json") {
      args.json = true;
      continue;
    }
    if (token === "--out") {
      args.out = argv[i + 1] || "";
      i += 1;
      continue;
    }
    if (token === "--timeout") {
      args.timeout = Number(argv[i + 1]) || 10000;
      i += 1;
      continue;
    }
    args.targets.push(token);
  }

  return args;
}

function statusIcon(passed) {
  return passed ? "OK" : "FAIL";
}

function severityTag(severity) {
  return severity.toUpperCase().padEnd(6, " ");
}

function printHumanReport(report) {
  console.log("\n=== AUDITORIA DE SEGURANCA WEB (PASSIVA) ===");
  console.log(`Alvo: ${report.target}`);
  console.log(`URL final: ${report.finalUrl}`);
  console.log(`HTTP status: ${report.status}`);
  console.log(`Score de risco: ${report.riskScore}`);
  console.log(
    `Resumo: ${report.summary.passed}/${report.summary.totalChecks} checks passaram | falhas: H=${report.summary.high}, M=${report.summary.medium}, L=${report.summary.low}`
  );

  for (const check of report.checks) {
    console.log(`\n[${statusIcon(check.passed)}] ${severityTag(check.severity)} ${check.title}`);
    console.log(`- Detalhe: ${check.details}`);
    if (!check.passed) {
      console.log(`- Recomendacao: ${check.recommendation}`);
    }
  }
}

async function main() {
  const argv = process.argv.slice(2);
  const options = parseArgs(argv);

  if (options.targets.length === 0) {
    console.error("Uso: npm run audit -- <url1> [url2] [--json] [--out arquivo.json] [--timeout 10000]");
    process.exitCode = 1;
    return;
  }

  const reports = [];

  for (const target of options.targets) {
    try {
      const report = await runAudit(target, options.timeout);
      reports.push(report);
      if (!options.json) {
        printHumanReport(report);
      }
    } catch (error) {
      reports.push({
        target,
        error: error?.message || "Falha desconhecida"
      });
      if (!options.json) {
        console.error(`\n[ERRO] ${target}: ${error?.message || "Falha desconhecida"}`);
      }
    }
  }

  if (options.json) {
    console.log(JSON.stringify(reports, null, 2));
  }

  if (options.out) {
    await fs.writeFile(options.out, JSON.stringify(reports, null, 2), "utf8");
    if (!options.json) {
      console.log(`\nRelatorio salvo em: ${options.out}`);
    }
  }
}

main();
