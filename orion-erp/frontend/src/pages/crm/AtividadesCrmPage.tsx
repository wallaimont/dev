import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { AtividadeCrm, Column } from '@/types';

const columns: Column<AtividadeCrm>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'titulo', label: 'Título' },
  { key: 'dataHora', label: 'Data/Hora' },
  { key: 'duracaoMinutos', label: 'Duração (min)' },
  { key: 'concluida', label: 'Concluída', render: (r) => <StatusBadge value={r.concluida ? 'SIM' : 'NÃO'} /> },
];

export default function AtividadesCrmPage() {
  return (
    <CrudPage<AtividadeCrm>
      title="Atividades CRM"
      labelSingular="Atividade"
      endpoint="/crm/atividades"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <SelectField
            label="Tipo"
            value={item.tipo ?? ''}
            onChange={(e) => set('tipo', e.target.value)}
            options={[
              { value: 'LIGACAO', label: 'Ligação' },
              { value: 'EMAIL', label: 'E-mail' },
              { value: 'REUNIAO', label: 'Reunião' },
              { value: 'VISITA', label: 'Visita' },
              { value: 'TAREFA', label: 'Tarefa' },
            ]}
            required
          />
          <InputField label="Título" value={item.titulo ?? ''} onChange={(e) => set('titulo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} />
          <InputField label="Lead ID" type="number" value={item.leadId ?? ''} onChange={(e) => set('leadId', Number(e.target.value))} />
          <InputField label="Oportunidade ID" type="number" value={item.oportunidadeId ?? ''} onChange={(e) => set('oportunidadeId', Number(e.target.value))} />
          <InputField label="Cliente ID" type="number" value={item.clienteId ?? ''} onChange={(e) => set('clienteId', Number(e.target.value))} />
          <InputField label="Responsável ID" type="number" value={item.responsavelId ?? ''} onChange={(e) => set('responsavelId', Number(e.target.value))} />
          <InputField label="Data/Hora" type="datetime-local" value={item.dataHora ?? ''} onChange={(e) => set('dataHora', e.target.value)} required />
          <InputField label="Duração (minutos)" type="number" value={item.duracaoMinutos ?? ''} onChange={(e) => set('duracaoMinutos', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
