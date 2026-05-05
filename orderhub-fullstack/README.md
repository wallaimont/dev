# OrderHub Full Stack

Projeto completo para apresentar em entrevista de Desenvolvedor Full Stack.

## Tecnologias
- Java 21
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL
- Angular 17
- HTML + CSS
- Git
- APIs REST e integração externa

## O que o sistema demonstra
- CRUD de clientes
- CRUD de produtos
- Criação de pedidos
- Autenticação com JWT
- Dashboard administrativo
- Integração com API externa de faturamento
- Comunicação entre front-end, back-end e banco relacional

## Estrutura
```text
orderhub-fullstack/
├── backend
├── frontend
├── docker-compose.yml
├── schema.sql
└── README.md
```

## Como rodar no VS Code

### 1) Banco de dados
Na raiz do projeto:
```bash
docker compose up -d
```

### 2) Back-end
Abra a pasta `backend` no terminal do VS Code:
```bash
mvn spring-boot:run
```

Back-end disponível em:
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

### 3) Front-end
Abra a pasta `frontend` no terminal do VS Code:
```bash
npm install
npm start
```

Front-end disponível em:
- `http://localhost:4200`

## Credenciais demo
- E-mail: `admin@orderhub.com`
- Senha: `123456`

## Endpoints principais

### Auth
- `POST /api/auth/login`
- `POST /api/auth/register`

### Clients
- `GET /api/clients`
- `POST /api/clients`
- `PUT /api/clients/{id}`
- `DELETE /api/clients/{id}`

### Products
- `GET /api/products`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Orders
- `GET /api/orders`
- `POST /api/orders`
- `PATCH /api/orders/{id}/status`
- `POST /api/orders/{id}/send-to-billing`
- `GET /api/orders/{id}/delivery-status`

## Como explicar na entrevista
Use esta narrativa:

> Desenvolvi um sistema full stack de gestão de pedidos com Java, Spring Boot, Angular, SQL e integração entre sistemas. No back-end, construí APIs REST seguras com JWT, regras de negócio para clientes, produtos e pedidos, além de integração com um serviço externo de faturamento. No front-end, usei Angular para criar uma interface administrativa com dashboard, login e formulários. O projeto também mostra modelagem relacional, versionamento com Git e organização em camadas.

## Pontos fortes para destacar
- Arquitetura em camadas
- Segurança com JWT
- Separação de responsabilidades
- Integração externa via HTTP
- Banco relacional e regras de estoque
- Front-end organizado por features
- Projeto pronto para evoluir para Docker, testes e CI/CD
