import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { OrdemProducao, Column } from '@/types';

const columns: Column<OrdemProducao>[] = [
  { key: 'id', label: 'ID' },
  { key: 'numero', label: 'Número' },
  { key: 'produtoId', label: 'Produto ID' },
  { key: 'quantidade', label: 'Quantidade' },
  { key: 'dataInicio', label: 'Data Início' },
  { key: 'dataPrevisaoFim', label: 'Previsão Fim' },
  { key: 'status', label: 'Status', render: (v) => <StatusBadge value={v} /> },
  { key: 'prioridade', label: 'Prioridade' },
];

export default function OrdensProducaoPage() {
  return (
    <CrudPage<OrdemProducao>
      title="Ordens de Produção"
      labelSingular="Ordem de Produção"
      endpoint="/pcp/ordens-producao"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Número" value={item.numero} onChange={(v) => set({ ...item, numero: v })} />
          <InputField label="Produto ID" type="number" value={item.produtoId} onChange={(v) => set({ ...item, produtoId: v })} />
          <InputField label="Quantidade" type="number" value={item.quantidade} onChange={(v) => set({ ...item, quantidade: v })} />
          <InputField label="Data Início" type="date" value={item.dataInicio} onChange={(v) => set({ ...item, dataInicio: v })} />
          <InputField label="Data Previsão Fim" type="date" value={item.dataPrevisaoFim} onChange={(v) => set({ ...item, dataPrevisaoFim: v })} />
          <InputField label="Data Fim" type="date" value={item.dataFim} onChange={(v) => set({ ...item, dataFim: v })} />
          <SelectField label="Status" value={item.status} onChange={(v) => set({ ...item, status: v })} options={['PLANEJADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA']} />
          <SelectField label="Prioridade" value={item.prioridade} onChange={(v) => set({ ...item, prioridade: v })} options={['BAIXA', 'NORMAL', 'ALTA', 'URGENTE']} />
          <InputField label="Centro Custo ID" type="number" value={item.centroCustoId} onChange={(v) => set({ ...item, centroCustoId: v })} />
          <InputField label="Observação" value={item.observacao} onChange={(v) => set({ ...item, observacao: v })} />
        </div>
      )}
    />
  );
}
