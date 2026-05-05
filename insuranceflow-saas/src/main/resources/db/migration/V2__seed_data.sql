-- =============================================================
-- InsuranceFlow SaaS - Migration V2 - Dados Iniciais (Seed)
-- =============================================================

-- ========== PLANOS ==========
INSERT INTO plano (id, nome, descricao, preco_mensal, preco_anual, limite_usuarios, limite_clientes, limite_propostas_mes, limite_apolices, limite_armazenamento_gb, portal_cliente, whatsapp_integrado, relatorios_avancados, white_label, acesso_api, suporte_prioritario)
VALUES
('a0000000-0000-0000-0000-000000000001', 'Starter', 'Ideal para administradoras que estão começando. Recursos essenciais para iniciar sua operação digital.', 197.00, 1970.00, 3, 100, 30, 100, 2, false, false, false, false, false, false),
('a0000000-0000-0000-0000-000000000002', 'Professional', 'Para administradoras em crescimento. Mais recursos e limites ampliados para escalar sua operação.', 497.00, 4970.00, 10, 500, 150, 500, 10, true, false, true, false, false, false),
('a0000000-0000-0000-0000-000000000003', 'Business', 'Para administradoras consolidadas. Recursos avançados, integrações e white-label inclusos.', 997.00, 9970.00, 25, 2000, 500, 2000, 50, true, true, true, true, true, true),
('a0000000-0000-0000-0000-000000000004', 'Enterprise', 'Para grandes operações. Sem limites, suporte dedicado e personalização total.', 2497.00, 24970.00, 9999, 99999, 99999, 99999, 500, true, true, true, true, true, true);

-- ========== EMPRESA MASTER (Dona da plataforma) ==========
INSERT INTO empresa (id, razao_social, nome_fantasia, cnpj, email, telefone, plano_id, status, slug)
VALUES ('b0000000-0000-0000-0000-000000000001', 'InsuranceFlow Tecnologia Ltda', 'InsuranceFlow', '00.000.000/0001-00', 'admin@insuranceflow.com.br', '(11) 99999-0000', 'a0000000-0000-0000-0000-000000000004', 'ATIVA', 'master');

-- ========== USUÁRIO ADMIN MASTER ==========
-- Senha: admin123
INSERT INTO usuario (id, empresa_id, nome, email, senha, perfil, cargo)
VALUES ('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Administrador Master', 'admin@insuranceflow.com.br', '$2a$10$XM/BgEJvaFzAt/cH0/iU6Oxi.tJoYlm6UZydchd6a/Anfx4/gPcDu', 'ADMIN_MASTER', 'Administrador da Plataforma');

-- ========== EMPRESA DEMO ==========
INSERT INTO empresa (id, razao_social, nome_fantasia, cnpj, email, telefone, celular, cep, logradouro, numero, bairro, cidade, estado, plano_id, status, data_inicio_trial, data_fim_trial, slug)
VALUES ('b0000000-0000-0000-0000-000000000002', 'Segura Mais Administradora de Seguros Ltda', 'Segura Mais', '12.345.678/0001-90', 'contato@seguramais.com.br', '(11) 3333-4444', '(11) 99888-7777', '01310-100', 'Av. Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP', 'a0000000-0000-0000-0000-000000000003', 'ATIVA', NOW() - INTERVAL '30 days', NOW() + INTERVAL '335 days', 'segura-mais');

-- ========== ASSINATURA DA EMPRESA DEMO ==========
INSERT INTO assinatura_empresa (id, empresa_id, plano_id, ciclo, valor, status, data_inicio, data_vencimento)
VALUES ('d0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003', 'MENSAL', 997.00, 'ATIVA', NOW() - INTERVAL '30 days', NOW() + INTERVAL '1 day');

-- ========== CONFIGURAÇÃO WHITE LABEL DA EMPRESA DEMO ==========
INSERT INTO configuracao_white_label (id, empresa_id, nome_sistema, cor_primaria, cor_secundaria, cor_acento, nome_portal_cliente, email_suporte, telefone_suporte)
VALUES ('e0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'Segura Mais - Sistema de Gestão', '#1A365D', '#2B6CB0', '#38A169', 'Portal Segura Mais', 'suporte@seguramais.com.br', '(11) 3333-4444');

-- ========== CONFIGURAÇÃO DA EMPRESA DEMO ==========
INSERT INTO configuracao_empresa (id, empresa_id)
VALUES ('f0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002');

-- ========== ONBOARDING EMPRESA DEMO ==========
INSERT INTO onboarding_progress (id, empresa_id, passo_atual, empresa_configurada, equipe_cadastrada, marca_personalizada, primeiro_cliente, primeira_proposta, concluido, data_conclusao)
VALUES ('f1000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 5, true, true, true, true, true, true, NOW() - INTERVAL '25 days');

-- ========== USUÁRIOS DA EMPRESA DEMO ==========
-- Senha: demo123
INSERT INTO usuario (id, empresa_id, nome, email, senha, perfil, cargo) VALUES
('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'Carlos Silva', 'carlos@seguramais.com.br', '$2a$10$s.meDyweEIX4pUEoy1QRNekMpTLZ2Xfog5zk0Qj.IhV9zXaGhpbLG', 'ADMIN_EMPRESA', 'Diretor'),
('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'Ana Oliveira', 'ana@seguramais.com.br', '$2a$10$s.meDyweEIX4pUEoy1QRNekMpTLZ2Xfog5zk0Qj.IhV9zXaGhpbLG', 'GERENTE', 'Gerente Comercial'),
('c0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'Pedro Santos', 'pedro@seguramais.com.br', '$2a$10$s.meDyweEIX4pUEoy1QRNekMpTLZ2Xfog5zk0Qj.IhV9zXaGhpbLG', 'OPERADOR', 'Analista de Seguros');

-- ========== RAMOS DE SEGURO ==========
INSERT INTO ramo_seguro (id, empresa_id, codigo, nome, descricao) VALUES
('70000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', '0531', 'Automóvel', 'Seguro de automóveis'),
('70000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', '0114', 'Incêndio', 'Seguro contra incêndio'),
('70000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', '0993', 'Vida Individual', 'Seguro de vida individual'),
('70000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', '0982', 'Vida em Grupo', 'Seguro de vida em grupo'),
('70000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000002', '0553', 'RC Geral', 'Responsabilidade civil geral'),
('70000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000002', '1061', 'Saúde', 'Seguro saúde'),
('70000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000002', '0171', 'Riscos de Engenharia', 'Riscos de engenharia'),
('70000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000002', '0310', 'Transporte', 'Seguro de transporte');

-- ========== SEGURADORAS DEMO ==========
INSERT INTO seguradora (id, empresa_id, nome, cnpj, email, telefone) VALUES
('80000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'Porto Seguro S.A.', '61.198.164/0001-60', 'comercial@portoseguro.com.br', '(11) 3366-3000'),
('80000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'Bradesco Seguros S.A.', '33.055.146/0001-33', 'seguros@bradesco.com.br', '(11) 4002-0022'),
('80000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'SulAmérica Seguros S.A.', '33.041.062/0001-09', 'contato@sulamerica.com.br', '(11) 3004-2000'),
('80000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'Tokio Marine Seguradora', '33.164.021/0001-00', 'atendimento@tokiomarine.com.br', '(11) 3065-5000');

-- ========== CORRETORAS DEMO ==========
INSERT INTO corretora (id, empresa_id, nome, cnpj, email, telefone, responsavel, comissao_padrao) VALUES
('90000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'Corretora Top Seguros Ltda', '45.678.901/0001-23', 'comercial@topseguros.com.br', '(11) 2222-3333', 'Roberto Lima', 15.00),
('90000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'Mega Corretora de Seguros', '56.789.012/0001-34', 'contato@megacorretora.com.br', '(11) 4444-5555', 'Fernanda Costa', 12.50);

-- ========== CLIENTES DEMO ==========
INSERT INTO cliente (id, empresa_id, tipo_pessoa, nome, cpf_cnpj, data_nascimento, sexo, email, telefone, celular, cep, logradouro, numero, bairro, cidade, estado, profissao) VALUES
('10000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'PF', 'Maria Fernanda Souza', '123.456.789-00', '1985-03-15', 'F', 'maria@email.com', '(11) 3333-0001', '(11) 99111-0001', '01310-100', 'Av. Paulista', '500', 'Bela Vista', 'São Paulo', 'SP', 'Administradora'),
('10000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'PF', 'João Ricardo Pereira', '234.567.890-11', '1978-07-22', 'M', 'joao@email.com', '(11) 3333-0002', '(11) 99111-0002', '04543-011', 'R. Fidêncio Ramos', '250', 'Vila Olímpia', 'São Paulo', 'SP', 'Engenheiro Civil'),
('10000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'PJ', 'Tech Solutions Ltda', '98.765.432/0001-10', NULL, NULL, 'financeiro@techsolutions.com.br', '(11) 3333-0003', '(11) 99111-0003', '04543-011', 'R. Funchal', '400', 'Vila Olímpia', 'São Paulo', 'SP', NULL),
('10000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'PF', 'Lucia Helena Santos', '345.678.901-22', '1990-11-08', 'F', 'lucia@email.com', '(11) 3333-0004', '(11) 99111-0004', '01415-000', 'R. da Consolação', '1800', 'Consolação', 'São Paulo', 'SP', 'Médica'),
('10000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000002', 'PF', 'Roberto Almeida Neto', '456.789.012-33', '1972-05-30', 'M', 'roberto@email.com', '(11) 3333-0005', '(11) 99111-0005', '04507-000', 'R. Verbo Divino', '900', 'Chácara Santo Antônio', 'São Paulo', 'SP', 'Empresário');

-- ========== PORTAL DO CLIENTE - Senha: demo123 ==========
INSERT INTO usuario (id, empresa_id, nome, email, senha, perfil, cargo) VALUES
('c0000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000002', 'Maria Fernanda Souza', 'maria@email.com', '$2a$10$s.meDyweEIX4pUEoy1QRNekMpTLZ2Xfog5zk0Qj.IhV9zXaGhpbLG', 'CLIENTE', 'Cliente');

-- ========== PROPOSTAS DEMO ==========
INSERT INTO proposta (id, empresa_id, numero, cliente_id, seguradora_id, corretora_id, ramo_id, tipo_seguro, status, data_proposta, data_inicio_vigencia, data_fim_vigencia, valor_importancia_segurada, valor_premio, numero_parcelas) VALUES
('20000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'PROP-2025-001', '10000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', 'Automóvel', 'ACEITA', '2025-01-10', '2025-02-01', '2026-02-01', 85000.00, 3200.00, 4),
('20000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'PROP-2025-002', '10000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000003', 'Vida Individual', 'ACEITA', '2025-01-15', '2025-02-01', '2026-02-01', 500000.00, 1800.00, 12),
('20000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'PROP-2025-003', '10000000-0000-0000-0000-000000000003', '80000000-0000-0000-0000-000000000003', '90000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000002', 'Incêndio', 'EM_ANALISE', '2025-02-20', '2025-03-01', '2026-03-01', 2000000.00, 8500.00, 6),
('20000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'PROP-2025-004', '10000000-0000-0000-0000-000000000004', '80000000-0000-0000-0000-000000000004', '90000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000006', 'Saúde', 'RASCUNHO', '2025-03-01', NULL, NULL, NULL, 650.00, 12);

-- ========== APÓLICES DEMO ==========
INSERT INTO apolice (id, empresa_id, numero_apolice, proposta_id, cliente_id, seguradora_id, corretora_id, ramo_id, tipo_seguro, status, data_emissao, data_inicio_vigencia, data_fim_vigencia, valor_importancia_segurada, valor_premio, numero_parcelas) VALUES
('30000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'APOL-2025-001', '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', '90000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', 'Automóvel', 'ATIVA', '2025-01-20', '2025-02-01', '2026-02-01', 85000.00, 3200.00, 4),
('30000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'APOL-2025-002', '20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000002', '90000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000003', 'Vida Individual', 'ATIVA', '2025-01-25', '2025-02-01', '2026-02-01', 500000.00, 1800.00, 12);

-- ========== SINISTROS DEMO ==========
INSERT INTO sinistro (id, empresa_id, numero_sinistro, apolice_id, cliente_id, data_ocorrencia, data_aviso, tipo, descricao, status, valor_estimado) VALUES
('40000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'SIN-2025-001', '30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '2025-03-10', '2025-03-11', 'Colisão', 'Colisão traseira em semáforo na Av. Paulista. Danos na traseira do veículo.', 'EM_ANALISE', 12000.00);

-- ========== LEADS SaaS DEMO ==========
INSERT INTO lead_saas (id, nome, empresa_nome, email, telefone, origem, interesse, status) VALUES
('50000000-0000-0000-0000-000000000001', 'Fernando Mendes', 'Alpha Corretora', 'fernando@alphacorretora.com.br', '(21) 99888-1111', 'Landing Page', 'Plano Professional', 'NOVO'),
('50000000-0000-0000-0000-000000000002', 'Camila Rocha', 'Beta Seguros Admin.', 'camila@betaseguros.com.br', '(31) 99777-2222', 'Indicação', 'Plano Business', 'DEMO_AGENDADA'),
('50000000-0000-0000-0000-000000000003', 'Ricardo Lopes', 'Sul Administradora', 'ricardo@suladm.com.br', '(51) 99666-3333', 'Google Ads', 'Plano Enterprise', 'NEGOCIANDO');
