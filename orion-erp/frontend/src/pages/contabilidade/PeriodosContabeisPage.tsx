import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { PeriodoContabil, Column } from '@/types';

const columns: Column<PeriodoContabil>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'ano', label: 'Ano' },
  { key: 'mes', label: 'Mês' },
  { key: 'dataInicio', label: 'Data Início' },
  { key: 'dataFim', label: 'Data Fim' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function PeriodosContabeisPage() {
  return (
    <CrudPage<PeriodoContabil>
      title="Períodos Contábeis"
      labelSingular="Período"
      endpoint="/contabilidade/periodos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Ano" type="number" value={item.ano ?? ''} onChange={(e) => set('ano', Number(e.target.value))} required />
          <InputField label="Mês" type="number" value={item.mes ?? ''} onChange={(e) => set('mes', Number(e.target.value))} required />
          <InputField label="Data Início" type="date" value={item.dataInicio ?? ''} onChange={(e) => set('dataInicio', e.target.value)} required />
          <InputField label="Data Fim" type="date" value={item.dataFim ?? ''} onChange={(e) => set('dataFim', e.target.value)} required />
          <SelectField label="Status" value={item.status ?? ''} onChange={(e) => set('status', e.target.value)} options={[{ label: 'Aberto', value: 'ABERTO' }, { label: 'Fechado', value: 'FECHADO' }]} />
        </div>
      )}
    />
  );
}
