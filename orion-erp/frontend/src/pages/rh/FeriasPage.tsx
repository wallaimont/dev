import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import type { Ferias, Column } from '@/types';

const columns: Column<Ferias>[] = [
  { key: 'id', label: 'ID' },
  { key: 'funcionarioId', label: 'Funcionário ID' },
  { key: 'periodoAquisitivoInicio', label: 'Período Aquisitivo' },
  { key: 'dataInicio', label: 'Data Início' },
  { key: 'dataFim', label: 'Data Fim' },
  { key: 'diasGozo', label: 'Dias' },
  { key: 'status', label: 'Status' },
];

const renderForm = (item: Partial<Ferias>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-2 gap-4">
    <InputField label="Funcionário ID" type="number" value={item.funcionarioId ?? ''} onChange={(v) => onChange('funcionarioId', v)} />
    <InputField label="Período Aquisitivo Início" type="date" value={item.periodoAquisitivoInicio ?? ''} onChange={(v) => onChange('periodoAquisitivoInicio', v)} />
    <InputField label="Período Aquisitivo Fim" type="date" value={item.periodoAquisitivoFim ?? ''} onChange={(v) => onChange('periodoAquisitivoFim', v)} />
    <InputField label="Data Início" type="date" value={item.dataInicio ?? ''} onChange={(v) => onChange('dataInicio', v)} />
    <InputField label="Data Fim" type="date" value={item.dataFim ?? ''} onChange={(v) => onChange('dataFim', v)} />
    <InputField label="Dias Gozo" type="number" value={item.diasGozo ?? ''} onChange={(v) => onChange('diasGozo', v)} />
    <InputField label="Dias Abono" type="number" value={item.diasAbono ?? ''} onChange={(v) => onChange('diasAbono', v)} />
    <InputField label="Dias Restantes" type="number" value={item.diasRestantes ?? ''} onChange={(v) => onChange('diasRestantes', v)} />
    <InputField label="Valor" type="number" value={item.valor ?? ''} onChange={(v) => onChange('valor', v)} />
    <SelectField
      label="Status"
      value={item.status ?? ''}
      onChange={(v) => onChange('status', v)}
      options={[
        { value: 'PROGRAMADA', label: 'Programada' },
        { value: 'EM_GOZO', label: 'Em Gozo' },
        { value: 'CONCLUIDA', label: 'Concluída' },
        { value: 'CANCELADA', label: 'Cancelada' },
      ]}
    />
  </div>
);

export default function FeriasPage() {
  return (
    <CrudPage<Ferias>
      title="Férias"
      endpoint="/rh/ferias"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
