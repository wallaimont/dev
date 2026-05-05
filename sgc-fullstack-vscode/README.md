# SGC Full Stack — pronto para rodar no VS Code

Projeto base com:
- Back-end em **Java 21 + Spring Boot 3**
- Front-end em **Angular 17**
- Banco relacional **PostgreSQL**
- Autenticação com **JWT**
- CRUD de clientes
- Estrutura pronta para Git, CI/CD e expansão

## 1. Estrutura

```bash
sgc-fullstack-vscode/
  backend/
  frontend/
  docker-compose.yml
  README.md
```

## 2. Pré-requisitos

Instale na sua máquina:
- Java 21
- Maven 3.9+
- Node.js 20+
- Angular CLI (`npm install -g @angular/cli`)
- Docker Desktop
- VS Code

## 3. Abrir no VS Code

1. Extraia a pasta ZIP
2. Abra a pasta `sgc-fullstack-vscode` no VS Code
3. Abra dois terminais: um para o back-end e outro para o front-end

## 4. Subir o banco PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d
```

Banco criado:
- Database: `sgcdb`
- User: `postgres`
- Password: `postgres`
- Porta: `5432`

## 5. Rodar o back-end

```bash
cd backend
mvn spring-boot:run
```

API:
- `http://localhost:8080/api/health`
- `http://localhost:8080/api/auth/login`
- `http://localhost:8080/api/clientes`

Usuário padrão:
- Login: `admin@sgc.com`
- Senha: `123456`

## 6. Rodar o front-end

```bash
cd frontend
npm install
ng serve
```

Aplicação:
- `http://localhost:4200`

## 7. Fluxo de uso

1. Acesse `http://localhost:4200`
2. Faça login com `admin@sgc.com` / `123456`
3. Entre na tela de clientes
4. Cadastre, liste e exclua clientes

## 8. Endpoints principais

### Autenticação

```http
POST /api/auth/register
POST /api/auth/login
```

### Clientes

```http
GET    /api/clientes
GET    /api/clientes/{id}
POST   /api/clientes
PUT    /api/clientes/{id}
DELETE /api/clientes/{id}
```

## 9. Exemplo de login

```json
{
  "email": "admin@sgc.com",
  "senha": "123456"
}
```

## 10. Sugestões para próxima evolução

- módulo de produtos
- módulo de pedidos
- dashboard com métricas reais
- testes automatizados
- GitHub Actions
- Docker do back-end e front-end em produção
- refresh token
- controle por perfis mais detalhado

## 11. Git

Inicialização sugerida:

```bash
git init
git add .
git commit -m "Projeto base full stack Java + Angular"
```

## 12. Observação importante

Este projeto foi preparado para abrir e evoluir no VS Code com facilidade. A base está funcional e serve como ponto de partida profissional para portfólio, estudo, entrevista ou implantação interna.
