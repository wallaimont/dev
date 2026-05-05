-- =============================================
-- V8__seeds.sql
-- Dados iniciais do sistema
-- =============================================

-- Empresa padrão
INSERT INTO empresas (codigo, razao_social, nome_fantasia, cnpj, cidade, uf, ativo, created_by)
VALUES ('001', 'OrionERP Ltda', 'OrionERP', '00.000.000/0001-00', 'São Paulo', 'SP', true, 'SYSTEM');

-- Filial matriz
INSERT INTO filiais (empresa_id, codigo, razao_social, nome_fantasia, cnpj, cidade, uf, matriz, ativo, created_by)
VALUES (1, '001', 'OrionERP Ltda - Matriz', 'OrionERP Matriz', '00.000.000/0001-00', 'São Paulo', 'SP', true, true, 'SYSTEM');

-- Perfil Administrador
INSERT INTO perfis (empresa_id, codigo, nome, descricao, admin, ativo, created_by)
VALUES (1, 'ADMIN', 'Administrador', 'Acesso total ao sistema', true, true, 'SYSTEM');

-- Perfil Operador
INSERT INTO perfis (empresa_id, codigo, nome, descricao, admin, ativo, created_by)
VALUES (1, 'OPERADOR', 'Operador', 'Acesso operacional', false, true, 'SYSTEM');

-- Usuário admin (senha: Admin@123 — BCrypt)
INSERT INTO usuarios (empresa_id, filial_id, perfil_id, nome, email, senha_hash, ativo, created_by)
VALUES (1, 1, 1, 'Administrador', 'admin@orion.com',
        '$2a$10$ehNqUNtHGezRv6Q9ptGQm.uooNEUaearIsZwCQOsjJsrCL8uvkYZ2',
        true, 'SYSTEM');

-- Permissões base
INSERT INTO permissoes (recurso, acao, descricao, modulo) VALUES
-- Administração
('empresas', 'listar', 'Listar empresas', 'ADMINISTRACAO'),
('empresas', 'criar', 'Criar empresas', 'ADMINISTRACAO'),
('empresas', 'editar', 'Editar empresas', 'ADMINISTRACAO'),
('empresas', 'excluir', 'Excluir empresas', 'ADMINISTRACAO'),
('filiais', 'listar', 'Listar filiais', 'ADMINISTRACAO'),
('filiais', 'criar', 'Criar filiais', 'ADMINISTRACAO'),
('filiais', 'editar', 'Editar filiais', 'ADMINISTRACAO'),
('filiais', 'excluir', 'Excluir filiais', 'ADMINISTRACAO'),
('usuarios', 'listar', 'Listar usuários', 'ADMINISTRACAO'),
('usuarios', 'criar', 'Criar usuários', 'ADMINISTRACAO'),
('usuarios', 'editar', 'Editar usuários', 'ADMINISTRACAO'),
('usuarios', 'excluir', 'Excluir usuários', 'ADMINISTRACAO'),
('perfis', 'listar', 'Listar perfis', 'ADMINISTRACAO'),
('perfis', 'criar', 'Criar perfis', 'ADMINISTRACAO'),
('perfis', 'editar', 'Editar perfis', 'ADMINISTRACAO'),
('perfis', 'excluir', 'Excluir perfis', 'ADMINISTRACAO'),
('parametros', 'listar', 'Listar parâmetros', 'ADMINISTRACAO'),
('parametros', 'editar', 'Editar parâmetros', 'ADMINISTRACAO'),
('auditoria', 'listar', 'Visualizar auditoria', 'ADMINISTRACAO'),
('logs', 'listar', 'Visualizar logs', 'ADMINISTRACAO'),
-- Cadastros
('clientes', 'listar', 'Listar clientes', 'CADASTRO'),
('clientes', 'criar', 'Criar clientes', 'CADASTRO'),
('clientes', 'editar', 'Editar clientes', 'CADASTRO'),
('clientes', 'excluir', 'Excluir clientes', 'CADASTRO'),
('fornecedores', 'listar', 'Listar fornecedores', 'CADASTRO'),
('fornecedores', 'criar', 'Criar fornecedores', 'CADASTRO'),
('fornecedores', 'editar', 'Editar fornecedores', 'CADASTRO'),
('fornecedores', 'excluir', 'Excluir fornecedores', 'CADASTRO'),
('produtos', 'listar', 'Listar produtos', 'CADASTRO'),
('produtos', 'criar', 'Criar produtos', 'CADASTRO'),
('produtos', 'editar', 'Editar produtos', 'CADASTRO'),
('produtos', 'excluir', 'Excluir produtos', 'CADASTRO'),
('grupos_produto', 'listar', 'Listar grupos de produto', 'CADASTRO'),
('grupos_produto', 'criar', 'Criar grupos de produto', 'CADASTRO'),
('grupos_produto', 'editar', 'Editar grupos de produto', 'CADASTRO'),
('condicoes_pagamento', 'listar', 'Listar condições de pagamento', 'CADASTRO'),
('condicoes_pagamento', 'criar', 'Criar condições de pagamento', 'CADASTRO'),
('condicoes_pagamento', 'editar', 'Editar condições de pagamento', 'CADASTRO'),
('centros_custo', 'listar', 'Listar centros de custo', 'CADASTRO'),
('centros_custo', 'criar', 'Criar centros de custo', 'CADASTRO'),
('centros_custo', 'editar', 'Editar centros de custo', 'CADASTRO'),
-- Financeiro
('contas_pagar', 'listar', 'Listar contas a pagar', 'FINANCEIRO'),
('contas_pagar', 'criar', 'Criar contas a pagar', 'FINANCEIRO'),
('contas_pagar', 'baixar', 'Baixar contas a pagar', 'FINANCEIRO'),
('contas_receber', 'listar', 'Listar contas a receber', 'FINANCEIRO'),
('contas_receber', 'criar', 'Criar contas a receber', 'FINANCEIRO'),
('contas_receber', 'baixar', 'Baixar contas a receber', 'FINANCEIRO'),
('fluxo_caixa', 'listar', 'Visualizar fluxo de caixa', 'FINANCEIRO'),
-- Compras
('solicitacoes_compra', 'listar', 'Listar solicitações de compra', 'COMPRAS'),
('solicitacoes_compra', 'criar', 'Criar solicitações de compra', 'COMPRAS'),
('pedidos_compra', 'listar', 'Listar pedidos de compra', 'COMPRAS'),
('pedidos_compra', 'criar', 'Criar pedidos de compra', 'COMPRAS'),
('pedidos_compra', 'aprovar', 'Aprovar pedidos de compra', 'COMPRAS'),
('recebimentos', 'listar', 'Listar recebimentos', 'COMPRAS'),
('recebimentos', 'criar', 'Criar recebimentos', 'COMPRAS'),
-- Estoque
('estoque', 'listar', 'Visualizar estoque', 'ESTOQUE'),
('estoque', 'movimentar', 'Movimentar estoque', 'ESTOQUE'),
('inventarios', 'listar', 'Listar inventários', 'ESTOQUE'),
('inventarios', 'criar', 'Criar inventários', 'ESTOQUE'),
-- Vendas
('orcamentos', 'listar', 'Listar orçamentos', 'VENDAS'),
('orcamentos', 'criar', 'Criar orçamentos', 'VENDAS'),
('pedidos_venda', 'listar', 'Listar pedidos de venda', 'VENDAS'),
('pedidos_venda', 'criar', 'Criar pedidos de venda', 'VENDAS'),
('pedidos_venda', 'aprovar', 'Aprovar pedidos de venda', 'VENDAS'),
-- CRM
('leads', 'listar', 'Listar leads', 'CRM'),
('leads', 'criar', 'Criar leads', 'CRM'),
('leads', 'editar', 'Editar leads', 'CRM'),
('oportunidades', 'listar', 'Listar oportunidades', 'CRM'),
('oportunidades', 'criar', 'Criar oportunidades', 'CRM'),
('oportunidades', 'editar', 'Editar oportunidades', 'CRM'),
-- Workflow
('workflow', 'listar', 'Visualizar workflows', 'WORKFLOW'),
('workflow', 'aprovar', 'Aprovar workflows', 'WORKFLOW'),
-- Relatórios
('relatorios', 'visualizar', 'Visualizar relatórios', 'RELATORIOS'),
('relatorios', 'exportar', 'Exportar relatórios', 'RELATORIOS'),
('dashboard', 'visualizar', 'Visualizar dashboard', 'RELATORIOS');

-- Vincular todas as permissões ao perfil Admin
INSERT INTO perfil_permissoes (perfil_id, permissao_id)
SELECT 1, id FROM permissoes;

-- Menus do sistema
INSERT INTO menus (codigo, titulo, icone, rota, ordem, modulo) VALUES
('dashboard', 'Dashboard', 'LayoutDashboard', '/dashboard', 1, 'DASHBOARD');

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('admin', 'Administração', 'Shield', 2, 'ADMINISTRACAO');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='admin'), 'admin.empresas', 'Empresas', 'Building2', '/admin/empresas', 1, 'ADMINISTRACAO', 'empresas'),
((SELECT id FROM menus WHERE codigo='admin'), 'admin.filiais', 'Filiais', 'GitBranch', '/admin/filiais', 2, 'ADMINISTRACAO', 'filiais'),
((SELECT id FROM menus WHERE codigo='admin'), 'admin.usuarios', 'Usuários', 'Users', '/admin/usuarios', 3, 'ADMINISTRACAO', 'usuarios'),
((SELECT id FROM menus WHERE codigo='admin'), 'admin.perfis', 'Perfis', 'UserCog', '/admin/perfis', 4, 'ADMINISTRACAO', 'perfis'),
((SELECT id FROM menus WHERE codigo='admin'), 'admin.parametros', 'Parâmetros', 'Settings', '/admin/parametros', 5, 'ADMINISTRACAO', 'parametros'),
((SELECT id FROM menus WHERE codigo='admin'), 'admin.auditoria', 'Auditoria', 'FileSearch', '/admin/auditoria', 6, 'ADMINISTRACAO', 'auditoria'),
((SELECT id FROM menus WHERE codigo='admin'), 'admin.logs', 'Logs', 'ScrollText', '/admin/logs', 7, 'ADMINISTRACAO', 'logs');

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('cadastros', 'Cadastros', 'Database', 3, 'CADASTRO');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.clientes', 'Clientes', 'UserCheck', '/cadastros/clientes', 1, 'CADASTRO', 'clientes'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.fornecedores', 'Fornecedores', 'Truck', '/cadastros/fornecedores', 2, 'CADASTRO', 'fornecedores'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.produtos', 'Produtos', 'Package', '/cadastros/produtos', 3, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.grupos', 'Grupos de Produto', 'FolderTree', '/cadastros/grupos-produto', 4, 'CADASTRO', 'grupos_produto'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.condpag', 'Condições Pagamento', 'CreditCard', '/cadastros/condicoes-pagamento', 5, 'CADASTRO', 'condicoes_pagamento'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.centrocusto', 'Centros de Custo', 'Target', '/cadastros/centros-custo', 6, 'CADASTRO', 'centros_custo');

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('financeiro', 'Financeiro', 'DollarSign', 4, 'FINANCEIRO');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='financeiro'), 'fin.pagar', 'Contas a Pagar', 'ArrowUpCircle', '/financeiro/contas-pagar', 1, 'FINANCEIRO', 'contas_pagar'),
((SELECT id FROM menus WHERE codigo='financeiro'), 'fin.receber', 'Contas a Receber', 'ArrowDownCircle', '/financeiro/contas-receber', 2, 'FINANCEIRO', 'contas_receber'),
((SELECT id FROM menus WHERE codigo='financeiro'), 'fin.fluxo', 'Fluxo de Caixa', 'TrendingUp', '/financeiro/fluxo-caixa', 3, 'FINANCEIRO', 'fluxo_caixa');

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('compras', 'Compras', 'ShoppingCart', 5, 'COMPRAS');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='compras'), 'comp.solicitacoes', 'Solicitações', 'FileText', '/compras/solicitacoes', 1, 'COMPRAS', 'solicitacoes_compra'),
((SELECT id FROM menus WHERE codigo='compras'), 'comp.pedidos', 'Pedidos de Compra', 'ClipboardList', '/compras/pedidos', 2, 'COMPRAS', 'pedidos_compra'),
((SELECT id FROM menus WHERE codigo='compras'), 'comp.recebimentos', 'Recebimentos', 'PackageCheck', '/compras/recebimentos', 3, 'COMPRAS', 'recebimentos');

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('estoque', 'Estoque', 'Warehouse', 6, 'ESTOQUE');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='estoque'), 'est.saldos', 'Saldos', 'BarChart3', '/estoque/saldos', 1, 'ESTOQUE', 'estoque'),
((SELECT id FROM menus WHERE codigo='estoque'), 'est.movimentacoes', 'Movimentações', 'ArrowLeftRight', '/estoque/movimentacoes', 2, 'ESTOQUE', 'estoque'),
((SELECT id FROM menus WHERE codigo='estoque'), 'est.inventarios', 'Inventários', 'ClipboardCheck', '/estoque/inventarios', 3, 'ESTOQUE', 'inventarios');

INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('vendas', 'Vendas', 'ShoppingBag', 7, 'VENDAS');

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='vendas'), 'vnd.orcamentos', 'Orçamentos', 'FileSpreadsheet', '/vendas/orcamentos', 1, 'VENDAS', 'orcamentos'),
((SELECT id FROM menus WHERE codigo='vendas'), 'vnd.pedidos', 'Pedidos de Venda', 'Receipt', '/vendas/pedidos', 2, 'VENDAS', 'pedidos_venda');

INSERT INTO menus (codigo, titulo, icone, rota, ordem, modulo) VALUES
('crm', 'CRM', 'HeartHandshake', '/crm/leads', 8, 'CRM'),
('workflow', 'Workflow', 'GitPullRequest', '/workflow/aprovacoes', 9, 'WORKFLOW'),
('relatorios', 'Relatórios', 'PieChart', '/relatorios', 10, 'RELATORIOS');

-- Unidades de Medida padrão
INSERT INTO unidades_medida (codigo, nome) VALUES
('UN', 'Unidade'), ('KG', 'Quilograma'), ('LT', 'Litro'),
('MT', 'Metro'), ('CX', 'Caixa'), ('PC', 'Peça'),
('M2', 'Metro Quadrado'), ('M3', 'Metro Cúbico'),
('TON', 'Tonelada'), ('ML', 'Mililitro');

-- Bancos principais
INSERT INTO bancos (codigo_banco, nome) VALUES
('001', 'Banco do Brasil'), ('033', 'Santander'), ('104', 'Caixa Econômica Federal'),
('237', 'Bradesco'), ('341', 'Itaú Unibanco'), ('389', 'Banco Mercantil'),
('422', 'Banco Safra'), ('745', 'Citibank'), ('260', 'Nubank'),
('077', 'Banco Inter'), ('336', 'C6 Bank'), ('212', 'Banco Original');

-- Parâmetros iniciais
INSERT INTO parametros_sistema (empresa_id, chave, valor, tipo, descricao, modulo) VALUES
(1, 'MOEDA', 'BRL', 'STRING', 'Moeda padrão do sistema', 'GERAL'),
(1, 'DECIMAL_CASAS_QUANTIDADE', '4', 'INTEGER', 'Casas decimais para quantidade', 'GERAL'),
(1, 'DECIMAL_CASAS_VALOR', '2', 'INTEGER', 'Casas decimais para valores', 'GERAL'),
(1, 'ESTOQUE_CUSTO_METODO', 'MEDIO', 'STRING', 'Método de custeio (MEDIO, FIFO)', 'ESTOQUE'),
(1, 'VENDA_APROVACAO_OBRIGATORIA', 'true', 'BOOLEAN', 'Exigir aprovação de pedido de venda', 'VENDAS'),
(1, 'COMPRA_APROVACAO_OBRIGATORIA', 'true', 'BOOLEAN', 'Exigir aprovação de pedido de compra', 'COMPRAS'),
(1, 'FINANCEIRO_JUROS_DIA', '0.033', 'DECIMAL', 'Juros ao dia para atraso (%)', 'FINANCEIRO'),
(1, 'FINANCEIRO_MULTA_ATRASO', '2.00', 'DECIMAL', 'Multa por atraso (%)', 'FINANCEIRO'),
(1, 'SENHA_MIN_CARACTERES', '8', 'INTEGER', 'Mínimo de caracteres para senha', 'SEGURANCA'),
(1, 'SENHA_EXIGIR_MAIUSCULA', 'true', 'BOOLEAN', 'Exigir letra maiúscula na senha', 'SEGURANCA'),
(1, 'SENHA_EXIGIR_NUMERO', 'true', 'BOOLEAN', 'Exigir número na senha', 'SEGURANCA'),
(1, 'SESSAO_TIMEOUT_MINUTOS', '30', 'INTEGER', 'Timeout de sessão em minutos', 'SEGURANCA'),
(1, 'LOGIN_MAX_TENTATIVAS', '5', 'INTEGER', 'Máximo de tentativas de login', 'SEGURANCA'),
(1, 'LOGIN_BLOQUEIO_MINUTOS', '15', 'INTEGER', 'Tempo de bloqueio após falhas', 'SEGURANCA');
