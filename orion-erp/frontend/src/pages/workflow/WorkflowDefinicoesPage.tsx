import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { WorkflowDefinicao, Column } from '@/types';

const columns: Column<WorkflowDefinicao>[] = [
  { key: 'id', label: 'ID' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'modulo', label: 'Módulo' },
  { key: 'entidade', label: 'Entidade' },
  { key: 'ativo', label: 'Status', render: (v) => <StatusBadge value={v} /> },
];

export default function WorkflowDefinicoesPage() {
  return (
    <CrudPage<WorkflowDefinicao>
      title="Definições de Workflow"
      labelSingular="Definição de Workflow"
      endpoint="/workflow/definicoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo} onChange={(v) => set({ ...item, codigo: v })} />
          <InputField label="Nome" value={item.nome} onChange={(v) => set({ ...item, nome: v })} />
          <InputField label="Descrição" value={item.descricao} onChange={(v) => set({ ...item, descricao: v })} />
          <InputField label="Módulo" value={item.modulo} onChange={(v) => set({ ...item, modulo: v })} />
          <InputField label="Entidade" value={item.entidade} onChange={(v) => set({ ...item, entidade: v })} />
        </div>
      )}
    />
  );
}
