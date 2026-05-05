import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { DepartamentoRh, Column } from '@/types';

const columns: Column<DepartamentoRh>[] = [
  { key: 'id', label: 'ID' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'responsavelId', label: 'Responsável ID' },
  { key: 'centroCustoId', label: 'Centro Custo ID' },
  {
    key: 'ativo',
    label: 'Status',
    render: (v) => <StatusBadge ativo={v as boolean} />,
  },
];

const renderForm = (item: Partial<DepartamentoRh>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-2 gap-4">
    <InputField label="Código" value={item.codigo ?? ''} onChange={(v) => onChange('codigo', v)} />
    <InputField label="Nome" value={item.nome ?? ''} onChange={(v) => onChange('nome', v)} />
    <InputField label="Descrição" value={item.descricao ?? ''} onChange={(v) => onChange('descricao', v)} />
    <InputField label="Responsável ID" type="number" value={item.responsavelId ?? ''} onChange={(v) => onChange('responsavelId', v)} />
    <InputField label="Centro Custo ID" type="number" value={item.centroCustoId ?? ''} onChange={(v) => onChange('centroCustoId', v)} />
  </div>
);

export default function DepartamentosRhPage() {
  return (
    <CrudPage<DepartamentoRh>
      title="Departamentos RH"
      endpoint="/rh/departamentos"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
