# Dicionário de Dados — OrionERP

## Convenções
- PK: `id BIGSERIAL`
- UUID: `uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE`
- Soft delete: `deleted BOOLEAN DEFAULT FALSE`, `deleted_at TIMESTAMP`
- Auditoria: `created_at`, `updated_at`, `created_by`, `updated_by`
- Multiempresa: `empresa_id BIGINT REFERENCES empresas(id)`
- Multifilial: `filial_id BIGINT REFERENCES filiais(id)`
- Status: ENUM ou VARCHAR com domínio definido

## Ordem de Criação

### Fase 1 — Administração
1. empresas
2. filiais
3. perfis
4. usuarios
5. permissoes
6. perfil_permissoes
7. parametros_sistema
8. menus
9. audit_log
10. login_log

### Fase 2 — Cadastros
11. departamentos
12. cargos
13. bancos
14. contas_bancarias
15. centros_custo
16. naturezas_financeiras
17. condicoes_pagamento
18. condicao_pagamento_parcelas
19. unidades_medida
20. grupos_produto
21. subgrupos_produto
22. marcas
23. categorias
24. produtos
25. tabelas_preco
26. tabela_preco_itens
27. clientes
28. fornecedores
29. transportadoras
30. contatos

### Fase 3 — Financeiro
31. titulos
32. titulo_parcelas
33. titulo_baixas
34. fluxo_caixa

### Fase 4 — Compras
35. solicitacoes_compra
36. solicitacao_compra_itens
37. cotacoes
38. cotacao_itens
39. cotacao_fornecedores
40. pedidos_compra
41. pedido_compra_itens
42. recebimentos
43. recebimento_itens

### Fase 5 — Estoque
44. armazens
45. localizacoes
46. saldos_estoque
47. movimentacoes_estoque
48. inventarios
49. inventario_itens

### Fase 6 — Vendas
50. orcamentos
51. orcamento_itens
52. pedidos_venda
53. pedido_venda_itens
54. comissoes

### Fase 7 — Fiscal
55. cfops
56. csts
57. ncms
58. naturezas_operacao
59. regras_fiscais

### Fase 8 — CRM
60. leads
61. oportunidades
62. atividades_crm

### Fase 9 — RH
63. funcionarios

### Fase 10 — Workflow
64. workflow_definicoes
65. workflow_alcadas
66. workflow_aprovacoes
