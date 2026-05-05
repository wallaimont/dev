# Web Security Auditor

Ferramenta CLI em duas camadas para auditoria de seguranca web.

> **AVISO LEGAL:** Use SOMENTE em sistemas que voce possui, gerencia, ou tem autorizacao escrita do proprietario.
> Varreduras nao autorizadas sao ilegais em diversos paises (inclusive no Brasil, Art. 154-A do Codigo Penal).

---

## Camada 1: Auditoria Passiva (headers, TLS, cookies)

Nenhum payload enviado. Apenas leitura de respostas HTTP.

### O que verifica

- HTTPS e HSTS
- Content-Security-Policy
- Protecao de clickjacking (X-Frame-Options / frame-ancestors)
- X-Content-Type-Options, Referrer-Policy, Permissions-Policy
- CORS permissivo
- Exposicao de headers Server/X-Powered-By
- Flags de cookies (Secure, HttpOnly, SameSite)
- Presenca de `/.well-known/security.txt` e `/robots.txt`
- Dias restantes para expiracao do certificado TLS

### Como usar

```powershell
cd "c:\Users\wallace\Desktop\Nova pasta (3)\web-security-auditor"

# Auditoria simples
npm run audit -- https://exemplo.com

# Multiplos alvos + JSON
npm run audit -- https://exemplo.com https://api.exemplo.com --json --out relatorio.json

# Timeout customizado (ms)
npm run audit -- https://exemplo.com --timeout 15000
```

---

## Camada 2: DAST com OWASP ZAP (baseline e full scan)

Testes ativos: XSS, SQLi, CSRF, RCE, IDOR, exposicao de dados e mais de 50 outras categorias OWASP.
Gera relatorio HTML e JSON. Requer **Docker**.

### Pre-requisito

Instale o Docker Desktop: https://www.docker.com/products/docker-desktop/

### Modos de scan

| Modo       | Velocidade | Ataque ativo | Indicado para                  |
|------------|-----------|-------------|-------------------------------|
| `baseline` | ~2 min     | Nao         | CI/CD, verificacao rapida      |
| `full`     | 5-30 min   | Sim         | Pentest autorizado, staging    |

### Como usar

```powershell
# Scan baseline (passivo rapido, sem payloads de ataque)
npm run zap:baseline -- https://staging.meuapp.com

# Scan full (ativo - envia payloads de XSS, SQLi, etc.)
npm run zap:full -- https://staging.meuapp.com

# Parametros avancados
npm run zap -- https://staging.meuapp.com --mode full --minutes 15 --out C:\relatorios --summary

# Com arquivo de contexto ZAP (para autenticacao, escopo, etc.)
npm run zap -- https://staging.meuapp.com --mode full --context C:\meu-app.context --summary
```

### Saida gerada

- `relatorios-zap/zap-<host>-<timestamp>.html` — relatorio visual completo
- `relatorios-zap/zap-<host>-<timestamp>.json` — dados estruturados para integracao
- Resumo no terminal com alertas High/Medium/Low e recomendacoes (CWE)

---

---

## Camada 3: API HTTP + Dashboard Web

Servidor HTTP local que expoe a auditoria passiva via API REST e um dashboard visual.

### Iniciar servidor

```powershell
npm run server
# Servidor ouvindo em http://localhost:3001 (ou PORT=XXXX npm run server)
```

### Dashboard

Abra `http://localhost:3001` no navegador. Funcionalidades:

- Cole URLs (uma por linha, max 50) e clique em **Auditar**
- Cards de resumo: Total, Risco medio, Critico / Alto / Medio / Baixo
- Tabela com score de risco, badge de criticidade, HTTPS, HSTS, CSP, dias de TLS e cookies
- Clique em qualquer linha para expandir os 14 checks com detalhes e recomendacoes
- Filtro por criticidade e busca por URL
- Exportar **CSV** (UTF-8 com BOM, separado por ponto-e-virgula) ou **JSON** com todos os dados

### API REST

| Metodo | Rota | Descricao |
|--------|------|-----------|
| `GET`  | `/health` | Retorna `{ ok: true, ts: "..." }` |
| `POST` | `/api/audit` | Auditoria em lote, body JSON |
| `GET`  | `/api/audit?urls=...` | Mesma auditoria via query string |

**Body da requisicao (POST):**
```json
{ "urls": ["https://exemplo.com", "https://api.exemplo.com"], "timeout": 12000 }
```

**Exemplo com curl:**
```bash
curl -s -X POST http://localhost:3001/api/audit \
  -H "Content-Type: application/json" \
  -d '{"urls":["https://example.com","https://github.com"]}' | jq .
```

**Resposta:**
```json
{
  "geradoEm": "2026-05-04T23:51:51.963Z",
  "total": 2,
  "avgRiskScore": 9,
  "porCriticidade": { "Critico": 1, "Alto": 0, "Medio": 1, "Baixo": 0 },
  "sites": [ { "target": "https://example.com", "riskScore": 13, "criticidade": "Critico", ... } ]
}
```

---

## Arquitetura dos arquivos

```
src/
  checks.js     — engine de auditoria passiva
  index.js      — CLI da camada 1
  zap.js        — executor Docker/ZAP e leitor de relatorio
  zap-cli.js    — CLI da camada 2
  server.js     — API HTTP + servico de arquivos estaticos (camada 3)
public/
  index.html    — dashboard web com dark theme, filtros e exportacao CSV/JSON
```
