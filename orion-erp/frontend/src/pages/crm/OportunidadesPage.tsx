import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import { formatCurrency } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { Oportunidade, Column } from '@/types';

const columns: Column<Oportunidade>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'titulo', label: 'Título' },
  { key: 'clienteNome', label: 'Cliente', render: (r) => r.clienteNome ?? r.leadNome ?? '-' },
  { key: 'valorEstimado', label: 'Valor Estimado', render: (r) => formatCurrency(r.valorEstimado) },
  { key: 'probabilidade', label: 'Prob.', render: (r) => `${r.probabilidade}%` },
  { key: 'etapa', label: 'Etapa' },
  { key: 'situacao', label: 'Situação', render: (r) => <StatusBadge value={r.situacao} /> },
];

export default function OportunidadesPage() {
  return (
    <CrudPage<Oportunidade>
      title="Oportunidades"
      labelSingular="Oportunidade"
      endpoint="/crm/oportunidades"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Título" value={item.titulo ?? ''} onChange={(e) => set('titulo', e.target.value)} required />
          <InputField label="ID Lead" type="number" value={item.leadId ?? ''} onChange={(e) => set('leadId', Number(e.target.value))} />
          <InputField label="ID Cliente" type="number" value={item.clienteId ?? ''} onChange={(e) => set('clienteId', Number(e.target.value))} />
          <InputField label="Valor Estimado" type="number" step="0.01" value={item.valorEstimado ?? ''} onChange={(e) => set('valorEstimado', Number(e.target.value))} />
          <InputField label="Data Prev. Fechamento" type="date" value={item.dataPrevisaoFechamento ?? ''} onChange={(e) => set('dataPrevisaoFechamento', e.target.value)} />
        </div>
      )}
    />
  );
}
