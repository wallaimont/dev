import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { FuncionarioBeneficio, Column } from '@/types';

const columns: Column<FuncionarioBeneficio>[] = [
  { key: 'id', label: 'ID' },
  { key: 'funcionarioId', label: 'Funcionário ID' },
  { key: 'beneficioId', label: 'Benefício ID' },
  { key: 'dataInicio', label: 'Data Início' },
  { key: 'dataFim', label: 'Data Fim' },
  { key: 'valor', label: 'Valor' },
  {
    key: 'ativo',
    label: 'Status',
    render: (v) => <StatusBadge ativo={v as boolean} />,
  },
];

const renderForm = (item: Partial<FuncionarioBeneficio>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-2 gap-4">
    <InputField label="Funcionário ID" type="number" value={item.funcionarioId ?? ''} onChange={(v) => onChange('funcionarioId', v)} />
    <InputField label="Benefício ID" type="number" value={item.beneficioId ?? ''} onChange={(v) => onChange('beneficioId', v)} />
    <InputField label="Data Início" type="date" value={item.dataInicio ?? ''} onChange={(v) => onChange('dataInicio', v)} />
    <InputField label="Data Fim" type="date" value={item.dataFim ?? ''} onChange={(v) => onChange('dataFim', v)} />
    <InputField label="Valor" type="number" value={item.valor ?? ''} onChange={(v) => onChange('valor', v)} />
  </div>
);

export default function FuncionarioBeneficiosPage() {
  return (
    <CrudPage<FuncionarioBeneficio>
      title="Funcionário Benefícios"
      endpoint="/rh/funcionario-beneficios"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
