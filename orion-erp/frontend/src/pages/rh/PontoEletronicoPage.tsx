import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import type { PontoEletronico, Column } from '@/types';

const columns: Column<PontoEletronico>[] = [
  { key: 'id', label: 'ID' },
  { key: 'funcionarioId', label: 'Funcionário ID' },
  { key: 'data', label: 'Data' },
  { key: 'entrada1', label: 'Entrada 1' },
  { key: 'saida1', label: 'Saída 1' },
  { key: 'entrada2', label: 'Entrada 2' },
  { key: 'saida2', label: 'Saída 2' },
  { key: 'totalHoras', label: 'Total Horas' },
];

const renderForm = (item: Partial<PontoEletronico>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-3 gap-4">
    <InputField label="Funcionário ID" type="number" value={item.funcionarioId ?? ''} onChange={(v) => onChange('funcionarioId', v)} />
    <InputField label="Data" type="date" value={item.data ?? ''} onChange={(v) => onChange('data', v)} />
    <InputField label="Entrada 1" type="time" value={item.entrada1 ?? ''} onChange={(v) => onChange('entrada1', v)} />
    <InputField label="Saída 1" type="time" value={item.saida1 ?? ''} onChange={(v) => onChange('saida1', v)} />
    <InputField label="Entrada 2" type="time" value={item.entrada2 ?? ''} onChange={(v) => onChange('entrada2', v)} />
    <InputField label="Saída 2" type="time" value={item.saida2 ?? ''} onChange={(v) => onChange('saida2', v)} />
    <InputField label="Total Horas" value={item.totalHoras ?? ''} onChange={(v) => onChange('totalHoras', v)} />
    <InputField label="Horas Extras" value={item.horasExtras ?? ''} onChange={(v) => onChange('horasExtras', v)} />
    <InputField label="Justificativa" value={item.justificativa ?? ''} onChange={(v) => onChange('justificativa', v)} />
    <SelectField
      label="Status"
      value={item.status ?? ''}
      onChange={(v) => onChange('status', v)}
      options={[
        { value: 'NORMAL', label: 'Normal' },
        { value: 'FALTA', label: 'Falta' },
        { value: 'ATESTADO', label: 'Atestado' },
        { value: 'FERIAS', label: 'Férias' },
        { value: 'FOLGA', label: 'Folga' },
      ]}
    />
  </div>
);

export default function PontoEletronicoPage() {
  return (
    <CrudPage<PontoEletronico>
      title="Ponto Eletrônico"
      endpoint="/rh/ponto-eletronico"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
