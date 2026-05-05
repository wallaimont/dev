import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { WorkflowAprovacao, Column } from '@/types';

const columns: Column<WorkflowAprovacao>[] = [
  { key: 'id', label: 'ID' },
  { key: 'workflowDefinicaoId', label: 'Workflow' },
  { key: 'entidade', label: 'Entidade' },
  { key: 'entidadeId', label: 'Entidade ID' },
  { key: 'nivel', label: 'Nível' },
  { key: 'decisao', label: 'Decisão' },
  { key: 'status', label: 'Status', render: (v) => <StatusBadge value={v} /> },
];

export default function WorkflowAprovacoesPage() {
  return (
    <CrudPage<WorkflowAprovacao>
      title="Aprovações de Workflow"
      labelSingular="Aprovação de Workflow"
      endpoint="/workflow/aprovacoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Workflow Definição ID" type="number" value={item.workflowDefinicaoId} onChange={(v) => set({ ...item, workflowDefinicaoId: v })} />
          <InputField label="Entidade" value={item.entidade} onChange={(v) => set({ ...item, entidade: v })} />
          <InputField label="Entidade ID" type="number" value={item.entidadeId} onChange={(v) => set({ ...item, entidadeId: v })} />
          <InputField label="Nível" type="number" value={item.nivel} onChange={(v) => set({ ...item, nivel: v })} />
          <InputField label="Aprovador ID" type="number" value={item.aprovadorId} onChange={(v) => set({ ...item, aprovadorId: v })} />
          <SelectField label="Decisão" value={item.decisao} onChange={(v) => set({ ...item, decisao: v })} options={['PENDENTE', 'APROVADO', 'REPROVADO']} />
          <InputField label="Justificativa" value={item.justificativa} onChange={(v) => set({ ...item, justificativa: v })} />
          <SelectField label="Status" value={item.status} onChange={(v) => set({ ...item, status: v })} options={['PENDENTE', 'APROVADO', 'REPROVADO', 'CANCELADO']} />
        </div>
      )}
    />
  );
}
