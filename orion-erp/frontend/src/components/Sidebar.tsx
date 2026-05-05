import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard, Settings, Warehouse,
  DollarSign, TrendingUp, ShoppingCart,
  Receipt, Scale, BookOpen, Briefcase, Shield, ChevronDown, ChevronRight,
  Calculator, FileText, Factory, Building2, GitBranch, BarChart3, HardDrive,
} from 'lucide-react';
import { useState } from 'react';
import clsx from 'clsx';

interface NavItem {
  label: string;
  icon: React.ReactNode;
  to?: string;
  children?: { label: string; to: string }[];
}

const navigation: NavItem[] = [
  { label: 'Dashboard', icon: <LayoutDashboard size={20} />, to: '/' },
  {
    label: 'Administração', icon: <Settings size={20} />,
    children: [
      { label: 'Empresas', to: '/admin/empresas' },
      { label: 'Filiais', to: '/admin/filiais' },
      { label: 'Usuários', to: '/admin/usuarios' },
      { label: 'Perfis', to: '/admin/perfis' },
      { label: 'Parâmetros', to: '/admin/parametros' },
      { label: 'Menus', to: '/admin/menus' },
      { label: 'Permissões', to: '/admin/permissoes' },
    ],
  },
  {
    label: 'Cadastros', icon: <BookOpen size={20} />,
    children: [
      { label: 'Clientes', to: '/cadastros/clientes' },
      { label: 'Fornecedores', to: '/cadastros/fornecedores' },
      { label: 'Produtos', to: '/cadastros/produtos' },
      { label: 'Bancos', to: '/cadastros/bancos' },
      { label: 'Categorias', to: '/cadastros/categorias' },
      { label: 'Centros de Custo', to: '/cadastros/centros-custo' },
      { label: 'Cond. Pagamento', to: '/cadastros/condicoes-pagamento' },
      { label: 'Contas Bancárias', to: '/cadastros/contas-bancarias' },
      { label: 'Grupos de Produto', to: '/cadastros/grupos-produto' },
      { label: 'Marcas', to: '/cadastros/marcas' },
      { label: 'Nat. Financeiras', to: '/cadastros/naturezas-financeiras' },
      { label: 'Subgrupos Produto', to: '/cadastros/subgrupos-produto' },
      { label: 'Tabelas de Preço', to: '/cadastros/tabelas-preco' },
      { label: 'Transportadoras', to: '/cadastros/transportadoras' },
      { label: 'Unid. Medida', to: '/cadastros/unidades-medida' },
    ],
  },
  {
    label: 'Financeiro', icon: <DollarSign size={20} />,
    children: [
      { label: 'Títulos', to: '/financeiro/titulos' },
      { label: 'Fluxo de Caixa', to: '/financeiro/fluxo-caixa' },
      { label: 'Conciliações', to: '/financeiro/conciliacoes' },
    ],
  },
  {
    label: 'Contabilidade', icon: <Calculator size={20} />,
    children: [
      { label: 'Plano de Contas', to: '/contabilidade/plano-contas' },
      { label: 'Centros Resultado', to: '/contabilidade/centros-resultado' },
      { label: 'Lançamentos', to: '/contabilidade/lancamentos' },
      { label: 'Períodos', to: '/contabilidade/periodos' },
    ],
  },
  {
    label: 'Estoque', icon: <Warehouse size={20} />,
    children: [
      { label: 'Armazéns', to: '/estoque/armazens' },
      { label: 'Movimentações', to: '/estoque/movimentacoes' },
      { label: 'Inventários', to: '/estoque/inventarios' },
      { label: 'Localizações', to: '/estoque/localizacoes' },
      { label: 'Saldos', to: '/estoque/saldos' },
    ],
  },
  {
    label: 'Compras', icon: <ShoppingCart size={20} />,
    children: [
      { label: 'Pedidos', to: '/compras/pedidos' },
      { label: 'Recebimentos', to: '/compras/recebimentos' },
    ],
  },
  {
    label: 'Vendas', icon: <Receipt size={20} />,
    children: [
      { label: 'Pedidos', to: '/vendas/pedidos' },
      { label: 'Comissões', to: '/vendas/comissoes' },
      { label: 'Tab. Preço Itens', to: '/vendas/tabela-preco-itens' },
    ],
  },
  {
    label: 'Faturamento', icon: <FileText size={20} />,
    children: [
      { label: 'Notas Fiscais', to: '/faturamento/notas-fiscais' },
      { label: 'Itens NF', to: '/faturamento/nota-fiscal-itens' },
    ],
  },
  {
    label: 'Fiscal', icon: <Scale size={20} />,
    children: [
      { label: 'Nat. Operação', to: '/fiscal/naturezas-operacao' },
      { label: 'Regras Fiscais', to: '/fiscal/regras-fiscais' },
      { label: 'CFOPs', to: '/fiscal/cfops' },
      { label: 'CSTs', to: '/fiscal/csts' },
      { label: 'NCMs', to: '/fiscal/ncms' },
    ],
  },
  {
    label: 'CRM', icon: <TrendingUp size={20} />,
    children: [
      { label: 'Leads', to: '/crm/leads' },
      { label: 'Oportunidades', to: '/crm/oportunidades' },
      { label: 'Atividades', to: '/crm/atividades' },
    ],
  },
  {
    label: 'RH', icon: <Briefcase size={20} />,
    children: [
      { label: 'Funcionários', to: '/rh/funcionarios' },
      { label: 'Cargos', to: '/rh/cargos' },
      { label: 'Departamentos', to: '/rh/departamentos' },
      { label: 'Benefícios', to: '/rh/beneficios' },
      { label: 'Func. Benefícios', to: '/rh/funcionario-beneficios' },
      { label: 'Folha Pagamento', to: '/rh/folha-pagamento' },
      { label: 'Férias', to: '/rh/ferias' },
      { label: 'Ponto Eletrônico', to: '/rh/ponto-eletronico' },
    ],
  },
  {
    label: 'PCP', icon: <Factory size={20} />,
    children: [
      { label: 'Ordens Produção', to: '/pcp/ordens-producao' },
      { label: 'Itens OP', to: '/pcp/ordem-producao-itens' },
      { label: 'Estruturas', to: '/pcp/estruturas-produto' },
      { label: 'Apontamentos', to: '/pcp/apontamentos' },
    ],
  },
  {
    label: 'Patrimônio', icon: <Building2 size={20} />,
    children: [
      { label: 'Bens', to: '/patrimonio/bens' },
      { label: 'Depreciações', to: '/patrimonio/depreciacoes' },
    ],
  },
  {
    label: 'Contratos', icon: <HardDrive size={20} />,
    children: [
      { label: 'Contratos', to: '/contratos' },
      { label: 'Parcelas', to: '/contrato-parcelas' },
    ],
  },
  {
    label: 'Workflow', icon: <GitBranch size={20} />,
    children: [
      { label: 'Definições', to: '/workflow/definicoes' },
      { label: 'Aprovações', to: '/workflow/aprovacoes' },
    ],
  },
  {
    label: 'Seguros', icon: <Shield size={20} />,
    children: [
      { label: 'Seguradoras', to: '/seguros/seguradoras' },
      { label: 'Corretoras', to: '/seguros/corretoras' },
      { label: 'Propostas', to: '/seguros/propostas' },
      { label: 'Apólices', to: '/seguros/apolices' },
      { label: 'Notificações', to: '/seguros/notificacoes' },
    ],
  },
  {
    label: 'Relatórios', icon: <BarChart3 size={20} />,
    children: [
      { label: 'Contábil', to: '/relatorios/contabil' },
      { label: 'Estoque', to: '/relatorios/estoque' },
      { label: 'Financeiro', to: '/relatorios/financeiro' },
      { label: 'Fiscal', to: '/relatorios/fiscal' },
      { label: 'RH', to: '/relatorios/rh' },
      { label: 'Vendas', to: '/relatorios/vendas' },
    ],
  },
];

function NavGroup({ item }: { item: NavItem }) {
  const [open, setOpen] = useState(false);

  if (item.to) {
    return (
      <NavLink
        to={item.to}
        end
        className={({ isActive }) =>
          clsx(
            'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
            isActive ? 'bg-primary-600 text-white' : 'text-gray-300 hover:bg-gray-700 hover:text-white',
          )
        }
      >
        {item.icon}
        <span>{item.label}</span>
      </NavLink>
    );
  }

  return (
    <div>
      <button
        onClick={() => setOpen(!open)}
        className="flex w-full items-center justify-between px-3 py-2 rounded-lg text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
      >
        <span className="flex items-center gap-3">
          {item.icon}
          <span>{item.label}</span>
        </span>
        {open ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
      </button>
      {open && (
        <div className="ml-8 mt-1 space-y-1">
          {item.children?.map((child) => (
            <NavLink
              key={child.to}
              to={child.to}
              className={({ isActive }) =>
                clsx(
                  'block px-3 py-1.5 rounded-md text-sm transition-colors',
                  isActive ? 'bg-primary-600 text-white' : 'text-gray-400 hover:bg-gray-700 hover:text-white',
                )
              }
            >
              {child.label}
            </NavLink>
          ))}
        </div>
      )}
    </div>
  );
}

export default function Sidebar() {
  return (
    <aside className="fixed inset-y-0 left-0 z-30 flex w-64 flex-col bg-gray-900">
      <div className="flex h-16 items-center justify-center border-b border-gray-700">
        <span className="text-xl font-bold text-white tracking-wider">OrionERP</span>
      </div>
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
        {navigation.map((item) => (
          <NavGroup key={item.label} item={item} />
        ))}
      </nav>
    </aside>
  );
}
