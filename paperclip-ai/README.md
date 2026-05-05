# Paperclip AI — Configuração local

## Versão
`paperclipai` v2026.428.0 (via npx)

## Iniciar
```powershell
# Na pasta do projeto ou qualquer diretório:
npx paperclipai onboard --yes
# Responder "y" ao prompt de migração pendente
```
Servidor sobe em: **http://127.0.0.1:3100**

## Banco de dados embarcado
| Parâmetro  | Valor            |
|------------|-----------------|
| Host       | 127.0.0.1       |
| Porta      | 54329           |
| Usuário    | paperclip       |
| Senha      | paperclip       |
| Database   | paperclip       |

## Caminhos importantes
| Arquivo                | Caminho                                                                 |
|------------------------|-------------------------------------------------------------------------|
| Config principal       | `C:\Users\wallace\.paperclip\instances\default\config.json`            |
| Data dir PostgreSQL    | `C:\Users\wallace\.paperclip\instances\default\db`                     |
| pg_hba.conf            | `C:\Users\wallace\.paperclip\instances\default\db\pg_hba.conf`         |
| pg_ctl.exe             | `C:\Users\wallace\AppData\Local\npm-cache\_npx\43414d9b790239bb\node_modules\@embedded-postgres\windows-x64\native\bin\pg_ctl.exe` |
| Script de fix          | `fix_paperclip.js` (nesta pasta)                                        |

## fix_paperclip.js — quando usar?
Se uma futura migração falhar com erro de UNIQUE INDEX na tabela `issues`:
```powershell
node fix_paperclip.js
```
O script oculta linhas duplicadas (`hidden_at = NOW()`) sem deletar, evitando violação de FK.
As linhas duplicadas eram do tipo `origin_kind = 'stranded_issue_recovery'`.

## pg_hba.conf — autenticação
Alterado para `trust` nas conexões locais para permitir conexão sem senha:
```
local   all   all                       trust
host    all   all   127.0.0.1/32        trust
host    all   all   ::1/128             trust
```

## Parar o PostgreSQL embarcado manualmente
```powershell
Stop-Process -Name "postgres" -Force -ErrorAction SilentlyContinue
```

## Dependência do node_modules
O script `fix_paperclip.js` usa o módulo `pg` do cache npx:
```
C:/Users/wallace/AppData/Local/npm-cache/_npx/43414d9b790239bb/node_modules/pg
```
Se o cache for limpo, ajuste o caminho no script ou instale `pg` localmente.
