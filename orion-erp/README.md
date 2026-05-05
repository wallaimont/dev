# OrionERP

> **Gestão inteligente que conecta sua empresa ao futuro.**

ERP web enterprise, multiempresa, multifilial, multiusuário, modular — construído com stack moderna Java + React.

## Stack

| Camada     | Tecnologia                                              |
|------------|--------------------------------------------------------|
| Backend    | Java 21, Spring Boot 3.3, Spring Security, JWT, JPA    |
| Frontend   | React 18, TypeScript, Vite, Tailwind CSS, TanStack     |
| Banco      | PostgreSQL 16, Flyway                                   |
| Infra      | Docker, Docker Compose                                  |
| Docs       | Swagger / OpenAPI 3                                     |

## Documentacao das Etapas

- ETAPA 1 (produto, valor e arquitetura): `docs/ETAPA1_ESTRATEGIA_PRODUTO.md`
- ETAPA 2 (modelagem relacional e Flyway): `docs/ETAPA2_MODELAGEM_BANCO.md`
- Arquitetura complementar: `docs/ARCHITECTURE.md`
- Dicionario resumido legado: `docs/DATA_DICTIONARY.md`

## Módulos

1. **Administração** — Empresas, filiais, usuários, perfis, permissões, parâmetros, auditoria
2. **Cadastros Gerais** — Clientes, fornecedores, produtos, tabelas de preço, condições de pagamento
3. **Financeiro** — Contas a pagar/receber, títulos, fluxo de caixa, conciliação
4. **Compras** — Solicitação, cotação, pedido, aprovação, recebimento
5. **Estoque** — Armazéns, saldos, lote, kardex, inventário, transferências
6. **Vendas** — Orçamento, pedido, aprovação, faturamento, comissões
7. **Fiscal** — CFOP, CST, NCM, naturezas, regras fiscais
8. **CRM** — Leads, oportunidades, funil, atividades
9. **RH Básico** — Funcionários, departamentos, cargos
10. **Workflow** — Alçadas, aprovações, trilha de decisão
11. **Relatórios** — Dashboards, exportação PDF/Excel

## Pré-requisitos

- Java 21+
- Node.js 20+
- PostgreSQL 16+
- Maven 3.9+
- Docker & Docker Compose (opcional)

## Executar localmente

```bash
# 1. Subir infraestrutura
docker compose up -d postgres

# 2. Backend
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Frontend
cd frontend
npm install
npm run dev
```

Acesse: http://localhost:5173

API Docs: http://localhost:8081/swagger-ui.html

Health: http://localhost:8081/actuator/health

## Usuário padrão

| Login          | Senha     | Perfil          |
|----------------|-----------|-----------------|
| admin@orion.com | Admin@123 | Administrador   |

## Licença

Proprietário — OrionERP © 2026
