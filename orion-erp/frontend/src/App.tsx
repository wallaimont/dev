import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import Layout from '@/components/Layout';
import LoginPage from '@/pages/LoginPage';
import DashboardPage from '@/pages/DashboardPage';

/* Admin */
import EmpresasPage from '@/pages/admin/EmpresasPage';
import FiliaisPage from '@/pages/admin/FiliaisPage';
import UsuariosPage from '@/pages/admin/UsuariosPage';
import PerfisPage from '@/pages/admin/PerfisPage';
import ParametrosPage from '@/pages/admin/ParametrosPage';
import MenusPage from '@/pages/admin/MenusPage';
import PermissoesPage from '@/pages/admin/PermissoesPage';

/* Cadastros */
import ClientesPage from '@/pages/cadastros/ClientesPage';
import FornecedoresPage from '@/pages/cadastros/FornecedoresPage';
import ProdutosPage from '@/pages/cadastros/ProdutosPage';
import BancosPage from '@/pages/cadastros/BancosPage';
import CategoriasPage from '@/pages/cadastros/CategoriasPage';
import CentrosCustoPage from '@/pages/cadastros/CentrosCustoPage';
import CondicoesPagamentoPage from '@/pages/cadastros/CondicoesPagamentoPage';
import ContasBancariasPage from '@/pages/cadastros/ContasBancariasPage';
import GruposProdutoPage from '@/pages/cadastros/GruposProdutoPage';
import MarcasPage from '@/pages/cadastros/MarcasPage';
import NaturezasFinanceirasPage from '@/pages/cadastros/NaturezasFinanceirasPage';
import SubgruposProdutoPage from '@/pages/cadastros/SubgruposProdutoPage';
import TabelasPrecoPage from '@/pages/cadastros/TabelasPrecoPage';
import TransportadorasPage from '@/pages/cadastros/TransportadorasPage';
import UnidadesMedidaPage from '@/pages/cadastros/UnidadesMedidaPage';

/* Financeiro */
import TitulosPage from '@/pages/financeiro/TitulosPage';
import FluxoCaixaPage from '@/pages/financeiro/FluxoCaixaPage';
import ConciliacoesBancariasPage from '@/pages/financeiro/ConciliacoesBancariasPage';

/* Contabilidade */
import PlanoContasPage from '@/pages/contabilidade/PlanoContasPage';
import CentrosResultadoPage from '@/pages/contabilidade/CentrosResultadoPage';
import LancamentosContabeisPage from '@/pages/contabilidade/LancamentosContabeisPage';
import PeriodosContabeisPage from '@/pages/contabilidade/PeriodosContabeisPage';

/* Estoque */
import ArmazensPage from '@/pages/estoque/ArmazensPage';
import MovimentacoesPage from '@/pages/estoque/MovimentacoesPage';
import InventariosPage from '@/pages/estoque/InventariosPage';
import LocalizacoesPage from '@/pages/estoque/LocalizacoesPage';
import SaldosEstoquePage from '@/pages/estoque/SaldosEstoquePage';

/* Compras */
import PedidosCompraPage from '@/pages/compras/PedidosCompraPage';
import RecebimentosPage from '@/pages/compras/RecebimentosPage';

/* Vendas */
import PedidosVendaPage from '@/pages/vendas/PedidosVendaPage';
import ComissoesPage from '@/pages/vendas/ComissoesPage';
import TabelaPrecoItensPage from '@/pages/vendas/TabelaPrecoItensPage';

/* Faturamento */
import NotasFiscaisPage from '@/pages/faturamento/NotasFiscaisPage';
import NotaFiscalItensPage from '@/pages/faturamento/NotaFiscalItensPage';

/* Fiscal */
import NaturezasOperacaoPage from '@/pages/fiscal/NaturezasOperacaoPage';
import RegrasFiscaisPage from '@/pages/fiscal/RegrasFiscaisPage';
import CfopsPage from '@/pages/fiscal/CfopsPage';
import CstsPage from '@/pages/fiscal/CstsPage';
import NcmsPage from '@/pages/fiscal/NcmsPage';

/* CRM */
import LeadsPage from '@/pages/crm/LeadsPage';
import OportunidadesPage from '@/pages/crm/OportunidadesPage';
import AtividadesCrmPage from '@/pages/crm/AtividadesCrmPage';

/* RH */
import FuncionariosPage from '@/pages/rh/FuncionariosPage';
import CargosPage from '@/pages/rh/CargosPage';
import DepartamentosRhPage from '@/pages/rh/DepartamentosRhPage';
import BeneficiosPage from '@/pages/rh/BeneficiosPage';
import FuncionarioBeneficiosPage from '@/pages/rh/FuncionarioBeneficiosPage';
import FolhaPagamentoPage from '@/pages/rh/FolhaPagamentoPage';
import FeriasPage from '@/pages/rh/FeriasPage';
import PontoEletronicoPage from '@/pages/rh/PontoEletronicoPage';

/* PCP */
import OrdensProducaoPage from '@/pages/pcp/OrdensProducaoPage';
import OrdemProducaoItensPage from '@/pages/pcp/OrdemProducaoItensPage';
import EstruturasProdutoPage from '@/pages/pcp/EstruturasProdutoPage';
import ApontamentosProducaoPage from '@/pages/pcp/ApontamentosProducaoPage';

/* Patrimônio */
import BensPatrimoniaisPage from '@/pages/patrimonio/BensPatrimoniaisPage';
import DepreciacoesPage from '@/pages/patrimonio/DepreciacoesPage';

/* Contratos */
import ContratosPage from '@/pages/contratos/ContratosPage';
import ContratoParcelasPage from '@/pages/contratos/ContratoParcelasPage';

/* Workflow */
import WorkflowDefinicoesPage from '@/pages/workflow/WorkflowDefinicoesPage';
import WorkflowAprovacoesPage from '@/pages/workflow/WorkflowAprovacoesPage';

/* Seguros */
import SeguradorasPage from '@/pages/seguros/SeguradorasPage';
import CorretorasPage from '@/pages/seguros/CorretorasPage';
import PropostasPage from '@/pages/seguros/PropostasPage';
import ApolicesPage from '@/pages/seguros/ApolicesPage';
import NotificacoesPage from '@/pages/seguros/NotificacoesPage';

/* Relatórios */
import RelatorioContabilPage from '@/pages/relatorios/RelatorioContabilPage';
import RelatorioEstoquePage from '@/pages/relatorios/RelatorioEstoquePage';
import RelatorioFinanceiroPage from '@/pages/relatorios/RelatorioFinanceiroPage';
import RelatorioFiscalPage from '@/pages/relatorios/RelatorioFiscalPage';
import RelatorioRhPage from '@/pages/relatorios/RelatorioRhPage';
import RelatorioVendasPage from '@/pages/relatorios/RelatorioVendasPage';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, loading } = useAuth();
  if (loading) return <div className="flex h-screen items-center justify-center text-gray-400">Carregando...</div>;
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

function AppRoutes() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return <div className="flex h-screen items-center justify-center text-gray-400">Carregando...</div>;

  return (
    <Routes>
      <Route path="/login" element={isAuthenticated ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<DashboardPage />} />

        {/* Admin */}
        <Route path="admin/empresas" element={<EmpresasPage />} />
        <Route path="admin/filiais" element={<FiliaisPage />} />
        <Route path="admin/usuarios" element={<UsuariosPage />} />
        <Route path="admin/perfis" element={<PerfisPage />} />
        <Route path="admin/parametros" element={<ParametrosPage />} />
        <Route path="admin/menus" element={<MenusPage />} />
        <Route path="admin/permissoes" element={<PermissoesPage />} />

        {/* Cadastros */}
        <Route path="cadastros/clientes" element={<ClientesPage />} />
        <Route path="cadastros/fornecedores" element={<FornecedoresPage />} />
        <Route path="cadastros/produtos" element={<ProdutosPage />} />
        <Route path="cadastros/bancos" element={<BancosPage />} />
        <Route path="cadastros/categorias" element={<CategoriasPage />} />
        <Route path="cadastros/centros-custo" element={<CentrosCustoPage />} />
        <Route path="cadastros/condicoes-pagamento" element={<CondicoesPagamentoPage />} />
        <Route path="cadastros/contas-bancarias" element={<ContasBancariasPage />} />
        <Route path="cadastros/grupos-produto" element={<GruposProdutoPage />} />
        <Route path="cadastros/marcas" element={<MarcasPage />} />
        <Route path="cadastros/naturezas-financeiras" element={<NaturezasFinanceirasPage />} />
        <Route path="cadastros/subgrupos-produto" element={<SubgruposProdutoPage />} />
        <Route path="cadastros/tabelas-preco" element={<TabelasPrecoPage />} />
        <Route path="cadastros/transportadoras" element={<TransportadorasPage />} />
        <Route path="cadastros/unidades-medida" element={<UnidadesMedidaPage />} />

        {/* Financeiro */}
        <Route path="financeiro/titulos" element={<TitulosPage />} />
        <Route path="financeiro/fluxo-caixa" element={<FluxoCaixaPage />} />
        <Route path="financeiro/conciliacoes" element={<ConciliacoesBancariasPage />} />

        {/* Contabilidade */}
        <Route path="contabilidade/plano-contas" element={<PlanoContasPage />} />
        <Route path="contabilidade/centros-resultado" element={<CentrosResultadoPage />} />
        <Route path="contabilidade/lancamentos" element={<LancamentosContabeisPage />} />
        <Route path="contabilidade/periodos" element={<PeriodosContabeisPage />} />

        {/* Estoque */}
        <Route path="estoque/armazens" element={<ArmazensPage />} />
        <Route path="estoque/movimentacoes" element={<MovimentacoesPage />} />
        <Route path="estoque/inventarios" element={<InventariosPage />} />
        <Route path="estoque/localizacoes" element={<LocalizacoesPage />} />
        <Route path="estoque/saldos" element={<SaldosEstoquePage />} />

        {/* Compras */}
        <Route path="compras/pedidos" element={<PedidosCompraPage />} />
        <Route path="compras/recebimentos" element={<RecebimentosPage />} />

        {/* Vendas */}
        <Route path="vendas/pedidos" element={<PedidosVendaPage />} />
        <Route path="vendas/comissoes" element={<ComissoesPage />} />
        <Route path="vendas/tabela-preco-itens" element={<TabelaPrecoItensPage />} />

        {/* Faturamento */}
        <Route path="faturamento/notas-fiscais" element={<NotasFiscaisPage />} />
        <Route path="faturamento/nota-fiscal-itens" element={<NotaFiscalItensPage />} />

        {/* Fiscal */}
        <Route path="fiscal/naturezas-operacao" element={<NaturezasOperacaoPage />} />
        <Route path="fiscal/regras-fiscais" element={<RegrasFiscaisPage />} />
        <Route path="fiscal/cfops" element={<CfopsPage />} />
        <Route path="fiscal/csts" element={<CstsPage />} />
        <Route path="fiscal/ncms" element={<NcmsPage />} />

        {/* CRM */}
        <Route path="crm/leads" element={<LeadsPage />} />
        <Route path="crm/oportunidades" element={<OportunidadesPage />} />
        <Route path="crm/atividades" element={<AtividadesCrmPage />} />

        {/* RH */}
        <Route path="rh/funcionarios" element={<FuncionariosPage />} />
        <Route path="rh/cargos" element={<CargosPage />} />
        <Route path="rh/departamentos" element={<DepartamentosRhPage />} />
        <Route path="rh/beneficios" element={<BeneficiosPage />} />
        <Route path="rh/funcionario-beneficios" element={<FuncionarioBeneficiosPage />} />
        <Route path="rh/folha-pagamento" element={<FolhaPagamentoPage />} />
        <Route path="rh/ferias" element={<FeriasPage />} />
        <Route path="rh/ponto-eletronico" element={<PontoEletronicoPage />} />

        {/* PCP */}
        <Route path="pcp/ordens-producao" element={<OrdensProducaoPage />} />
        <Route path="pcp/ordem-producao-itens" element={<OrdemProducaoItensPage />} />
        <Route path="pcp/estruturas-produto" element={<EstruturasProdutoPage />} />
        <Route path="pcp/apontamentos" element={<ApontamentosProducaoPage />} />

        {/* Patrimônio */}
        <Route path="patrimonio/bens" element={<BensPatrimoniaisPage />} />
        <Route path="patrimonio/depreciacoes" element={<DepreciacoesPage />} />

        {/* Contratos */}
        <Route path="contratos" element={<ContratosPage />} />
        <Route path="contrato-parcelas" element={<ContratoParcelasPage />} />

        {/* Workflow */}
        <Route path="workflow/definicoes" element={<WorkflowDefinicoesPage />} />
        <Route path="workflow/aprovacoes" element={<WorkflowAprovacoesPage />} />

        {/* Seguros */}
        <Route path="seguros/seguradoras" element={<SeguradorasPage />} />
        <Route path="seguros/corretoras" element={<CorretorasPage />} />
        <Route path="seguros/propostas" element={<PropostasPage />} />
        <Route path="seguros/apolices" element={<ApolicesPage />} />
        <Route path="seguros/notificacoes" element={<NotificacoesPage />} />

        {/* Relatórios */}
        <Route path="relatorios/contabil" element={<RelatorioContabilPage />} />
        <Route path="relatorios/estoque" element={<RelatorioEstoquePage />} />
        <Route path="relatorios/financeiro" element={<RelatorioFinanceiroPage />} />
        <Route path="relatorios/fiscal" element={<RelatorioFiscalPage />} />
        <Route path="relatorios/rh" element={<RelatorioRhPage />} />
        <Route path="relatorios/vendas" element={<RelatorioVendasPage />} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
