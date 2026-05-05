## Site Java da seguradora

Também foi adicionado um site institucional em Java na pasta [seguradora-java-site](seguradora-java-site).

### Atalho rapido (raiz do workspace)

Para iniciar sem navegar entre pastas:

- `powershell -ExecutionPolicy Bypass -File .\start-local.ps1`

Outras acoes:

- `powershell -ExecutionPolicy Bypass -File .\start-local.ps1 -Action status`
- `powershell -ExecutionPolicy Bypass -File .\start-local.ps1 -Action restart`
- `powershell -ExecutionPolicy Bypass -File .\start-local.ps1 -Action stop`
- `powershell -ExecutionPolicy Bypass -File .\start-local.ps1 -Action logs`

Atalho para duplo clique no Windows (Batch):

- `start-local.bat` inicia o servidor (acao padrao `start`)
- `start-local.bat status`
- `start-local.bat restart`
- `start-local.bat stop`
- `start-local.bat logs`

### Tecnologias

- Java 17
- Spring Boot
- Thymeleaf

### Como rodar o site

1. Entre na pasta do projeto Java:

   - `cd seguradora-java-site`

2. Execute a aplicação com Maven:

   - `mvn spring-boot:run`

3. Abra no navegador:

   - `http://localhost:8081`

### Comandos estaveis de operacao (PowerShell)

Para evitar falhas por caminho com espacos, use o script de controle:

1. Entrar na pasta do projeto:

   - `cd seguradora-java-site`

2. Iniciar servidor:

   - `powershell -ExecutionPolicy Bypass -File .\scripts\server-control.ps1 -Action start`

3. Verificar status:

   - `powershell -ExecutionPolicy Bypass -File .\scripts\server-control.ps1 -Action status`

4. Ver logs:

   - `powershell -ExecutionPolicy Bypass -File .\scripts\server-control.ps1 -Action logs`

5. Reiniciar servidor:

   - `powershell -ExecutionPolicy Bypass -File .\scripts\server-control.ps1 -Action restart`

6. Parar servidor:

   - `powershell -ExecutionPolicy Bypass -File .\scripts\server-control.ps1 -Action stop`

4. Console do banco H2:

   - `http://localhost:8081/h2-console`

5. Swagger UI da API:

   - `http://localhost:8081/swagger-ui/index.html`

### Estrutura do site

- Página inicial institucional com destaques e navegação completa
- Página de portfólio em `/seguros`
- Área de simulação em `/cotacao` com cálculo inicial de mensalidade e franquia
- Página de contato em `/contato` com envio para o backend

### Fluxo do backend

- As solicitações enviadas em `/cotacao` e `/contato` são processadas pelo Spring Boot
- Os leads ficam persistidos em banco H2 local
- A cotação gera uma estimativa inicial para apoiar o retorno do time comercial

### API REST

- `POST /api/auth/login` autentica usuário e retorna JWT
- `POST /api/auth/register` cadastra novo usuário (`ADMIN` ou `USER`) com senha criptografada (requer token ADMIN)
- `GET /api/leads` lista os leads salvos
- `POST /api/contatos` cria um contato via JSON
- `POST /api/cotacoes` gera uma cotação via JSON e salva o lead
- `GET /api/mock/protheus/health` verifica o status do mock local do Protheus
- `POST /api/mock/protheus/leads` envia um lead de teste para o mock do Protheus
- `GET /api/mock/protheus/leads` lista os leads recebidos pelo mock
- `DELETE /api/mock/protheus/leads` limpa os leads do mock
- `POST /api/rag/ingest` ingere um documento textual para a base vetorial em memoria (LangChain4j)
- `POST /api/rag/ask` executa fluxo completo RAG (recuperacao + geracao)
- `GET /api/rag/status` retorna contadores da base RAG
- Documentação OpenAPI JSON: `GET /v3/api-docs`
- Interface Swagger UI: `GET /swagger-ui/index.html`

#### Credenciais padrão (demonstração)

- usuário: `admin`
- senha: `admin123`
- o usuário é criado automaticamente na tabela `APP_USER` do H2 na primeira execução

Exemplo de payload para `POST /api/auth/login`:

- `{"username":"admin","password":"admin123"}`

Use o token retornado no header das rotas protegidas:

- `Authorization: Bearer <seu_token>`

Exemplo de payload para `POST /api/auth/register`:

- `{"username":"consultor","password":"consultor123","role":"USER"}`

Exemplo de payload para `POST /api/cotacoes`:

- `{"nome":"Cliente API","email":"cliente@email.com","telefone":"11999999999","tipoSeguro":"Seguro Auto","cobertura":"Completa","valorBem":75000,"mensagem":"Quero receber proposta"}`

Exemplo de payload para `POST /api/mock/protheus/leads`:

- `{"nome":"Lead Teste","email":"lead@email.com","telefone":"11999999999","tipoSeguro":"Seguro Empresarial","mensagem":"Teste de integração com Protheus"}`

Exemplo de payload para `POST /api/rag/ingest`:

- `{"titulo":"faq-coberturas","conteudo":"Seguro Auto cobre colisao e roubo. Seguro de Vida cobre morte e invalidez."}`

Exemplo de payload para `POST /api/rag/ask`:

- `{"pergunta":"O seguro de vida cobre invalidez?","maxResultados":4,"scoreMinimo":0.55}`

### RAG com LangChain4j

- O projeto inclui um exemplo completo de RAG com LangChain4j em `POST /api/rag/ingest` e `POST /api/rag/ask`.
- A base vetorial usada no exemplo e em memoria (reinicia ao subir a aplicacao).
- O seed automatico de base inicial e opcional e vem desativado por padrao.

Variaveis de ambiente utilizadas:

- `OPENAI_API_KEY` obrigatoria para o modulo RAG responder consultas
- `OPENAI_MODEL` opcional (padrao: `gpt-4o-mini`)
- `OPENAI_EMBEDDING_MODEL` opcional (padrao: `text-embedding-3-small`)
- `app.rag.seed-on-startup` opcional (padrao: `false`)

### Mock do Protheus

- O projeto agora expõe um mock local de Protheus protegido por JWT para testes de integração.
- O mock roda na mesma aplicação Spring Boot e mantém os leads em memória.
- Ao enviar um lead, a API retorna um protocolo no formato `PTM-000001` e status `RECEBIDO`.
- Se o campo `origem` não for enviado, o mock preenche `integracao-teste` automaticamente.

### Banco de dados

- O arquivo do banco fica em `seguradora-java-site/data/seguradora-db`
- O schema é criado/atualizado automaticamente pelo Hibernate

### SQL Server

- O projeto agora suporta SQL Server via perfil Spring `sqlserver`.
- A configuração fica em `seguradora-java-site/src/main/resources/application-sqlserver.properties`.
- A dependência JDBC usada é o driver atual da Microsoft para SQL Server.

Para subir a aplicação com SQL Server no PowerShell:

- `$env:SPRING_PROFILES_ACTIVE='sqlserver'`
- `$env:DB_HOST='localhost'`
- `$env:DB_PORT='1433'`
- `$env:DB_NAME='seguradora_site'`
- `$env:DB_USERNAME='sa'`
- `$env:DB_PASSWORD='SuaSenhaForteAqui'`
- `$env:DB_ENCRYPT='true'`
- `$env:DB_TRUST_CERTIFICATE='true'`
- `mvn spring-boot:run`

Também é possível informar a URL JDBC completa em vez de host e porta:

- `$env:DB_URL='jdbc:sqlserver://localhost:1433;databaseName=seguradora_site;encrypt=true;trustServerCertificate=true;loginTimeout=30'`

Exemplo de URL JDBC gerada:

- `jdbc:sqlserver://localhost:1433;databaseName=seguradora_site;encrypt=true;trustServerCertificate=true;loginTimeout=30`

Observações:

- O perfil padrão continua usando H2 para não quebrar desenvolvimento local e testes automatizados.
- Para SQL Server em ambiente local de homologação, `trustServerCertificate=true` simplifica o setup inicial.
- Em produção, ajuste `DB_ENCRYPT` e `DB_TRUST_CERTIFICATE` conforme o certificado real do servidor.
- Um arquivo de setup para instalação local do SQL Server foi preparado em `seguradora-java-site/sqlserver/ConfigurationFile.ini`.

### Observação

- Para compilar ou executar, é necessário ter Java 17 e Maven instalados no ambiente
