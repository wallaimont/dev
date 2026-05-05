import { useState } from 'react';
import api from '@/lib/api';
import { FileText, Search } from 'lucide-react';

const fmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

type Tab = 'folha' | 'aniversariantes';

interface FolhaResumo {
  totalFuncionarios: number;
  totalProventos: number;
  totalDescontos: number;
  totalLiquido: number;
  totalFGTS: number;
  totalINSS: number;
  totalIRRF: number;
}

interface Aniversariante {
  funcionarioId: number;
  nome: string;
  dataNascimento: string;
  departamento: string;
}

export default function RelatorioRhPage() {
  const [tab, setTab] = useState<Tab>('folha');
  const [empresaId, setEmpresaId] = useState('1');
  const [ano, setAno] = useState(String(new Date().getFullYear()));
  const [mes, setMes] = useState(String(new Date().getMonth() + 1));
  const [folha, setFolha] = useState<FolhaResumo | null>(null);
  const [aniversariantes, setAniversariantes] = useState<Aniversariante[]>([]);
  const [loading, setLoading] = useState(false);

  async function gerar() {
    setLoading(true);
    try {
      if (tab === 'folha') {
        const { data: res } = await api.get('/relatorios/rh/folha-resumo', {
          baseURL: '/api',
          params: { empresaId, ano, mes },
        });
        setFolha(res.data ?? res);
      } else {
        const { data: res } = await api.get('/relatorios/rh/aniversariantes', {
          baseURL: '/api',
          params: { empresaId, mes },
        });
        setAniversariantes(res.data ?? res);
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
        <h1 className="text-2xl font-bold text-gray-800">Relatório de RH</h1>
      </div>

      <div className="flex gap-2">
        <button className={tabCls('folha')} onClick={() => setTab('folha')}>Resumo Folha</button>
        <button className={tabCls('aniversariantes')} onClick={() => setTab('aniversariantes')}>Aniversariantes</button>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <div className="grid grid-cols-4 gap-4 items-end">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Empresa ID</label>
            <input type="number" value={empresaId} onChange={e => setEmpresaId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          {tab === 'folha' && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Ano</label>
              <input type="number" value={ano} onChange={e => setAno(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
            </div>
          )}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mês</label>
            <input type="number" min={1} max={12} value={mes} onChange={e => setMes(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <button onClick={gerar} disabled={loading}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 flex items-center gap-2">
            <Search size={18} /> {loading ? 'Gerando...' : 'Gerar Relatório'}
          </button>
        </div>
      </div>

      {/* Folha Resumo */}
      {tab === 'folha' && folha && (
        <div className="bg-white rounded-lg shadow p-6">
          <div className="grid grid-cols-4 gap-4">
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Total Funcionários</p>
              <p className="text-xl font-bold text-blue-600">{folha.totalFuncionarios}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Total Proventos</p>
              <p className="text-xl font-bold text-green-600">{fmt.format(folha.totalProventos)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Total Descontos</p>
              <p className="text-xl font-bold text-red-600">{fmt.format(folha.totalDescontos)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">Líquido</p>
              <p className="text-xl font-bold text-gray-900">{fmt.format(folha.totalLiquido)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">FGTS</p>
              <p className="text-xl font-bold text-orange-600">{fmt.format(folha.totalFGTS)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">INSS</p>
              <p className="text-xl font-bold text-orange-600">{fmt.format(folha.totalINSS)}</p>
            </div>
            <div className="border rounded-lg p-4">
              <p className="text-sm text-gray-500">IRRF</p>
              <p className="text-xl font-bold text-orange-600">{fmt.format(folha.totalIRRF)}</p>
            </div>
          </div>
        </div>
      )}

      {/* Aniversariantes */}
      {tab === 'aniversariantes' && aniversariantes.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nome</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Data Nascimento</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Departamento</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {aniversariantes.map(r => (
                <tr key={r.funcionarioId}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.funcionarioId}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.nome}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.dataNascimento}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{r.departamento}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
