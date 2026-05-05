import { useState } from 'react';
import api from '@/lib/api';
import { FileText, Search } from 'lucide-react';

const fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

type Tab = 'posicao' | 'movimentacoes';

interface Posicao {
  produtoId: number;
  produtoCodigo: string;
  produtoNome: string;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
}

interface Movimentacao {
  produtoId: number;
  produtoCodigo: string;
  produtoNome: string;
  tipo: string;
  quantidade: number;
  dataMovimentacao: string;
}

export default function RelatorioEstoquePage() {
  const [tab, setTab] = useState<Tab>('posicao');
  const [empresaId, setEmpresaId] = useState('1');
  const [inicio, setInicio] = useState('');
  const [fim, setFim] = useState('');
  const [posicaoData, setPosicaoData] = useState<Posicao[]>([]);
  const [movData, setMovData] = useState<Movimentacao[]>([]);
  const [loading, setLoading] = useState(false);

  async function gerar() {
    setLoading(true);
    try {
      if (tab === 'posicao') {
        const { data: res } = await api.get('/relatorios/estoque/posicao', {
          baseURL: '/api',
          params: { empresaId },
        });
        setPosicaoData(res.data ?? res);
      } else {
        const { data: res } = await api.get('/relatorios/estoque/movimentacoes', {
          baseURL: '/api',
          params: { empresaId, inicio, fim },
        });
        setMovData(res.data ?? res);
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
        <h1 className="text-2xl font-bold text-gray-800">Relatório de Estoque</h1>
      </div>

      <div className="flex gap-2">
        <button className={tabCls('posicao')} onClick={() => setTab('posicao')}>Posição de Estoque</button>
        <button className={tabCls('movimentacoes')} onClick={() => setTab('movimentacoes')}>Movimentações</button>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <div className="grid grid-cols-4 gap-4 items-end">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Empresa ID</label>
            <input type="number" value={empresaId} onChange={e => setEmpresaId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          {tab === 'movimentacoes' && (
            <>
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
            </>
          )}
          <button onClick={gerar} disabled={loading}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 flex items-center gap-2">
            <Search size={18} /> {loading ? 'Gerando...' : 'Gerar Relatório'}
          </button>
        </div>
      </div>

      {tab === 'posicao' && posicaoData.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Código</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Produto</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Quantidade</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor Unitário</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor Total</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {posicaoData.map(r => (
                <tr key={r.produtoId}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.produtoCodigo}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.produtoNome}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{r.quantidade}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{fmt.format(r.valorUnitario)}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{fmt.format(r.valorTotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === 'movimentacoes' && movData.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Código</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Produto</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Quantidade</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Data</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {movData.map((r, i) => (
                <tr key={i}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.produtoCodigo}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.produtoNome}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${r.tipo === 'ENTRADA' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {r.tipo}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{r.quantidade}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.dataMovimentacao}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
