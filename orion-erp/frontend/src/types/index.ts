/* ── Respostas da API ── */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

/* ── Auth ── */
export interface LoginRequest {
  email: string;
  senha: string;
}

export interface AuthToken {
  accessToken: string;
  refreshToken: string;
  tipo: string;
  expiraEm: number;
}

export interface Usuario {
  id: number;
  nome: string;
  login: string;
  email: string;
  perfilId: number;
  perfilNome: string;
  ativo: boolean;
}

/* ── Admin ── */
export interface Empresa {
  id: number;
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  inscricaoEstadual?: string;
  ativo: boolean;
}

export interface Filial {
  id: number;
  nome: string;
  cnpj: string;
  empresaId: number;
  empresaNome: string;
  ativo: boolean;
}

export interface Perfil {
  id: number;
  nome: string;
  descricao: string;
  ativo: boolean;
}

export interface ParametroSistema {
  id: number;
  chave: string;
  valor: string;
  descricao: string;
}

export interface Menu {
  id: number;
  titulo: string;
  icone?: string;
  rota?: string;
  ordem: number;
  parentId?: number;
  filhos?: Menu[];
}

/* ── Cadastros ── */
export interface Cliente {
  id: number;
  razaoSocial: string;
  nomeFantasia?: string;
  cpfCnpj: string;
  tipoPessoa: 'F' | 'J';
  email?: string;
  telefone?: string;
  limiteCredito: number;
  ativo: boolean;
}

export interface Fornecedor {
  id: number;
  razaoSocial: string;
  nomeFantasia?: string;
  cpfCnpj: string;
  tipoPessoa: 'F' | 'J';
  email?: string;
  telefone?: string;
  ativo: boolean;
}

export interface Produto {
  id: number;
  codigo: string;
  descricao: string;
  unidadeMedida: string;
  grupoProdutoId?: number;
  grupoProdutoNome?: string;
  ncm?: string;
  precoVenda: number;
  precoCusto: number;
  ativo: boolean;
}

export interface Banco {
  id: number;
  codigoBanco: string;
  nome: string;
  ativo: boolean;
}

export interface Categoria {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  parentId?: number;
  ativo: boolean;
}

export interface CentroCusto {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  parentId?: number;
  nivel: number;
  ativo: boolean;
}

export interface CondicaoPagamento {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  tipo: string;
  ativo: boolean;
}

export interface ContaBancaria {
  id: number;
  empresaId: number;
  filialId: number;
  bancoId: number;
  agencia: string;
  conta: string;
  digito: string;
  tipo: string;
  descricao: string;
  saldoInicial: number;
  ativo: boolean;
}

export interface GrupoProduto {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  ativo: boolean;
}

export interface Marca {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  ativo: boolean;
}

export interface NaturezaFinanceira {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  tipo: string;
  parentId?: number;
  ativo: boolean;
}

export interface SubgrupoProduto {
  id: number;
  empresaId: number;
  grupoId: number;
  codigo: string;
  nome: string;
  ativo: boolean;
}

export interface TabelaPreco {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  vigenciaInicio: string;
  vigenciaFim: string;
  ativo: boolean;
}

export interface Transportadora {
  id: number;
  empresaId: number;
  codigo: string;
  razaoSocial: string;
  nomeFantasia?: string;
  cpfCnpj: string;
  endereco?: string;
  cidade?: string;
  uf?: string;
  telefone?: string;
  email?: string;
  ativo: boolean;
}

export interface UnidadeMedida {
  id: number;
  codigo: string;
  nome: string;
  ativo: boolean;
}

/* ── Financeiro ── */
export interface Titulo {
  id: number;
  tipo: 'PAGAR' | 'RECEBER';
  numero: string;
  clienteId?: number;
  clienteNome?: string;
  fornecedorId?: number;
  fornecedorNome?: string;
  valorOriginal: number;
  valorSaldo: number;
  dataEmissao: string;
  dataVencimento: string;
  situacao: 'ABERTO' | 'PARCIAL' | 'QUITADO' | 'CANCELADO';
  naturezaFinanceiraId: number;
  naturezaFinanceiraNome?: string;
}

export interface FluxoCaixa {
  id: number;
  data: string;
  tipo: string;
  descricao: string;
  valor: number;
  saldoAcumulado: number;
}

export interface ConciliacaoBancaria {
  id: number;
  empresaId: number;
  filialId: number;
  contaBancariaId: number;
  dataInicio: string;
  dataFim: string;
  saldoExtrato: number;
  saldoSistema: number;
  diferenca: number;
  status: string;
  observacao?: string;
  ativo: boolean;
}

/* ── Estoque ── */
export interface Armazem {
  id: number;
  codigo: string;
  descricao: string;
  ativo: boolean;
}

export interface MovimentacaoEstoque {
  id: number;
  tipo: 'ENTRADA' | 'SAIDA' | 'AJUSTE';
  produtoId: number;
  produtoDescricao?: string;
  armazemId: number;
  armazemDescricao?: string;
  quantidade: number;
  custoUnitario: number;
  dataMovimentacao: string;
  observacao?: string;
}

export interface SaldoEstoque {
  id: number;
  empresaId: number;
  filialId: number;
  armazemId: number;
  armazemNome?: string;
  localizacaoId?: number;
  produtoId: number;
  produtoCodigo?: string;
  produtoNome?: string;
  lote?: string;
  validade?: string;
  quantidade: number;
  custoMedio: number;
  reservado: number;
  disponivel: number;
}

export interface Localizacao {
  id: number;
  armazemId: number;
  armazemNome?: string;
  codigo: string;
  descricao: string;
  ativo: boolean;
}

export interface Inventario {
  id: number;
  armazemId: number;
  armazemDescricao: string;
  dataInventario: string;
  situacao: 'EM_CONTAGEM' | 'FINALIZADO' | 'CANCELADO';
  observacao?: string;
}

/* ── Compras ── */
export interface PedidoCompra {
  id: number;
  numero: string;
  fornecedorId: number;
  fornecedorNome?: string;
  dataEmissao: string;
  situacao: 'RASCUNHO' | 'APROVADO' | 'REPROVADO' | 'CANCELADO' | 'RECEBIDO';
  valorTotal: number;
  observacao?: string;
}

/* ── Vendas ── */
export interface PedidoVenda {
  id: number;
  numero: string;
  clienteId: number;
  clienteNome?: string;
  dataEmissao: string;
  situacao: 'RASCUNHO' | 'APROVADO' | 'REPROVADO' | 'CANCELADO' | 'FATURADO';
  valorTotal: number;
  observacao?: string;
}

/* ── Fiscal ── */
export interface NaturezaOperacao {
  id: number;
  codigo: string;
  descricao: string;
  tipo: 'ENTRADA' | 'SAIDA';
  ativo: boolean;
}

export interface RegraFiscal {
  id: string;
  naturezaOperacaoId: number;
  ufOrigem?: string;
  ufDestino?: string;
  ncm?: string;
  cfopCodigo: string;
  cstIcms: string;
  aliquotaIcms: number;
  cstPis: string;
  aliquotaPis: number;
  cstCofins: string;
  aliquotaCofins: number;
  ativo: boolean;
}

export interface Cfop {
  id: number;
  codigo: string;
  descricao: string;
  tipo: string;
  ativo: boolean;
}

export interface Cst {
  id: number;
  codigo: string;
  descricao: string;
  tipoImposto: string;
  ativo: boolean;
}

export interface Ncm {
  id: number;
  codigo: string;
  descricao: string;
  aliquotaIpi: number;
  ativo: boolean;
}

/* ── CRM ── */
export interface Lead {
  id: number;
  nome: string;
  empresa?: string;
  email?: string;
  telefone?: string;
  status: 'NOVO' | 'CONTATADO' | 'QUALIFICADO' | 'CONVERTIDO' | 'PERDIDO';
  origem?: string;
  observacao?: string;
}

export interface Oportunidade {
  id: number;
  titulo: string;
  leadId?: number;
  leadNome?: string;
  clienteId?: number;
  clienteNome?: string;
  valorEstimado: number;
  probabilidade: number;
  etapa: string;
  situacao: 'ABERTA' | 'GANHA' | 'PERDIDA';
  dataPrevisaoFechamento?: string;
}

export interface AtividadeCrm {
  id: number;
  empresaId: number;
  tipo: string;
  titulo: string;
  descricao?: string;
  leadId?: number;
  oportunidadeId?: number;
  clienteId?: number;
  responsavelId?: number;
  dataHora: string;
  duracaoMinutos?: number;
  concluida: boolean;
}

/* ── Contabilidade ── */
export interface PlanoContas {
  id: number;
  empresaId: number;
  codigo: string;
  descricao: string;
  tipo: string;
  natureza: string;
  classificacao?: string;
  contaPaiId?: number;
  nivel: number;
  aceitaLancamento: boolean;
  ativo: boolean;
}

export interface CentroResultado {
  id: number;
  empresaId: number;
  codigo: string;
  descricao: string;
  tipo: string;
  responsavel?: string;
  ativo: boolean;
}

export interface LancamentoContabil {
  id: number;
  empresaId: number;
  filialId: number;
  lote: string;
  sublote?: string;
  numero: number;
  dataLancamento: string;
  contaDebitoId: number;
  contaCreditoId: number;
  valor: number;
  historico: string;
  documento?: string;
  centroCustoId?: number;
  centroResultadoId?: number;
  tipo: string;
  origem?: string;
  origemId?: number;
  status: string;
}

export interface PeriodoContabil {
  id: number;
  empresaId: number;
  ano: number;
  mes: number;
  dataInicio: string;
  dataFim: string;
  status: string;
  fechadoPor?: string;
  fechadoEm?: string;
}

/* ── Contratos ── */
export interface Contrato {
  id: number;
  empresaId: number;
  filialId: number;
  numero: string;
  clienteId: number;
  tipo: string;
  descricao?: string;
  dataInicio: string;
  dataFim: string;
  dataAssinatura?: string;
  valorTotal: number;
  valorMensal: number;
  formaPagamento?: string;
  diaVencimento?: number;
  status: string;
  observacao?: string;
  ativo: boolean;
}

export interface ContratoParcela {
  id: number;
  empresaId: number;
  filialId: number;
  contratoId: number;
  numeroParcela: number;
  dataVencimento: string;
  dataPagamento?: string;
  valor: number;
  valorPago?: number;
  status: string;
  ativo: boolean;
}

/* ── Faturamento ── */
export interface NotaFiscal {
  id: number;
  empresaId: number;
  filialId: number;
  tipo: string;
  serie: string;
  numero: string;
  chaveAcesso?: string;
  modelo?: string;
  naturezaOperacaoId?: number;
  cfopPredominante?: string;
  dataEmissao: string;
  dataSaidaEntrada?: string;
  clienteId?: number;
  fornecedorId?: number;
  transportadoraId?: number;
  fretePorConta?: string;
  valorProdutos: number;
  valorFrete: number;
  valorSeguro: number;
  valorDesconto: number;
  valorOutrasDespesas: number;
  valorIpi: number;
  valorIcms: number;
  valorIcmsSt: number;
  valorPis: number;
  valorCofins: number;
  valorTotal: number;
  informacoesComplementares?: string;
  pedidoVendaId?: number;
  pedidoCompraId?: number;
  status: string;
  protocoloAutorizacao?: string;
}

export interface NotaFiscalItem {
  id: number;
  notaFiscalId: number;
  numeroItem: number;
  produtoId: number;
  descricao: string;
  ncm?: string;
  cfop?: string;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
  valorDesconto: number;
  icmsCst?: string;
  icmsBase: number;
  icmsAliquota: number;
  icmsValor: number;
}

/* ── Patrimônio ── */
export interface BemPatrimonial {
  id: number;
  empresaId: number;
  filialId: number;
  codigo: string;
  descricao: string;
  numeroPatrimonio?: string;
  dataAquisicao: string;
  valorAquisicao: number;
  valorResidual: number;
  vidaUtilMeses: number;
  taxaDepreciacao: number;
  grupo?: string;
  localizacao?: string;
  centroCustoId?: number;
  fornecedorId?: number;
  notaFiscalId?: number;
  status: string;
  ativo: boolean;
}

export interface Depreciacao {
  id: number;
  empresaId: number;
  filialId: number;
  bemPatrimonialId: number;
  dataDepreciacao: string;
  valorDepreciacao: number;
  valorAcumulado: number;
  valorLiquido: number;
  mesReferencia: number;
  anoReferencia: number;
}

/* ── PCP ── */
export interface OrdemProducao {
  id: number;
  empresaId: number;
  filialId: number;
  numero: string;
  produtoId: number;
  quantidade: number;
  dataInicio: string;
  dataPrevisaoFim: string;
  dataFim?: string;
  status: string;
  prioridade: string;
  centroCustoId?: number;
  observacao?: string;
  ativo: boolean;
}

export interface OrdemProducaoItem {
  id: number;
  empresaId: number;
  filialId: number;
  ordemProducaoId: number;
  produtoId: number;
  quantidadePrevista: number;
  quantidadeUtilizada: number;
  unidadeMedida?: string;
  custoUnitario: number;
  ativo: boolean;
}

export interface EstruturaProduto {
  id: number;
  empresaId: number;
  filialId: number;
  produtoPaiId: number;
  produtoFilhoId: number;
  quantidade: number;
  unidadeMedida?: string;
  perdaPercentual: number;
  observacao?: string;
  ativo: boolean;
}

export interface ApontamentoProducao {
  id: number;
  empresaId: number;
  filialId: number;
  ordemProducaoId: number;
  funcionarioId: number;
  dataInicio: string;
  dataFim: string;
  quantidadeProduzida: number;
  quantidadeRejeitada: number;
  maquina?: string;
  observacao?: string;
  ativo: boolean;
}

/* ── RH ── */
export interface Funcionario {
  id: number;
  nome: string;
  cpf: string;
  email?: string;
  cargo?: string;
  departamento?: string;
  dataAdmissao: string;
  dataDemissao?: string;
  salario: number;
  situacao: 'ATIVO' | 'AFASTADO' | 'FERIAS' | 'DEMITIDO';
}

export interface Cargo {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  cbo?: string;
  salarioBase: number;
  nivel: string;
  ativo: boolean;
}

export interface DepartamentoRh {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  centroCustoId?: number;
  gestorId?: number;
  ativo: boolean;
}

export interface Beneficio {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  tipo: string;
  valorEmpresa: number;
  valorFuncionario: number;
  descontoFolha: boolean;
  ativo: boolean;
}

export interface FuncionarioBeneficio {
  id: number;
  empresaId: number;
  funcionarioId: number;
  beneficioId: number;
  dataInicio: string;
  dataFim?: string;
  valorCustomizado?: number;
  ativo: boolean;
}

export interface FolhaPagamento {
  id: number;
  empresaId: number;
  filialId: number;
  ano: number;
  mes: number;
  tipo: string;
  dataCalculo?: string;
  dataPagamento?: string;
  totalProventos: number;
  totalDescontos: number;
  totalLiquido: number;
  totalEncargos: number;
  status: string;
}

export interface Ferias {
  id: number;
  empresaId: number;
  filialId: number;
  funcionarioId: number;
  periodoAquisitivoInicio: string;
  periodoAquisitivoFim: string;
  dataInicio: string;
  dataFim: string;
  diasGozo: number;
  diasAbono: number;
  valorFerias: number;
  valorAbono: number;
  valorAdiantamento13: number;
  status: string;
}

export interface PontoEletronico {
  id: number;
  empresaId: number;
  filialId: number;
  funcionarioId: number;
  data: string;
  entrada1?: string;
  saida1?: string;
  entrada2?: string;
  saida2?: string;
  horasTrabalhadas: number;
  horasExtras: number;
  horasFalta: number;
  tipo: string;
  aprovado: boolean;
}

/* ── Vendas (extras) ── */
export interface Comissao {
  id: number;
  empresaId: number;
  filialId: number;
  vendedorId: number;
  pedidoVendaId: number;
  percentual: number;
  valorBase: number;
  valorComissao: number;
  status: string;
  dataPagamento?: string;
}

export interface TabelaPrecoItem {
  id: number;
  empresaId: number;
  filialId: number;
  tabelaPrecoId: number;
  produtoId: number;
  preco: number;
  precoPromocional?: number;
  quantidadeMinima?: number;
  ativo: boolean;
}

/* ── Workflow ── */
export interface WorkflowDefinicao {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  descricao?: string;
  modulo: string;
  entidade: string;
  ativo: boolean;
}

export interface WorkflowAprovacao {
  id: number;
  empresaId: number;
  filialId: number;
  workflowDefinicaoId: number;
  workflowNome?: string;
  alcadaId?: number;
  alcadaNome?: string;
  entidade: string;
  entidadeId: number;
  nivel: number;
  aprovadorId: number;
  decisao?: string;
  justificativa?: string;
  dataDecisao?: string;
  status: string;
}

/* ── Admin (extras) ── */
export interface Permissao {
  id: number;
  modulo: string;
  recurso: string;
  acao: string;
  descricao?: string;
}

/* ── Seguros ── */
export interface Seguradora {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  cnpj: string;
  registroSusep?: string;
  email?: string;
  telefone?: string;
  endereco?: string;
  cidade?: string;
  uf?: string;
  cep?: string;
  contato?: string;
  observacao?: string;
  ativo: boolean;
}

export interface Corretora {
  id: number;
  empresaId: number;
  codigo: string;
  nome: string;
  cnpj: string;
  responsavel?: string;
  email?: string;
  telefone?: string;
  endereco?: string;
  cidade?: string;
  uf?: string;
  cep?: string;
  percentualComissao: number;
  observacao?: string;
  ativo: boolean;
}

export interface PropostaSeguro {
  id: number;
  empresaId: number;
  numero: string;
  clienteId: number;
  clienteNome?: string;
  seguradoraId: number;
  seguradoraNome?: string;
  corretoraId?: number;
  corretoraNome?: string;
  ramo: string;
  vigenciaInicio: string;
  vigenciaFim: string;
  premioLiquido: number;
  premioTotal: number;
  percentualComissao: number;
  responsavel?: string;
  observacao?: string;
  status: 'COTACAO' | 'ENVIADA' | 'APROVADA' | 'RECUSADA' | 'CANCELADA';
}

export interface Apolice {
  id: number;
  empresaId: number;
  numero: string;
  propostaId?: number;
  propostaNumero?: string;
  clienteId: number;
  clienteNome?: string;
  seguradoraId: number;
  seguradoraNome?: string;
  corretoraId?: number;
  corretoraNome?: string;
  ramo: string;
  vigenciaInicio: string;
  vigenciaFim: string;
  premioTotal: number;
  importanciaSegurada: number;
  franquia: number;
  percentualComissao: number;
  certificadoInclusao?: string;
  observacao?: string;
  status: 'ATIVA' | 'VENCIDA' | 'CANCELADA' | 'SUSPENSA';
}

export interface NotificacaoSeguro {
  id: number;
  empresaId: number;
  tipo: 'VENCIMENTO' | 'STATUS' | 'ALERTA';
  titulo: string;
  descricao?: string;
  destinatario?: string;
  lida: boolean;
  createdAt: string;
}

/* ── Genérico p/ tabelas ── */
export interface Column<T> {
  key: keyof T | string;
  label: string;
  render?: (row: T) => React.ReactNode;
  className?: string;
}

export interface FilterOption {
  label: string;
  value: string;
}
