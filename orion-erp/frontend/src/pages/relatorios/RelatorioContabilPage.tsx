import { useState } from 'react';
import api from '@/lib/api';
import { FileText, Search } from 'lucide-react';

const fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

interface DreData {
  periodo: string;
  receitas: number;
  deducoes: number;
  custos: number;
  despesas: number;
  resultadoOperacional: number;
  resultadoLiquido: number;
}

export default function RelatorioContabilPage() {
  const [empresaId, setEmpresaId] = useState('1');
  const [inicio, setInicio] = useState('');
  const [fim, setFim] = useState('');
  const [data, setData] = useState<DreData | null>(null);
  const [loading, setLoading] = useState(false);

  async function gerar() {
    setLoading(true);
    try {
      const { data: res } = await api.get('/relatorios/contabil/dre', {
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

  const rows = data
    ? [
        { label: 'Receita Bruta', value: data.receitas, bold: false },
        { label: '(-) Deduções', value: -data.deducoes, bold: false },
        { label: '(=) Receita Líquida', value: data.receitas - data.deducoes, bold: true },
        { label: '(-) Custos', value: -data.custos, bold: false },
        { label: '(=) Lucro Bruto', value: data.receitas - data.deducoes - data.custos, bold: true },
        { label: '(-) Despesas Operacionais', value: -data.despesas, bold: false },
        { label: '(=) Resultado Operacional', value: data.resultadoOperacional, bold: true },
        { label: '(=) Resultado Líquido', value: data.resultadoLiquido, bold: true },
      ]
    : [];

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <FileText className="text-blue-600" size={28} />
        <h1 className="text-2xl font-bold text-gray-800">Relatório Contábil — DRE Simplificada</h1>
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
        <div className="bg-white rounded-lg shadow p-6">
          {data.periodo && (
            <p className="text-sm text-gray-500 mb-4">Período: {data.periodo}</p>
          )}
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descrição</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Valor</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {rows.map((r, i) => (
                <tr key={i} className={r.bold ? 'bg-gray-50' : ''}>
                  <td className={`px-6 py-4 whitespace-nowrap text-sm ${r.bold ? 'font-semibold text-gray-900' : 'text-gray-700'}`}>
                    {r.label}
                  </td>
                  <td className={`px-6 py-4 whitespace-nowrap text-sm text-right ${r.bold ? 'font-semibold text-gray-900' : 'text-gray-700'} ${r.value < 0 ? 'text-red-600' : ''}`}>
                    {fmt.format(r.value)}
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
