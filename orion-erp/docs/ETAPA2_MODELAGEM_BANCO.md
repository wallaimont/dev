# ETAPA 2 - Modelagem de Banco e Estratégia Flyway

## 1. Visão Geral
A modelagem relacional do OrionERP foi estruturada em PostgreSQL com foco em:
- consistência transacional
- performance por índices funcionais e compostos
- rastreabilidade com campos de auditoria
- suporte a multiempresa e multifilial
- soft delete nas entidades de negócio críticas

Migrations existentes:
- V1__admin_empresas_filiais_usuarios.sql
- V2__cadastros_gerais.sql
- V3__financeiro.sql
- V4__compras.sql
- V5__estoque.sql
- V6__vendas.sql
- V7__fiscal_crm_rh_workflow.sql
- V8__seeds.sql

## 2. Padrões de Modelagem
Campos padrão quando aplicável:
- id BIGSERIAL (PK)
- uuid UUID UNIQUE
- ativo BOOLEAN
- deleted BOOLEAN
- deleted_at TIMESTAMP
- created_at TIMESTAMP
- updated_at TIMESTAMP
- created_by VARCHAR
- updated_by VARCHAR
- empresa_id BIGINT
- filial_id BIGINT

Padrões de relacionamento:
- 1:N para entidades de cadastro e transacionais.
- N:N via tabela de junção para permissões de perfis.
- self reference para hierarquias (menus, centros de custo, categorias).

## 3. Dicionário por Domínio

### 3.1 Administração
1. empresas: cadastro de empresas clientes.
- PK: id
- UK: codigo, cnpj, uuid
- Índices: codigo, cnpj, deleted

2. filiais: unidades por empresa.
- FK: empresa_id -> empresas.id
- UK: (empresa_id, codigo)
- Índices: empresa_id, cnpj, deleted

3. perfis: papéis de acesso.
- FK: empresa_id -> empresas.id
- UK: (empresa_id, codigo)

4. usuarios: usuários do sistema.
- FK: empresa_id -> empresas.id
- FK: filial_id -> filiais.id
- FK: perfil_id -> perfis.id
- UK: email
- Índices: empresa_id, perfil_id, email, deleted

5. permissoes: ações por recurso.
- UK: (recurso, acao)
- Índices: recurso, modulo

6. perfil_permissoes: associação perfil-permissão.
- FK: perfil_id -> perfis.id
- FK: permissao_id -> permissoes.id
- UK: (perfil_id, permissao_id)

7. parametros_sistema: parâmetros globais, por empresa e por filial.
- FK: empresa_id -> empresas.id
- FK: filial_id -> filiais.id
- UK funcional: coalesce(empresa_id,0), coalesce(filial_id,0), chave

8. menus: árvore de navegação e vínculo por permissão.
- FK: parent_id -> menus.id
- UK: codigo
- Índices: parent_id, modulo

9. audit_log: trilha de alterações críticas.
- Índices: empresa_id, (entidade, entidade_id), usuario_id, created_at

10. login_log: histórico de sucesso e falha de login.
- Índices: email, created_at

11. refresh_tokens: persistência de sessão renovável.
- FK: usuario_id -> usuarios.id
- UK: token
- Índices: usuario_id, token

### 3.2 Cadastros Gerais
12. departamentos
13. cargos
14. bancos
15. contas_bancarias
16. centros_custo
17. naturezas_financeiras
18. condicoes_pagamento
19. condicao_pagamento_parcelas
20. unidades_medida
21. grupos_produto
22. subgrupos_produto
23. marcas
24. categorias
25. produtos
26. tabelas_preco
27. tabela_preco_itens
28. clientes
29. fornecedores
30. transportadoras
31. contatos

Pontos-chave:
- produtos referencia dimensões de classificação e unidade.
- clientes e fornecedores mantêm controle de crédito e condição de pagamento.
- centros_custo e naturezas_financeiras permitem estrutura hierárquica.
- tabela_preco_itens mantém granularidade de preço por item.

### 3.3 Financeiro
32. titulos
33. titulo_parcelas
34. titulo_baixas
35. fluxo_caixa

Regras estruturais:
- título pode ser a pagar ou receber.
- parcelamento em N parcelas por título.
- baixa registra liquidação com juros, multa e desconto.
- fluxo_caixa integra conta bancária e origem do lançamento.

### 3.4 Compras
36. solicitacoes_compra
37. solicitacao_compra_itens
38. cotacoes
39. cotacao_itens
40. cotacao_fornecedores
41. pedidos_compra
42. pedido_compra_itens
43. recebimentos
44. recebimento_itens

Regras estruturais:
- solicitação pode evoluir para cotação e pedido.
- pedido pode gerar recebimentos parciais.
- recebimento prepara integração com estoque e financeiro.

### 3.5 Estoque
45. armazens
46. localizacoes
47. saldos_estoque
48. movimentacoes_estoque
49. inventarios
50. inventario_itens

Regras estruturais:
- saldo por empresa, filial, armazém, localização, produto e lote.
- movimentações formam kardex com saldo antes/depois.
- inventário fecha ciclo de contagem e ajuste.

### 3.6 Vendas
51. orcamentos
52. orcamento_itens
53. pedidos_venda
54. pedido_venda_itens
55. comissoes

Regras estruturais:
- orçamento pode ser convertido em pedido.
- pedido possui estados de crédito, separação e faturamento lógico.
- comissões vinculam vendedor e pedido.

### 3.7 Fiscal
56. cfops
57. csts
58. ncms
59. naturezas_operacao
60. regras_fiscais

Regras estruturais:
- base parametrizada para cálculos e classificação fiscal.
- preparada para evolução de emissão fiscal eletrônica futura.

### 3.8 CRM
61. leads
62. oportunidades
63. atividades_crm

Regras estruturais:
- lead pode converter em cliente.
- oportunidade segue etapas de funil.
- atividades consolidam histórico de relacionamento.

### 3.9 RH
64. funcionarios

Regras estruturais:
- vínculo com departamento, cargo, filial e usuário opcional.

### 3.10 Workflow
65. workflow_definicoes
66. workflow_alcadas
67. workflow_aprovacoes

Regras estruturais:
- definição por módulo/entidade.
- alçadas por nível, perfil ou usuário.
- aprovações com decisão, justificativa e trilha temporal.

## 4. Cardinalidades Principais
- empresas 1:N filiais
- empresas 1:N perfis
- perfis 1:N usuarios
- perfis N:N permissoes (via perfil_permissoes)
- condicoes_pagamento 1:N condicao_pagamento_parcelas
- pedidos_compra 1:N pedido_compra_itens
- recebimentos 1:N recebimento_itens
- titulos 1:N titulo_parcelas
- titulo_parcelas 1:N titulo_baixas
- orcamentos 1:N orcamento_itens
- pedidos_venda 1:N pedido_venda_itens
- inventarios 1:N inventario_itens
- workflow_definicoes 1:N workflow_alcadas
- workflow_alcadas 1:N workflow_aprovacoes

## 5. Índices e Constraints
Estratégias adotadas:
- índices por chaves de busca operacional (status, data, empresa/filial).
- índices compostos em escopos multiempresa/multifilial.
- uniques funcionais para parâmetros por nível.
- checks para domínios fechados (status, tipo, decisão).

## 6. Seeds Iniciais
V8 carrega:
- empresa e filial padrão
- perfis ADMIN e OPERADOR
- usuário admin padrão
- permissões base por módulo
- menus dinâmicos iniciais
- unidades de medida, bancos e parâmetros de sistema

## 7. Ordem Lógica de Criação
1. Administração (base de segurança, tenant e governança)
2. Cadastros Gerais (domínios mestres)
3. Financeiro
4. Compras
5. Estoque
6. Vendas
7. Fiscal + CRM + RH + Workflow
8. Seeds

A ordem evita dependências cíclicas e garante integridade referencial.

## 8. Estratégia Flyway
- Versionamento incremental por macrodomínio.
- Scripts imutáveis após aplicação em ambiente compartilhado.
- Novas mudanças em arquivos V9+ apenas.
- Scripts de seed idempotentes para ambientes de desenvolvimento.
- baseline-on-migrate habilitado para facilitar adoção gradual.

Padrões operacionais:
- DDL sempre com FK explícita e índices necessários.
- Nomeação consistente para constraints e índices.
- Revisão de performance em queries críticas antes de produção.

## 9. Checklist de Governança de Dados
- Multiempresa/multifilial aplicado em entidades transacionais.
- Soft delete aplicado nas entidades de ciclo de negócio.
- Auditoria e trilha de login disponíveis.
- Estrutura pronta para relatórios e BI por módulo.
- Estrutura fiscal preparada para extensão legal futura.
