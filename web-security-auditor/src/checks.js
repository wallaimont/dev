import tls from "node:tls";

function scoreFromSeverity(severity) {
  if (severity === "high") return 3;
  if (severity === "medium") return 2;
  if (severity === "low") return 1;
  return 0;
}

function createCheck({ id, title, passed, severity, details, recommendation }) {
  return {
    id,
    title,
    passed,
    severity,
    details,
    recommendation,
    score: passed ? 0 : scoreFromSeverity(severity)
  };
}

function getHeader(headers, name) {
  return headers.get(name) || headers.get(name.toLowerCase()) || "";
}

function parseCookieFlags(cookieLine) {
  const parts = cookieLine.split(";").map((x) => x.trim().toLowerCase());
  return {
    secure: parts.includes("secure"),
    httpOnly: parts.includes("httponly"),
    sameSite: parts.some((x) => x.startsWith("samesite="))
  };
}

function getSetCookieList(headers) {
  if (typeof headers.getSetCookie === "function") {
    return headers.getSetCookie();
  }

  const raw = getHeader(headers, "set-cookie");
  if (!raw) return [];
  return raw.split(/,(?=\s*[^;]+=)/g);
}

function tlsInfo(url, timeoutMs) {
  return new Promise((resolve) => {
    const { hostname } = new URL(url);

    const socket = tls.connect(
      {
        host: hostname,
        port: 443,
        servername: hostname,
        rejectUnauthorized: false,
        timeout: timeoutMs
      },
      () => {
        try {
          const cert = socket.getPeerCertificate();
          if (!cert || !cert.valid_to) {
            resolve({ ok: false, reason: "Certificado indisponivel" });
          } else {
            const expiresAt = new Date(cert.valid_to);
            const daysLeft = Math.round((expiresAt.getTime() - Date.now()) / (1000 * 60 * 60 * 24));
            resolve({ ok: true, subject: cert.subject, issuer: cert.issuer, expiresAt: expiresAt.toISOString(), daysLeft });
          }
        } catch {
          resolve({ ok: false, reason: "Falha ao ler certificado" });
        } finally {
          socket.end();
        }
      }
    );

    socket.on("error", () => resolve({ ok: false, reason: "Erro de conexao TLS" }));
    socket.on("timeout", () => {
      socket.destroy();
      resolve({ ok: false, reason: "Timeout TLS" });
    });
  });
}

async function fetchWithTimeout(url, timeoutMs) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);

  try {
    return await fetch(url, {
      redirect: "follow",
      signal: controller.signal,
      headers: {
        "user-agent": "web-security-auditor/1.0"
      }
    });
  } finally {
    clearTimeout(timer);
  }
}

async function checkAuxFile(baseUrl, filePath, timeoutMs) {
  const url = new URL(filePath, baseUrl).toString();
  try {
    const res = await fetchWithTimeout(url, timeoutMs);
    return { ok: res.ok, status: res.status, url };
  } catch {
    return { ok: false, status: 0, url };
  }
}

export async function runAudit(rawUrl, timeoutMs = 10000) {
  const normalized = /^https?:\/\//i.test(rawUrl) ? rawUrl : `https://${rawUrl}`;
  const startedAt = new Date().toISOString();

  const response = await fetchWithTimeout(normalized, timeoutMs);
  const finalUrl = response.url;
  const headers = response.headers;
  const checks = [];

  const isHttps = finalUrl.toLowerCase().startsWith("https://");
  checks.push(
    createCheck({
      id: "https",
      title: "Uso de HTTPS",
      passed: isHttps,
      severity: "high",
      details: isHttps ? `Conexao HTTPS ativa em ${finalUrl}` : "Site sem HTTPS no endpoint final",
      recommendation: "Forcar HTTPS com redirecionamento 301 e HSTS"
    })
  );

  const hsts = getHeader(headers, "strict-transport-security");
  checks.push(
    createCheck({
      id: "hsts",
      title: "HSTS configurado",
      passed: !!hsts,
      severity: "high",
      details: hsts || "Header HSTS ausente",
      recommendation: "Adicionar Strict-Transport-Security com max-age adequado"
    })
  );

  const csp = getHeader(headers, "content-security-policy");
  checks.push(
    createCheck({
      id: "csp",
      title: "Content Security Policy",
      passed: !!csp,
      severity: "medium",
      details: csp || "CSP ausente",
      recommendation: "Definir CSP restritiva para reduzir XSS"
    })
  );

  const xfo = getHeader(headers, "x-frame-options");
  const frameAncestors = /frame-ancestors/i.test(csp);
  checks.push(
    createCheck({
      id: "clickjacking",
      title: "Protecao contra clickjacking",
      passed: !!xfo || frameAncestors,
      severity: "medium",
      details: xfo ? `X-Frame-Options: ${xfo}` : frameAncestors ? "frame-ancestors presente no CSP" : "Sem X-Frame-Options/frame-ancestors",
      recommendation: "Configurar X-Frame-Options DENY/SAMEORIGIN ou frame-ancestors no CSP"
    })
  );

  const xcto = getHeader(headers, "x-content-type-options");
  checks.push(
    createCheck({
      id: "x-content-type-options",
      title: "Protecao MIME sniffing",
      passed: xcto.toLowerCase() === "nosniff",
      severity: "medium",
      details: xcto || "Header X-Content-Type-Options ausente",
      recommendation: "Definir X-Content-Type-Options: nosniff"
    })
  );

  const referrerPolicy = getHeader(headers, "referrer-policy");
  checks.push(
    createCheck({
      id: "referrer-policy",
      title: "Referrer-Policy",
      passed: !!referrerPolicy,
      severity: "low",
      details: referrerPolicy || "Referrer-Policy ausente",
      recommendation: "Definir Referrer-Policy para limitar vazamento de URL"
    })
  );

  const permissionsPolicy = getHeader(headers, "permissions-policy");
  checks.push(
    createCheck({
      id: "permissions-policy",
      title: "Permissions-Policy",
      passed: !!permissionsPolicy,
      severity: "low",
      details: permissionsPolicy || "Permissions-Policy ausente",
      recommendation: "Definir Permissions-Policy para restringir recursos do navegador"
    })
  );

  const acao = getHeader(headers, "access-control-allow-origin");
  const acac = getHeader(headers, "access-control-allow-credentials");
  const wildcardCors = acao.trim() === "*";
  const dangerousCors = wildcardCors && acac.toLowerCase() === "true";
  checks.push(
    createCheck({
      id: "cors",
      title: "Configuracao CORS",
      passed: !wildcardCors && !dangerousCors,
      severity: dangerousCors ? "high" : "medium",
      details: acao ? `ACAO=${acao}${acac ? `, ACAC=${acac}` : ""}` : "Sem cabecalhos CORS relevantes",
      recommendation: "Evitar wildcard em producao e validar origens permitidas"
    })
  );

  const serverHeader = getHeader(headers, "server");
  checks.push(
    createCheck({
      id: "server-banner",
      title: "Exposicao de banner do servidor",
      passed: !serverHeader,
      severity: "low",
      details: serverHeader ? `Server: ${serverHeader}` : "Sem banner do servidor",
      recommendation: "Ocultar ou reduzir detalhes de versao do servidor"
    })
  );

  const xPoweredBy = getHeader(headers, "x-powered-by");
  checks.push(
    createCheck({
      id: "x-powered-by",
      title: "Exposicao de tecnologia",
      passed: !xPoweredBy,
      severity: "low",
      details: xPoweredBy ? `X-Powered-By: ${xPoweredBy}` : "Sem exposicao X-Powered-By",
      recommendation: "Remover header X-Powered-By"
    })
  );

  const setCookies = getSetCookieList(headers);
  if (setCookies.length === 0) {
    checks.push(
      createCheck({
        id: "cookies",
        title: "Flags de cookies de sessao",
        passed: true,
        severity: "low",
        details: "Nenhum cookie Set-Cookie detectado na resposta",
        recommendation: "Aplicar Secure, HttpOnly e SameSite quando houver cookies"
      })
    );
  } else {
    const badCookies = setCookies
      .map((line) => ({ line, flags: parseCookieFlags(line) }))
      .filter(({ flags }) => !flags.secure || !flags.httpOnly || !flags.sameSite)
      .map(({ line }) => line.split(";")[0]);

    checks.push(
      createCheck({
        id: "cookies",
        title: "Flags de cookies de sessao",
        passed: badCookies.length === 0,
        severity: "medium",
        details:
          badCookies.length === 0
            ? "Todos os cookies avaliados possuem Secure/HttpOnly/SameSite"
            : `Cookies sem flags completas: ${badCookies.join(", ")}`,
        recommendation: "Definir Secure, HttpOnly e SameSite para cookies sensiveis"
      })
    );
  }

  const [robots, securityTxt, tls] = await Promise.all([
    checkAuxFile(finalUrl, "/robots.txt", timeoutMs),
    checkAuxFile(finalUrl, "/.well-known/security.txt", timeoutMs),
    isHttps ? tlsInfo(finalUrl, timeoutMs) : Promise.resolve({ ok: false, reason: "Nao se aplica para HTTP" })
  ]);

  checks.push(
    createCheck({
      id: "security-txt",
      title: "security.txt publicado",
      passed: securityTxt.ok,
      severity: "low",
      details: securityTxt.ok ? `Disponivel (${securityTxt.status})` : "Arquivo security.txt nao encontrado",
      recommendation: "Publicar /.well-known/security.txt com canal de contato de seguranca"
    })
  );

  checks.push(
    createCheck({
      id: "robots",
      title: "robots.txt acessivel",
      passed: robots.ok,
      severity: "info",
      details: robots.ok ? `Disponivel (${robots.status})` : "robots.txt nao encontrado ou inacessivel",
      recommendation: "Publicar robots.txt para melhorar higiene operacional"
    })
  );

  if (isHttps) {
    const certDaysLeft = tls.ok ? tls.daysLeft : -1;
    checks.push(
      createCheck({
        id: "tls-cert-expiry",
        title: "Validade do certificado TLS",
        passed: tls.ok && certDaysLeft > 14,
        severity: "high",
        details: tls.ok ? `Expira em ${certDaysLeft} dia(s)` : tls.reason,
        recommendation: "Renovar certificado antes de 14 dias para evitar indisponibilidade"
      })
    );
  }

  const riskScore = checks.reduce((sum, c) => sum + c.score, 0);
  const failed = checks.filter((c) => !c.passed);

  return {
    target: rawUrl,
    normalizedUrl: normalized,
    finalUrl,
    status: response.status,
    startedAt,
    finishedAt: new Date().toISOString(),
    riskScore,
    summary: {
      totalChecks: checks.length,
      passed: checks.length - failed.length,
      failed: failed.length,
      high: failed.filter((x) => x.severity === "high").length,
      medium: failed.filter((x) => x.severity === "medium").length,
      low: failed.filter((x) => x.severity === "low").length
    },
    tls,
    checks
  };
}
