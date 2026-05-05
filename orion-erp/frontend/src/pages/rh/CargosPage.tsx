import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Cargo, Column } from '@/types';

const columns: Column<Cargo>[] = [
  { key: 'id', label: 'ID' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'cbo', label: 'CBO' },
  { key: 'nivel', label: 'Nível' },
  {
    key: 'ativo',
    label: 'Status',
    render: (v) => <StatusBadge ativo={v as boolean} />,
  },
];

const renderForm = (item: Partial<Cargo>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-2 gap-4">
    <InputField label="Código" value={item.codigo ?? ''} onChange={(v) => onChange('codigo', v)} />
    <InputField label="Nome" value={item.nome ?? ''} onChange={(v) => onChange('nome', v)} />
    <InputField label="Descrição" value={item.descricao ?? ''} onChange={(v) => onChange('descricao', v)} />
    <InputField label="CBO" value={item.cbo ?? ''} onChange={(v) => onChange('cbo', v)} />
    <SelectField
      label="Nível"
      value={item.nivel ?? ''}
      onChange={(v) => onChange('nivel', v)}
      options={[
        { value: 'JUNIOR', label: 'Júnior' },
        { value: 'PLENO', label: 'Pleno' },
        { value: 'SENIOR', label: 'Sênior' },
        { value: 'GERENTE', label: 'Gerente' },
        { value: 'DIRETOR', label: 'Diretor' },
      ]}
    />
    <InputField label="Salário Base" type="number" value={item.salarioBase ?? ''} onChange={(v) => onChange('salarioBase', v)} />
  </div>
);

export default function CargosPage() {
  return (
    <CrudPage<Cargo>
      title="Cargos"
      endpoint="/rh/cargos"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
