import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import type { ApontamentoProducao, Column } from '@/types';

const columns: Column<ApontamentoProducao>[] = [
  { key: 'id', label: 'ID' },
  { key: 'ordemProducaoId', label: 'Ordem ID' },
  { key: 'funcionarioId', label: 'Funcionário ID' },
  { key: 'dataInicio', label: 'Data Início' },
  { key: 'dataFim', label: 'Data Fim' },
  { key: 'qtdProduzida', label: 'Qtd Produzida' },
  { key: 'qtdRejeitada', label: 'Qtd Rejeitada' },
];

export default function ApontamentosProducaoPage() {
  return (
    <CrudPage<ApontamentoProducao>
      title="Apontamentos de Produção"
      labelSingular="Apontamento de Produção"
      endpoint="/pcp/apontamentos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Ordem Produção ID" type="number" value={item.ordemProducaoId} onChange={(v) => set({ ...item, ordemProducaoId: v })} />
          <InputField label="Funcionário ID" type="number" value={item.funcionarioId} onChange={(v) => set({ ...item, funcionarioId: v })} />
          <InputField label="Data Início" type="datetime-local" value={item.dataInicio} onChange={(v) => set({ ...item, dataInicio: v })} />
          <InputField label="Data Fim" type="datetime-local" value={item.dataFim} onChange={(v) => set({ ...item, dataFim: v })} />
          <InputField label="Qtd Produzida" type="number" value={item.qtdProduzida} onChange={(v) => set({ ...item, qtdProduzida: v })} />
          <InputField label="Qtd Rejeitada" type="number" value={item.qtdRejeitada} onChange={(v) => set({ ...item, qtdRejeitada: v })} />
          <InputField label="Máquina" value={item.maquina} onChange={(v) => set({ ...item, maquina: v })} />
          <InputField label="Observação" value={item.observacao} onChange={(v) => set({ ...item, observacao: v })} />
        </div>
      )}
    />
  );
}
