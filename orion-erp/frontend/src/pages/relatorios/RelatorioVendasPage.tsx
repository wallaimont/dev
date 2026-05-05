import { useState } from 'react';
import api from '@/lib/api';
import { FileText, Search } from 'lucide-react';

const fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

type Tab = 'periodo' | 'comissoes';

interface VendasPeriodo {
  periodo: string;
  totalPedidos: number;
  valorTotalPedidos: number;
  ticketMedio: number;
  pedidosCancelados: number;
}

interface Comissao {
  vendedorId: number;
  vendedorNome: string;
  totalVendas: number;
  valorTotal: number;
  comissaoPercentual: number;
  valorComissao: number;
}

export default function RelatorioVendasPage() {
  const [tab, setTab] = useState<Tab>('periodo');
  const [empresaId, setEmpresaId] = useState('1');
  const [inicio, setInicio] = useState('');
  const [fim, setFim] = useState('');
  const [vendas, setVendas] = useState<VendasPeriodo | null>(null);
  const [comissoes, setComissoes] = useState<Comissao[]>([]);
  const [loading, setLoading] = useState(false);

  async function gerar() {
    setLoading(true);
    try {
      if (tab === 'periodo') {
        const { data: res } = await api.get('/relatorios/vendas/periodo', {
          baseURL: '/api',
          params: { empresaId, inicio, fim },
        });
        setVendas(res.data ?? res);
      } else {
        const { data: res } = await api.get('/relatorios/vendas/comissoes', {
          baseURL: '/api',
          params: { empresaId, inicio, fim },
        });
        setComissoes(res.data ?? res);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  const tabCls = (t: Tab) =>
    `px-4 py-2 rounded-lg ${tab === t ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'}`;

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <FileText className="text-blue-600" size={28} />
        <h1 className="text-2xl font-bold text-gray-800">Relatório de Vendas</h1>
      </div>

      <div className="flex gap-2">
        <button className={tabCls('periodo')} onClick={() => setTab('periodo')}>Vendas por Período</button>
        <button className={tabCls('comissoes')} onClick={() => setTab('comissoes')}>Comissões por Vendedor</button>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <div className="grid grid-cols-4 gap-4 items-end">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Empresa ID</label>
            <input type="number" value={empresaId} onChange={e => setEmpresaId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Data Início</label>
            <input type="date" value={inicio} onChange={e => setInicio(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Data Fim</label>
            <input type="date" value={fim} onChange={e => setFim(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <button onClick={gerar} disabled={loading}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 flex items-center gap-2">
            <Search size={18} /> {loading ? 'Gerando...' : 'Gerar Relatório'}
          </button>
        </div>
      </div>

      {/* Vendas por Período */}
      {tab === 'periodo' && vendas && (
        <div className="bg-white rounded-lg shadow p-6">
          {vendas.periodo && (
            <p className="text-sm text-gray-500 mb-4">Período: {vendas.periodo}</p>
          )}
          <div className="grid grid-cols-4 gap-4">
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Total Pedidos</p>
              <p className="text-xl font-bold text-blue-600">{vendas.totalPedidos}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Valor Total</p>
              <p className="text-xl font-bold text-green-600">{fmt.format(vendas.valorTotalPedidos)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Ticket Médio</p>
              <p className="text-xl font-bold text-gray-900">{fmt.format(vendas.ticketMedio)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Cancelados</p>
              <p className="text-xl font-bold text-red-600">{vendas.pedidosCancelados}</p>
            </div>
          </div>
        </div>
      )}

      {/* Comissões */}
      {tab === 'comissoes' && comissoes.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Vendedor</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Qtd Vendas</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor Total</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">% Comissão</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor Comissão</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {comissoes.map(r => (
                <tr key={r.vendedorId}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.vendedorId}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.vendedorNome}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{r.totalVendas}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{fmt.format(r.valorTotal)}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{r.comissaoPercentual}%</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-green-600 text-right font-medium">{fmt.format(r.valorComissao)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
