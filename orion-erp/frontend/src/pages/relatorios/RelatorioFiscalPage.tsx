import { useState } from 'react';
import api from '@/lib/api';
import { FileText, Search } from 'lucide-react';

const fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

interface FaturamentoData {
  periodo: string;
  totalNotas: number;
  valorTotalProdutos: number;
  valorTotalServicos: number;
  valorTotalImpostos: number;
  valorTotalNF: number;
}

export default function RelatorioFiscalPage() {
  const [empresaId, setEmpresaId] = useState('1');
  const [inicio, setInicio] = useState('');
  const [fim, setFim] = useState('');
  const [data, setData] = useState<FaturamentoData | null>(null);
  const [loading, setLoading] = useState(false);

  async function gerar() {
    setLoading(true);
    try {
      const { data: res } = await api.get('/relatorios/fiscal/faturamento', {
        baseURL: '/api',
        params: { empresaId, inicio, fim },
      });
      setData(res.data ?? res);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <FileText className="text-blue-600" size={28} />
        <h1 className="text-2xl font-bold text-gray-800">Relatório Fiscal — Faturamento</h1>
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

      {data && (
        <div className="bg-white rounded-lg shadow p-6 space-y-6">
          {data.periodo && (
            <p className="text-sm text-gray-500">Período: {data.periodo}</p>
          )}

          <div className="grid grid-cols-3 gap-4">
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Total de Notas</p>
              <p className="text-xl font-bold text-blue-600">{data.totalNotas}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Valor Total NF</p>
              <p className="text-xl font-bold text-green-600">{fmt.format(data.valorTotalNF)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Total Impostos</p>
              <p className="text-xl font-bold text-red-600">{fmt.format(data.valorTotalImpostos)}</p>
            </div>
          </div>

          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descrição</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {[
                { label: 'Valor Total Produtos', value: data.valorTotalProdutos },
                { label: 'Valor Total Serviços', value: data.valorTotalServicos },
                { label: 'Valor Total Impostos', value: data.valorTotalImpostos },
                { label: 'Valor Total NF', value: data.valorTotalNF },
              ].map((r, i) => (
                <tr key={i}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.label}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 text-right">{fmt.format(r.value)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
