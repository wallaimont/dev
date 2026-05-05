-- ============================================================
-- V18 - Seeds de Teste Completo para TODAS as tabelas do OrionERP
-- Popula com dados realistas para demonstração/teste
-- ============================================================

-- ============================================================
-- 1. CADASTROS BASE
-- ============================================================

-- Marcas
INSERT INTO marcas (id, empresa_id, codigo, nome, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'MRC001', 'Samsung', true, false, NOW(), NOW()),
  (2, 1, 'MRC002', 'LG Electronics', true, false, NOW(), NOW()),
  (3, 1, 'MRC003', 'Bosch', true, false, NOW(), NOW()),
  (4, 1, 'MRC004', 'Tramontina', true, false, NOW(), NOW()),
  (5, 1, 'MRC005', 'WEG', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Departamentos
INSERT INTO departamentos (id, empresa_id, codigo, nome, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'DEP001', 'Administrativo', true, false, NOW(), NOW()),
  (2, 1, 'DEP002', 'Comercial', true, false, NOW(), NOW()),
  (3, 1, 'DEP003', 'Financeiro', true, false, NOW(), NOW()),
  (4, 1, 'DEP004', 'Produção', true, false, NOW(), NOW()),
  (5, 1, 'DEP005', 'RH', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Transportadoras
INSERT INTO transportadoras (id, empresa_id, codigo, razao_social, nome_fantasia, cpf_cnpj, endereco, cidade, uf, telefone, email, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 'TRA002', 'Jadlog Logística S.A.', 'Jadlog', '04.884.082/0001-35', 'Av. Pirâmide 663', 'São Paulo', 'SP', '(11) 3375-9000', 'contato@jadlog.com.br', true, false, NOW(), NOW()),
  (3, 1, 'TRA003', 'Total Express', 'Total Express', '08.758.037/0001-57', 'Rua das Flores 321', 'Barueri', 'SP', '(11) 4133-9999', 'atendimento@totalexpress.com.br', true, false, NOW(), NOW()),
  (4, 1, 'TRA004', 'Braspress Transportes Urgentes', 'Braspress', '48.740.351/0001-65', 'Rod. Fernão Dias Km 110', 'Belo Horizonte', 'MG', '(31) 3309-7000', 'sac@braspress.com', true, false, NOW(), NOW()),
  (5, 1, 'TRA005', 'Azul Cargo Express', 'Azul Cargo', '09.296.295/0001-60', 'Aeroporto de Campinas', 'Campinas', 'SP', '(19) 3725-3040', 'cargo@voeazul.com.br', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Armazéns
INSERT INTO armazens (id, empresa_id, filial_id, codigo, nome, tipo, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 1, 'ARM002', 'Armazém Produtos Acabados', 'ACABADO', true, false, NOW(), NOW()),
  (3, 1, 1, 'ARM003', 'Armazém Matéria-Prima', 'MATERIA_PRIMA', true, false, NOW(), NOW()),
  (4, 1, 1, 'ARM004', 'Armazém Expedição', 'EXPEDICAO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Localizações nos armazéns
INSERT INTO localizacoes (id, armazem_id, codigo, descricao, ativo, created_at, updated_at)
VALUES
  (1, 1, 'A01-01', 'Corredor A, Prateleira 01', true, NOW(), NOW()),
  (2, 1, 'A01-02', 'Corredor A, Prateleira 02', true, NOW(), NOW()),
  (3, 2, 'B01-01', 'Corredor B, Prateleira 01', true, NOW(), NOW()),
  (4, 3, 'C01-01', 'Corredor C, Prateleira 01', true, NOW(), NOW()),
  (5, 4, 'EXP-01', 'Doca de Expedição 01', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Tabelas de Preço
INSERT INTO tabelas_preco (id, empresa_id, codigo, nome, vigencia_inicio, vigencia_fim, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'TAB001', 'Tabela Varejo', '2025-01-01', '2025-12-31', true, false, NOW(), NOW()),
  (2, 1, 'TAB002', 'Tabela Atacado', '2025-01-01', '2025-12-31', true, false, NOW(), NOW()),
  (3, 1, 'TAB003', 'Tabela Promocional', '2025-06-01', '2025-08-31', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 2. CLIENTES (adicionais)
-- ============================================================
INSERT INTO clientes (id, empresa_id, codigo, tipo_pessoa, razao_social, nome_fantasia, cpf_cnpj, inscricao_estadual, endereco, numero, bairro, cidade, uf, cep, telefone, celular, email, limite_credito, saldo_devedor, ativo, deleted, created_at, updated_at, condicao_pagamento_id, tabela_preco_id)
VALUES
  (2, 1, 'CLI002', 'J', 'Tech Solutions Informática Ltda', 'Tech Solutions', '12.345.678/0001-90', '123.456.789.00', 'Rua dos Programadores 100', '100', 'Centro', 'São Paulo', 'SP', '01000-000', '(11) 3000-1000', '(11) 99000-1000', 'contato@techsolutions.com.br', 50000.00, 0.00, true, false, NOW(), NOW(), 25, 1),
  (3, 1, 'CLI003', 'J', 'Metalúrgica Ferro & Aço S.A.', 'Ferro & Aço', '23.456.789/0001-01', '234.567.890.00', 'Av. Industrial 500', '500', 'Distrito Industrial', 'Guarulhos', 'SP', '07000-000', '(11) 2000-2000', '(11) 98000-2000', 'compras@ferroaco.com.br', 100000.00, 0.00, true, false, NOW(), NOW(), 26, 1),
  (4, 1, 'CLI004', 'F', 'Maria Aparecida Santos', NULL, '123.456.789-00', NULL, 'Rua das Acácias 42', '42', 'Jardim Primavera', 'Campinas', 'SP', '13000-000', '(19) 3200-3000', '(19) 97000-3000', 'maria.santos@email.com', 10000.00, 0.00, true, false, NOW(), NOW(), 25, 1),
  (5, 1, 'CLI005', 'J', 'Construtora Horizonte Ltda', 'Horizonte Engenharia', '34.567.890/0001-12', '345.678.901.00', 'Rua dos Engenheiros 750', '750', 'Alphaville', 'Barueri', 'SP', '06400-000', '(11) 4500-4000', '(11) 96000-4000', 'projetos@horizonte.eng.br', 200000.00, 0.00, true, false, NOW(), NOW(), 27, 2),
  (6, 1, 'CLI006', 'J', 'Supermercado Bom Preço Ltda', 'Bom Preço', '45.678.901/0001-23', '456.789.012.00', 'Av. Brasil 1200', '1200', 'Centro', 'Curitiba', 'PR', '80000-000', '(41) 3100-5000', '(41) 95000-5000', 'compras@bompreco.com.br', 75000.00, 0.00, true, false, NOW(), NOW(), 26, 2)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 3. FORNECEDORES (adicionais)
-- ============================================================
INSERT INTO fornecedores (id, empresa_id, codigo, tipo_pessoa, razao_social, nome_fantasia, cpf_cnpj, inscricao_estadual, endereco, cidade, uf, telefone, email, ativo, deleted, created_at, updated_at, condicao_pagamento_id)
VALUES
  (2, 1, 'FOR002', 'J', 'Distribuidora Nacional de Insumos S.A.', 'DNI', '56.789.012/0001-34', '567.890.123.00', 'Rod. Anhanguera Km 45', 'Jundiaí', 'SP', '(11) 4600-6000', 'vendas@dni.com.br', true, false, NOW(), NOW(), 25),
  (3, 1, 'FOR003', 'J', 'Indústria Química Brasileira Ltda', 'IQB', '67.890.123/0001-45', '678.901.234.00', 'Av. Química 200', 'Paulínia', 'SP', '(19) 3800-7000', 'comercial@iqb.ind.br', true, false, NOW(), NOW(), 26),
  (4, 1, 'FOR004', 'J', 'Aço Forte Siderúrgica S.A.', 'Aço Forte', '78.901.234/0001-56', '789.012.345.00', 'Distrito Industrial SN', 'Volta Redonda', 'RJ', '(24) 3600-8000', 'vendas@acoforte.com.br', true, false, NOW(), NOW(), 27),
  (5, 1, 'FOR005', 'J', 'Embalagens Premium Ltda', 'Premium Pack', '89.012.345/0001-67', '890.123.456.00', 'Rua dos Embaladores 80', 'Cotia', 'SP', '(11) 4700-9000', 'contato@premiumpack.com.br', true, false, NOW(), NOW(), 25)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 4. PRODUTOS (adicionais)
-- ============================================================
INSERT INTO produtos (id, empresa_id, codigo, nome, descricao, grupo_id, marca_id, unidade_medida_id, preco_custo, preco_venda, estoque_minimo, estoque_maximo, controla_estoque, controla_lote, controla_validade, tipo, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 'PRD002', 'Parafuso Sextavado M10', 'Parafuso sextavado M10 x 40mm aço galvanizado', 19, 3, 1, 0.35, 0.85, 1000, 10000, true, true, false, 'REVENDA', true, false, NOW(), NOW()),
  (3, 1, 'PRD003', 'Motor Elétrico 1CV', 'Motor elétrico trifásico 1CV 220/380V', 19, 5, 1, 450.00, 890.00, 5, 50, true, false, false, 'REVENDA', true, false, NOW(), NOW()),
  (4, 1, 'PRD004', 'Óleo Lubrificante SAE 20W50', 'Óleo lubrificante mineral 1 litro', 19, 3, 2, 12.50, 28.90, 50, 500, true, true, true, 'REVENDA', true, false, NOW(), NOW()),
  (5, 1, 'PRD005', 'Chapa Aço Carbono 3mm', 'Chapa de aço carbono SAE 1020 3mm 1x2m', 17, NULL, 3, 180.00, 320.00, 10, 100, true, false, false, 'MATERIA_PRIMA', true, false, NOW(), NOW()),
  (6, 1, 'PRD006', 'Rolamento 6205 ZZ', 'Rolamento rígido de esferas blindado', 19, NULL, 1, 18.00, 42.00, 20, 200, true, false, false, 'REVENDA', true, false, NOW(), NOW()),
  (7, 1, 'PRD007', 'Fita Isolante 18mm', 'Fita isolante PVC 18mm x 20m preta', 19, NULL, 1, 3.20, 7.50, 100, 1000, true, false, false, 'REVENDA', true, false, NOW(), NOW()),
  (8, 1, 'PRD008', 'Sensor de Temperatura PT100', 'Sensor de temperatura PT100 classe A', 19, NULL, 1, 85.00, 165.00, 10, 100, true, false, false, 'REVENDA', true, false, NOW(), NOW()),
  (9, 1, 'PRD009', 'Graxa Industrial EP2', 'Graxa para rolamentos multi-uso 500g', 19, 3, 1, 22.00, 48.00, 30, 300, true, true, true, 'REVENDA', true, false, NOW(), NOW()),
  (10, 1, 'PRD010', 'Conjunto Montado EletroMec', 'Conjunto eletromecânico montado linha Alpha', 18, 5, 1, 1200.00, 2500.00, 2, 20, true, false, false, 'PRODUCAO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Tabela Preço Itens
INSERT INTO tabela_preco_itens (id, tabela_preco_id, produto_id, preco, percentual_desconto_maximo, ativo, created_at, updated_at)
VALUES
  (1, 1, 1, 150.00, 10.00, true, NOW(), NOW()),
  (2, 1, 2, 0.85, 5.00, true, NOW(), NOW()),
  (3, 1, 3, 890.00, 8.00, true, NOW(), NOW()),
  (4, 1, 4, 28.90, 5.00, true, NOW(), NOW()),
  (5, 1, 5, 320.00, 10.00, true, NOW(), NOW()),
  (6, 2, 1, 130.00, 15.00, true, NOW(), NOW()),
  (7, 2, 2, 0.70, 10.00, true, NOW(), NOW()),
  (8, 2, 3, 780.00, 12.00, true, NOW(), NOW()),
  (9, 3, 4, 22.90, 5.00, true, NOW(), NOW()),
  (10, 3, 7, 5.90, 5.00, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Estrutura de Produto (BOM para produto 10: Conjunto Montado)
INSERT INTO estrutura_produto (id, empresa_id, produto_pai_id, produto_componente_id, quantidade, perda_percentual, sequencia, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 10, 3, 1.00, 0.00, 1, true, false, NOW(), NOW()),
  (2, 1, 10, 6, 4.00, 2.00, 2, true, false, NOW(), NOW()),
  (3, 1, 10, 8, 2.00, 0.00, 3, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 5. CONTAS BANCÁRIAS (adicionais)
-- ============================================================
INSERT INTO contas_bancarias (id, empresa_id, banco_id, agencia, conta, digito, tipo, descricao, saldo_inicial, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 2, '0001', '54321', '0', 'CORRENTE', 'Conta Corrente Itaú', 50000.00, true, false, NOW(), NOW()),
  (3, 1, 3, '3456', '789012', '3', 'CORRENTE', 'Conta Corrente Bradesco', 25000.00, true, false, NOW(), NOW()),
  (4, 1, 1, '1234', '000111', '1', 'APLICACAO', 'Conta Aplicação BB', 100000.00, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 6. RH - Cargos, Departamentos, Funcionários, Benefícios
-- ============================================================
INSERT INTO cargos (id, empresa_id, codigo, nome, descricao, cbo, salario_base, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 'CAR002', 'Analista de Sistemas', 'Análise e desenvolvimento de sistemas', '212205', 6500.00, true, false, NOW(), NOW()),
  (3, 1, 'CAR003', 'Vendedor Externo', 'Vendas externas e prospecção', '524110', 3200.00, true, false, NOW(), NOW()),
  (4, 1, 'CAR004', 'Operador de Produção', 'Operação de máquinas industriais', '811130', 2800.00, true, false, NOW(), NOW()),
  (5, 1, 'CAR005', 'Contador', 'Contabilidade geral e fiscal', '252210', 7500.00, true, false, NOW(), NOW()),
  (6, 1, 'CAR006', 'Auxiliar Administrativo', 'Apoio administrativo geral', '411005', 2200.00, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO departamentos_rh (id, empresa_id, codigo, nome, centro_custo_id, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 'DRH002', 'Tecnologia da Informação', 56, true, false, NOW(), NOW()),
  (3, 1, 'DRH003', 'Departamento Comercial', 58, true, false, NOW(), NOW()),
  (4, 1, 'DRH004', 'Produção Industrial', 63, true, false, NOW(), NOW()),
  (5, 1, 'DRH005', 'Contabilidade', 55, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO beneficios (id, empresa_id, codigo, nome, tipo, valor_empresa, valor_funcionario, desconto_folha, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'BEN001', 'Vale Transporte', 'VALE_TRANSPORTE', 250.00, 150.00, true, true, false, NOW(), NOW()),
  (2, 1, 'BEN002', 'Vale Refeição', 'VALE_REFEICAO', 660.00, 0.00, false, true, false, NOW(), NOW()),
  (3, 1, 'BEN003', 'Plano de Saúde', 'PLANO_SAUDE', 450.00, 150.00, true, true, false, NOW(), NOW()),
  (4, 1, 'BEN004', 'Seguro de Vida', 'SEGURO_VIDA', 80.00, 0.00, false, true, false, NOW(), NOW()),
  (5, 1, 'BEN005', 'Plano Odontológico', 'PLANO_ODONTOLOGICO', 120.00, 40.00, true, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO funcionarios (id, empresa_id, filial_id, codigo, nome, cpf, rg, data_nascimento, sexo, estado_civil, endereco, cidade, uf, cep, telefone, email, departamento_id, cargo_id, data_admissao, salario, situacao, pis, tipo_contrato, jornada_trabalho, banco_id, agencia, conta_corrente, ativo, deleted, created_at, updated_at)
VALUES
  (4, 1, 1, 'FUN004', 'Carlos Eduardo Mendes', '111.222.333-44', '12.345.678-9', '1990-05-15', 'M', 'CASADO', 'Rua Java 100', 'São Paulo', 'SP', '01100-000', '(11) 99111-2222', 'carlos.mendes@orion.com', 2, 2, '2023-03-01', 6500.00, 'ATIVO', '123.45678.90-1', 'CLT', '44h', 1, '1234', '56789-0', true, false, NOW(), NOW()),
  (5, 1, 1, 'FUN005', 'Ana Paula Ferreira', '222.333.444-55', '23.456.789-0', '1988-11-22', 'F', 'SOLTEIRO', 'Av. Vendas 200', 'São Paulo', 'SP', '01200-000', '(11) 99222-3333', 'ana.ferreira@orion.com', 3, 3, '2022-08-15', 3200.00, 'ATIVO', '234.56789.01-2', 'CLT', '44h', 2, '0001', '12345-6', true, false, NOW(), NOW()),
  (6, 1, 1, 'FUN006', 'Roberto Silva Santos', '333.444.555-66', '34.567.890-1', '1995-02-10', 'M', 'CASADO', 'Rua Fábrica 300', 'Guarulhos', 'SP', '07100-000', '(11) 99333-4444', 'roberto.santos@orion.com', 4, 4, '2024-01-10', 2800.00, 'ATIVO', '345.67890.12-3', 'CLT', '44h', 1, '1234', '98765-4', true, false, NOW(), NOW()),
  (7, 1, 1, 'FUN007', 'Luciana Costa Oliveira', '444.555.666-77', '45.678.901-2', '1985-07-30', 'F', 'CASADO', 'Rua Contábil 400', 'São Paulo', 'SP', '01300-000', '(11) 99444-5555', 'luciana.oliveira@orion.com', 5, 5, '2021-05-20', 7500.00, 'ATIVO', '456.78901.23-4', 'CLT', '44h', 3, '3456', '11111-2', true, false, NOW(), NOW()),
  (8, 1, 1, 'FUN008', 'Pedro Henrique Almeida', '555.666.777-88', '56.789.012-3', '1998-09-05', 'M', 'SOLTEIRO', 'Rua Admin 500', 'São Paulo', 'SP', '01400-000', '(11) 99555-6666', 'pedro.almeida@orion.com', 1, 6, '2024-06-01', 2200.00, 'ATIVO', '567.89012.34-5', 'CLT', '44h', 2, '0001', '22222-3', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Funcionário-Benefício
INSERT INTO funcionario_beneficio (id, funcionario_id, beneficio_id, data_inicio, ativo, deleted, created_at, updated_at, empresa_id)
VALUES
  (1, 4, 1, '2023-03-01', true, false, NOW(), NOW(), 1),
  (2, 4, 2, '2023-03-01', true, false, NOW(), NOW(), 1),
  (3, 4, 3, '2023-06-01', true, false, NOW(), NOW(), 1),
  (4, 5, 1, '2022-08-15', true, false, NOW(), NOW(), 1),
  (5, 5, 2, '2022-08-15', true, false, NOW(), NOW(), 1),
  (6, 6, 1, '2024-01-10', true, false, NOW(), NOW(), 1),
  (7, 6, 2, '2024-01-10', true, false, NOW(), NOW(), 1),
  (8, 7, 2, '2021-05-20', true, false, NOW(), NOW(), 1),
  (9, 7, 3, '2021-05-20', true, false, NOW(), NOW(), 1),
  (10, 8, 1, '2024-06-01', true, false, NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- Ponto Eletrônico (últimos 5 dias úteis para cada funcionário)
INSERT INTO ponto_eletronico (id, empresa_id, funcionario_id, data, entrada1, saida1, entrada2, saida2, horas_trabalhadas, horas_extras, horas_falta, tipo, aprovado, ativo, created_at, updated_at)
VALUES
  (1, 1, 4, '2025-07-14', '08:00', '12:00', '13:00', '17:00', 8.00, 0.00, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (2, 1, 4, '2025-07-15', '08:00', '12:00', '13:00', '18:00', 9.00, 1.00, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (3, 1, 5, '2025-07-14', '09:00', '12:00', '13:00', '18:00', 8.00, 0.00, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (4, 1, 5, '2025-07-15', '09:00', '12:00', '13:00', '18:00', 8.00, 0.00, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (5, 1, 6, '2025-07-14', '06:00', '11:00', '12:00', '15:22', 8.37, 0.37, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (6, 1, 6, '2025-07-15', '06:00', '11:00', '12:00', '15:00', 8.00, 0.00, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (7, 1, 7, '2025-07-14', '08:00', '12:00', '13:00', '17:00', 8.00, 0.00, 0.00, 'NORMAL', true, true, NOW(), NOW()),
  (8, 1, 8, '2025-07-14', '08:00', '12:00', '13:00', '17:00', 8.00, 0.00, 0.00, 'NORMAL', true, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Férias
INSERT INTO ferias (id, empresa_id, funcionario_id, periodo_aquisitivo_inicio, periodo_aquisitivo_fim, data_inicio, data_fim, dias_gozo, dias_abono, valor_ferias, valor_terco, valor_abono, valor_adiantamento_13, total_bruto, total_descontos, total_liquido, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 4, '2023-03-01', '2024-02-28', '2024-07-01', '2024-07-30', 30, 0, 6500.00, 2166.67, 0.00, 0.00, 8666.67, 1820.00, 6846.67, 'CONCLUIDA', true, false, NOW(), NOW()),
  (2, 1, 5, '2022-08-15', '2023-08-14', '2023-12-18', '2024-01-16', 30, 0, 3200.00, 1066.67, 0.00, 0.00, 4266.67, 895.00, 3371.67, 'CONCLUIDA', true, false, NOW(), NOW()),
  (3, 1, 7, '2021-05-20', '2022-05-19', '2022-11-01', '2022-11-30', 30, 0, 7500.00, 2500.00, 0.00, 0.00, 10000.00, 2250.00, 7750.00, 'CONCLUIDA', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Folha de Pagamento
INSERT INTO folha_pagamento (id, empresa_id, filial_id, ano, mes, tipo, data_calculo, data_pagamento, total_proventos, total_descontos, total_liquido, total_encargos, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 2025, 6, 'MENSAL', '2025-06-28', '2025-07-05', 22200.00, 5328.00, 16872.00, 6660.00, 'FECHADA', true, false, NOW(), NOW()),
  (2, 1, 1, 2025, 7, 'MENSAL', NULL, NULL, 0.00, 0.00, 0.00, 0.00, 'ABERTA', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Folha Pagamento Itens (mês 06/2025)
INSERT INTO folha_pagamento_item (id, folha_pagamento_id, funcionario_id, empresa_id, salario_base, horas_extras_50, horas_extras_100, adicional_noturno, adicional_periculosidade, adicional_insalubridade, comissao, gratificacao, outros_proventos, total_proventos, inss, irrf, vale_transporte, vale_refeicao, plano_saude, faltas, adiantamento, outros_descontos, total_descontos, salario_liquido, fgts, inss_empresa, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 4, 1, 6500.00, 2, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 6825.00, 889.28, 660.57, 150.00, 0.00, 150.00, 0, 0.00, 0.00, 1849.85, 4975.15, 546.00, 1365.00, true, false, NOW(), NOW()),
  (2, 1, 5, 1, 3200.00, 0, 0, 0.00, 0.00, 0.00, 850.00, 0.00, 0.00, 4050.00, 445.50, 131.79, 150.00, 0.00, 0.00, 0, 0.00, 0.00, 727.29, 3322.71, 324.00, 810.00, true, false, NOW(), NOW()),
  (3, 1, 6, 1, 2800.00, 3, 0, 200.00, 0.00, 0.00, 0.00, 0.00, 0.00, 3190.91, 350.99, 0.00, 150.00, 0.00, 0.00, 0, 0.00, 0.00, 500.99, 2689.92, 255.27, 638.18, true, false, NOW(), NOW()),
  (4, 1, 7, 1, 7500.00, 0, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 7500.00, 828.38, 1050.37, 0.00, 0.00, 150.00, 0, 0.00, 0.00, 2028.75, 5471.25, 600.00, 1500.00, true, false, NOW(), NOW()),
  (5, 1, 8, 1, 2200.00, 0, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 2200.00, 198.00, 0.00, 150.00, 0.00, 0.00, 0, 0.00, 0.00, 348.00, 1852.00, 176.00, 440.00, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 7. SEGUROS - Seguradoras, Corretoras, Propostas, Apólices
-- ============================================================
INSERT INTO seguradoras (id, empresa_id, codigo, nome, cnpj, registro_susep, email, telefone, endereco, cidade, uf, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 'SEG002', 'Porto Seguro S.A.', '61.198.164/0001-60', '0656', 'atendimento@portoseguro.com.br', '(11) 3366-3000', 'Al. Barão de Piracicaba 740', 'São Paulo', 'SP', true, false, NOW(), NOW()),
  (3, 1, 'SEG003', 'Bradesco Seguros S.A.', '51.014.223/0001-49', '5738', 'seguros@bradesco.com.br', '(11) 4002-0022', 'Rua Barão de Itapetininga 225', 'São Paulo', 'SP', true, false, NOW(), NOW()),
  (4, 1, 'SEG004', 'SulAmérica Seguros', '33.041.062/0001-09', '0785', 'sac@sulamerica.com.br', '(21) 2536-6000', 'Rua Beatriz Larragoiti Lucas 121', 'Rio de Janeiro', 'RJ', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO corretoras (id, empresa_id, codigo, nome, cnpj, responsavel, email, telefone, percentual_comissao, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 'COR002', 'Corretora Fênix Seguros Ltda', '90.123.456/0001-78', 'Ricardo Lima', 'ricardo@fenixseguros.com.br', '(11) 3200-7890', 12.00, true, false, NOW(), NOW()),
  (3, 1, 'COR003', 'Proteção Total Corretagem', '01.234.567/0001-89', 'Sandra Rocha', 'sandra@protecaototal.com.br', '(11) 3300-8901', 15.00, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO propostas_seguro (id, empresa_id, numero, cliente_id, seguradora_id, corretora_id, ramo, vigencia_inicio, vigencia_fim, premio_liquido, premio_total, percentual_comissao, responsavel, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'PROP-001', 2, 2, 2, 'PATRIMONIAL', '2025-08-01', '2026-07-31', 4500.00, 5200.00, 12.00, 'Ricardo Lima', 'APROVADA', true, false, NOW(), NOW()),
  (2, 1, 'PROP-002', 3, 3, 3, 'RESPONSABILIDADE_CIVIL', '2025-09-01', '2026-08-31', 8000.00, 9500.00, 15.00, 'Sandra Rocha', 'ENVIADA', true, false, NOW(), NOW()),
  (3, 1, 'PROP-003', 5, 4, 2, 'ENGENHARIA', '2025-07-01', '2026-06-30', 15000.00, 17500.00, 10.00, 'Ricardo Lima', 'APROVADA', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO apolices (id, empresa_id, numero, proposta_id, cliente_id, seguradora_id, corretora_id, ramo, vigencia_inicio, vigencia_fim, premio_total, importancia_segurada, franquia, percentual_comissao, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'APOL-001', 1, 2, 2, 2, 'PATRIMONIAL', '2025-08-01', '2026-07-31', 5200.00, 500000.00, 2500.00, 12.00, 'ATIVA', true, false, NOW(), NOW()),
  (2, 1, 'APOL-002', 3, 5, 4, 2, 'ENGENHARIA', '2025-07-01', '2026-06-30', 17500.00, 2000000.00, 10000.00, 10.00, 'ATIVA', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO notificacoes_seguro (id, empresa_id, tipo, titulo, descricao, destinatario, tabela_origem, registro_id, lida, ativo, created_at, updated_at)
VALUES
  (1, 1, 'VENCIMENTO', 'Apólice APOL-001 vence em 30 dias', 'A apólice APOL-001 do cliente Tech Solutions vencerá em 01/08/2026', 'admin@orion.com', 'apolices', 1, false, true, NOW(), NOW()),
  (2, 1, 'STATUS', 'Proposta PROP-001 aprovada', 'A proposta PROP-001 foi aprovada e convertida na apólice APOL-001', 'admin@orion.com', 'propostas_seguro', 1, true, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 8. CRM - Leads, Oportunidades, Atividades
-- ============================================================
INSERT INTO leads (id, empresa_id, nome, email, telefone, empresa_lead, cargo, origem, responsavel_id, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'Fernando Gomes', 'fernando@novaempresa.com.br', '(11) 99700-1234', 'Nova Empresa Ltda', 'Diretor Comercial', 'INDICACAO', 1, 'NOVO', true, false, NOW(), NOW()),
  (2, 1, 'Patrícia Ramos', 'patricia@startuptech.io', '(21) 99800-5678', 'Startup Tech', 'CTO', 'SITE', 1, 'QUALIFICADO', true, false, NOW(), NOW()),
  (3, 1, 'Marcos Vinícius dos Santos', 'marcos@industrial.com.br', '(19) 99600-9012', 'Industrial Paulista', 'Comprador', 'FEIRA', 1, 'CONTATADO', true, false, NOW(), NOW()),
  (4, 1, 'Juliana Martins', 'juliana@retailgroup.com', '(11) 98500-3456', 'Retail Group', 'Gerente de Compras', 'LINKEDIN', 1, 'CONVERTIDO', true, false, NOW(), NOW()),
  (5, 1, 'Diego Ramos Oliveira', 'diego@construtoraalpha.com', '(31) 97400-7890', 'Construtora Alpha', 'Eng. Orçamentos', 'INDICACAO', 1, 'PERDIDO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO oportunidades (id, empresa_id, titulo, lead_id, cliente_id, responsavel_id, valor_estimado, probabilidade, etapa_funil, data_previsao_fechamento, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'Fornecimento de motores elétricos', 2, NULL, 1, 45000.00, 60, 'PROPOSTA', '2025-09-15', true, false, NOW(), NOW()),
  (2, 1, 'Projeto de automação industrial', 3, NULL, 1, 120000.00, 40, 'QUALIFICACAO', '2025-10-30', true, false, NOW(), NOW()),
  (3, 1, 'Contrato anual de manutenção', NULL, 2, 1, 36000.00, 80, 'NEGOCIACAO', '2025-08-15', true, false, NOW(), NOW()),
  (4, 1, 'Venda de sensores PT100', NULL, 3, 1, 18000.00, 90, 'FECHAMENTO', '2025-07-30', true, false, NOW(), NOW()),
  (5, 1, 'Equipamentos para obra Fase 2', NULL, 5, 1, 250000.00, 30, 'PROSPECCAO', '2025-12-01', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO atividades_crm (id, empresa_id, tipo, titulo, descricao, lead_id, oportunidade_id, cliente_id, responsavel_id, data_hora, duracao_minutos, concluida, ativo, created_at, updated_at)
VALUES
  (1, 1, 'LIGACAO', 'Ligar para Fernando Gomes', 'Primeiro contato para apresentação', 1, NULL, NULL, 1, '2025-07-16 10:00:00', 30, false, true, NOW(), NOW()),
  (2, 1, 'REUNIAO', 'Reunião com Patrícia - Startup Tech', 'Apresentação de catálogo de motores', 2, 1, NULL, 1, '2025-07-17 14:00:00', 60, false, true, NOW(), NOW()),
  (3, 1, 'EMAIL', 'Enviar proposta Tech Solutions', 'Envio da proposta de contrato de manutenção anual', NULL, 3, 2, 1, '2025-07-15 09:00:00', 15, true, true, NOW(), NOW()),
  (4, 1, 'VISITA', 'Visita técnica Ferro & Aço', 'Visita para levantamento técnico de sensores', NULL, 4, 3, 1, '2025-07-18 08:30:00', 120, false, true, NOW(), NOW()),
  (5, 1, 'LIGACAO', 'Follow-up Construtora Horizonte', 'Retornar sobre equipamentos Fase 2', NULL, 5, 5, 1, '2025-07-19 11:00:00', 20, false, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Contatos
INSERT INTO contatos (id, entidade_tipo, entidade_id, nome, cargo, telefone, celular, email, principal, ativo, created_at, updated_at)
VALUES
  (1, 'CLIENTE', 2, 'João da Silva', 'Compras', '(11) 3000-1001', '(11) 99000-1001', 'joao@techsolutions.com.br', true, true, NOW(), NOW()),
  (2, 'CLIENTE', 3, 'Marta Souza', 'Diretora', '(11) 2000-2001', '(11) 98000-2001', 'marta@ferroaco.com.br', true, true, NOW(), NOW()),
  (3, 'FORNECEDOR', 2, 'Antônio Pereira', 'Vendas', '(11) 4600-6001', '(11) 97000-6001', 'antonio@dni.com.br', true, true, NOW(), NOW()),
  (4, 'FORNECEDOR', 3, 'Beatriz Lima', 'Comercial', '(19) 3800-7001', '(19) 96000-7001', 'beatriz@iqb.ind.br', true, true, NOW(), NOW()),
  (5, 'CLIENTE', 5, 'Eng. Rafael Torres', 'Gerente de Projetos', '(11) 4500-4001', '(11) 96000-4001', 'rafael@horizonte.eng.br', true, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 9. COMPRAS - Solicitações, Cotações, Pedidos, Recebimentos
-- ============================================================
INSERT INTO solicitacoes_compra (id, empresa_id, filial_id, numero, data_solicitacao, solicitante_id, departamento_id, prioridade, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 'SC-001', '2025-07-01', 1, 4, 'ALTA', 'APROVADA', true, false, NOW(), NOW()),
  (2, 1, 1, 'SC-002', '2025-07-10', 1, 2, 'MEDIA', 'PENDENTE', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO solicitacao_compra_itens (id, solicitacao_compra_id, produto_id, quantidade, ativo, created_at)
VALUES
  (1, 1, 5, 20.00, true, NOW()),
  (2, 1, 2, 5000.00, true, NOW()),
  (3, 2, 8, 15.00, true, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cotacoes (id, empresa_id, filial_id, numero, data_cotacao, data_validade, solicitacao_compra_id, comprador_id, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 'COT-001', '2025-07-03', '2025-07-20', 1, 1, 'APROVADA', true, false, NOW(), NOW()),
  (2, 1, 1, 'COT-002', '2025-07-12', '2025-07-30', 2, 1, 'EM_ANALISE', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cotacao_itens (id, cotacao_id, produto_id, quantidade, ativo, created_at)
VALUES
  (1, 1, 5, 20.00, true, NOW()),
  (2, 1, 2, 5000.00, true, NOW()),
  (3, 2, 8, 15.00, true, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cotacao_fornecedores (id, cotacao_item_id, fornecedor_id, preco_unitario, prazo_entrega, condicao_pagamento, selecionado, created_at, ativo)
VALUES
  (1, 1, 4, 175.00, 15, '30/60 DDL', true, NOW(), true),
  (2, 1, 2, 185.00, 10, '30 DDL', false, NOW(), true),
  (3, 2, 2, 0.32, 7, 'A Vista', true, NOW(), true),
  (4, 2, 5, 0.38, 5, '30 DDL', false, NOW(), true),
  (5, 3, 2, 82.00, 20, '30/60/90 DDL', false, NOW(), true)
ON CONFLICT DO NOTHING;

INSERT INTO pedidos_compra (id, empresa_id, filial_id, numero, fornecedor_id, cotacao_id, data_pedido, data_previsao_entrega, condicao_pagamento_id, valor_total, valor_frete, valor_desconto, status, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 1, 'PC-002', 4, 1, '2025-07-05', '2025-07-20', 26, 3500.00, 150.00, 0.00, 'RECEBIDO', true, false, NOW(), NOW()),
  (3, 1, 1, 'PC-003', 2, 1, '2025-07-06', '2025-07-13', 25, 1600.00, 0.00, 0.00, 'RECEBIDO', true, false, NOW(), NOW()),
  (4, 1, 1, 'PC-004', 3, NULL, '2025-07-14', '2025-07-28', 27, 12500.00, 500.00, 250.00, 'APROVADO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO pedido_compra_itens (id, pedido_compra_id, produto_id, quantidade, quantidade_recebida, preco_unitario, valor_total, ativo, created_at, updated_at)
VALUES
  (1, 2, 5, 20.00, 20.00, 175.00, 3500.00, true, NOW(), NOW()),
  (2, 3, 2, 5000.00, 5000.00, 0.32, 1600.00, true, NOW(), NOW()),
  (3, 4, 4, 200.00, 0.00, 12.50, 2500.00, true, NOW(), NOW()),
  (4, 4, 9, 100.00, 0.00, 22.00, 2200.00, true, NOW(), NOW()),
  (5, 4, 3, 5.00, 0.00, 450.00, 2250.00, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Recebimentos
INSERT INTO recebimentos (id, empresa_id, filial_id, numero, pedido_compra_id, fornecedor_id, data_recebimento, numero_nf, serie_nf, valor_total, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 'REC-001', 2, 4, '2025-07-18', '12345', '1', 3500.00, 'FINALIZADO', true, false, NOW(), NOW()),
  (2, 1, 1, 'REC-002', 3, 2, '2025-07-12', '67890', '1', 1600.00, 'FINALIZADO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO recebimento_itens (id, recebimento_id, pedido_compra_item_id, produto_id, quantidade, preco_unitario, valor_total, armazem_id, ativo, created_at)
VALUES
  (1, 1, 1, 5, 20.00, 175.00, 3500.00, 3, true, NOW()),
  (2, 2, 2, 2, 5000.00, 0.32, 1600.00, 1, true, NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 10. VENDAS - Orçamentos, Pedidos de Venda, Comissões
-- ============================================================
INSERT INTO orcamentos (id, empresa_id, filial_id, numero, cliente_id, vendedor_id, data_orcamento, data_validade, condicao_pagamento_id, valor_produtos, valor_desconto, valor_frete, valor_total, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 'ORC-001', 2, 1, '2025-07-10', '2025-07-25', 25, 4450.00, 222.50, 0.00, 4227.50, 'APROVADO', true, false, NOW(), NOW()),
  (2, 1, 1, 'ORC-002', 3, 1, '2025-07-12', '2025-07-27', 26, 18500.00, 925.00, 350.00, 17925.00, 'ABERTO', true, false, NOW(), NOW()),
  (3, 1, 1, 'ORC-003', 6, 1, '2025-07-14', '2025-07-29', 25, 890.00, 0.00, 50.00, 940.00, 'APROVADO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO orcamento_itens (id, orcamento_id, produto_id, quantidade, preco_unitario, percentual_desconto, valor_desconto, valor_total, ativo, created_at, updated_at)
VALUES
  (1, 1, 3, 5.00, 890.00, 5.00, 222.50, 4227.50, true, NOW(), NOW()),
  (2, 2, 10, 5.00, 2500.00, 5.00, 625.00, 11875.00, true, NOW(), NOW()),
  (3, 2, 6, 50.00, 42.00, 5.00, 105.00, 1995.00, true, NOW(), NOW()),
  (4, 2, 8, 30.00, 165.00, 0.00, 0.00, 4950.00, true, NOW(), NOW()),
  (5, 3, 3, 1.00, 890.00, 0.00, 0.00, 890.00, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO pedidos_venda (id, empresa_id, filial_id, numero, orcamento_id, cliente_id, vendedor_id, transportadora_id, data_pedido, data_previsao_entrega, condicao_pagamento_id, valor_produtos, valor_desconto, valor_frete, valor_total, status, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 1, 'PV-002', 1, 2, 1, 2, '2025-07-11', '2025-07-18', 25, 4450.00, 222.50, 80.00, 4307.50, 'FATURADO', true, false, NOW(), NOW()),
  (3, 1, 1, 'PV-003', 3, 6, 1, 3, '2025-07-15', '2025-07-22', 25, 890.00, 0.00, 50.00, 940.00, 'APROVADO', true, false, NOW(), NOW()),
  (4, 1, 1, 'PV-004', NULL, 4, 1, NULL, '2025-07-16', '2025-07-25', 25, 165.00, 0.00, 0.00, 165.00, 'PENDENTE', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO pedido_venda_itens (id, pedido_venda_id, produto_id, quantidade, quantidade_entregue, preco_unitario, percentual_desconto, valor_desconto, valor_total, ativo, created_at, updated_at)
VALUES
  (1, 2, 3, 5.00, 5.00, 890.00, 5.00, 222.50, 4227.50, true, NOW(), NOW()),
  (2, 3, 3, 1.00, 0.00, 890.00, 0.00, 0.00, 890.00, true, NOW(), NOW()),
  (3, 4, 8, 1.00, 0.00, 165.00, 0.00, 0.00, 165.00, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Comissões
INSERT INTO comissoes (id, empresa_id, vendedor_id, pedido_venda_id, percentual, valor_base, valor_comissao, status, ativo, created_at, updated_at)
VALUES
  (1, 1, 1, 2, 5.00, 4227.50, 211.38, 'PAGA', true, NOW(), NOW()),
  (2, 1, 1, 3, 5.00, 890.00, 44.50, 'PENDENTE', true, NOW(), NOW()),
  (3, 1, 1, 4, 3.00, 165.00, 4.95, 'PENDENTE', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 11. FISCAL - Notas Fiscais, Regras Fiscais
-- ============================================================
INSERT INTO notas_fiscais (id, empresa_id, filial_id, tipo, serie, numero, modelo, natureza_operacao_id, data_emissao, cliente_id, fornecedor_id, transportadora_id, frete_por_conta, valor_produtos, valor_frete, valor_seguro, valor_desconto, valor_outras_despesas, valor_ipi, valor_icms, valor_icms_st, valor_pis, valor_cofins, valor_total, pedido_venda_id, pedido_compra_id, status, ativo, deleted, created_at, updated_at)
VALUES
  (7, 1, 1, 'SAIDA', '1', '000007', '55', 37, '2025-07-12', 2, NULL, 2, 'EMITENTE', 4450.00, 80.00, 0.00, 222.50, 0.00, 0.00, 648.00, 0.00, 73.85, 340.40, 4307.50, 2, NULL, 'AUTORIZADA', true, false, NOW(), NOW()),
  (8, 1, 1, 'ENTRADA', '1', '012345', '55', 40, '2025-07-18', NULL, 4, NULL, 'EMITENTE', 3500.00, 150.00, 0.00, 0.00, 0.00, 0.00, 540.00, 0.00, 57.75, 266.00, 3650.00, NULL, 2, 'AUTORIZADA', true, false, NOW(), NOW()),
  (9, 1, 1, 'SAIDA', '1', '000009', '55', 37, '2025-07-15', 6, NULL, 3, 'TERCEIROS', 890.00, 50.00, 0.00, 0.00, 0.00, 0.00, 129.60, 0.00, 14.69, 67.72, 940.00, 3, NULL, 'DIGITADA', true, false, NOW(), NOW()),
  (10, 1, 1, 'ENTRADA', '1', '067890', '55', 40, '2025-07-12', NULL, 2, NULL, 'EMITENTE', 1600.00, 0.00, 0.00, 0.00, 0.00, 0.00, 288.00, 0.00, 26.40, 121.60, 1600.00, NULL, 3, 'AUTORIZADA', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO nota_fiscal_itens (id, empresa_id, nota_fiscal_id, numero_item, produto_id, descricao, cfop, unidade_medida, quantidade, valor_unitario, valor_total, valor_desconto, base_icms, aliquota_icms, valor_icms, base_icms_st, aliquota_icms_st, valor_icms_st, base_ipi, aliquota_ipi, valor_ipi, base_pis, aliquota_pis, valor_pis, base_cofins, aliquota_cofins, valor_cofins, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 7, 1, 3, 'Motor Elétrico 1CV', '5102', 'UN', 5.00, 890.00, 4450.00, 222.50, 4227.50, 18.00, 760.95, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 4227.50, 1.65, 69.75, 4227.50, 7.60, 321.29, true, false, NOW(), NOW()),
  (2, 1, 8, 1, 5, 'Chapa Aço Carbono 3mm', '1102', 'UN', 20.00, 175.00, 3500.00, 0.00, 3500.00, 18.00, 630.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 3500.00, 1.65, 57.75, 3500.00, 7.60, 266.00, true, false, NOW(), NOW()),
  (3, 1, 9, 1, 3, 'Motor Elétrico 1CV', '5102', 'UN', 1.00, 890.00, 890.00, 0.00, 890.00, 18.00, 160.20, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 890.00, 1.65, 14.69, 890.00, 7.60, 67.64, true, false, NOW(), NOW()),
  (4, 1, 10, 1, 2, 'Parafuso Sextavado M10', '1102', 'UN', 5000.00, 0.32, 1600.00, 0.00, 1600.00, 18.00, 288.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1600.00, 1.65, 26.40, 1600.00, 7.60, 121.60, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Regras Fiscais
INSERT INTO regras_fiscais (id, empresa_id, uf_origem, uf_destino, aliquota_icms, aliquota_pis, aliquota_cofins, aliquota_ipi, ativo, created_at, updated_at)
VALUES
  (1, 1, 'SP', 'SP', 18.00, 1.65, 7.60, 0.00, true, NOW(), NOW()),
  (2, 1, 'SP', 'MG', 12.00, 1.65, 7.60, 0.00, true, NOW(), NOW()),
  (3, 1, 'SP', 'RJ', 12.00, 1.65, 7.60, 0.00, true, NOW(), NOW()),
  (4, 1, 'SP', 'PR', 12.00, 1.65, 7.60, 0.00, true, NOW(), NOW()),
  (5, 1, 'SP', 'RS', 12.00, 1.65, 7.60, 0.00, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 12. ESTOQUE - Saldos e Movimentações
-- ============================================================
INSERT INTO saldos_estoque (id, empresa_id, filial_id, armazem_id, produto_id, quantidade, custo_medio, reservado, ativo, updated_at)
VALUES
  (1, 1, 1, 1, 1, 50.00, 85.00, 0.00, true, NOW()),
  (2, 1, 1, 1, 2, 8500.00, 0.33, 0.00, true, NOW()),
  (3, 1, 1, 2, 3, 22.00, 460.00, 1.00, true, NOW()),
  (4, 1, 1, 1, 4, 180.00, 12.80, 0.00, true, NOW()),
  (5, 1, 1, 3, 5, 45.00, 177.50, 0.00, true, NOW()),
  (6, 1, 1, 1, 6, 95.00, 18.50, 0.00, true, NOW()),
  (7, 1, 1, 1, 7, 420.00, 3.25, 0.00, true, NOW()),
  (8, 1, 1, 1, 8, 38.00, 85.00, 1.00, true, NOW()),
  (9, 1, 1, 1, 9, 120.00, 22.50, 0.00, true, NOW()),
  (10, 1, 1, 2, 10, 8.00, 1250.00, 0.00, true, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO movimentacoes_estoque (id, empresa_id, filial_id, armazem_id, produto_id, tipo, quantidade, custo_unitario, custo_total, saldo_anterior, saldo_posterior, documento_tipo, documento_numero, ativo, created_at)
VALUES
  (1, 1, 1, 3, 5, 'ENTRADA', 20.00, 175.00, 3500.00, 25.00, 45.00, 'NF_ENTRADA', '012345', true, '2025-07-18'),
  (2, 1, 1, 1, 2, 'ENTRADA', 5000.00, 0.32, 1600.00, 3500.00, 8500.00, 'NF_ENTRADA', '067890', true, '2025-07-12'),
  (3, 1, 1, 2, 3, 'SAIDA', 5.00, 460.00, 2300.00, 27.00, 22.00, 'NF_SAIDA', '000007', true, '2025-07-12'),
  (4, 1, 1, 1, 4, 'ENTRADA', 100.00, 12.50, 1250.00, 80.00, 180.00, 'AJUSTE', 'AJ-001', true, '2025-07-10'),
  (5, 1, 1, 1, 7, 'SAIDA', 30.00, 3.25, 97.50, 450.00, 420.00, 'PRODUCAO', 'OP-001', true, '2025-07-14')
ON CONFLICT DO NOTHING;

-- Inventários
INSERT INTO inventarios (id, empresa_id, filial_id, armazem_id, numero, data_inventario, responsavel_id, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 1, 'INV-001', '2025-07-01', 1, 'FINALIZADO', true, false, NOW(), NOW()),
  (2, 1, 1, 2, 'INV-002', '2025-07-15', 1, 'EM_CONTAGEM', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO inventario_itens (id, inventario_id, produto_id, quantidade_sistema, quantidade_contada, diferenca, ajustado, ativo, created_at, updated_at)
VALUES
  (1, 1, 1, 48.00, 50.00, 2.00, true, true, NOW(), NOW()),
  (2, 1, 2, 3500.00, 3480.00, -20.00, true, true, NOW(), NOW()),
  (3, 1, 7, 455.00, 450.00, -5.00, true, true, NOW(), NOW()),
  (4, 2, 3, 22.00, NULL, NULL, false, true, NOW(), NOW()),
  (5, 2, 10, 8.00, NULL, NULL, false, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 13. FINANCEIRO - Títulos, Parcelas, Baixas, Fluxo de Caixa
-- ============================================================
INSERT INTO titulos (id, empresa_id, filial_id, tipo, numero, serie, cliente_id, fornecedor_id, natureza_financeira_id, centro_custo_id, conta_bancaria_id, documento_origem, documento_origem_id, data_emissao, valor_original, valor_aberto, status, ativo, deleted, created_at, updated_at)
VALUES
  (2, 1, 1, 'RECEBER', 'TIT-002', '1', 2, NULL, 81, 58, 1, 'NOTA_FISCAL', 7, '2025-07-12', 4307.50, 0.00, 'QUITADO', true, false, NOW(), NOW()),
  (3, 1, 1, 'PAGAR', 'TIT-003', '1', NULL, 4, 91, 63, 2, 'NOTA_FISCAL', 8, '2025-07-18', 3650.00, 3650.00, 'ABERTO', true, false, NOW(), NOW()),
  (4, 1, 1, 'RECEBER', 'TIT-004', '1', 6, NULL, 81, 58, 1, 'NOTA_FISCAL', 9, '2025-07-15', 940.00, 940.00, 'ABERTO', true, false, NOW(), NOW()),
  (5, 1, 1, 'PAGAR', 'TIT-005', '1', NULL, 2, 91, 63, 2, 'NOTA_FISCAL', 10, '2025-07-12', 1600.00, 0.00, 'QUITADO', true, false, NOW(), NOW()),
  (6, 1, 1, 'PAGAR', 'TIT-006', '1', NULL, 3, 92, 63, 3, NULL, NULL, '2025-07-14', 12750.00, 12750.00, 'ABERTO', true, false, NOW(), NOW()),
  (7, 1, 1, 'RECEBER', 'TIT-007', '1', 5, NULL, 83, 58, 1, NULL, NULL, '2025-07-16', 250000.00, 250000.00, 'ABERTO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO titulo_parcelas (id, titulo_id, numero_parcela, data_vencimento, valor, valor_pago, valor_juros, valor_multa, valor_desconto, status, ativo, created_at, updated_at)
VALUES
  (1, 2, 1, '2025-08-12', 4307.50, 4307.50, 0.00, 0.00, 0.00, 'QUITADO', true, NOW(), NOW()),
  (2, 3, 1, '2025-08-18', 1216.67, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (3, 3, 2, '2025-09-18', 1216.67, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (4, 3, 3, '2025-10-18', 1216.66, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (5, 4, 1, '2025-08-15', 940.00, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (6, 5, 1, '2025-08-12', 1600.00, 1600.00, 0.00, 0.00, 0.00, 'QUITADO', true, NOW(), NOW()),
  (7, 6, 1, '2025-08-14', 4250.00, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (8, 6, 2, '2025-09-14', 4250.00, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (9, 6, 3, '2025-10-14', 4250.00, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (10, 7, 1, '2025-08-16', 125000.00, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW()),
  (11, 7, 2, '2025-09-16', 125000.00, 0.00, 0.00, 0.00, 0.00, 'ABERTO', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO titulo_baixas (id, parcela_id, data_baixa, valor_pago, valor_juros, valor_multa, valor_desconto, conta_bancaria_id, forma_pagamento, estornado, ativo, created_at)
VALUES
  (1, 1, '2025-08-10', 4307.50, 0.00, 0.00, 0.00, 1, 'TRANSFERENCIA', false, true, NOW()),
  (2, 6, '2025-08-10', 1600.00, 0.00, 0.00, 0.00, 2, 'BOLETO', false, true, NOW())
ON CONFLICT DO NOTHING;

-- Fluxo de Caixa
INSERT INTO fluxo_caixa (id, empresa_id, filial_id, conta_bancaria_id, tipo, valor, data_lancamento, descricao, titulo_id, baixa_id, ativo, created_at)
VALUES
  (1, 1, 1, 1, 'ENTRADA', 4307.50, '2025-08-10', 'Recebimento NF 000007 - Tech Solutions', 2, 1, true, NOW()),
  (2, 1, 1, 2, 'SAIDA', 1600.00, '2025-08-10', 'Pagamento NF 067890 - DNI', 5, 2, true, NOW()),
  (3, 1, 1, 1, 'ENTRADA', 100000.00, '2025-01-02', 'Aporte inicial sócios', NULL, NULL, true, NOW()),
  (4, 1, 1, 2, 'ENTRADA', 50000.00, '2025-01-02', 'Transferência inicial Itaú', NULL, NULL, true, NOW())
ON CONFLICT DO NOTHING;

-- Conciliação Bancária
INSERT INTO conciliacao_bancaria (id, empresa_id, conta_bancaria_id, data_inicio, data_fim, saldo_banco, saldo_sistema, diferenca, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, '2025-07-01', '2025-07-31', 104307.50, 104307.50, 0.00, 'CONCILIADA', true, false, NOW(), NOW()),
  (2, 1, 2, '2025-07-01', '2025-07-31', 48400.00, 48400.00, 0.00, 'EM_ANDAMENTO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO conciliacao_bancaria_item (id, conciliacao_id, data_movimento, descricao, valor, tipo, origem, conciliado, ativo, created_at)
VALUES
  (1, 1, '2025-08-10', 'TED Recebida - Tech Solutions', 4307.50, 'C', 'BANCO', true, true, NOW()),
  (2, 1, '2025-08-10', 'Recebimento Título TIT-002', 4307.50, 'C', 'SISTEMA', true, true, NOW()),
  (3, 2, '2025-08-10', 'Boleto Pago - NF 067890', 1600.00, 'D', 'BANCO', true, true, NOW()),
  (4, 2, '2025-08-10', 'Pagamento Título TIT-005', 1600.00, 'D', 'SISTEMA', true, true, NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 14. CONTRATOS (adicionais)
-- ============================================================
INSERT INTO contratos (id, empresa_id, numero, tipo, descricao, cliente_id, data_inicio, data_fim, valor_total, valor_mensal, dia_vencimento, status, renovacao_automatica, ativo, deleted, created_at, updated_at)
VALUES
  (4, 1, 'CTR-004', 'SERVICO', 'Contrato de Manutenção Preventiva - Motores', 2, '2025-08-01', '2026-07-31', 36000.00, 3000.00, 15, 'VIGENTE', true, true, false, NOW(), NOW()),
  (5, 1, 'CTR-005', 'FORNECEDOR', 'Fornecimento Mensal de Insumos', NULL, '2025-07-01', '2025-12-31', 60000.00, 10000.00, 10, 'VIGENTE', false, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Atualizar contrato 5 com fornecedor
UPDATE contratos SET fornecedor_id = 2 WHERE id = 5 AND fornecedor_id IS NULL;

INSERT INTO contrato_parcelas (id, contrato_id, empresa_id, numero_parcela, data_vencimento, valor, valor_pago, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 4, 1, 1, '2025-08-15', 3000.00, 0.00, 'PENDENTE', true, false, NOW(), NOW()),
  (2, 4, 1, 2, '2025-09-15', 3000.00, 0.00, 'PENDENTE', true, false, NOW(), NOW()),
  (3, 4, 1, 3, '2025-10-15', 3000.00, 0.00, 'PENDENTE', true, false, NOW(), NOW()),
  (4, 5, 1, 1, '2025-07-10', 10000.00, 10000.00, 'PAGO', true, false, NOW(), NOW()),
  (5, 5, 1, 2, '2025-08-10', 10000.00, 0.00, 'PENDENTE', true, false, NOW(), NOW()),
  (6, 5, 1, 3, '2025-09-10', 10000.00, 0.00, 'PENDENTE', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 15. PATRIMÔNIO - Bens, Depreciação
-- ============================================================
INSERT INTO bens_patrimoniais (id, empresa_id, codigo, descricao, tipo_bem, marca, modelo, data_aquisicao, valor_aquisicao, valor_residual, taxa_depreciacao_anual, metodo_depreciacao, vida_util_meses, valor_depreciado_acumulado, valor_contabil, localizacao, responsavel, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'BEM001', 'Torno CNC Romi Galaxy 15', 'TANGIVEL', 'Romi', 'Galaxy 15', '2024-01-15', 280000.00, 28000.00, 10.00, 'LINEAR', 120, 14000.00, 266000.00, 'Galpão Produção', 'Roberto Silva', 'ATIVO', true, false, NOW(), NOW()),
  (2, 1, 'BEM002', 'Empilhadeira Toyota 8FBN25', 'TANGIVEL', 'Toyota', '8FBN25', '2023-06-01', 95000.00, 9500.00, 20.00, 'LINEAR', 60, 38000.00, 57000.00, 'Armazém Central', 'Equipe Logística', 'ATIVO', true, false, NOW(), NOW()),
  (3, 1, 'BEM003', 'Servidor Dell PowerEdge R750', 'TANGIVEL', 'Dell', 'PowerEdge R750', '2024-06-01', 45000.00, 4500.00, 20.00, 'LINEAR', 60, 9000.00, 36000.00, 'Sala de TI', 'Carlos Mendes', 'ATIVO', true, false, NOW(), NOW()),
  (4, 1, 'BEM004', 'Compressor Atlas Copco GA22', 'TANGIVEL', 'Atlas Copco', 'GA22', '2022-03-01', 65000.00, 6500.00, 10.00, 'LINEAR', 120, 19500.00, 45500.00, 'Sala de Compressores', 'Manutenção', 'ATIVO', true, false, NOW(), NOW()),
  (5, 1, 'BEM005', 'Notebook Lenovo ThinkPad T14', 'TANGIVEL', 'Lenovo', 'ThinkPad T14', '2023-01-10', 8500.00, 850.00, 20.00, 'LINEAR', 60, 4250.00, 4250.00, 'Depto Comercial', 'Ana Paula', 'ATIVO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO depreciacao (id, empresa_id, bem_patrimonial_id, ano, mes, data_calculo, valor_base, taxa_mensal, valor_depreciacao, valor_acumulado, valor_contabil_apos, ativo, deleted, created_at)
VALUES
  (1, 1, 1, 2025, 6, '2025-06-30', 252000.00, 0.83, 2100.00, 14000.00, 266000.00, true, false, NOW()),
  (2, 1, 1, 2025, 7, '2025-07-31', 252000.00, 0.83, 2100.00, 16100.00, 263900.00, true, false, NOW()),
  (3, 1, 2, 2025, 6, '2025-06-30', 85500.00, 1.67, 1425.00, 38000.00, 57000.00, true, false, NOW()),
  (4, 1, 2, 2025, 7, '2025-07-31', 85500.00, 1.67, 1425.00, 39425.00, 55575.00, true, false, NOW()),
  (5, 1, 3, 2025, 6, '2025-06-30', 40500.00, 1.67, 675.00, 9000.00, 36000.00, true, false, NOW()),
  (6, 1, 3, 2025, 7, '2025-07-31', 40500.00, 1.67, 675.00, 9675.00, 35325.00, true, false, NOW()),
  (7, 1, 4, 2025, 6, '2025-06-30', 58500.00, 0.83, 487.50, 19500.00, 45500.00, true, false, NOW()),
  (8, 1, 5, 2025, 6, '2025-06-30', 7650.00, 1.67, 127.50, 4250.00, 4250.00, true, false, NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 16. PRODUÇÃO - Ordens, Itens, Apontamentos
-- ============================================================
INSERT INTO ordens_producao (id, empresa_id, numero, produto_id, quantidade_prevista, quantidade_produzida, quantidade_perda, data_abertura, data_previsao, data_inicio, armazem_id, prioridade, custo_previsto, custo_real, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'OP-001', 10, 10.00, 8.00, 1.00, '2025-07-01', '2025-07-15', '2025-07-02', 2, 'ALTA', 12000.00, 10200.00, 'EM_PRODUCAO', true, false, NOW(), NOW()),
  (2, 1, 'OP-002', 10, 5.00, 0.00, 0.00, '2025-07-14', '2025-07-28', NULL, 2, 'MEDIA', 6000.00, 0.00, 'PLANEJADA', true, false, NOW(), NOW()),
  (3, 1, 'OP-003', 10, 20.00, 20.00, 2.00, '2025-06-01', '2025-06-20', '2025-06-02', 2, 'ALTA', 24000.00, 25100.00, 'FINALIZADA', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO ordem_producao_itens (id, empresa_id, ordem_producao_id, produto_id, quantidade_prevista, quantidade_consumida, custo_unitario, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, 3, 10.00, 8.00, 460.00, true, false, NOW(), NOW()),
  (2, 1, 1, 6, 40.00, 32.00, 18.50, true, false, NOW(), NOW()),
  (3, 1, 1, 8, 20.00, 16.00, 85.00, true, false, NOW(), NOW()),
  (4, 1, 2, 3, 5.00, 0.00, 460.00, true, false, NOW(), NOW()),
  (5, 1, 2, 6, 20.00, 0.00, 18.50, true, false, NOW(), NOW()),
  (6, 1, 2, 8, 10.00, 0.00, 85.00, true, false, NOW(), NOW()),
  (7, 1, 3, 3, 20.00, 22.00, 460.00, true, false, NOW(), NOW()),
  (8, 1, 3, 6, 80.00, 85.00, 18.50, true, false, NOW(), NOW()),
  (9, 1, 3, 8, 40.00, 42.00, 85.00, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO apontamento_producao (id, empresa_id, ordem_producao_id, data_apontamento, quantidade_boa, quantidade_refugo, hora_inicio, hora_fim, funcionario_id, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 1, '2025-07-02', 3.00, 0.00, '06:00', '14:00', 6, true, false, NOW(), NOW()),
  (2, 1, 1, '2025-07-03', 3.00, 1.00, '06:00', '14:00', 6, true, false, NOW(), NOW()),
  (3, 1, 1, '2025-07-04', 2.00, 0.00, '06:00', '12:00', 6, true, false, NOW(), NOW()),
  (4, 1, 3, '2025-06-02', 8.00, 1.00, '06:00', '18:00', 6, true, false, NOW(), NOW()),
  (5, 1, 3, '2025-06-03', 7.00, 0.00, '06:00', '18:00', 6, true, false, NOW(), NOW()),
  (6, 1, 3, '2025-06-04', 5.00, 1.00, '06:00', '14:00', 6, true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 17. CONTABILIDADE - Períodos, Lançamentos, Centro Resultado
-- ============================================================
INSERT INTO periodo_contabil (id, empresa_id, ano, mes, data_inicio, data_fim, status, ativo, created_at, updated_at)
VALUES
  (1, 1, 2025, 1, '2025-01-01', '2025-01-31', 'FECHADO', true, NOW(), NOW()),
  (2, 1, 2025, 2, '2025-02-01', '2025-02-28', 'FECHADO', true, NOW(), NOW()),
  (3, 1, 2025, 3, '2025-03-01', '2025-03-31', 'FECHADO', true, NOW(), NOW()),
  (4, 1, 2025, 4, '2025-04-01', '2025-04-30', 'FECHADO', true, NOW(), NOW()),
  (5, 1, 2025, 5, '2025-05-01', '2025-05-31', 'FECHADO', true, NOW(), NOW()),
  (6, 1, 2025, 6, '2025-06-01', '2025-06-30', 'FECHADO', true, NOW(), NOW()),
  (7, 1, 2025, 7, '2025-07-01', '2025-07-31', 'ABERTO', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO centro_resultado (id, empresa_id, codigo, descricao, tipo, responsavel, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, 'CR001', 'Vendas Produtos Industriais', 'RECEITA', 'Ana Paula Ferreira', true, false, NOW(), NOW()),
  (2, 1, 'CR002', 'Serviços de Manutenção', 'RECEITA', 'Carlos Mendes', true, false, NOW(), NOW()),
  (3, 1, 'CR003', 'Produção e Manufatura', 'DESPESA', 'Roberto Silva', true, false, NOW(), NOW()),
  (4, 1, 'CR004', 'Administrativo e Geral', 'DESPESA', 'Pedro Almeida', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO lancamento_contabil (id, empresa_id, lote, numero, data_lancamento, conta_debito_id, conta_credito_id, valor, historico, tipo, status, ativo, deleted, created_at, updated_at)
VALUES
  (1, 1, '2025/07', 1, '2025-07-12', 360, 455, 4307.50, 'Venda NF 000007 - Tech Solutions', 'NORMAL', 'EFETIVADO', true, false, NOW(), NOW()),
  (2, 1, '2025/07', 2, '2025-07-12', 476, 364, 2300.00, 'CMV NF 000007 - 5 motores', 'NORMAL', 'EFETIVADO', true, false, NOW(), NOW()),
  (3, 1, '2025/07', 3, '2025-07-18', 365, 402, 3650.00, 'Entrada NF 012345 - Chapa Aço Forte', 'NORMAL', 'EFETIVADO', true, false, NOW(), NOW()),
  (4, 1, '2025/07', 4, '2025-07-12', 365, 402, 1600.00, 'Entrada NF 067890 - Parafusos DNI', 'NORMAL', 'EFETIVADO', true, false, NOW(), NOW()),
  (5, 1, '2025/07', 5, '2025-07-31', 485, 408, 22200.00, 'Folha Pagamento 06/2025 - Proventos', 'NORMAL', 'EFETIVADO', true, false, NOW(), NOW()),
  (6, 1, '2025/07', 6, '2025-07-31', 408, 411, 5328.00, 'Folha Pagamento 06/2025 - Descontos', 'NORMAL', 'EFETIVADO', true, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 18. WORKFLOW - Definições, Alçadas, Aprovações
-- ============================================================
INSERT INTO workflow_definicoes (id, empresa_id, codigo, nome, descricao, modulo, entidade, ativo, created_at, updated_at)
VALUES
  (1, 1, 'WF001', 'Aprovação Pedido de Compra', 'Workflow de aprovação para pedidos de compra acima de R$ 5.000', 'COMPRAS', 'PEDIDO_COMPRA', true, NOW(), NOW()),
  (2, 1, 'WF002', 'Aprovação Desconto Venda', 'Workflow para descontos acima de 10% em pedidos de venda', 'VENDAS', 'PEDIDO_VENDA', true, NOW(), NOW()),
  (3, 1, 'WF003', 'Aprovação Pagamento', 'Workflow para pagamentos acima de R$ 10.000', 'FINANCEIRO', 'TITULO', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO workflow_alcadas (id, workflow_definicao_id, nivel, nome, perfil_id, usuario_id, valor_minimo, valor_maximo, obrigatorio, ordem, ativo, created_at, updated_at)
VALUES
  (1, 1, 1, 'Gerente de Compras', NULL, 1, 5000.00, 20000.00, true, 1, true, NOW(), NOW()),
  (2, 1, 2, 'Diretor Financeiro', NULL, 1, 20000.01, 100000.00, true, 2, true, NOW(), NOW()),
  (3, 2, 1, 'Gerente Comercial', NULL, 1, NULL, NULL, true, 1, true, NOW(), NOW()),
  (4, 3, 1, 'Controller', NULL, 1, 10000.00, 50000.00, true, 1, true, NOW(), NOW()),
  (5, 3, 2, 'Diretor Geral', NULL, 1, 50000.01, NULL, true, 2, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO workflow_aprovacoes (id, empresa_id, filial_id, workflow_definicao_id, alcada_id, entidade, entidade_id, nivel, aprovador_id, decisao, justificativa, data_decisao, status, ativo, created_at, updated_at)
VALUES
  (1, 1, 1, 1, 1, 'PEDIDO_COMPRA', 4, 1, 1, 'APROVADO', 'Valores dentro do orçamento previsto', '2025-07-14 10:30:00', 'CONCLUIDA', true, NOW(), NOW()),
  (2, 1, 1, 3, 4, 'TITULO', 6, 1, NULL, NULL, NULL, NULL, 'PENDENTE', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 19. PARÂMETROS DO SISTEMA (adicionais)
-- ============================================================
INSERT INTO parametros_sistema (id, empresa_id, chave, valor, tipo, descricao, modulo, editavel, ativo, created_at, updated_at)
VALUES
  (1, 1, 'EMPRESA_RAZAO_SOCIAL', 'Orion ERP Indústria e Comércio Ltda', 'STRING', 'Razão social da empresa', 'ADMIN', true, true, NOW(), NOW()),
  (2, 1, 'FISCAL_REGIME_TRIBUTARIO', '3', 'INTEGER', 'Regime tributário: 1=Simples, 2=Lucro Presumido, 3=Lucro Real', 'FISCAL', true, true, NOW(), NOW()),
  (3, 1, 'ESTOQUE_CUSTEIO_METODO', 'CUSTO_MEDIO', 'STRING', 'Método de custeio do estoque', 'ESTOQUE', true, true, NOW(), NOW()),
  (4, 1, 'FINANCEIRO_JUROS_MORA', '1.00', 'DECIMAL', 'Taxa de juros de mora mensal (%)', 'FINANCEIRO', true, true, NOW(), NOW()),
  (5, 1, 'FINANCEIRO_MULTA_ATRASO', '2.00', 'DECIMAL', 'Multa por atraso (%)', 'FINANCEIRO', true, true, NOW(), NOW()),
  (6, 1, 'VENDAS_DESCONTO_MAXIMO', '15.00', 'DECIMAL', 'Desconto máximo permitido sem aprovação (%)', 'VENDAS', true, true, NOW(), NOW()),
  (7, 1, 'RH_HORA_EXTRA_50', '1.50', 'DECIMAL', 'Fator de hora extra 50%', 'RH', true, true, NOW(), NOW()),
  (8, 1, 'RH_HORA_EXTRA_100', '2.00', 'DECIMAL', 'Fator de hora extra 100%', 'RH', true, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- 20. MENUS DE NAVEGAÇÃO
-- ============================================================
INSERT INTO menus (id, parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso, ativo, created_at, updated_at)
VALUES
  (1, NULL, 'MNU_ADMIN', 'Administração', 'Settings', NULL, 1, 'ADMIN', NULL, true, NOW(), NOW()),
  (2, 1, 'MNU_USUARIOS', 'Usuários', 'Users', '/admin/usuarios', 1, 'ADMIN', 'usuarios', true, NOW(), NOW()),
  (3, 1, 'MNU_PERFIS', 'Perfis', 'Shield', '/admin/perfis', 2, 'ADMIN', 'perfis', true, NOW(), NOW()),
  (4, NULL, 'MNU_CADASTROS', 'Cadastros', 'Database', NULL, 2, 'CADASTROS', NULL, true, NOW(), NOW()),
  (5, 4, 'MNU_CLIENTES', 'Clientes', 'Users', '/cadastros/clientes', 1, 'CADASTROS', 'clientes', true, NOW(), NOW()),
  (6, 4, 'MNU_FORNECEDORES', 'Fornecedores', 'Truck', '/cadastros/fornecedores', 2, 'CADASTROS', 'fornecedores', true, NOW(), NOW()),
  (7, 4, 'MNU_PRODUTOS', 'Produtos', 'Package', '/cadastros/produtos', 3, 'CADASTROS', 'produtos', true, NOW(), NOW()),
  (8, NULL, 'MNU_COMPRAS', 'Compras', 'ShoppingCart', NULL, 3, 'COMPRAS', NULL, true, NOW(), NOW()),
  (9, 8, 'MNU_PED_COMPRA', 'Pedidos de Compra', 'FileText', '/compras/pedidos', 1, 'COMPRAS', 'pedidos_compra', true, NOW(), NOW()),
  (10, NULL, 'MNU_VENDAS', 'Vendas', 'TrendingUp', NULL, 4, 'VENDAS', NULL, true, NOW(), NOW()),
  (11, 10, 'MNU_PED_VENDA', 'Pedidos de Venda', 'FileText', '/vendas/pedidos', 1, 'VENDAS', 'pedidos_venda', true, NOW(), NOW()),
  (12, NULL, 'MNU_FINANCEIRO', 'Financeiro', 'DollarSign', NULL, 5, 'FINANCEIRO', NULL, true, NOW(), NOW()),
  (13, 12, 'MNU_TITULOS', 'Títulos', 'CreditCard', '/financeiro/titulos', 1, 'FINANCEIRO', 'titulos', true, NOW(), NOW()),
  (14, NULL, 'MNU_ESTOQUE', 'Estoque', 'Archive', NULL, 6, 'ESTOQUE', NULL, true, NOW(), NOW()),
  (15, NULL, 'MNU_FISCAL', 'Fiscal', 'FileSpreadsheet', NULL, 7, 'FISCAL', NULL, true, NOW(), NOW()),
  (16, 15, 'MNU_NF', 'Notas Fiscais', 'File', '/fiscal/notas', 1, 'FISCAL', 'notas_fiscais', true, NOW(), NOW()),
  (17, NULL, 'MNU_RH', 'Recursos Humanos', 'Users', NULL, 8, 'RH', NULL, true, NOW(), NOW()),
  (18, 17, 'MNU_FUNCIONARIOS', 'Funcionários', 'UserCheck', '/rh/funcionarios', 1, 'RH', 'funcionarios', true, NOW(), NOW()),
  (19, NULL, 'MNU_PRODUCAO', 'Produção', 'Factory', NULL, 9, 'PCP', NULL, true, NOW(), NOW()),
  (20, NULL, 'MNU_CONTABIL', 'Contabilidade', 'Calculator', NULL, 10, 'CONTABILIDADE', NULL, true, NOW(), NOW()),
  (21, NULL, 'MNU_SEGUROS', 'Seguros', 'Shield', NULL, 11, 'SEGUROS', NULL, true, NOW(), NOW()),
  (22, NULL, 'MNU_CRM', 'CRM', 'Heart', NULL, 12, 'CRM', NULL, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================================
-- AJUSTAR SEQUENCES para evitar conflitos futuros
-- ============================================================
SELECT setval('marcas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM marcas));
SELECT setval('departamentos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM departamentos));
SELECT setval('transportadoras_id_seq', (SELECT COALESCE(MAX(id), 1) FROM transportadoras));
SELECT setval('armazens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM armazens));
SELECT setval('localizacoes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM localizacoes));
SELECT setval('tabelas_preco_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tabelas_preco));
SELECT setval('clientes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM clientes));
SELECT setval('fornecedores_id_seq', (SELECT COALESCE(MAX(id), 1) FROM fornecedores));
SELECT setval('produtos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM produtos));
SELECT setval('tabela_preco_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tabela_preco_itens));
SELECT setval('estrutura_produto_id_seq', (SELECT COALESCE(MAX(id), 1) FROM estrutura_produto));
SELECT setval('contas_bancarias_id_seq', (SELECT COALESCE(MAX(id), 1) FROM contas_bancarias));
SELECT setval('cargos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM cargos));
SELECT setval('departamentos_rh_id_seq', (SELECT COALESCE(MAX(id), 1) FROM departamentos_rh));
SELECT setval('beneficios_id_seq', (SELECT COALESCE(MAX(id), 1) FROM beneficios));
SELECT setval('funcionarios_id_seq', (SELECT COALESCE(MAX(id), 1) FROM funcionarios));
SELECT setval('funcionario_beneficio_id_seq', (SELECT COALESCE(MAX(id), 1) FROM funcionario_beneficio));
SELECT setval('ponto_eletronico_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ponto_eletronico));
SELECT setval('ferias_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ferias));
SELECT setval('folha_pagamento_id_seq', (SELECT COALESCE(MAX(id), 1) FROM folha_pagamento));
SELECT setval('folha_pagamento_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM folha_pagamento_item));
SELECT setval('seguradoras_id_seq', (SELECT COALESCE(MAX(id), 1) FROM seguradoras));
SELECT setval('corretoras_id_seq', (SELECT COALESCE(MAX(id), 1) FROM corretoras));
SELECT setval('propostas_seguro_id_seq', (SELECT COALESCE(MAX(id), 1) FROM propostas_seguro));
SELECT setval('apolices_id_seq', (SELECT COALESCE(MAX(id), 1) FROM apolices));
SELECT setval('notificacoes_seguro_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notificacoes_seguro));
SELECT setval('leads_id_seq', (SELECT COALESCE(MAX(id), 1) FROM leads));
SELECT setval('oportunidades_id_seq', (SELECT COALESCE(MAX(id), 1) FROM oportunidades));
SELECT setval('atividades_crm_id_seq', (SELECT COALESCE(MAX(id), 1) FROM atividades_crm));
SELECT setval('contatos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM contatos));
SELECT setval('solicitacoes_compra_id_seq', (SELECT COALESCE(MAX(id), 1) FROM solicitacoes_compra));
SELECT setval('solicitacao_compra_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM solicitacao_compra_itens));
SELECT setval('cotacoes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM cotacoes));
SELECT setval('cotacao_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM cotacao_itens));
SELECT setval('cotacao_fornecedores_id_seq', (SELECT COALESCE(MAX(id), 1) FROM cotacao_fornecedores));
SELECT setval('pedidos_compra_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pedidos_compra));
SELECT setval('pedido_compra_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pedido_compra_itens));
SELECT setval('recebimentos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM recebimentos));
SELECT setval('recebimento_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM recebimento_itens));
SELECT setval('orcamentos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM orcamentos));
SELECT setval('orcamento_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM orcamento_itens));
SELECT setval('pedidos_venda_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pedidos_venda));
SELECT setval('pedido_venda_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pedido_venda_itens));
SELECT setval('comissoes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM comissoes));
SELECT setval('notas_fiscais_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notas_fiscais));
SELECT setval('nota_fiscal_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM nota_fiscal_itens));
SELECT setval('regras_fiscais_id_seq', (SELECT COALESCE(MAX(id), 1) FROM regras_fiscais));
SELECT setval('saldos_estoque_id_seq', (SELECT COALESCE(MAX(id), 1) FROM saldos_estoque));
SELECT setval('movimentacoes_estoque_id_seq', (SELECT COALESCE(MAX(id), 1) FROM movimentacoes_estoque));
SELECT setval('inventarios_id_seq', (SELECT COALESCE(MAX(id), 1) FROM inventarios));
SELECT setval('inventario_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM inventario_itens));
SELECT setval('titulos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM titulos));
SELECT setval('titulo_parcelas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM titulo_parcelas));
SELECT setval('titulo_baixas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM titulo_baixas));
SELECT setval('fluxo_caixa_id_seq', (SELECT COALESCE(MAX(id), 1) FROM fluxo_caixa));
SELECT setval('conciliacao_bancaria_id_seq', (SELECT COALESCE(MAX(id), 1) FROM conciliacao_bancaria));
SELECT setval('conciliacao_bancaria_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM conciliacao_bancaria_item));
SELECT setval('contratos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM contratos));
SELECT setval('contrato_parcelas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM contrato_parcelas));
SELECT setval('bens_patrimoniais_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bens_patrimoniais));
SELECT setval('depreciacao_id_seq', (SELECT COALESCE(MAX(id), 1) FROM depreciacao));
SELECT setval('ordens_producao_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ordens_producao));
SELECT setval('ordem_producao_itens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ordem_producao_itens));
SELECT setval('apontamento_producao_id_seq', (SELECT COALESCE(MAX(id), 1) FROM apontamento_producao));
SELECT setval('periodo_contabil_id_seq', (SELECT COALESCE(MAX(id), 1) FROM periodo_contabil));
SELECT setval('centro_resultado_id_seq', (SELECT COALESCE(MAX(id), 1) FROM centro_resultado));
SELECT setval('lancamento_contabil_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lancamento_contabil));
SELECT setval('workflow_definicoes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM workflow_definicoes));
SELECT setval('workflow_alcadas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM workflow_alcadas));
SELECT setval('workflow_aprovacoes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM workflow_aprovacoes));
SELECT setval('parametros_sistema_id_seq', (SELECT COALESCE(MAX(id), 1) FROM parametros_sistema));
SELECT setval('menus_id_seq', (SELECT COALESCE(MAX(id), 1) FROM menus));
