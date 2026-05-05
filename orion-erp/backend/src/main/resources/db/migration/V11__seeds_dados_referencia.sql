-- =====================================================================
-- V11 - Dados de Referência e Seeds Completas (padrão TOTVS Protheus)
-- CFOPs, CSTs, NCMs, Plano de Contas, Naturezas Financeiras,
-- Centros de Custo, Condições de Pagamento, Naturezas de Operação,
-- Grupos/Subgrupos de Produto, Dados de demonstração
-- =====================================================================

-- =====================================================================
-- 1. CFOP — Código Fiscal de Operações e Prestações
-- =====================================================================
INSERT INTO cfops (codigo, descricao, tipo) VALUES
-- Entradas (dentro do estado)
('1.101', 'Compra para industrialização ou produção rural', 'ENTRADA'),
('1.102', 'Compra para comercialização', 'ENTRADA'),
('1.111', 'Compra para industrialização de mercadoria recebida anteriormente em consignação', 'ENTRADA'),
('1.113', 'Compra para comercialização de mercadoria recebida anteriormente em consignação', 'ENTRADA'),
('1.116', 'Compra para industrialização originada de encomenda para recebimento futuro', 'ENTRADA'),
('1.117', 'Compra para comercialização originada de encomenda para recebimento futuro', 'ENTRADA'),
('1.120', 'Compra para industrialização em que a mercadoria foi remetida pelo fornecedor ao deposit. sob o regime de depósito', 'ENTRADA'),
('1.121', 'Compra para comercialização em que a mercadoria foi remetida pelo fornecedor ao deposit. sob o regime de depósito', 'ENTRADA'),
('1.122', 'Compra para industrialização em que a mercadoria foi remetida diretamente pelo vendedor remetente ao destinatário, sem transitar pelo estabelecimento do adquirente originário', 'ENTRADA'),
('1.124', 'Industrialização efetuada por outra empresa', 'ENTRADA'),
('1.125', 'Industrialização efetuada por outra empresa quando a mercadoria remetida para utilização no processo não transitou pelo estabelecimento adquirente', 'ENTRADA'),
('1.126', 'Compra para utilização na prestação de serviço sujeita ao ICMS', 'ENTRADA'),
('1.128', 'Compra para utilização na prestação de serviço sujeita ao ISSQN', 'ENTRADA'),
('1.151', 'Transferência para industrialização ou produção rural', 'ENTRADA'),
('1.152', 'Transferência para comercialização', 'ENTRADA'),
('1.153', 'Transferência de energia elétrica para distribuição', 'ENTRADA'),
('1.154', 'Transferência para utilização na prestação de serviço', 'ENTRADA'),
('1.201', 'Devolução de venda de produção do estabelecimento', 'ENTRADA'),
('1.202', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros', 'ENTRADA'),
('1.203', 'Devolução de venda de produção do estabelecimento destinada à Zona Franca de Manaus', 'ENTRADA'),
('1.204', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros destinada à ZFM', 'ENTRADA'),
('1.206', 'Anulação de valor relativo à aquisição de serviço de comunicação', 'ENTRADA'),
('1.207', 'Anulação de valor relativo à aquisição de serviço de transporte', 'ENTRADA'),
('1.208', 'Anulação de valor relativo à prestação de serviço de transporte', 'ENTRADA'),
('1.209', 'Anulação de valor relativo à prestação de serviço de comunicação', 'ENTRADA'),
('1.251', 'Compra de energia elétrica para distribuição ou comercialização', 'ENTRADA'),
('1.252', 'Compra de energia elétrica por estabelecimento industrial', 'ENTRADA'),
('1.253', 'Compra de energia elétrica por estabelecimento comercial', 'ENTRADA'),
('1.254', 'Compra de energia elétrica por estabelecimento prestador de serviço de transporte', 'ENTRADA'),
('1.255', 'Compra de energia elétrica por estabelecimento prestador de serviço de comunicação', 'ENTRADA'),
('1.256', 'Compra de energia elétrica por estabelecimento de produtor rural', 'ENTRADA'),
('1.301', 'Aquisição de serviço de comunicação para execução de serviço da mesma natureza', 'ENTRADA'),
('1.302', 'Aquisição de serviço de comunicação por estabelecimento industrial', 'ENTRADA'),
('1.303', 'Aquisição de serviço de comunicação por estabelecimento comercial', 'ENTRADA'),
('1.351', 'Aquisição de serviço de transporte para execução de serviço da mesma natureza', 'ENTRADA'),
('1.352', 'Aquisição de serviço de transporte por estabelecimento industrial', 'ENTRADA'),
('1.353', 'Aquisição de serviço de transporte por estabelecimento comercial', 'ENTRADA'),
('1.401', 'Compra para industrialização ou produção rural em operação com mercadoria sujeita ao regime de ST', 'ENTRADA'),
('1.403', 'Compra para comercialização em operação com mercadoria sujeita ao regime de ST', 'ENTRADA'),
('1.406', 'Compra de bem para o ativo imobilizado cuja mercadoria está sujeita ao regime de ST', 'ENTRADA'),
('1.407', 'Compra de mercadoria para uso ou consumo cuja mercadoria está sujeita ao regime de ST', 'ENTRADA'),
('1.501', 'Entrada de mercadoria recebida com fim específico de exportação', 'ENTRADA'),
('1.551', 'Compra de bem para o ativo imobilizado', 'ENTRADA'),
('1.553', 'Devolução de venda de bem do ativo imobilizado', 'ENTRADA'),
('1.556', 'Compra de material para uso ou consumo', 'ENTRADA'),
('1.557', 'Transferência de material para uso ou consumo', 'ENTRADA'),
('1.901', 'Entrada para industrialização por encomenda', 'ENTRADA'),
('1.902', 'Retorno de mercadoria remetida para industrialização por encomenda', 'ENTRADA'),
('1.903', 'Entrada de mercadoria remetida para industrialização e não aplicada no processo', 'ENTRADA'),
('1.904', 'Retorno de remessa para venda fora do estabelecimento', 'ENTRADA'),
('1.905', 'Entrada de mercadoria recebida para depósito em depósito fechado ou armazém geral', 'ENTRADA'),
('1.906', 'Retorno de mercadoria remetida para depósito fechado ou armazém geral', 'ENTRADA'),
('1.907', 'Retorno simbólico de mercadoria remetida para depósito fechado ou armazém geral', 'ENTRADA'),
('1.908', 'Entrada de bem por conta de contrato de comodato', 'ENTRADA'),
('1.909', 'Retorno de bem remetido por conta de contrato de comodato', 'ENTRADA'),
('1.910', 'Entrada de bonificação, doação ou brinde', 'ENTRADA'),
('1.911', 'Entrada de amostra grátis', 'ENTRADA'),
('1.912', 'Entrada de mercadoria ou bem recebido para demonstração', 'ENTRADA'),
('1.913', 'Retorno de mercadoria ou bem remetido para demonstração', 'ENTRADA'),
('1.914', 'Retorno de mercadoria ou bem remetido para exposição ou feira', 'ENTRADA'),
('1.916', 'Retorno de mercadoria ou bem remetido para conserto ou reparo', 'ENTRADA'),
('1.917', 'Entrada de mercadoria recebida em consignação mercantil ou industrial', 'ENTRADA'),
('1.918', 'Devolução de mercadoria remetida em consignação mercantil ou industrial', 'ENTRADA'),
('1.919', 'Devolução simbólica de mercadoria vendida ou utilizada em processo industrial, remetida anteriormente em consignação', 'ENTRADA'),
('1.920', 'Entrada de vasilhame ou sacaria', 'ENTRADA'),
('1.921', 'Retorno de vasilhame ou sacaria', 'ENTRADA'),
('1.922', 'Lançamento efetuado a título de simples faturamento para entrega futura', 'ENTRADA'),
('1.923', 'Entrada de mercadoria recebida do vendedor remetente, em venda à ordem', 'ENTRADA'),
('1.924', 'Entrada para industrialização por conta e ordem do adquirente', 'ENTRADA'),
('1.925', 'Retorno de mercadoria remetida para industrialização por conta e ordem do adquirente', 'ENTRADA'),
('1.926', 'Lançamento a título de reclassificação de mercadoria decorrente de formação de kit ou produto intermediário', 'ENTRADA'),
('1.949', 'Outra entrada de mercadoria ou prestação de serviço não especificada', 'ENTRADA'),
-- Entradas (de outros estados)
('2.101', 'Compra para industrialização ou produção rural', 'ENTRADA'),
('2.102', 'Compra para comercialização', 'ENTRADA'),
('2.151', 'Transferência para industrialização ou produção rural', 'ENTRADA'),
('2.152', 'Transferência para comercialização', 'ENTRADA'),
('2.201', 'Devolução de venda de produção do estabelecimento', 'ENTRADA'),
('2.202', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros', 'ENTRADA'),
('2.401', 'Compra para industrialização em operação com mercadoria sujeita ao regime de ST', 'ENTRADA'),
('2.403', 'Compra para comercialização em operação com mercadoria sujeita ao regime de ST', 'ENTRADA'),
('2.551', 'Compra de bem para o ativo imobilizado', 'ENTRADA'),
('2.556', 'Compra de material para uso ou consumo', 'ENTRADA'),
('2.901', 'Entrada para industrialização por encomenda', 'ENTRADA'),
('2.902', 'Retorno de mercadoria remetida para industrialização por encomenda', 'ENTRADA'),
('2.910', 'Entrada de bonificação, doação ou brinde', 'ENTRADA'),
('2.949', 'Outra entrada de mercadoria ou prestação de serviço não especificada', 'ENTRADA'),
-- Entradas (exterior)
('3.101', 'Compra para industrialização ou produção rural', 'ENTRADA'),
('3.102', 'Compra para comercialização', 'ENTRADA'),
('3.127', 'Compra para industrialização sob o regime de drawback', 'ENTRADA'),
('3.201', 'Devolução de venda de produção do estabelecimento', 'ENTRADA'),
('3.202', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros', 'ENTRADA'),
('3.551', 'Compra de bem para o ativo imobilizado', 'ENTRADA'),
('3.556', 'Compra de material para uso ou consumo', 'ENTRADA'),
('3.949', 'Outra entrada de mercadoria ou prestação de serviço não especificada', 'ENTRADA'),
-- Saídas (dentro do estado)
('5.101', 'Venda de produção do estabelecimento', 'SAIDA'),
('5.102', 'Venda de mercadoria adquirida ou recebida de terceiros', 'SAIDA'),
('5.103', 'Venda de produção do estabelecimento efetuada fora do estabelecimento', 'SAIDA'),
('5.104', 'Venda de mercadoria adquirida ou recebida de terceiros, efetuada fora do estabelecimento', 'SAIDA'),
('5.105', 'Venda de produção do estabelecimento que não deva por ele transitar', 'SAIDA'),
('5.106', 'Venda de mercadoria adquirida ou recebida de terceiros, que não deva por ele transitar', 'SAIDA'),
('5.109', 'Venda de produção do estabelecimento destinada à ZFM', 'SAIDA'),
('5.110', 'Venda de mercadoria adquirida ou recebida de terceiros, destinada à ZFM', 'SAIDA'),
('5.111', 'Venda de produção do estabelecimento remetida anteriormente em consignação', 'SAIDA'),
('5.112', 'Venda de mercadoria adquirida ou recebida de terceiros, remetida anteriormente em consignação', 'SAIDA'),
('5.113', 'Venda de produção do estabelecimento remetida anteriormente em consignação mercantil', 'SAIDA'),
('5.114', 'Venda de mercadoria adquirida ou recebida de terceiros remetida anteriormente em consignação mercantil', 'SAIDA'),
('5.115', 'Venda de mercadoria adquirida ou recebida de terceiros, recebida anteriormente em consignação mercantil', 'SAIDA'),
('5.116', 'Venda de produção do estabelecimento originada de encomenda para entrega futura', 'SAIDA'),
('5.117', 'Venda de mercadoria adquirida ou recebida de terceiros, originada de encomenda para entrega futura', 'SAIDA'),
('5.118', 'Venda de produção do estabelecimento entregue ao destinatário por conta e ordem do adquirente originário, em venda à ordem', 'SAIDA'),
('5.119', 'Venda de mercadoria adquirida ou recebida de terceiros entregue ao destinatário por conta e ordem, em venda à ordem', 'SAIDA'),
('5.120', 'Venda de mercadoria adquirida ou recebida de terceiros entregue ao destinatário pelo vendedor remetente, em venda à ordem', 'SAIDA'),
('5.122', 'Venda de produção do estabelecimento remetida para industrialização por conta e ordem do adquirente sem transitar pelo estab.', 'SAIDA'),
('5.123', 'Venda de mercadoria adquirida ou recebida de terceiros remetida para industrialização por conta e ordem do adquirente', 'SAIDA'),
('5.124', 'Industrialização efetuada para outra empresa', 'SAIDA'),
('5.125', 'Industrialização efetuada para outra empresa quando a mercadoria recebida não transitou pelo estab. adquirente', 'SAIDA'),
('5.151', 'Transferência de produção do estabelecimento', 'SAIDA'),
('5.152', 'Transferência de mercadoria adquirida ou recebida de terceiros', 'SAIDA'),
('5.153', 'Transferência de energia elétrica', 'SAIDA'),
('5.155', 'Transferência de produção do estabelecimento que não deva por ele transitar', 'SAIDA'),
('5.156', 'Transferência de mercadoria adquirida ou recebida de terceiros que não deva por ele transitar', 'SAIDA'),
('5.201', 'Devolução de compra para industrialização ou produção rural', 'SAIDA'),
('5.202', 'Devolução de compra para comercialização, ou de anulação de valor relativo à aquisição de serviço', 'SAIDA'),
('5.206', 'Anulação de valor relativo a prestação de serviço de comunicação', 'SAIDA'),
('5.207', 'Anulação de valor relativo a prestação de serviço de transporte', 'SAIDA'),
('5.208', 'Anulação de valor relativo à aquisição de serviço de transporte', 'SAIDA'),
('5.209', 'Anulação de valor relativo à aquisição de serviço de comunicação', 'SAIDA'),
('5.210', 'Devolução de compra para utilização na prestação de serviço', 'SAIDA'),
('5.401', 'Venda de produção do estabelecimento em operação com produto sujeito ao regime de ST', 'SAIDA'),
('5.402', 'Venda de produção do estabelecimento de produto sujeito ao regime de ST, em operação entre contribuintes substitutos', 'SAIDA'),
('5.403', 'Venda de mercadoria adquirida ou recebida de terceiros em operação com mercadoria sujeita ao regime de ST', 'SAIDA'),
('5.405', 'Venda de mercadoria adquirida ou recebida de terceiros em operação com mercadoria sujeita ao regime de ST, destinada a consumidor final', 'SAIDA'),
('5.501', 'Remessa de produção do estabelecimento com fim específico de exportação', 'SAIDA'),
('5.502', 'Remessa de mercadoria adquirida ou recebida de terceiros com fim específico de exportação', 'SAIDA'),
('5.551', 'Venda de bem do ativo imobilizado', 'SAIDA'),
('5.553', 'Devolução de compra de bem para o ativo imobilizado', 'SAIDA'),
('5.556', 'Devolução de compra de material de uso ou consumo', 'SAIDA'),
('5.557', 'Transferência de material de uso ou consumo', 'SAIDA'),
('5.901', 'Remessa para industrialização por encomenda', 'SAIDA'),
('5.902', 'Retorno de mercadoria utilizada na industrialização por encomenda', 'SAIDA'),
('5.903', 'Retorno de mercadoria recebida para industrialização e não aplicada no processo', 'SAIDA'),
('5.904', 'Remessa para venda fora do estabelecimento', 'SAIDA'),
('5.905', 'Remessa para depósito fechado ou armazém geral', 'SAIDA'),
('5.906', 'Retorno de mercadoria depositada em depósito fechado ou armazém geral', 'SAIDA'),
('5.907', 'Retorno simbólico de mercadoria depositada em depósito fechado ou armazém geral', 'SAIDA'),
('5.908', 'Remessa de bem por conta de contrato de comodato', 'SAIDA'),
('5.909', 'Retorno de bem recebido por conta de contrato de comodato', 'SAIDA'),
('5.910', 'Remessa em bonificação, doação ou brinde', 'SAIDA'),
('5.911', 'Remessa de amostra grátis', 'SAIDA'),
('5.912', 'Remessa de mercadoria ou bem para demonstração', 'SAIDA'),
('5.913', 'Retorno de mercadoria ou bem recebido para demonstração', 'SAIDA'),
('5.914', 'Remessa de mercadoria ou bem para exposição ou feira', 'SAIDA'),
('5.915', 'Remessa de mercadoria ou bem para conserto ou reparo', 'SAIDA'),
('5.916', 'Retorno de mercadoria ou bem recebido para conserto ou reparo', 'SAIDA'),
('5.917', 'Remessa de mercadoria em consignação mercantil ou industrial', 'SAIDA'),
('5.918', 'Devolução de mercadoria recebida em consignação mercantil ou industrial', 'SAIDA'),
('5.919', 'Devolução simbólica de mercadoria vendida ou utilizada em processo industrial recebida anteriormente em consignação', 'SAIDA'),
('5.920', 'Remessa de vasilhame ou sacaria', 'SAIDA'),
('5.921', 'Devolução de vasilhame ou sacaria', 'SAIDA'),
('5.922', 'Lançamento efetuado a título de simples faturamento para entrega futura', 'SAIDA'),
('5.923', 'Remessa de mercadoria por conta e ordem de terceiros, em venda à ordem', 'SAIDA'),
('5.924', 'Remessa para industrialização por conta e ordem do adquirente', 'SAIDA'),
('5.925', 'Retorno de mercadoria recebida para industrialização por conta e ordem do adquirente', 'SAIDA'),
('5.926', 'Lançamento a título de reclassificação de mercadoria decorrente de formação de kit', 'SAIDA'),
('5.929', 'Lançamento efetuado em decorrência de emissão de documento fiscal relativo a operação ou prestação também registrada em ECF', 'SAIDA'),
('5.949', 'Outra saída de mercadoria ou prestação de serviço não especificada', 'SAIDA'),
-- Saídas (outros estados)
('6.101', 'Venda de produção do estabelecimento', 'SAIDA'),
('6.102', 'Venda de mercadoria adquirida ou recebida de terceiros', 'SAIDA'),
('6.103', 'Venda de produção do estabelecimento efetuada fora do estabelecimento', 'SAIDA'),
('6.104', 'Venda de mercadoria adquirida ou recebida de terceiros efetuada fora do estabelecimento', 'SAIDA'),
('6.107', 'Venda de produção do estabelecimento destinada a não contribuinte', 'SAIDA'),
('6.108', 'Venda de mercadoria adquirida ou recebida de terceiros destinada a não contribuinte', 'SAIDA'),
('6.109', 'Venda de produção do estabelecimento destinada à ZFM', 'SAIDA'),
('6.110', 'Venda de mercadoria adquirida ou recebida de terceiros destinada à ZFM', 'SAIDA'),
('6.151', 'Transferência de produção do estabelecimento', 'SAIDA'),
('6.152', 'Transferência de mercadoria adquirida ou recebida de terceiros', 'SAIDA'),
('6.201', 'Devolução de compra para industrialização ou produção rural', 'SAIDA'),
('6.202', 'Devolução de compra para comercialização', 'SAIDA'),
('6.401', 'Venda de produção do estabelecimento em operação com mercadoria sujeita ao regime de ST', 'SAIDA'),
('6.403', 'Venda de mercadoria adquirida ou recebida de terceiros sujeita ao regime de ST', 'SAIDA'),
('6.501', 'Remessa de produção do estabelecimento com fim específico de exportação', 'SAIDA'),
('6.502', 'Remessa de mercadoria adquirida ou recebida de terceiros com fim específico de exportação', 'SAIDA'),
('6.551', 'Venda de bem do ativo imobilizado', 'SAIDA'),
('6.556', 'Devolução de compra de material de uso ou consumo', 'SAIDA'),
('6.901', 'Remessa para industrialização por encomenda', 'SAIDA'),
('6.902', 'Retorno de mercadoria utilizada na industrialização por encomenda', 'SAIDA'),
('6.910', 'Remessa em bonificação, doação ou brinde', 'SAIDA'),
('6.949', 'Outra saída de mercadoria ou prestação de serviço não especificada', 'SAIDA'),
-- Saídas (exterior)
('7.101', 'Venda de produção do estabelecimento', 'SAIDA'),
('7.102', 'Venda de mercadoria adquirida ou recebida de terceiros', 'SAIDA'),
('7.127', 'Venda de produção do estabelecimento sob regime de drawback', 'SAIDA'),
('7.201', 'Devolução de compra para industrialização ou produção rural', 'SAIDA'),
('7.202', 'Devolução de compra para comercialização', 'SAIDA'),
('7.501', 'Exportação direta de mercadorias', 'SAIDA'),
('7.551', 'Venda de bem do ativo imobilizado', 'SAIDA'),
('7.949', 'Outra saída de mercadoria ou prestação de serviço não especificada', 'SAIDA')
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================================
-- 2. CST — Código da Situação Tributária
-- =====================================================================
INSERT INTO csts (codigo, descricao, tipo_imposto) VALUES
-- CST ICMS (Tabela A + B)
('00', 'Tributada integralmente', 'ICMS'),
('10', 'Tributada e com cobrança do ICMS por ST', 'ICMS'),
('20', 'Com redução de base de cálculo', 'ICMS'),
('30', 'Isenta ou não tributada e com cobrança do ICMS por ST', 'ICMS'),
('40', 'Isenta', 'ICMS'),
('41', 'Não tributada', 'ICMS'),
('50', 'Suspensão', 'ICMS'),
('51', 'Diferimento', 'ICMS'),
('60', 'ICMS cobrado anteriormente por ST', 'ICMS'),
('70', 'Com redução de base de cálculo e cobrança do ICMS por ST', 'ICMS'),
('90', 'Outras', 'ICMS'),
-- CSOSN (Simples Nacional)
('101', 'Tributada com permissão de crédito', 'ICMS_SN'),
('102', 'Tributada sem permissão de crédito', 'ICMS_SN'),
('103', 'Isenção do ICMS para faixa de receita bruta', 'ICMS_SN'),
('201', 'Tributada com permissão de crédito e com cobrança do ICMS por ST', 'ICMS_SN'),
('202', 'Tributada sem permissão de crédito e com cobrança do ICMS por ST', 'ICMS_SN'),
('203', 'Isenção do ICMS para faixa de receita bruta e com cobrança do ICMS por ST', 'ICMS_SN'),
('300', 'Imune', 'ICMS_SN'),
('400', 'Não tributada pelo Simples Nacional', 'ICMS_SN'),
('500', 'ICMS cobrado anteriormente por ST ou por antecipação', 'ICMS_SN'),
('900', 'Outros (Simples Nacional)', 'ICMS_SN'),
-- CST IPI
('50', 'Saída tributada', 'IPI'),
('51', 'Saída tributada com alíquota zero', 'IPI'),
('52', 'Saída isenta', 'IPI'),
('53', 'Saída não tributada', 'IPI'),
('54', 'Saída imune', 'IPI'),
('55', 'Saída com suspensão', 'IPI'),
('99', 'Outras saídas', 'IPI'),
('00', 'Entrada com recuperação de crédito', 'IPI_ENTRADA'),
('01', 'Entrada tributada com alíquota zero', 'IPI_ENTRADA'),
('02', 'Entrada isenta', 'IPI_ENTRADA'),
('03', 'Entrada não tributada', 'IPI_ENTRADA'),
('04', 'Entrada imune', 'IPI_ENTRADA'),
('05', 'Entrada com suspensão', 'IPI_ENTRADA'),
('49', 'Outras entradas', 'IPI_ENTRADA'),
-- CST PIS
('01', 'Operação tributável com alíquota básica', 'PIS'),
('02', 'Operação tributável com alíquota diferenciada', 'PIS'),
('03', 'Operação tributável com alíquota por unidade de medida', 'PIS'),
('04', 'Operação tributável monofásica — revenda a alíquota zero', 'PIS'),
('05', 'Operação tributável por ST', 'PIS'),
('06', 'Operação tributável a alíquota zero', 'PIS'),
('07', 'Operação isenta da contribuição', 'PIS'),
('08', 'Operação sem incidência da contribuição', 'PIS'),
('09', 'Operação com suspensão da contribuição', 'PIS'),
('49', 'Outras operações de saída', 'PIS'),
('50', 'Operação com direito a crédito — vinculada exclusivamente a receita tributada no mercado interno', 'PIS'),
('51', 'Operação com direito a crédito — vinculada exclusivamente a receita não tributada', 'PIS'),
('52', 'Operação com direito a crédito — vinculada exclusivamente a receita de exportação', 'PIS'),
('53', 'Operação com direito a crédito — vinculada a receitas tributadas e não tributadas', 'PIS'),
('54', 'Operação com direito a crédito — vinculada exclusivamente a receita tributada no mercado interno e exportação', 'PIS'),
('55', 'Operação com direito a crédito — vinculada exclusivamente a receita não tributada e exportação', 'PIS'),
('56', 'Operação com direito a crédito — vinculada a receitas tributadas, não tributadas e exportação', 'PIS'),
('60', 'Crédito presumido — operação de aquisição vinculada exclusivamente a receita tributada', 'PIS'),
('61', 'Crédito presumido — operação de aquisição vinculada exclusivamente a receita não tributada', 'PIS'),
('62', 'Crédito presumido — operação de aquisição vinculada exclusivamente a receita de exportação', 'PIS'),
('63', 'Crédito presumido — operação de aquisição vinculada a receitas tributadas e não tributadas', 'PIS'),
('64', 'Crédito presumido — operação de aquisição vinculada exclusivamente a receita tributada e exportação', 'PIS'),
('65', 'Crédito presumido — operação de aquisição vinculada exclusivamente a receita não tributada e exportação', 'PIS'),
('66', 'Crédito presumido — operação de aquisição vinculada a receitas tributadas, não tributadas e exportação', 'PIS'),
('67', 'Crédito presumido — outras operações', 'PIS'),
('70', 'Operação de aquisição sem direito a crédito', 'PIS'),
('71', 'Operação de aquisição com isenção', 'PIS'),
('72', 'Operação de aquisição com suspensão', 'PIS'),
('73', 'Operação de aquisição a alíquota zero', 'PIS'),
('74', 'Operação de aquisição sem incidência da contribuição', 'PIS'),
('75', 'Operação de aquisição por ST', 'PIS'),
('98', 'Outras operações de entrada', 'PIS'),
('99', 'Outras operações', 'PIS'),
-- CST COFINS (mesmos códigos, mas tipo separado)
('01', 'Operação tributável com alíquota básica', 'COFINS'),
('02', 'Operação tributável com alíquota diferenciada', 'COFINS'),
('03', 'Operação tributável com alíquota por unidade de medida', 'COFINS'),
('04', 'Operação tributável monofásica — revenda a alíquota zero', 'COFINS'),
('05', 'Operação tributável por ST', 'COFINS'),
('06', 'Operação tributável a alíquota zero', 'COFINS'),
('07', 'Operação isenta da contribuição', 'COFINS'),
('08', 'Operação sem incidência da contribuição', 'COFINS'),
('09', 'Operação com suspensão da contribuição', 'COFINS'),
('49', 'Outras operações de saída', 'COFINS'),
('50', 'Operação com direito a crédito — vinculada exclusivamente a receita tributada no mercado interno', 'COFINS'),
('60', 'Crédito presumido — operação de aquisição vinculada exclusivamente a receita tributada', 'COFINS'),
('70', 'Operação de aquisição sem direito a crédito', 'COFINS'),
('98', 'Outras operações de entrada', 'COFINS'),
('99', 'Outras operações', 'COFINS')
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================================
-- 3. NCM — Nomenclatura Comum do Mercosul (principais)
-- =====================================================================
INSERT INTO ncms (codigo, descricao, aliquota_ipi) VALUES
('84713012', 'Notebooks e laptops', 15.0000),
('84714900', 'Outras máquinas automáticas para processamento de dados', 15.0000),
('84715000', 'Unidades de processamento digitais (exceto subpos. 8471.41 e 8471.49)', 15.0000),
('84716052', 'Monitores de vídeo com tela plana', 15.0000),
('84716053', 'Monitores de vídeo com tela de cristal líquido', 15.0000),
('84717012', 'Unidades de discos magnéticos para discos flexíveis (drives)', 10.0000),
('84717019', 'Outras unidades de memória', 10.0000),
('84718000', 'Outras unidades de máquinas automáticas para processamento de dados', 10.0000),
('84719012', 'Leitores de códigos de barras', 10.0000),
('84719014', 'Digitalizadores (scanner)', 10.0000),
('85044090', 'Outros conversores estáticos', 15.0000),
('85171210', 'Telefones celulares portáteis', 0.0000),
('85171290', 'Outros telefones para redes celulares', 10.0000),
('85176231', 'Roteadores digitais', 15.0000),
('94019090', 'Partes de assentos (exceto dos da posição 9402)', 5.0000),
('94032000', 'Outros móveis de metal', 5.0000),
('94033000', 'Móveis de madeira para escritórios', 5.0000),
('94034000', 'Móveis de madeira para cozinhas', 5.0000),
('94035000', 'Móveis de madeira para quartos de dormir', 5.0000),
('94036000', 'Outros móveis de madeira', 5.0000),
('48025610', 'Papel offset, sem fibras obtidas por processo mecânico, 40-150 g/m²', 5.0000),
('48101319', 'Outros papéis e cartões cuchê leve (LWC)', 5.0000),
('48192000', 'Caixas e cartonagens de papel ou cartão ondulado', 5.0000),
('39202000', 'Chapas/folhas de polímeros de propileno', 5.0000),
('39219090', 'Outras chapas/folhas/películas de plásticos', 5.0000),
('39231000', 'Caixas, engradados e artigos semelhantes de plásticos', 15.0000),
('39241000', 'Serviços de mesa e outros artigos de uso doméstico de plásticos', 15.0000),
('73089090', 'Outras construções e partes de construções de ferro ou aço', 5.0000),
('73102900', 'Outras caixas e recipientes de ferro ou aço, capacidade < 50 litros', 5.0000),
('73181500', 'Outros parafusos e pinos, mesmo com porcas e arruelas', 10.0000),
('72142000', 'Barras de ferro/aço, dentadas, com nervuras, sulcos ou deformações', 5.0000),
('25232900', 'Outros cimentos Portland', 0.0000),
('68022100', 'Mármore, travertino e alabastro', 0.0000),
('69072300', 'Ladrilhos e placas de cerâmica, coeficiente de absorção de água <= 0,5%', 5.0000),
('02013000', 'Carnes desossadas de bovinos, frescas ou refrigeradas', 0.0000),
('02023000', 'Carnes desossadas de bovinos, congeladas', 0.0000),
('02032900', 'Outras carnes de suínos, congeladas', 0.0000),
('02071200', 'Carnes de galos/galinhas, não cortadas em pedaços, congeladas', 0.0000),
('02071400', 'Pedaços e miudezas de galos/galinhas, congelados', 0.0000),
('04012100', 'Leite e creme de leite, não concentrado, com teor de gordura >1% e <=6%', 0.0000),
('04022110', 'Leite em pó integral', 0.0000),
('04061000', 'Queijo fresco (não curado)', 0.0000),
('10059010', 'Milho em grão', 0.0000),
('10063021', 'Arroz semibranqueado ou branqueado, polido ou brunido, parboilizado', 0.0000),
('11010010', 'Farinha de trigo', 0.0000),
('15079011', 'Óleo de soja, refinado, em recipientes com capacidade <= 5 litros', 0.0000),
('17011400', 'Outros açúcares de cana', 0.0000),
('09011110', 'Café não torrado, não descafeinado, em grão', 0.0000),
('22011000', 'Águas minerais e águas gaseificadas', 0.0000),
('22021000', 'Água adicionada de açúcar ou aromatizada (refrigerantes)', 4.0000),
('22030000', 'Cervejas de malte', 40.0000),
('27101259', 'Outros óleos combustíveis (gasolina)', 0.0000),
('27101921', 'Óleo diesel', 0.0000),
('27111300', 'Butanos liquefeitos (GLP)', 0.0000),
('30049099', 'Outros medicamentos (composições) para venda a retalho', 0.0000),
('30042099', 'Outros medicamentos com antibióticos para venda a retalho', 0.0000),
('33049100', 'Pós para maquilagem ou cuidados de pele', 10.0000),
('33051000', 'Xampus', 10.0000),
('34011190', 'Outros sabões de toucador', 0.0000),
('34022000', 'Preparações acondicionadas para venda a retalho (detergentes)', 5.0000),
('87032310', 'Veículos a gasolina 1000 <= cilindrada < 1500 cm³', 25.0000),
('87032390', 'Outros veículos a gasolina 1500 <= cilindrada < 3000 cm³', 25.0000),
('87033319', 'Outros veículos a diesel 1500 <= cilindrada < 2500 cm³', 10.0000),
('87060010', 'Chassis com motor para veículos automóveis de passageiros', 5.0000),
('87082990', 'Outras partes e acessórios de carroçarias', 10.0000),
('40111000', 'Pneumáticos novos de borracha para automóveis de passageiros', 15.0000),
('40112000', 'Pneumáticos novos de borracha para ônibus e caminhões', 15.0000),
('27101110', 'Álcool etílico anidro desnaturado (etanol)', 0.0000),
('38089119', 'Outros inseticidas', 0.0000),
('31021010', 'Ureia, mesmo em solução aquosa, teor de N > 45%', 0.0000),
('31053000', 'Hidrogeno-ortofosfato de diamônio (fosfato diamônico ou DAP)', 0.0000)
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================================
-- 4. PLANO DE CONTAS PADRÃO (estrutura mínima — PCG Brasileiro)
-- =====================================================================
INSERT INTO plano_contas (empresa_id, codigo, descricao, tipo, natureza, classificacao, nivel, aceita_lancamento, created_by) VALUES
-- 1. ATIVO
(1, '1', 'ATIVO', 'S', 'D', '1', 1, false, 'SYSTEM'),
(1, '1.1', 'ATIVO CIRCULANTE', 'S', 'D', '1.1', 2, false, 'SYSTEM'),
(1, '1.1.01', 'Caixa e Equivalentes de Caixa', 'S', 'D', '1.1.01', 3, false, 'SYSTEM'),
(1, '1.1.01.001', 'Caixa Geral', 'A', 'D', '1.1.01.001', 4, true, 'SYSTEM'),
(1, '1.1.01.002', 'Bancos Conta Movimento', 'A', 'D', '1.1.01.002', 4, true, 'SYSTEM'),
(1, '1.1.01.003', 'Aplicações Financeiras de Liquidez Imediata', 'A', 'D', '1.1.01.003', 4, true, 'SYSTEM'),
(1, '1.1.02', 'Contas a Receber', 'S', 'D', '1.1.02', 3, false, 'SYSTEM'),
(1, '1.1.02.001', 'Clientes - Duplicatas a Receber', 'A', 'D', '1.1.02.001', 4, true, 'SYSTEM'),
(1, '1.1.02.002', 'Clientes - Cheques a Receber', 'A', 'D', '1.1.02.002', 4, true, 'SYSTEM'),
(1, '1.1.02.003', '(-) PDD - Provisão p/ Devedores Duvidosos', 'A', 'C', '1.1.02.003', 4, true, 'SYSTEM'),
(1, '1.1.03', 'Estoques', 'S', 'D', '1.1.03', 3, false, 'SYSTEM'),
(1, '1.1.03.001', 'Mercadorias para Revenda', 'A', 'D', '1.1.03.001', 4, true, 'SYSTEM'),
(1, '1.1.03.002', 'Matérias-Primas', 'A', 'D', '1.1.03.002', 4, true, 'SYSTEM'),
(1, '1.1.03.003', 'Produtos em Elaboração', 'A', 'D', '1.1.03.003', 4, true, 'SYSTEM'),
(1, '1.1.03.004', 'Produtos Acabados', 'A', 'D', '1.1.03.004', 4, true, 'SYSTEM'),
(1, '1.1.03.005', 'Almoxarifado / Material de Uso e Consumo', 'A', 'D', '1.1.03.005', 4, true, 'SYSTEM'),
(1, '1.1.04', 'Impostos a Recuperar', 'S', 'D', '1.1.04', 3, false, 'SYSTEM'),
(1, '1.1.04.001', 'ICMS a Recuperar', 'A', 'D', '1.1.04.001', 4, true, 'SYSTEM'),
(1, '1.1.04.002', 'IPI a Recuperar', 'A', 'D', '1.1.04.002', 4, true, 'SYSTEM'),
(1, '1.1.04.003', 'PIS a Recuperar', 'A', 'D', '1.1.04.003', 4, true, 'SYSTEM'),
(1, '1.1.04.004', 'COFINS a Recuperar', 'A', 'D', '1.1.04.004', 4, true, 'SYSTEM'),
(1, '1.1.04.005', 'IRRF a Recuperar', 'A', 'D', '1.1.04.005', 4, true, 'SYSTEM'),
(1, '1.1.05', 'Adiantamentos', 'S', 'D', '1.1.05', 3, false, 'SYSTEM'),
(1, '1.1.05.001', 'Adiantamento a Fornecedores', 'A', 'D', '1.1.05.001', 4, true, 'SYSTEM'),
(1, '1.1.05.002', 'Adiantamento a Funcionários', 'A', 'D', '1.1.05.002', 4, true, 'SYSTEM'),
(1, '1.1.05.003', 'Adiantamento de Viagens', 'A', 'D', '1.1.05.003', 4, true, 'SYSTEM'),
(1, '1.2', 'ATIVO NÃO CIRCULANTE', 'S', 'D', '1.2', 2, false, 'SYSTEM'),
(1, '1.2.01', 'Realizável a Longo Prazo', 'S', 'D', '1.2.01', 3, false, 'SYSTEM'),
(1, '1.2.01.001', 'Títulos a Receber LP', 'A', 'D', '1.2.01.001', 4, true, 'SYSTEM'),
(1, '1.2.01.002', 'Depósitos Judiciais', 'A', 'D', '1.2.01.002', 4, true, 'SYSTEM'),
(1, '1.2.02', 'Investimentos', 'S', 'D', '1.2.02', 3, false, 'SYSTEM'),
(1, '1.2.02.001', 'Participações Societárias', 'A', 'D', '1.2.02.001', 4, true, 'SYSTEM'),
(1, '1.2.03', 'Imobilizado', 'S', 'D', '1.2.03', 3, false, 'SYSTEM'),
(1, '1.2.03.001', 'Terrenos', 'A', 'D', '1.2.03.001', 4, true, 'SYSTEM'),
(1, '1.2.03.002', 'Edificações', 'A', 'D', '1.2.03.002', 4, true, 'SYSTEM'),
(1, '1.2.03.003', 'Máquinas e Equipamentos', 'A', 'D', '1.2.03.003', 4, true, 'SYSTEM'),
(1, '1.2.03.004', 'Móveis e Utensílios', 'A', 'D', '1.2.03.004', 4, true, 'SYSTEM'),
(1, '1.2.03.005', 'Veículos', 'A', 'D', '1.2.03.005', 4, true, 'SYSTEM'),
(1, '1.2.03.006', 'Equipamentos de Informática', 'A', 'D', '1.2.03.006', 4, true, 'SYSTEM'),
(1, '1.2.03.007', 'Instalações', 'A', 'D', '1.2.03.007', 4, true, 'SYSTEM'),
(1, '1.2.03.008', 'Benfeitorias em Imóveis de Terceiros', 'A', 'D', '1.2.03.008', 4, true, 'SYSTEM'),
(1, '1.2.03.009', '(-) Depreciação Acumulada', 'A', 'C', '1.2.03.009', 4, true, 'SYSTEM'),
(1, '1.2.04', 'Intangível', 'S', 'D', '1.2.04', 3, false, 'SYSTEM'),
(1, '1.2.04.001', 'Software / Licenças', 'A', 'D', '1.2.04.001', 4, true, 'SYSTEM'),
(1, '1.2.04.002', 'Marcas e Patentes', 'A', 'D', '1.2.04.002', 4, true, 'SYSTEM'),
(1, '1.2.04.003', '(-) Amortização Acumulada', 'A', 'C', '1.2.04.003', 4, true, 'SYSTEM'),
-- 2. PASSIVO
(1, '2', 'PASSIVO', 'S', 'C', '2', 1, false, 'SYSTEM'),
(1, '2.1', 'PASSIVO CIRCULANTE', 'S', 'C', '2.1', 2, false, 'SYSTEM'),
(1, '2.1.01', 'Fornecedores', 'S', 'C', '2.1.01', 3, false, 'SYSTEM'),
(1, '2.1.01.001', 'Fornecedores Nacionais', 'A', 'C', '2.1.01.001', 4, true, 'SYSTEM'),
(1, '2.1.01.002', 'Fornecedores Exterior', 'A', 'C', '2.1.01.002', 4, true, 'SYSTEM'),
(1, '2.1.02', 'Empréstimos e Financiamentos CP', 'S', 'C', '2.1.02', 3, false, 'SYSTEM'),
(1, '2.1.02.001', 'Empréstimos Bancários', 'A', 'C', '2.1.02.001', 4, true, 'SYSTEM'),
(1, '2.1.02.002', 'Financiamentos', 'A', 'C', '2.1.02.002', 4, true, 'SYSTEM'),
(1, '2.1.03', 'Obrigações Trabalhistas', 'S', 'C', '2.1.03', 3, false, 'SYSTEM'),
(1, '2.1.03.001', 'Salários a Pagar', 'A', 'C', '2.1.03.001', 4, true, 'SYSTEM'),
(1, '2.1.03.002', 'FGTS a Recolher', 'A', 'C', '2.1.03.002', 4, true, 'SYSTEM'),
(1, '2.1.03.003', 'INSS a Recolher', 'A', 'C', '2.1.03.003', 4, true, 'SYSTEM'),
(1, '2.1.03.004', 'IRRF a Recolher (Folha)', 'A', 'C', '2.1.03.004', 4, true, 'SYSTEM'),
(1, '2.1.03.005', 'Férias a Pagar', 'A', 'C', '2.1.03.005', 4, true, 'SYSTEM'),
(1, '2.1.03.006', '13º Salário a Pagar', 'A', 'C', '2.1.03.006', 4, true, 'SYSTEM'),
(1, '2.1.03.007', 'Contribuição Sindical a Recolher', 'A', 'C', '2.1.03.007', 4, true, 'SYSTEM'),
(1, '2.1.04', 'Obrigações Fiscais', 'S', 'C', '2.1.04', 3, false, 'SYSTEM'),
(1, '2.1.04.001', 'ICMS a Recolher', 'A', 'C', '2.1.04.001', 4, true, 'SYSTEM'),
(1, '2.1.04.002', 'IPI a Recolher', 'A', 'C', '2.1.04.002', 4, true, 'SYSTEM'),
(1, '2.1.04.003', 'PIS a Recolher', 'A', 'C', '2.1.04.003', 4, true, 'SYSTEM'),
(1, '2.1.04.004', 'COFINS a Recolher', 'A', 'C', '2.1.04.004', 4, true, 'SYSTEM'),
(1, '2.1.04.005', 'ISS a Recolher', 'A', 'C', '2.1.04.005', 4, true, 'SYSTEM'),
(1, '2.1.04.006', 'IRPJ a Recolher', 'A', 'C', '2.1.04.006', 4, true, 'SYSTEM'),
(1, '2.1.04.007', 'CSLL a Recolher', 'A', 'C', '2.1.04.007', 4, true, 'SYSTEM'),
(1, '2.1.04.008', 'Simples Nacional a Recolher', 'A', 'C', '2.1.04.008', 4, true, 'SYSTEM'),
(1, '2.1.05', 'Outras Obrigações CP', 'S', 'C', '2.1.05', 3, false, 'SYSTEM'),
(1, '2.1.05.001', 'Adiantamento de Clientes', 'A', 'C', '2.1.05.001', 4, true, 'SYSTEM'),
(1, '2.1.05.002', 'Aluguéis a Pagar', 'A', 'C', '2.1.05.002', 4, true, 'SYSTEM'),
(1, '2.1.05.003', 'Contas a Pagar Diversas', 'A', 'C', '2.1.05.003', 4, true, 'SYSTEM'),
(1, '2.2', 'PASSIVO NÃO CIRCULANTE', 'S', 'C', '2.2', 2, false, 'SYSTEM'),
(1, '2.2.01', 'Empréstimos e Financiamentos LP', 'S', 'C', '2.2.01', 3, false, 'SYSTEM'),
(1, '2.2.01.001', 'Empréstimos Bancários LP', 'A', 'C', '2.2.01.001', 4, true, 'SYSTEM'),
(1, '2.2.01.002', 'Financiamentos LP', 'A', 'C', '2.2.01.002', 4, true, 'SYSTEM'),
(1, '2.2.02', 'Provisões', 'S', 'C', '2.2.02', 3, false, 'SYSTEM'),
(1, '2.2.02.001', 'Provisão p/ Contingências Trabalhistas', 'A', 'C', '2.2.02.001', 4, true, 'SYSTEM'),
(1, '2.2.02.002', 'Provisão p/ Contingências Tributárias', 'A', 'C', '2.2.02.002', 4, true, 'SYSTEM'),
-- 3. PATRIMÔNIO LÍQUIDO
(1, '3', 'PATRIMÔNIO LÍQUIDO', 'S', 'C', '3', 1, false, 'SYSTEM'),
(1, '3.1', 'Capital Social', 'S', 'C', '3.1', 2, false, 'SYSTEM'),
(1, '3.1.01', 'Capital Social', 'S', 'C', '3.1.01', 3, false, 'SYSTEM'),
(1, '3.1.01.001', 'Capital Social Subscrito', 'A', 'C', '3.1.01.001', 4, true, 'SYSTEM'),
(1, '3.1.01.002', '(-) Capital Social a Integralizar', 'A', 'D', '3.1.01.002', 4, true, 'SYSTEM'),
(1, '3.2', 'Reservas', 'S', 'C', '3.2', 2, false, 'SYSTEM'),
(1, '3.2.01', 'Reservas de Capital', 'S', 'C', '3.2.01', 3, false, 'SYSTEM'),
(1, '3.2.01.001', 'Ágio na Emissão de Ações', 'A', 'C', '3.2.01.001', 4, true, 'SYSTEM'),
(1, '3.2.02', 'Reservas de Lucros', 'S', 'C', '3.2.02', 3, false, 'SYSTEM'),
(1, '3.2.02.001', 'Reserva Legal', 'A', 'C', '3.2.02.001', 4, true, 'SYSTEM'),
(1, '3.2.02.002', 'Reserva Estatutária', 'A', 'C', '3.2.02.002', 4, true, 'SYSTEM'),
(1, '3.2.02.003', 'Reserva para Contingências', 'A', 'C', '3.2.02.003', 4, true, 'SYSTEM'),
(1, '3.3', 'Lucros/Prejuízos Acumulados', 'S', 'C', '3.3', 2, false, 'SYSTEM'),
(1, '3.3.01', 'Lucros ou Prejuízos Acumulados', 'S', 'C', '3.3.01', 3, false, 'SYSTEM'),
(1, '3.3.01.001', 'Lucros Acumulados', 'A', 'C', '3.3.01.001', 4, true, 'SYSTEM'),
(1, '3.3.01.002', '(-) Prejuízos Acumulados', 'A', 'D', '3.3.01.002', 4, true, 'SYSTEM'),
-- 4. RECEITAS
(1, '4', 'RECEITAS', 'S', 'C', '4', 1, false, 'SYSTEM'),
(1, '4.1', 'RECEITA OPERACIONAL', 'S', 'C', '4.1', 2, false, 'SYSTEM'),
(1, '4.1.01', 'Receita Bruta de Vendas', 'S', 'C', '4.1.01', 3, false, 'SYSTEM'),
(1, '4.1.01.001', 'Venda de Produtos', 'A', 'C', '4.1.01.001', 4, true, 'SYSTEM'),
(1, '4.1.01.002', 'Venda de Mercadorias', 'A', 'C', '4.1.01.002', 4, true, 'SYSTEM'),
(1, '4.1.01.003', 'Prestação de Serviços', 'A', 'C', '4.1.01.003', 4, true, 'SYSTEM'),
(1, '4.1.02', 'Deduções de Receita', 'S', 'D', '4.1.02', 3, false, 'SYSTEM'),
(1, '4.1.02.001', '(-) Devoluções de Vendas', 'A', 'D', '4.1.02.001', 4, true, 'SYSTEM'),
(1, '4.1.02.002', '(-) Abatimentos sobre Vendas', 'A', 'D', '4.1.02.002', 4, true, 'SYSTEM'),
(1, '4.1.02.003', '(-) Descontos Incondicionais Concedidos', 'A', 'D', '4.1.02.003', 4, true, 'SYSTEM'),
(1, '4.1.02.004', '(-) ICMS sobre Vendas', 'A', 'D', '4.1.02.004', 4, true, 'SYSTEM'),
(1, '4.1.02.005', '(-) PIS sobre Faturamento', 'A', 'D', '4.1.02.005', 4, true, 'SYSTEM'),
(1, '4.1.02.006', '(-) COFINS sobre Faturamento', 'A', 'D', '4.1.02.006', 4, true, 'SYSTEM'),
(1, '4.1.02.007', '(-) ISS sobre Serviços', 'A', 'D', '4.1.02.007', 4, true, 'SYSTEM'),
(1, '4.2', 'RECEITA NÃO OPERACIONAL', 'S', 'C', '4.2', 2, false, 'SYSTEM'),
(1, '4.2.01', 'Receitas Financeiras', 'S', 'C', '4.2.01', 3, false, 'SYSTEM'),
(1, '4.2.01.001', 'Juros Recebidos', 'A', 'C', '4.2.01.001', 4, true, 'SYSTEM'),
(1, '4.2.01.002', 'Descontos Obtidos', 'A', 'C', '4.2.01.002', 4, true, 'SYSTEM'),
(1, '4.2.01.003', 'Rendimentos de Aplicações Financeiras', 'A', 'C', '4.2.01.003', 4, true, 'SYSTEM'),
(1, '4.2.02', 'Outras Receitas', 'S', 'C', '4.2.02', 3, false, 'SYSTEM'),
(1, '4.2.02.001', 'Ganho na Venda de Imobilizado', 'A', 'C', '4.2.02.001', 4, true, 'SYSTEM'),
(1, '4.2.02.002', 'Receitas Eventuais', 'A', 'C', '4.2.02.002', 4, true, 'SYSTEM'),
-- 5. CUSTOS E DESPESAS
(1, '5', 'CUSTOS E DESPESAS', 'S', 'D', '5', 1, false, 'SYSTEM'),
(1, '5.1', 'CUSTOS', 'S', 'D', '5.1', 2, false, 'SYSTEM'),
(1, '5.1.01', 'Custo das Mercadorias Vendidas', 'S', 'D', '5.1.01', 3, false, 'SYSTEM'),
(1, '5.1.01.001', 'CMV - Custo das Mercadorias Vendidas', 'A', 'D', '5.1.01.001', 4, true, 'SYSTEM'),
(1, '5.1.01.002', 'CPV - Custo dos Produtos Vendidos', 'A', 'D', '5.1.01.002', 4, true, 'SYSTEM'),
(1, '5.1.01.003', 'CSP - Custo dos Serviços Prestados', 'A', 'D', '5.1.01.003', 4, true, 'SYSTEM'),
(1, '5.1.02', 'Custos de Produção', 'S', 'D', '5.1.02', 3, false, 'SYSTEM'),
(1, '5.1.02.001', 'Matéria-Prima Consumida', 'A', 'D', '5.1.02.001', 4, true, 'SYSTEM'),
(1, '5.1.02.002', 'Mão de Obra Direta', 'A', 'D', '5.1.02.002', 4, true, 'SYSTEM'),
(1, '5.1.02.003', 'Custos Indiretos de Fabricação', 'A', 'D', '5.1.02.003', 4, true, 'SYSTEM'),
(1, '5.2', 'DESPESAS OPERACIONAIS', 'S', 'D', '5.2', 2, false, 'SYSTEM'),
(1, '5.2.01', 'Despesas Administrativas', 'S', 'D', '5.2.01', 3, false, 'SYSTEM'),
(1, '5.2.01.001', 'Salários e Ordenados', 'A', 'D', '5.2.01.001', 4, true, 'SYSTEM'),
(1, '5.2.01.002', 'Encargos Sociais (FGTS)', 'A', 'D', '5.2.01.002', 4, true, 'SYSTEM'),
(1, '5.2.01.003', 'Encargos Sociais (INSS)', 'A', 'D', '5.2.01.003', 4, true, 'SYSTEM'),
(1, '5.2.01.004', 'Férias e 13º Salário', 'A', 'D', '5.2.01.004', 4, true, 'SYSTEM'),
(1, '5.2.01.005', 'Benefícios (VT, VR, Plano Saúde)', 'A', 'D', '5.2.01.005', 4, true, 'SYSTEM'),
(1, '5.2.01.006', 'Aluguéis e Condomínios', 'A', 'D', '5.2.01.006', 4, true, 'SYSTEM'),
(1, '5.2.01.007', 'Energia Elétrica', 'A', 'D', '5.2.01.007', 4, true, 'SYSTEM'),
(1, '5.2.01.008', 'Água e Esgoto', 'A', 'D', '5.2.01.008', 4, true, 'SYSTEM'),
(1, '5.2.01.009', 'Telefone e Internet', 'A', 'D', '5.2.01.009', 4, true, 'SYSTEM'),
(1, '5.2.01.010', 'Material de Escritório', 'A', 'D', '5.2.01.010', 4, true, 'SYSTEM'),
(1, '5.2.01.011', 'Material de Limpeza', 'A', 'D', '5.2.01.011', 4, true, 'SYSTEM'),
(1, '5.2.01.012', 'Manutenção e Conservação', 'A', 'D', '5.2.01.012', 4, true, 'SYSTEM'),
(1, '5.2.01.013', 'Seguros', 'A', 'D', '5.2.01.013', 4, true, 'SYSTEM'),
(1, '5.2.01.014', 'Depreciação', 'A', 'D', '5.2.01.014', 4, true, 'SYSTEM'),
(1, '5.2.01.015', 'Honorários Contábeis', 'A', 'D', '5.2.01.015', 4, true, 'SYSTEM'),
(1, '5.2.01.016', 'Honorários Advocatícios', 'A', 'D', '5.2.01.016', 4, true, 'SYSTEM'),
(1, '5.2.01.017', 'Serviços de Terceiros PJ', 'A', 'D', '5.2.01.017', 4, true, 'SYSTEM'),
(1, '5.2.01.018', 'Serviços de Terceiros PF', 'A', 'D', '5.2.01.018', 4, true, 'SYSTEM'),
(1, '5.2.01.019', 'Despesas com TI e Software', 'A', 'D', '5.2.01.019', 4, true, 'SYSTEM'),
(1, '5.2.01.020', 'Despesas com Viagens', 'A', 'D', '5.2.01.020', 4, true, 'SYSTEM'),
(1, '5.2.01.021', 'Despesas com Treinamentos', 'A', 'D', '5.2.01.021', 4, true, 'SYSTEM'),
(1, '5.2.01.022', 'Despesas Diversas', 'A', 'D', '5.2.01.022', 4, true, 'SYSTEM'),
(1, '5.2.02', 'Despesas Comerciais / Vendas', 'S', 'D', '5.2.02', 3, false, 'SYSTEM'),
(1, '5.2.02.001', 'Comissões sobre Vendas', 'A', 'D', '5.2.02.001', 4, true, 'SYSTEM'),
(1, '5.2.02.002', 'Fretes sobre Vendas', 'A', 'D', '5.2.02.002', 4, true, 'SYSTEM'),
(1, '5.2.02.003', 'Propaganda e Publicidade', 'A', 'D', '5.2.02.003', 4, true, 'SYSTEM'),
(1, '5.2.02.004', 'Brindes e Amostras', 'A', 'D', '5.2.02.004', 4, true, 'SYSTEM'),
(1, '5.2.02.005', 'Despesas com Inadimplência', 'A', 'D', '5.2.02.005', 4, true, 'SYSTEM'),
(1, '5.3', 'DESPESAS FINANCEIRAS', 'S', 'D', '5.3', 2, false, 'SYSTEM'),
(1, '5.3.01', 'Despesas Financeiras', 'S', 'D', '5.3.01', 3, false, 'SYSTEM'),
(1, '5.3.01.001', 'Juros Pagos', 'A', 'D', '5.3.01.001', 4, true, 'SYSTEM'),
(1, '5.3.01.002', 'Multas Pagas', 'A', 'D', '5.3.01.002', 4, true, 'SYSTEM'),
(1, '5.3.01.003', 'Descontos Concedidos', 'A', 'D', '5.3.01.003', 4, true, 'SYSTEM'),
(1, '5.3.01.004', 'Tarifas Bancárias', 'A', 'D', '5.3.01.004', 4, true, 'SYSTEM'),
(1, '5.3.01.005', 'IOF', 'A', 'D', '5.3.01.005', 4, true, 'SYSTEM'),
(1, '5.3.01.006', 'Variação Cambial Passiva', 'A', 'D', '5.3.01.006', 4, true, 'SYSTEM'),
(1, '5.4', 'OUTRAS DESPESAS', 'S', 'D', '5.4', 2, false, 'SYSTEM'),
(1, '5.4.01', 'Despesas Não Operacionais', 'S', 'D', '5.4.01', 3, false, 'SYSTEM'),
(1, '5.4.01.001', 'Perda na Venda de Imobilizado', 'A', 'D', '5.4.01.001', 4, true, 'SYSTEM'),
(1, '5.4.01.002', 'Perdas Diversas', 'A', 'D', '5.4.01.002', 4, true, 'SYSTEM'),
(1, '5.5', 'PROVISÃO PARA TRIBUTOS', 'S', 'D', '5.5', 2, false, 'SYSTEM'),
(1, '5.5.01', 'Tributos sobre o Lucro', 'S', 'D', '5.5.01', 3, false, 'SYSTEM'),
(1, '5.5.01.001', 'Provisão para IRPJ', 'A', 'D', '5.5.01.001', 4, true, 'SYSTEM'),
(1, '5.5.01.002', 'Provisão para CSLL', 'A', 'D', '5.5.01.002', 4, true, 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 5. CENTROS DE CUSTO PADRÃO
-- =====================================================================
INSERT INTO centros_custo (empresa_id, codigo, nome, nivel, created_by) VALUES
(1, '01', 'ADMINISTRATIVO', 1, 'SYSTEM'),
(1, '01.01', 'Diretoria', 2, 'SYSTEM'),
(1, '01.02', 'Recursos Humanos', 2, 'SYSTEM'),
(1, '01.03', 'Financeiro', 2, 'SYSTEM'),
(1, '01.04', 'Contabilidade', 2, 'SYSTEM'),
(1, '01.05', 'Tecnologia da Informação', 2, 'SYSTEM'),
(1, '01.06', 'Jurídico', 2, 'SYSTEM'),
(1, '02', 'COMERCIAL', 1, 'SYSTEM'),
(1, '02.01', 'Vendas Internas', 2, 'SYSTEM'),
(1, '02.02', 'Vendas Externas', 2, 'SYSTEM'),
(1, '02.03', 'Marketing', 2, 'SYSTEM'),
(1, '02.04', 'Pós-Venda / SAC', 2, 'SYSTEM'),
(1, '03', 'PRODUÇÃO / OPERACIONAL', 1, 'SYSTEM'),
(1, '03.01', 'Fabricação', 2, 'SYSTEM'),
(1, '03.02', 'Controle de Qualidade', 2, 'SYSTEM'),
(1, '03.03', 'Manutenção Industrial', 2, 'SYSTEM'),
(1, '03.04', 'Almoxarifado / Estoque', 2, 'SYSTEM'),
(1, '03.05', 'Logística / Expedição', 2, 'SYSTEM'),
(1, '04', 'COMPRAS', 1, 'SYSTEM'),
(1, '04.01', 'Compras e Suprimentos', 2, 'SYSTEM'),
(1, '04.02', 'Importação', 2, 'SYSTEM'),
(1, '05', 'INFRAESTRUTURA', 1, 'SYSTEM'),
(1, '05.01', 'Manutenção Predial', 2, 'SYSTEM'),
(1, '05.02', 'Segurança Patrimonial', 2, 'SYSTEM'),
(1, '05.03', 'Limpeza e Conservação', 2, 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 6. NATUREZAS FINANCEIRAS
-- =====================================================================
INSERT INTO naturezas_financeiras (empresa_id, codigo, nome, tipo, created_by) VALUES
-- Receitas
(1, 'R01', 'Venda de Produtos', 'RECEITA', 'SYSTEM'),
(1, 'R02', 'Venda de Mercadorias', 'RECEITA', 'SYSTEM'),
(1, 'R03', 'Prestação de Serviços', 'RECEITA', 'SYSTEM'),
(1, 'R04', 'Receita de Aluguel', 'RECEITA', 'SYSTEM'),
(1, 'R05', 'Receita Financeira (Juros)', 'RECEITA', 'SYSTEM'),
(1, 'R06', 'Receita Financeira (Aplicações)', 'RECEITA', 'SYSTEM'),
(1, 'R07', 'Recuperação de Despesas', 'RECEITA', 'SYSTEM'),
(1, 'R08', 'Receita com Comissões', 'RECEITA', 'SYSTEM'),
(1, 'R09', 'Receita com Royalties', 'RECEITA', 'SYSTEM'),
(1, 'R10', 'Receitas Diversas', 'RECEITA', 'SYSTEM'),
-- Despesas
(1, 'D01', 'Compra de Matéria-Prima', 'DESPESA', 'SYSTEM'),
(1, 'D02', 'Compra de Mercadorias', 'DESPESA', 'SYSTEM'),
(1, 'D03', 'Compra de Material de Uso e Consumo', 'DESPESA', 'SYSTEM'),
(1, 'D04', 'Folha de Pagamento', 'DESPESA', 'SYSTEM'),
(1, 'D05', 'Encargos Trabalhistas (INSS/FGTS)', 'DESPESA', 'SYSTEM'),
(1, 'D06', 'Benefícios (VT/VR/Saúde)', 'DESPESA', 'SYSTEM'),
(1, 'D07', 'Aluguel', 'DESPESA', 'SYSTEM'),
(1, 'D08', 'Energia Elétrica', 'DESPESA', 'SYSTEM'),
(1, 'D09', 'Água/Esgoto', 'DESPESA', 'SYSTEM'),
(1, 'D10', 'Telefone/Internet', 'DESPESA', 'SYSTEM'),
(1, 'D11', 'Combustível e Lubrificantes', 'DESPESA', 'SYSTEM'),
(1, 'D12', 'Manutenção de Veículos', 'DESPESA', 'SYSTEM'),
(1, 'D13', 'Manutenção de Equipamentos', 'DESPESA', 'SYSTEM'),
(1, 'D14', 'Fretes e Carretos', 'DESPESA', 'SYSTEM'),
(1, 'D15', 'Seguros', 'DESPESA', 'SYSTEM'),
(1, 'D16', 'Honorários Contábeis', 'DESPESA', 'SYSTEM'),
(1, 'D17', 'Honorários Advocatícios', 'DESPESA', 'SYSTEM'),
(1, 'D18', 'Serviços de Terceiros PJ', 'DESPESA', 'SYSTEM'),
(1, 'D19', 'Serviços de Terceiros PF', 'DESPESA', 'SYSTEM'),
(1, 'D20', 'Propaganda e Publicidade', 'DESPESA', 'SYSTEM'),
(1, 'D21', 'Material de Escritório', 'DESPESA', 'SYSTEM'),
(1, 'D22', 'Material de Limpeza', 'DESPESA', 'SYSTEM'),
(1, 'D23', 'Despesas com TI / Software', 'DESPESA', 'SYSTEM'),
(1, 'D24', 'Despesas com Viagens', 'DESPESA', 'SYSTEM'),
(1, 'D25', 'Despesas com Treinamentos', 'DESPESA', 'SYSTEM'),
(1, 'D26', 'Juros Pagos', 'DESPESA', 'SYSTEM'),
(1, 'D27', 'Multas e Penalidades', 'DESPESA', 'SYSTEM'),
(1, 'D28', 'Tarifas Bancárias', 'DESPESA', 'SYSTEM'),
(1, 'D29', 'Impostos e Taxas', 'DESPESA', 'SYSTEM'),
(1, 'D30', 'Despesas Diversas', 'DESPESA', 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 7. CONDIÇÕES DE PAGAMENTO
-- =====================================================================
INSERT INTO condicoes_pagamento (empresa_id, codigo, nome, tipo, created_by) VALUES
(1, 'AV', 'À Vista', 'A_VISTA', 'SYSTEM'),
(1, '30D', '30 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '30-60', '30/60 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '30-60-90', '30/60/90 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '28-56-84', '28/56/84 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '15-30-45', '15/30/45 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '7D', '7 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '14D', '14 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '60D', '60 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '90D', '90 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '30-60-90-120', '30/60/90/120 Dias', 'A_PRAZO', 'SYSTEM'),
(1, '50AV-50-30', '50% Antecipado + 50% em 30 dias', 'A_PRAZO', 'SYSTEM') ON CONFLICT DO NOTHING;

-- Parcelas das condições
INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, 1, 0, 100.0000 FROM condicoes_pagamento cp WHERE cp.codigo = 'AV' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, 1, 30, 100.0000 FROM condicoes_pagamento cp WHERE cp.codigo = '30D' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, v.numero_parcela, v.dias, v.percentual
FROM condicoes_pagamento cp
CROSS JOIN (VALUES (1, 30, 50.0000), (2, 60, 50.0000)) AS v(numero_parcela, dias, percentual)
WHERE cp.codigo = '30-60' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, v.numero_parcela, v.dias, v.percentual
FROM condicoes_pagamento cp
CROSS JOIN (VALUES (1, 30, 33.3334), (2, 60, 33.3333), (3, 90, 33.3333)) AS v(numero_parcela, dias, percentual)
WHERE cp.codigo = '30-60-90' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, v.numero_parcela, v.dias, v.percentual
FROM condicoes_pagamento cp
CROSS JOIN (VALUES (1, 28, 33.3334), (2, 56, 33.3333), (3, 84, 33.3333)) AS v(numero_parcela, dias, percentual)
WHERE cp.codigo = '28-56-84' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, v.numero_parcela, v.dias, v.percentual
FROM condicoes_pagamento cp
CROSS JOIN (VALUES (1, 15, 33.3334), (2, 30, 33.3333), (3, 45, 33.3333)) AS v(numero_parcela, dias, percentual)
WHERE cp.codigo = '15-30-45' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, 1, 7, 100.0000 FROM condicoes_pagamento cp WHERE cp.codigo = '7D' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, 1, 14, 100.0000 FROM condicoes_pagamento cp WHERE cp.codigo = '14D' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, 1, 60, 100.0000 FROM condicoes_pagamento cp WHERE cp.codigo = '60D' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, 1, 90, 100.0000 FROM condicoes_pagamento cp WHERE cp.codigo = '90D' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, v.numero_parcela, v.dias, v.percentual
FROM condicoes_pagamento cp
CROSS JOIN (VALUES (1, 30, 25.0000), (2, 60, 25.0000), (3, 90, 25.0000), (4, 120, 25.0000)) AS v(numero_parcela, dias, percentual)
WHERE cp.codigo = '30-60-90-120' AND cp.empresa_id = 1;

INSERT INTO condicao_pagamento_parcelas (condicao_pagamento_id, numero_parcela, dias, percentual)
SELECT cp.id, v.numero_parcela, v.dias, v.percentual
FROM condicoes_pagamento cp
CROSS JOIN (VALUES (1, 0, 50.0000), (2, 30, 50.0000)) AS v(numero_parcela, dias, percentual)
WHERE cp.codigo = '50AV-50-30' AND cp.empresa_id = 1;

-- =====================================================================
-- 8. GRUPOS E SUBGRUPOS DE PRODUTO
-- =====================================================================
INSERT INTO grupos_produto (empresa_id, codigo, nome, created_by) VALUES
(1, 'MP', 'Matéria-Prima', 'SYSTEM'),
(1, 'PA', 'Produto Acabado', 'SYSTEM'),
(1, 'MC', 'Mercadoria para Revenda', 'SYSTEM'),
(1, 'PI', 'Produto Intermediário', 'SYSTEM'),
(1, 'EM', 'Embalagem', 'SYSTEM'),
(1, 'MUC', 'Material de Uso e Consumo', 'SYSTEM'),
(1, 'SV', 'Serviço', 'SYSTEM'),
(1, 'AI', 'Ativo Imobilizado', 'SYSTEM') ON CONFLICT DO NOTHING;

INSERT INTO subgrupos_produto (empresa_id, grupo_id, codigo, nome, created_by)
SELECT 1, g.id, v.codigo, v.nome, 'SYSTEM'
FROM grupos_produto g
CROSS JOIN (VALUES
    ('MP.01', 'Aço e Metais'),
    ('MP.02', 'Plásticos e Polímeros'),
    ('MP.03', 'Químicos'),
    ('MP.04', 'Madeira'),
    ('MP.05', 'Tecidos e Fibras')
) AS v(codigo, nome)
WHERE g.codigo = 'MP' AND g.empresa_id = 1;

INSERT INTO subgrupos_produto (empresa_id, grupo_id, codigo, nome, created_by)
SELECT 1, g.id, v.codigo, v.nome, 'SYSTEM'
FROM grupos_produto g
CROSS JOIN (VALUES
    ('MC.01', 'Eletrônicos'),
    ('MC.02', 'Informática'),
    ('MC.03', 'Móveis e Decoração'),
    ('MC.04', 'Material de Escritório'),
    ('MC.05', 'Alimentos e Bebidas'),
    ('MC.06', 'Higiene e Limpeza'),
    ('MC.07', 'Vestuário'),
    ('MC.08', 'Peças e Acessórios')
) AS v(codigo, nome)
WHERE g.codigo = 'MC' AND g.empresa_id = 1;

-- =====================================================================
-- 9. CATEGORIAS DE PRODUTO
-- =====================================================================
INSERT INTO categorias (empresa_id, codigo, nome, created_by) VALUES
(1, 'IND', 'Industrial', 'SYSTEM'),
(1, 'COM', 'Comercial', 'SYSTEM'),
(1, 'SER', 'Serviço', 'SYSTEM'),
(1, 'INF', 'Informática', 'SYSTEM'),
(1, 'ALI', 'Alimentício', 'SYSTEM'),
(1, 'AUT', 'Automotivo', 'SYSTEM'),
(1, 'CON', 'Construção Civil', 'SYSTEM'),
(1, 'ELE', 'Eletroeletrônico', 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 10. MARCAS DE EXEMPLO
-- =====================================================================
INSERT INTO marcas (empresa_id, codigo, nome, created_by) VALUES
(1, 'ORI', 'OrionERP', 'SYSTEM'),
(1, 'GEN', 'Genérica', 'SYSTEM'),
(1, 'NAC', 'Nacional', 'SYSTEM'),
(1, 'IMP', 'Importada', 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 11. NATUREZAS DE OPERAÇÃO PADRÃO
-- =====================================================================
INSERT INTO naturezas_operacao (empresa_id, codigo, nome, tipo, gera_financeiro, movimenta_estoque, created_by) VALUES
(1, 'VND', 'Venda de Mercadoria', 'SAIDA', true, true, 'SYSTEM'),
(1, 'VNS', 'Venda de Serviço', 'SAIDA', true, false, 'SYSTEM'),
(1, 'DEV', 'Devolução de Venda', 'ENTRADA', true, true, 'SYSTEM'),
(1, 'CMP', 'Compra de Mercadoria', 'ENTRADA', true, true, 'SYSTEM'),
(1, 'CMS', 'Compra de Serviço', 'ENTRADA', true, false, 'SYSTEM'),
(1, 'DVC', 'Devolução de Compra', 'SAIDA', true, true, 'SYSTEM'),
(1, 'TRF', 'Transferência entre Filiais', 'SAIDA', false, true, 'SYSTEM'),
(1, 'TRE', 'Transferência Entrada', 'ENTRADA', false, true, 'SYSTEM'),
(1, 'REM', 'Remessa para Industrialização', 'SAIDA', false, true, 'SYSTEM'),
(1, 'RET', 'Retorno de Industrialização', 'ENTRADA', false, true, 'SYSTEM'),
(1, 'BON', 'Bonificação / Doação / Brinde', 'SAIDA', false, true, 'SYSTEM'),
(1, 'CON', 'Consignação Mercantil Remessa', 'SAIDA', false, true, 'SYSTEM'),
(1, 'COR', 'Consignação Mercantil Retorno', 'ENTRADA', false, true, 'SYSTEM'),
(1, 'VEX', 'Venda para Exportação', 'SAIDA', true, true, 'SYSTEM'),
(1, 'IMP', 'Importação', 'ENTRADA', true, true, 'SYSTEM'),
(1, 'AIM', 'Compra de Ativo Imobilizado', 'ENTRADA', true, false, 'SYSTEM'),
(1, 'VAI', 'Venda de Ativo Imobilizado', 'SAIDA', true, false, 'SYSTEM'),
(1, 'CUC', 'Compra Material Uso/Consumo', 'ENTRADA', true, false, 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 12. CENTRO DE RESULTADO PADRÃO
-- =====================================================================
INSERT INTO centro_resultado (empresa_id, codigo, descricao, tipo, created_by) VALUES
(1, 'CR01', 'Resultado Operacional', 'RESULTADO', 'SYSTEM'),
(1, 'CR02', 'Resultado Comercial', 'RESULTADO', 'SYSTEM'),
(1, 'CR03', 'Resultado Industrial', 'RESULTADO', 'SYSTEM'),
(1, 'CR04', 'Resultado Financeiro', 'FINANCEIRO', 'SYSTEM'),
(1, 'CR05', 'Investimentos', 'INVESTIMENTO', 'SYSTEM') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 13. PERÍODO CONTÁBIL CORRENTE
-- =====================================================================
INSERT INTO periodo_contabil (empresa_id, ano, mes, data_inicio, data_fim, status) VALUES
(1, 2026, 1, '2026-01-01', '2026-01-31', 'FECHADO'),
(1, 2026, 2, '2026-02-01', '2026-02-28', 'FECHADO'),
(1, 2026, 3, '2026-03-01', '2026-03-31', 'FECHADO'),
(1, 2026, 4, '2026-04-01', '2026-04-30', 'ABERTO'),
(1, 2026, 5, '2026-05-01', '2026-05-31', 'ABERTO'),
(1, 2026, 6, '2026-06-01', '2026-06-30', 'ABERTO'),
(1, 2026, 7, '2026-07-01', '2026-07-31', 'ABERTO'),
(1, 2026, 8, '2026-08-01', '2026-08-31', 'ABERTO'),
(1, 2026, 9, '2026-09-01', '2026-09-30', 'ABERTO'),
(1, 2026, 10, '2026-10-01', '2026-10-31', 'ABERTO'),
(1, 2026, 11, '2026-11-01', '2026-11-30', 'ABERTO'),
(1, 2026, 12, '2026-12-01', '2026-12-31', 'ABERTO') ON CONFLICT DO NOTHING;

-- =====================================================================
-- 14. WORKFLOW DEFINITIONS PADRÃO (Aprovações)
-- =====================================================================
INSERT INTO workflow_definicoes (empresa_id, codigo, nome, descricao, modulo, entidade) VALUES
(1, 'WF_PED_VENDA', 'Aprovação Pedido de Venda', 'Workflow de aprovação para pedidos de venda acima do limite', 'VENDAS', 'PedidoVenda'),
(1, 'WF_PED_COMPRA', 'Aprovação Pedido de Compra', 'Workflow de aprovação para pedidos de compra', 'COMPRAS', 'PedidoCompra'),
(1, 'WF_CONTA_PAGAR', 'Aprovação Conta a Pagar', 'Workflow de aprovação para pagamentos acima do limite', 'FINANCEIRO', 'ContaPagar'),
(1, 'WF_DESCONTO', 'Aprovação de Desconto Especial', 'Workflow para desconto acima do limite da tabela de preço', 'VENDAS', 'PedidoVenda'),
(1, 'WF_DEVOLUCAO', 'Aprovação de Devolução', 'Workflow para aprovação de devoluções de venda', 'VENDAS', 'NotaFiscal'),
(1, 'WF_OP', 'Liberação Ordem de Produção', 'Workflow para liberação de ordem de produção', 'PCP', 'OrdemProducao') ON CONFLICT DO NOTHING;

-- Alçadas para WF Pedido de Venda
INSERT INTO workflow_alcadas (workflow_definicao_id, nivel, nome, perfil_id, valor_minimo, valor_maximo, obrigatorio, ordem)
SELECT wd.id, 1, 'Gerente Comercial', 1, 0, 50000.00, true, 1
FROM workflow_definicoes wd WHERE wd.codigo = 'WF_PED_VENDA';

INSERT INTO workflow_alcadas (workflow_definicao_id, nivel, nome, perfil_id, valor_minimo, valor_maximo, obrigatorio, ordem)
SELECT wd.id, 2, 'Diretoria', 1, 50000.01, NULL, true, 2
FROM workflow_definicoes wd WHERE wd.codigo = 'WF_PED_VENDA';

-- Alçadas para WF Pedido de Compra
INSERT INTO workflow_alcadas (workflow_definicao_id, nivel, nome, perfil_id, valor_minimo, valor_maximo, obrigatorio, ordem)
SELECT wd.id, 1, 'Gerente de Compras', 1, 0, 30000.00, true, 1
FROM workflow_definicoes wd WHERE wd.codigo = 'WF_PED_COMPRA';

INSERT INTO workflow_alcadas (workflow_definicao_id, nivel, nome, perfil_id, valor_minimo, valor_maximo, obrigatorio, ordem)
SELECT wd.id, 2, 'Diretoria Financeira', 1, 30000.01, NULL, true, 2
FROM workflow_definicoes wd WHERE wd.codigo = 'WF_PED_COMPRA';

-- Alçada para WF Conta a Pagar
INSERT INTO workflow_alcadas (workflow_definicao_id, nivel, nome, perfil_id, valor_minimo, valor_maximo, obrigatorio, ordem)
SELECT wd.id, 1, 'Gerente Financeiro', 1, 5000.00, NULL, true, 1
FROM workflow_definicoes wd WHERE wd.codigo = 'WF_CONTA_PAGAR';

-- =====================================================================
-- 15. PERMISSÕES EXTRAS (módulos novos)
-- =====================================================================
INSERT INTO permissoes (recurso, acao, descricao, modulo) VALUES
-- Contabilidade
('plano_contas', 'listar', 'Visualizar plano de contas', 'CONTABILIDADE'),
('plano_contas', 'criar', 'Criar conta contábil', 'CONTABILIDADE'),
('plano_contas', 'editar', 'Editar conta contábil', 'CONTABILIDADE'),
('lancamentos_contabeis', 'listar', 'Visualizar lançamentos contábeis', 'CONTABILIDADE'),
('lancamentos_contabeis', 'criar', 'Criar lançamento contábil', 'CONTABILIDADE'),
('lancamentos_contabeis', 'aprovar', 'Aprovar lançamento contábil', 'CONTABILIDADE'),
('periodos_contabeis', 'listar', 'Visualizar períodos contábeis', 'CONTABILIDADE'),
('periodos_contabeis', 'fechar', 'Fechar período contábil', 'CONTABILIDADE'),
-- Faturamento
('notas_fiscais', 'listar', 'Listar notas fiscais', 'FATURAMENTO'),
('notas_fiscais', 'criar', 'Criar nota fiscal', 'FATURAMENTO'),
('notas_fiscais', 'editar', 'Editar nota fiscal', 'FATURAMENTO'),
('notas_fiscais', 'autorizar', 'Autorizar nota fiscal (NF-e)', 'FATURAMENTO'),
('notas_fiscais', 'cancelar', 'Cancelar nota fiscal', 'FATURAMENTO'),
-- Patrimônio
('bens_patrimoniais', 'listar', 'Listar bens patrimoniais', 'PATRIMONIO'),
('bens_patrimoniais', 'criar', 'Cadastrar bem patrimonial', 'PATRIMONIO'),
('bens_patrimoniais', 'editar', 'Editar bem patrimonial', 'PATRIMONIO'),
('bens_patrimoniais', 'baixar', 'Baixar bem patrimonial', 'PATRIMONIO'),
('depreciacao', 'calcular', 'Calcular depreciação', 'PATRIMONIO'),
-- PCP
('ordens_producao', 'listar', 'Listar ordens de produção', 'PCP'),
('ordens_producao', 'criar', 'Criar ordem de produção', 'PCP'),
('ordens_producao', 'liberar', 'Liberar ordem de produção', 'PCP'),
('ordens_producao', 'apontar', 'Apontar produção', 'PCP'),
('estruturas_produto', 'listar', 'Visualizar estruturas de produto', 'PCP'),
('estruturas_produto', 'editar', 'Editar estruturas de produto', 'PCP'),
-- Contratos
('contratos', 'listar', 'Listar contratos', 'CONTRATOS'),
('contratos', 'criar', 'Criar contrato', 'CONTRATOS'),
('contratos', 'editar', 'Editar contrato', 'CONTRATOS'),
('contratos', 'encerrar', 'Encerrar contrato', 'CONTRATOS'),
-- RH Expandido
('funcionarios', 'listar', 'Listar funcionários', 'RH'),
('funcionarios', 'criar', 'Admitir funcionário', 'RH'),
('funcionarios', 'editar', 'Editar funcionário', 'RH'),
('funcionarios', 'demitir', 'Demitir funcionário', 'RH'),
('folha_pagamento', 'listar', 'Visualizar folha de pagamento', 'RH'),
('folha_pagamento', 'calcular', 'Calcular folha de pagamento', 'RH'),
('folha_pagamento', 'fechar', 'Fechar folha de pagamento', 'RH'),
('ponto_eletronico', 'listar', 'Visualizar registro de ponto', 'RH'),
('ponto_eletronico', 'aprovar', 'Aprovar registro de ponto', 'RH'),
('ferias', 'listar', 'Visualizar férias', 'RH'),
('ferias', 'criar', 'Programar férias', 'RH'),
('ferias', 'aprovar', 'Aprovar férias', 'RH'),
-- Fiscal
('cfops', 'listar', 'Visualizar CFOPs', 'FISCAL'),
('csts', 'listar', 'Visualizar CSTs', 'FISCAL'),
('ncms', 'listar', 'Visualizar NCMs', 'FISCAL'),
('naturezas_operacao', 'listar', 'Listar naturezas de operação', 'FISCAL'),
('naturezas_operacao', 'editar', 'Editar natureza de operação', 'FISCAL'),
('regras_fiscais', 'listar', 'Visualizar regras fiscais', 'FISCAL'),
('regras_fiscais', 'editar', 'Editar regras fiscais', 'FISCAL'),
-- Seguros
('propostas_seguro', 'listar', 'Listar propostas de seguro', 'SEGUROS'),
('propostas_seguro', 'criar', 'Criar proposta de seguro', 'SEGUROS'),
('apolices_seguro', 'listar', 'Listar apólices de seguro', 'SEGUROS'),
('sinistros', 'listar', 'Listar sinistros', 'SEGUROS'),
('sinistros', 'criar', 'Registrar sinistro', 'SEGUROS'),
-- Conciliação
('conciliacao_bancaria', 'listar', 'Visualizar conciliações', 'FINANCEIRO'),
('conciliacao_bancaria', 'criar', 'Criar conciliação bancária', 'FINANCEIRO'),
('conciliacao_bancaria', 'fechar', 'Fechar conciliação bancária', 'FINANCEIRO')
ON CONFLICT (recurso, acao) DO NOTHING;

-- Vincular permissões extras ao perfil Admin
INSERT INTO perfil_permissoes (perfil_id, permissao_id)
SELECT 1, p.id FROM permissoes p
WHERE p.id NOT IN (SELECT permissao_id FROM perfil_permissoes WHERE perfil_id = 1) ON CONFLICT DO NOTHING;

-- =====================================================================
-- 16. MENUS EXTRAS (módulos que faltavam)
-- =====================================================================

-- Fiscal
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('fiscal', 'Fiscal', 'Receipt', 11, 'FISCAL') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='fiscal'), 'fis.cfops', 'CFOPs', 'Hash', '/fiscal/cfops', 1, 'FISCAL', 'cfops'),
((SELECT id FROM menus WHERE codigo='fiscal'), 'fis.csts', 'CSTs', 'Hash', '/fiscal/csts', 2, 'FISCAL', 'csts'),
((SELECT id FROM menus WHERE codigo='fiscal'), 'fis.ncms', 'NCMs', 'Hash', '/fiscal/ncms', 3, 'FISCAL', 'ncms'),
((SELECT id FROM menus WHERE codigo='fiscal'), 'fis.natops', 'Naturezas de Operação', 'FileOutput', '/fiscal/naturezas-operacao', 4, 'FISCAL', 'naturezas_operacao'),
((SELECT id FROM menus WHERE codigo='fiscal'), 'fis.regras', 'Regras Fiscais', 'Scale', '/fiscal/regras-fiscais', 5, 'FISCAL', 'regras_fiscais') ON CONFLICT (codigo) DO NOTHING;

-- Contabilidade
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('contabilidade', 'Contabilidade', 'Calculator', 12, 'CONTABILIDADE') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='contabilidade'), 'ctb.plano', 'Plano de Contas', 'ListTree', '/contabilidade/plano-contas', 1, 'CONTABILIDADE', 'plano_contas'),
((SELECT id FROM menus WHERE codigo='contabilidade'), 'ctb.lancamentos', 'Lançamentos', 'FileEdit', '/contabilidade/lancamentos', 2, 'CONTABILIDADE', 'lancamentos_contabeis'),
((SELECT id FROM menus WHERE codigo='contabilidade'), 'ctb.periodos', 'Períodos', 'CalendarDays', '/contabilidade/periodos', 3, 'CONTABILIDADE', 'periodos_contabeis') ON CONFLICT (codigo) DO NOTHING;

-- Faturamento
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('faturamento', 'Faturamento', 'FileText', 13, 'FATURAMENTO') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='faturamento'), 'fat.notas', 'Notas Fiscais', 'FileStack', '/faturamento/notas-fiscais', 1, 'FATURAMENTO', 'notas_fiscais') ON CONFLICT (codigo) DO NOTHING;

-- Patrimônio
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('patrimonio', 'Patrimônio', 'Landmark', 14, 'PATRIMONIO') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='patrimonio'), 'pat.bens', 'Bens Patrimoniais', 'Box', '/patrimonio/bens', 1, 'PATRIMONIO', 'bens_patrimoniais'),
((SELECT id FROM menus WHERE codigo='patrimonio'), 'pat.depreciacao', 'Depreciação', 'TrendingDown', '/patrimonio/depreciacao', 2, 'PATRIMONIO', 'depreciacao') ON CONFLICT (codigo) DO NOTHING;

-- PCP / Produção
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('pcp', 'Produção (PCP)', 'Factory', 15, 'PCP') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='pcp'), 'pcp.estruturas', 'Estruturas de Produto', 'Network', '/pcp/estruturas', 1, 'PCP', 'estruturas_produto'),
((SELECT id FROM menus WHERE codigo='pcp'), 'pcp.ordens', 'Ordens de Produção', 'ClipboardList', '/pcp/ordens', 2, 'PCP', 'ordens_producao') ON CONFLICT (codigo) DO NOTHING;

-- Contratos
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('contratos', 'Contratos', 'ScrollText', 16, 'CONTRATOS') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='contratos'), 'ctr.lista', 'Gestão de Contratos', 'FileSignature', '/contratos', 1, 'CONTRATOS', 'contratos') ON CONFLICT (codigo) DO NOTHING;

-- RH
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('rh', 'Recursos Humanos', 'Users', 17, 'RH') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='rh'), 'rh.funcionarios', 'Funcionários', 'UserCheck', '/rh/funcionarios', 1, 'RH', 'funcionarios'),
((SELECT id FROM menus WHERE codigo='rh'), 'rh.folha', 'Folha de Pagamento', 'Banknote', '/rh/folha', 2, 'RH', 'folha_pagamento'),
((SELECT id FROM menus WHERE codigo='rh'), 'rh.ponto', 'Ponto Eletrônico', 'Clock', '/rh/ponto', 3, 'RH', 'ponto_eletronico'),
((SELECT id FROM menus WHERE codigo='rh'), 'rh.ferias', 'Férias', 'Palmtree', '/rh/ferias', 4, 'RH', 'ferias') ON CONFLICT (codigo) DO NOTHING;

-- Seguros
INSERT INTO menus (codigo, titulo, icone, ordem, modulo) VALUES
('seguros', 'Seguros', 'Shield', 18, 'SEGUROS') ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.propostas', 'Propostas', 'FileText', '/seguros/propostas', 1, 'SEGUROS', 'propostas_seguro'),
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.apolices', 'Apólices', 'FileCheck', '/seguros/apolices', 2, 'SEGUROS', 'apolices_seguro'),
((SELECT id FROM menus WHERE codigo='seguros'), 'seg.sinistros', 'Sinistros', 'AlertTriangle', '/seguros/sinistros', 3, 'SEGUROS', 'sinistros') ON CONFLICT (codigo) DO NOTHING;

-- CRM submenus (expandir o menu CRM existente)
INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='crm'), 'crm.leads', 'Leads', 'UserPlus', '/crm/leads', 1, 'CRM', 'leads'),
((SELECT id FROM menus WHERE codigo='crm'), 'crm.oportunidades', 'Oportunidades', 'Target', '/crm/oportunidades', 2, 'CRM', 'oportunidades') ON CONFLICT (codigo) DO NOTHING;

-- Relatórios submenus (expandir o menu Relatórios existente)
INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='relatorios'), 'rel.financeiro', 'Financeiro', 'DollarSign', '/relatorios/financeiro', 1, 'RELATORIOS', 'relatorios'),
((SELECT id FROM menus WHERE codigo='relatorios'), 'rel.vendas', 'Vendas', 'ShoppingBag', '/relatorios/vendas', 2, 'RELATORIOS', 'relatorios'),
((SELECT id FROM menus WHERE codigo='relatorios'), 'rel.estoque', 'Estoque', 'Warehouse', '/relatorios/estoque', 3, 'RELATORIOS', 'relatorios'),
((SELECT id FROM menus WHERE codigo='relatorios'), 'rel.rh', 'Recursos Humanos', 'Users', '/relatorios/rh', 4, 'RELATORIOS', 'relatorios'),
((SELECT id FROM menus WHERE codigo='relatorios'), 'rel.fiscal', 'Faturamento', 'Receipt', '/relatorios/fiscal', 5, 'RELATORIOS', 'relatorios'),
((SELECT id FROM menus WHERE codigo='relatorios'), 'rel.contabil', 'Contábil (DRE)', 'Calculator', '/relatorios/contabil', 6, 'RELATORIOS', 'relatorios') ON CONFLICT (codigo) DO NOTHING;

-- Cadastros submenus extras
INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.unidades', 'Unidades de Medida', 'Ruler', '/cadastros/unidades-medida', 7, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.marcas', 'Marcas', 'Tag', '/cadastros/marcas', 8, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.categorias', 'Categorias', 'Layers', '/cadastros/categorias', 9, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.transportadoras', 'Transportadoras', 'Truck', '/cadastros/transportadoras', 10, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.contasbancarias', 'Contas Bancárias', 'Wallet', '/cadastros/contas-bancarias', 11, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.natfin', 'Naturezas Financeiras', 'ArrowUpDown', '/cadastros/naturezas-financeiras', 12, 'CADASTRO', 'produtos'),
((SELECT id FROM menus WHERE codigo='cadastros'), 'cad.tabelapreco', 'Tabelas de Preço', 'Tag', '/cadastros/tabelas-preco', 13, 'CADASTRO', 'produtos') ON CONFLICT (codigo) DO NOTHING;

-- Vendas submenus extras
INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='vendas'), 'vnd.comissoes', 'Comissões', 'Percent', '/vendas/comissoes', 3, 'VENDAS', 'pedidos_venda') ON CONFLICT (codigo) DO NOTHING;

-- Estoque submenus extras
INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='estoque'), 'est.armazens', 'Armazéns', 'Home', '/estoque/armazens', 4, 'ESTOQUE', 'estoque'),
((SELECT id FROM menus WHERE codigo='estoque'), 'est.localizacoes', 'Localizações', 'MapPin', '/estoque/localizacoes', 5, 'ESTOQUE', 'estoque') ON CONFLICT (codigo) DO NOTHING;

-- Financeiro submenus extras
INSERT INTO menus (parent_id, codigo, titulo, icone, rota, ordem, modulo, permissao_recurso) VALUES
((SELECT id FROM menus WHERE codigo='financeiro'), 'fin.conciliacao', 'Conciliação Bancária', 'CheckSquare', '/financeiro/conciliacao', 4, 'FINANCEIRO', 'conciliacao_bancaria') ON CONFLICT (codigo) DO NOTHING;
