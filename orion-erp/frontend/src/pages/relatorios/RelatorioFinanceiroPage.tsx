import { useState } from 'react';
import api from '@/lib/api';
import { FileText, Search } from 'lucide-react';

const fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

type Tab = 'resumo' | 'vencidas' | 'fluxo';

interface Resumo {
  totalReceber: number;
  totalPagar: number;
  saldo: number;
  titulosVencidos: number;
  titulosAVencer: number;
}

interface ContaVencida {
  tituloId: number;
  tipo: string;
  clienteFornecedor: string;
  valor: number;
  vencimento: string;
  diasAtraso: number;
}

interface FluxoCaixa {
  data: string;
  entradas: number;
  saidas: number;
  saldo: number;
}

export default function RelatorioFinanceiroPage() {
  const [tab, setTab] = useState<Tab>('resumo');
  const [empresaId, setEmpresaId] = useState('1');
  const [inicio, setInicio] = useState('');
  const [fim, setFim] = useState('');
  const [resumo, setResumo] = useState<Resumo | null>(null);
  const [vencidas, setVencidas] = useState<ContaVencida[]>([]);
  const [fluxo, setFluxo] = useState<FluxoCaixa[]>([]);
  const [loading, setLoading] = useState(false);

  async function gerar() {
    setLoading(true);
    try {
      if (tab === 'resumo') {
        const { data: res } = await api.get('/relatorios/financeiro/resumo', {
          baseURL: '/api',
          params: { empresaId, inicio, fim },
        });
        setResumo(res.data ?? res);
      } else if (tab === 'vencidas') {
        const { data: res } = await api.get('/relatorios/financeiro/contas-vencidas', {
          baseURL: '/api',
          params: { empresaId },
        });
        setVencidas(res.data ?? res);
      } else {
        const { data: res } = await api.get('/relatorios/financeiro/fluxo-caixa', {
          baseURL: '/api',
          params: { empresaId, inicio, fim },
        });
        setFluxo(res.data ?? res);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  const tabCls = (t: Tab) =>
    `px-4 py-2 rounded-lg ${tab === t ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'}`;

  const needsDates = tab === 'resumo' || tab === 'fluxo';

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <FileText className="text-blue-600" size={28} />
        <h1 className="text-2xl font-bold text-gray-800">Relatório Financeiro</h1>
      </div>

      <div className="flex gap-2">
        <button className={tabCls('resumo')} onClick={() => setTab('resumo')}>Resumo</button>
        <button className={tabCls('vencidas')} onClick={() => setTab('vencidas')}>Contas Vencidas</button>
        <button className={tabCls('fluxo')} onClick={() => setTab('fluxo')}>Fluxo de Caixa</button>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <div className="grid grid-cols-4 gap-4 items-end">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Empresa ID</label>
            <input type="number" value={empresaId} onChange={e => setEmpresaId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          {needsDates && (
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

      {/* Resumo */}
      {tab === 'resumo' && resumo && (
        <div className="bg-white rounded-lg shadow p-6">
          <div className="grid grid-cols-3 gap-4">
            {([
              { label: 'Total a Receber', value: resumo.totalReceber, color: 'text-green-600' },
              { label: 'Total a Pagar', value: resumo.totalPagar, color: 'text-red-600' },
              { label: 'Saldo', value: resumo.saldo, color: resumo.saldo >= 0 ? 'text-green-600' : 'text-red-600' },
              { label: 'Títulos Vencidos', value: resumo.titulosVencidos, color: 'text-orange-600', isCount: true },
              { label: 'Títulos a Vencer', value: resumo.titulosAVencer, color: 'text-blue-600', isCount: true },
            ] as const).map((c, i) => (
              <div key={i} className="border rounded-lg p-4">
                <p className="text-sm text-gray-500">{c.label}</p>
                <p className={`text-xl font-bold ${c.color}`}>
                  {'isCount' in c && c.isCount ? c.value : fmt.format(c.value)}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Contas Vencidas */}
      {tab === 'vencidas' && vencidas.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Cliente/Fornecedor</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Vencimento</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Dias Atraso</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {vencidas.map(r => (
                <tr key={r.tituloId}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.tituloId}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.tipo}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.clienteFornecedor}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{fmt.format(r.valor)}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.vencimento}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-red-600 text-right font-medium">{r.diasAtraso}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Fluxo de Caixa */}
      {tab === 'fluxo' && fluxo.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Data</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Entradas</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Saídas</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Saldo</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {fluxo.map((r, i) => (
                <tr key={i}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.data}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-green-600 text-right">{fmt.format(r.entradas)}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-red-600 text-right">{fmt.format(r.saidas)}</td>
                  <td className={`px-6 py-4 whitespace-nowrap text-sm text-right font-medium ${r.saldo >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                    {fmt.format(r.saldo)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
