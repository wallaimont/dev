# SIGA Seguros – Sistema Integrado de Gestão para Administradora de Seguros

Sistema web completo para gestão de administradoras de seguros, desenvolvido com **Java 21**, **Spring Boot 3** e **PostgreSQL**.

---

## Funcionalidades

| Módulo | Descrição |
|--------|-----------|
| **Dashboard** | Visão geral com cards, gráficos (Chart.js) e indicadores |
| **Clientes** | Cadastro PF/PJ com filtros e paginação |
| **Propostas** | Criação, cotação, aprovação com geração automática de número |
| **Apólices** | Gestão completa com alertas de vencimento (30 dias) |
| **Renovações** | Controle de renovações com status e acompanhamento |
| **Sinistros** | Registro e acompanhamento de sinistros |
| **Financeiro** | Contas a receber/pagar com baixa de pagamentos |
| **Comissões** | Controle de comissões por seguradora e corretora |
| **Seguradoras** | Cadastro com % comissão padrão |
| **Corretoras** | Cadastro com dados de contato e comissão |
| **Usuários** | 6 perfis: Admin, Gestor, Comercial, Operador, Financeiro, Auditor |
| **Auditoria** | Log completo de todas as operações do sistema |

---

## Tecnologias

- **Backend:** Java 21, Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Autenticação:** JWT (jjwt 0.12.5) com refresh token
- **Banco de dados:** PostgreSQL 16 (produção) / H2 (desenvolvimento)
- **Migrações:** Flyway
- **Frontend:** Thymeleaf + Bootstrap 5.3 + Chart.js 4
- **Documentação API:** Swagger/OpenAPI (springdoc 2.5.0)
- **Relatórios:** Apache POI (Excel) + iText (PDF)
- **Containerização:** Docker + Docker Compose

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 16+ (ou Docker)

---

## Executando

### Modo Desenvolvimento (H2 em memória)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Acesse: [http://localhost:8080/login](http://localhost:8080/login)

### Com Docker Compose

```bash
mvn clean package -DskipTests
docker compose up -d
```

### Apenas o Banco PostgreSQL

```bash
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

---

## Credenciais Padrão

| Usuário | E-mail | Senha | Perfil |
|---------|--------|-------|--------|
| Administrador | admin@sigaseguros.com | admin123 | ADMIN |
| Maria Gestora | maria.gestora@sigaseguros.com | admin123 | GESTOR |
| João Comercial | joao.comercial@sigaseguros.com | admin123 | COMERCIAL |
| Ana Operadora | ana.operadora@sigaseguros.com | admin123 | OPERADOR |
| Carlos Financeiro | carlos.financeiro@sigaseguros.com | admin123 | FINANCEIRO |
| Paula Auditora | paula.auditora@sigaseguros.com | admin123 | AUDITOR |

---

## API REST

Documentação Swagger disponível em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Endpoints Principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/login` | Login (retorna JWT) |
| POST | `/api/auth/refresh` | Refresh do token |
| GET/POST | `/api/clientes` | Listar/Criar clientes |
| GET/POST | `/api/propostas` | Listar/Criar propostas |
| GET/POST | `/api/apolices` | Listar/Criar apólices |
| GET/POST | `/api/renovacoes` | Listar/Criar renovações |
| GET/POST | `/api/sinistros` | Listar/Criar sinistros |
| GET/POST | `/api/financeiro` | Listar/Criar lançamentos |
| GET/POST | `/api/comissoes` | Listar/Criar comissões |
| GET/POST | `/api/seguradoras` | Listar/Criar seguradoras |
| GET/POST | `/api/corretoras` | Listar/Criar corretoras |
| GET/POST | `/api/usuarios` | Listar/Criar usuários |
| GET | `/api/dashboard` | Dados do dashboard |
| GET | `/api/auditoria` | Logs de auditoria |

---

## Estrutura do Projeto

```
siga-seguros/
├── src/main/java/com/sigaseguros/
│   ├── SigaSegurosApplication.java
│   ├── config/          # SecurityConfig, OpenApiConfig, AuditConfig
│   ├── controller/      # REST Controllers + PageController
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # Entidades JPA
│   ├── enums/           # Enums (Status, Perfil, etc.)
│   ├── exception/       # Exception handlers
│   ├── repository/      # Spring Data JPA Repositories
│   ├── security/        # JWT Filter, Provider, UserDetails
│   └── service/         # Serviços de negócio
├── src/main/resources/
│   ├── application.properties
│   ├── db/migration/    # Flyway (V1__Schema, V2__Data)
│   ├── static/          # CSS, JS
│   └── templates/       # Thymeleaf (HTML)
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Controle de Acesso (Perfis)

| Perfil | Permissões |
|--------|-----------|
| **ADMIN** | Acesso total |
| **GESTOR** | Gestão completa exceto configurações de sistema |
| **COMERCIAL** | Clientes, propostas, apólices, renovações |
| **OPERADOR** | Apólices, sinistros, renovações, documentos |
| **FINANCEIRO** | Lançamentos financeiros, comissões |
| **AUDITOR** | Visualização de auditoria (somente leitura) |

---

## Licença

Projeto proprietário – Todos os direitos reservados.
