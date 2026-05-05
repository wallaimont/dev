import { useEffect, useState } from 'react';
import PageHeader from '@/components/PageHeader';
import { formatCurrency, formatDate } from '@/lib/formatters';
import api from '@/lib/api';
import type { FluxoCaixa, ApiResponse } from '@/types';

export default function FluxoCaixaPage() {
  const [items, setItems] = useState<FluxoCaixa[]>([]);
  const [loading, setLoading] = useState(false);
  const [dataInicio, setDataInicio] = useState('');
  const [dataFim, setDataFim] = useState('');

  const load = async () => {
    setLoading(true);
    try {
      const params: Record<string, string> = {};
      if (dataInicio) params.dataInicio = dataInicio;
      if (dataFim) params.dataFim = dataFim;
      const { data } = await api.get<ApiResponse<FluxoCaixa[]>>('/financeiro/fluxo-caixa', { params });
      setItems(data.data ?? []);
    } catch {
      // handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  return (
    <div>
      <PageHeader title="Fluxo de Caixa" subtitle="Visualização consolidada" />
      <div className="mb-4 flex gap-4 items-end">
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Início</label>
          <input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} className="rounded-lg border px-3 py-2 text-sm" />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Fim</label>
          <input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} className="rounded-lg border px-3 py-2 text-sm" />
        </div>
        <button onClick={load} className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700">
          Filtrar
        </button>
      </div>
      <div className="overflow-hidden rounded-lg border bg-white shadow-sm">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">Data</th>
              <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">Tipo</th>
              <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">Descrição</th>
              <th className="px-4 py-3 text-right text-xs font-semibold uppercase text-gray-500">Valor</th>
              <th className="px-4 py-3 text-right text-xs font-semibold uppercase text-gray-500">Saldo Acumulado</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-400">Carregando...</td></tr>
            ) : items.length === 0 ? (
              <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-400">Nenhum lançamento</td></tr>
            ) : items.map((item) => (
              <tr key={item.id}>
                <td className="px-4 py-3 text-sm">{formatDate(item.data)}</td>
                <td className="px-4 py-3 text-sm">{item.tipo}</td>
                <td className="px-4 py-3 text-sm">{item.descricao}</td>
                <td className={`px-4 py-3 text-sm text-right font-medium ${item.valor >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  {formatCurrency(item.valor)}
                </td>
                <td className="px-4 py-3 text-sm text-right font-medium">{formatCurrency(item.saldoAcumulado)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
