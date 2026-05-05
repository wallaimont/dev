# ETAPA 1 - Estratégia de Produto e Arquitetura

## 1. Nome do Produto
- Nome: OrionERP
- Slogan: Gestão inteligente que conecta sua empresa ao futuro.
- Descrição comercial curta: Plataforma ERP web enterprise para gestão integrada de operações administrativas, financeiras, comerciais e logísticas, com segurança corporativa e escalabilidade para múltiplas empresas e filiais.
- Posicionamento de mercado: ERP SaaS enterprise para empresas de pequeno, médio e grande porte que precisam governança, rastreabilidade e integração ponta a ponta sem dependência de soluções legadas.

## 2. Visão do Produto
Construir um ERP corporativo moderno, multiempresa e multifilial, com forte padronização de processos, foco em segurança e capacidade de evolução contínua.

Objetivos estratégicos:
- Reduzir retrabalho entre áreas por integração nativa de módulos.
- Fornecer base de decisão executiva com dashboards e trilhas auditáveis.
- Viabilizar expansão de clientes e filiais sem reescrita arquitetural.
- Garantir conformidade operacional e trilha de responsabilidades por usuário.

## 3. Proposta de Valor
- Plataforma unificada para Administração, Cadastros, Financeiro, Compras, Estoque, Vendas, Fiscal, CRM, RH e Workflow.
- Arquitetura preparada para alto volume transacional com PostgreSQL + cache evolutivo.
- Segurança by design: JWT, refresh token, RBAC, auditoria e logs de autenticação.
- Base técnica pronta para evolução para microsserviços por bounded contexts.

## 4. Arquitetura Recomendada
Padrão: monolito modular com DDD light e camadas claras.

Camadas:
1. Apresentação: controllers REST + validações de entrada.
2. Aplicação: serviços de caso de uso, orquestração e regras.
3. Domínio: entidades, enums e regras centrais.
4. Infraestrutura: persistência, segurança, mensageria futura e observabilidade.

Diretrizes:
- SOLID aplicado nos serviços e casos de uso.
- DTOs obrigatórios para borda de API.
- MapStruct para mapeamentos.
- Soft delete para entidades de negócio sensíveis.
- Auditoria em entidades críticas e fluxos de aprovação.

## 5. Justificativa da Stack
Backend:
- Java 21 (LTS): desempenho e suporte de longo prazo.
- Spring Boot 3.x: ecossistema maduro para APIs enterprise.
- Spring Security + JWT: autenticação stateless e RBAC.
- JPA/Hibernate: produtividade e abstração relacional robusta.
- Flyway: governança de schema com versionamento.
- Swagger/OpenAPI: contrato vivo de APIs.
- JUnit 5 + Mockito: base de qualidade automatizada.

Frontend:
- React + TypeScript + Vite: produtividade, tipagem e performance.
- React Router + TanStack Query: roteamento modular e cache de dados.
- React Hook Form + Zod: formulários robustos com validação.
- Tailwind CSS: design system consistente e escalável.

Infra:
- Docker e Docker Compose: ambiente reprodutível no VS Code.
- .env + scripts: onboarding rápido da equipe.

## 6. Estratégia de Segurança
Controles mandatórios:
- Access token JWT de curta duração e refresh token persistido.
- Hash de senha com BCrypt.
- Política de senha parametrizável por empresa.
- Logs de login e falha de autenticação.
- RBAC por recurso/ação com herança por perfil.
- Validação de entrada em backend (Bean Validation) e frontend (Zod).
- CORS por ambiente.
- Mascaramento de dados sensíveis em logs e responses.
- Auditoria de alterações (quem, quando, o que mudou).
- Conformidade básica LGPD: minimização de dados e rastreabilidade de acesso.

## 7. Estratégia Multiempresa e Multifilial
- Todas as entidades transacionais carregam empresa_id e filial_id quando aplicável.
- Identidade do usuário inclui escopo de empresa/filial no token.
- Regras de acesso restringem dados ao escopo autorizado.
- Parâmetros com três níveis:
  - global
  - por empresa
  - por filial

Regras de precedência de parâmetros:
1. Filial
2. Empresa
3. Global

## 8. Organização de Módulos
Módulos de negócio:
1. Administração
2. Cadastros Gerais
3. Financeiro
4. Compras
5. Estoque
6. Vendas
7. Fiscal
8. CRM
9. RH
10. Workflow
11. Relatórios

Cada módulo segue padrão:
- controller
- service
- repository
- domain
- dto
- mapper

## 9. Fluxo Macro Entre Módulos
1. Compras aprovadas geram entrada de estoque e título a pagar.
2. Vendas aprovadas geram saída de estoque e título a receber.
3. Workflow controla decisões críticas em Compras, Vendas e Financeiro.
4. Fiscal enriquece operações para futura emissão NF-e.
5. Dashboards consolidam indicadores financeiros, comerciais e operacionais.

## 10. Estrutura Inicial para VS Code
Estrutura alvo no repositório:
- backend: API Java/Spring
- frontend: SPA React/TypeScript
- docs: arquitetura, modelagem, contratos
- infra: compose, Dockerfiles, scripts

Critérios de execução local:
- backend com profile dev e PostgreSQL local/container.
- frontend com Vite e proxy para API.
- documentação OpenAPI disponível no backend.

## 11. Roadmap de Desenvolvimento
1. ETAPA 1: Estratégia, posicionamento e arquitetura.
2. ETAPA 2: Modelagem relacional completa + Flyway.
3. ETAPA 3: Backend base (security, JWT, refresh, auditoria, erros).
4. ETAPA 4: Administração completo.
5. ETAPA 5: Cadastros Gerais.
6. ETAPA 6: Financeiro.
7. ETAPA 7: Compras.
8. ETAPA 8: Estoque.
9. ETAPA 9: Vendas.
10. ETAPA 10: Fiscal + CRM + RH + Workflow.
11. ETAPA 11: Frontend completo por módulos.
12. ETAPA 12: Relatórios, exportações, testes, Docker final e guia operacional.

## 12. Critérios de Qualidade
- Cobertura de testes progressiva por módulo.
- Logs padronizados com correlação por request.
- API versionada em /api/v1.
- Migrações idempotentes e rastreáveis.
- Build reproduzível e execução local em menos de 10 minutos.
