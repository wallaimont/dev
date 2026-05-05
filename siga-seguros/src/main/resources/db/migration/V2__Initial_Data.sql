-- V2__Initial_Data.sql
-- Dados iniciais para demonstracao

-- Senha: admin123 (BCrypt)
INSERT INTO usuarios (nome, email, senha, perfil, active, created_at, created_by) VALUES
('Administrador', 'admin@sigaseguros.com.br', '$2a$10$clLWakCVZxkqI4IgGsPy4.xN/6axsxkolDNnLZpSRlIeLub.nINB6', 'ADMIN', TRUE, CURRENT_TIMESTAMP, 'system'),
('Carlos Gestor', 'gestor@sigaseguros.com.br', '$2a$10$clLWakCVZxkqI4IgGsPy4.xN/6axsxkolDNnLZpSRlIeLub.nINB6', 'GESTOR', TRUE, CURRENT_TIMESTAMP, 'system'),
('Maria Comercial', 'comercial@sigaseguros.com.br', '$2a$10$clLWakCVZxkqI4IgGsPy4.xN/6axsxkolDNnLZpSRlIeLub.nINB6', 'COMERCIAL', TRUE, CURRENT_TIMESTAMP, 'system'),
('João Operador', 'operador@sigaseguros.com.br', '$2a$10$clLWakCVZxkqI4IgGsPy4.xN/6axsxkolDNnLZpSRlIeLub.nINB6', 'OPERADOR', TRUE, CURRENT_TIMESTAMP, 'system'),
('Ana Financeiro', 'financeiro@sigaseguros.com.br', '$2a$10$clLWakCVZxkqI4IgGsPy4.xN/6axsxkolDNnLZpSRlIeLub.nINB6', 'FINANCEIRO', TRUE, CURRENT_TIMESTAMP, 'system'),
('Pedro Auditor', 'auditor@sigaseguros.com.br', '$2a$10$clLWakCVZxkqI4IgGsPy4.xN/6axsxkolDNnLZpSRlIeLub.nINB6', 'AUDITOR', TRUE, CURRENT_TIMESTAMP, 'system');

-- Ramos de Seguro
INSERT INTO ramos_seguro (nome, descricao, active, created_at, created_by) VALUES
('Automóvel', 'Seguro para veículos automotores', TRUE, CURRENT_TIMESTAMP, 'system'),
('Vida', 'Seguro de vida individual e coletivo', TRUE, CURRENT_TIMESTAMP, 'system'),
('Saúde', 'Planos de saúde e seguro saúde', TRUE, CURRENT_TIMESTAMP, 'system'),
('Residencial', 'Seguro para imóveis residenciais', TRUE, CURRENT_TIMESTAMP, 'system'),
('Empresarial', 'Seguro para empresas e estabelecimentos', TRUE, CURRENT_TIMESTAMP, 'system'),
('Responsabilidade Civil', 'Seguro de responsabilidade civil profissional e geral', TRUE, CURRENT_TIMESTAMP, 'system'),
('Transporte', 'Seguro de cargas e transporte', TRUE, CURRENT_TIMESTAMP, 'system'),
('Equipamentos', 'Seguro de equipamentos e máquinas', TRUE, CURRENT_TIMESTAMP, 'system'),
('Previdência', 'Previdência privada e complementar', TRUE, CURRENT_TIMESTAMP, 'system'),
('Viagem', 'Seguro viagem nacional e internacional', TRUE, CURRENT_TIMESTAMP, 'system');

-- Seguradoras
INSERT INTO seguradoras (nome, cnpj, codigo_interno, telefone, email, contato_comercial, percentual_comissao_padrao, active, created_at, created_by) VALUES
('Porto Seguro S.A.', '61.198.164/0001-60', 'PORTO', '(11) 3366-3000', 'comercial@portoseguro.com.br', 'Roberto Silva', 15.00, TRUE, CURRENT_TIMESTAMP, 'system'),
('SulAmérica Seguros', '33.000.167/0001-01', 'SULAM', '(21) 3873-4000', 'comercial@sulamerica.com.br', 'Ana Costa', 12.00, TRUE, CURRENT_TIMESTAMP, 'system'),
('Bradesco Seguros', '33.055.146/0001-09', 'BRADS', '(11) 4002-0022', 'comercial@bradescoseguros.com.br', 'Carlos Lima', 14.00, TRUE, CURRENT_TIMESTAMP, 'system'),
('Allianz Seguros', '61.573.796/0001-66', 'ALLIA', '(11) 3366-8000', 'comercial@allianz.com.br', 'Fernanda Oliveira', 13.00, TRUE, CURRENT_TIMESTAMP, 'system'),
('Tokio Marine', '33.164.021/0001-00', 'TOKIO', '(11) 3054-7000', 'comercial@tokiomarine.com.br', 'Marcos Santos', 16.00, TRUE, CURRENT_TIMESTAMP, 'system');

-- Corretoras Parceiras
INSERT INTO corretoras (nome, cnpj, responsavel, telefone, email, percentual_comissao, active, created_at, created_by) VALUES
('Corretora Delta Seguros', '12.345.678/0001-01', 'Ricardo Mendes', '(11) 3333-4444', 'contato@deltaseguros.com.br', 5.00, TRUE, CURRENT_TIMESTAMP, 'system'),
('Alpha Corretagem', '23.456.789/0001-02', 'Paula Ferreira', '(11) 5555-6666', 'contato@alphacorretagem.com.br', 4.50, TRUE, CURRENT_TIMESTAMP, 'system'),
('Omega Seguros', '34.567.890/0001-03', 'Fernando Dias', '(21) 7777-8888', 'contato@omegaseguros.com.br', 5.50, TRUE, CURRENT_TIMESTAMP, 'system');

-- Clientes PF
INSERT INTO clientes (tipo_pessoa, nome, cpf, data_nascimento, telefone, celular, email, cep, logradouro, numero, bairro, cidade, estado, active, created_at, created_by) VALUES
('PF', 'José da Silva Santos', '123.456.789-00', '1985-03-15', '(11) 3344-5566', '(11) 99876-5432', 'jose.santos@email.com', '01001-000', 'Rua das Flores', '100', 'Centro', 'São Paulo', 'SP', TRUE, CURRENT_TIMESTAMP, 'system'),
('PF', 'Maria Aparecida Oliveira', '987.654.321-00', '1990-07-22', '(11) 3355-6677', '(11) 98765-4321', 'maria.oliveira@email.com', '04001-000', 'Av. Paulista', '1500', 'Bela Vista', 'São Paulo', 'SP', TRUE, CURRENT_TIMESTAMP, 'system'),
('PF', 'Carlos Eduardo Pereira', '456.789.123-00', '1978-11-30', '(21) 3344-7788', '(21) 97654-3210', 'carlos.pereira@email.com', '20040-020', 'Rua do Ouvidor', '50', 'Centro', 'Rio de Janeiro', 'RJ', TRUE, CURRENT_TIMESTAMP, 'system');

-- Clientes PJ
INSERT INTO clientes (tipo_pessoa, razao_social, nome_fantasia, cnpj, data_fundacao, telefone, celular, email, cep, logradouro, numero, bairro, cidade, estado, active, created_at, created_by) VALUES
('PJ', 'Tech Solutions Ltda', 'TechSol', '11.222.333/0001-44', '2010-05-20', '(11) 3388-9900', '(11) 99988-7766', 'contato@techsol.com.br', '01310-100', 'Av. Brigadeiro Faria Lima', '2000', 'Itaim Bibi', 'São Paulo', 'SP', TRUE, CURRENT_TIMESTAMP, 'system'),
('PJ', 'Construtora Horizonte S.A.', 'Horizonte', '44.555.666/0001-77', '2005-01-10', '(21) 3399-0011', '(21) 98877-6655', 'contato@horizonte.com.br', '22041-080', 'Rua Visconde de Pirajá', '350', 'Ipanema', 'Rio de Janeiro', 'RJ', TRUE, CURRENT_TIMESTAMP, 'system');

-- Propostas
INSERT INTO propostas (numero_proposta, cliente_id, seguradora_id, corretora_id, ramo_seguro_id, vigencia_inicial, vigencia_final, premio_liquido, premio_total, percentual_comissao, valor_comissao, status, observacoes, responsavel_interno, active, created_at, created_by) VALUES
('PROP-2026-0001', 1, 1, 1, 1, '2026-04-01', '2027-04-01', 2800.00, 3200.00, 15.00, 480.00, 'APROVADO', 'Seguro auto completo - Honda Civic 2024', 'Maria Comercial', TRUE, CURRENT_TIMESTAMP, 'system'),
('PROP-2026-0002', 2, 2, NULL, 2, '2026-04-15', '2027-04-15', 1500.00, 1800.00, 12.00, 216.00, 'EM_ANALISE', 'Seguro de vida individual', 'Maria Comercial', TRUE, CURRENT_TIMESTAMP, 'system'),
('PROP-2026-0003', 4, 3, 2, 5, '2026-05-01', '2027-05-01', 15000.00, 18500.00, 14.00, 2590.00, 'COTADO', 'Seguro empresarial completo', 'Maria Comercial', TRUE, CURRENT_TIMESTAMP, 'system'),
('PROP-2026-0004', 3, 5, NULL, 4, '2026-03-20', '2027-03-20', 800.00, 950.00, 16.00, 152.00, 'EMITIDO', 'Seguro residencial', 'Maria Comercial', TRUE, CURRENT_TIMESTAMP, 'system'),
('PROP-2026-0005', 5, 4, 3, 7, '2026-06-01', '2027-06-01', 8000.00, 9500.00, 13.00, 1235.00, 'RECUSADO', 'Seguro transporte de cargas', 'Maria Comercial', TRUE, CURRENT_TIMESTAMP, 'system');

-- Apolices
INSERT INTO apolices (numero_apolice, proposta_id, cliente_id, seguradora_id, corretora_id, ramo_seguro_id, data_emissao, inicio_vigencia, fim_vigencia, premio_total, percentual_comissao, valor_comissao, forma_pagamento, quantidade_parcelas, status, responsavel_interno, active, created_at, created_by) VALUES
('APOL-2026-0001', 1, 1, 1, 1, 1, '2026-03-25', '2026-04-01', '2027-04-01', 3200.00, 15.00, 480.00, 'BOLETO', 4, 'ATIVA', 'João Operador', TRUE, CURRENT_TIMESTAMP, 'system'),
('APOL-2026-0002', 4, 3, 5, NULL, 4, '2026-03-18', '2026-03-20', '2027-03-20', 950.00, 16.00, 152.00, 'PIX', 1, 'ATIVA', 'João Operador', TRUE, CURRENT_TIMESTAMP, 'system'),
('APOL-2025-0010', NULL, 2, 2, NULL, 2, '2025-04-01', '2025-04-15', '2026-04-15', 1600.00, 12.00, 192.00, 'CARTAO_CREDITO', 12, 'ATIVA', 'João Operador', TRUE, CURRENT_TIMESTAMP, 'system');

-- Renovacoes
INSERT INTO renovacoes (apolice_id, cliente_id, data_vencimento, status, responsavel, observacoes, active, created_at, created_by) VALUES
(3, 2, '2026-04-15', 'PENDENTE', 'Maria Comercial', 'Apólice vencendo em breve - contatar cliente', TRUE, CURRENT_TIMESTAMP, 'system');

-- Sinistros
INSERT INTO sinistros (numero_sinistro, apolice_id, cliente_id, seguradora_id, data_aviso, descricao, valor_estimado, status, responsavel_interno, active, created_at, created_by) VALUES
('SIN-2026-0001', 1, 1, 1, '2026-03-20', 'Colisão traseira em estacionamento de shopping. Danos no para-choque traseiro e lanterna.', 4500.00, 'EM_ANALISE', 'João Operador', TRUE, CURRENT_TIMESTAMP, 'system');

-- Lancamentos Financeiros
INSERT INTO lancamentos_financeiros (tipo_lancamento, origem, cliente_id, apolice_id, descricao, valor, vencimento, status, numero_parcela, total_parcelas, active, created_at, created_by) VALUES
('RECEITA', 'APOLICE', 1, 1, 'Parcela 1/4 - Apólice APOL-2026-0001', 800.00, '2026-04-01', 'PENDENTE', 1, 4, TRUE, CURRENT_TIMESTAMP, 'system'),
('RECEITA', 'APOLICE', 1, 1, 'Parcela 2/4 - Apólice APOL-2026-0001', 800.00, '2026-05-01', 'PENDENTE', 2, 4, TRUE, CURRENT_TIMESTAMP, 'system'),
('RECEITA', 'APOLICE', 1, 1, 'Parcela 3/4 - Apólice APOL-2026-0001', 800.00, '2026-06-01', 'PENDENTE', 3, 4, TRUE, CURRENT_TIMESTAMP, 'system'),
('RECEITA', 'APOLICE', 1, 1, 'Parcela 4/4 - Apólice APOL-2026-0001', 800.00, '2026-07-01', 'PENDENTE', 4, 4, TRUE, CURRENT_TIMESTAMP, 'system');

-- Comissoes
INSERT INTO comissoes (origem, apolice_id, seguradora_id, corretora_id, favorecido, percentual, valor, status, data_prevista, active, created_at, created_by) VALUES
('APOLICE', 1, 1, 1, 'SIGA Seguros', 15.00, 480.00, 'PENDENTE', '2026-05-01', TRUE, CURRENT_TIMESTAMP, 'system'),
('APOLICE', 1, 1, 1, 'Corretora Delta Seguros', 5.00, 160.00, 'PENDENTE', '2026-05-01', TRUE, CURRENT_TIMESTAMP, 'system'),
('APOLICE', 2, 5, NULL, 'SIGA Seguros', 16.00, 152.00, 'PENDENTE', '2026-04-20', TRUE, CURRENT_TIMESTAMP, 'system');

-- Notificacoes
INSERT INTO notificacoes (titulo, mensagem, tipo, link, destinatario, lida, data_criacao) VALUES
('Apólice vencendo em breve', 'A apólice APOL-2025-0010 do cliente Maria Aparecida Oliveira vence em 15/04/2026.', 'ALERTA', '/apolices/3', 'comercial@sigaseguros.com.br', FALSE, CURRENT_TIMESTAMP),
('Novo sinistro registrado', 'Sinistro SIN-2026-0001 registrado para a apólice APOL-2026-0001.', 'INFO', '/sinistros/1', 'operador@sigaseguros.com.br', FALSE, CURRENT_TIMESTAMP),
('Proposta aguardando aprovação', 'A proposta PROP-2026-0002 está aguardando análise.', 'PENDENCIA', '/propostas/2', 'gestor@sigaseguros.com.br', FALSE, CURRENT_TIMESTAMP);
