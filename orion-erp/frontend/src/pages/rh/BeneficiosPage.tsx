import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Beneficio, Column } from '@/types';

const columns: Column<Beneficio>[] = [
  { key: 'id', label: 'ID' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'valorEmpresa', label: 'Valor Empresa' },
  { key: 'valorFuncionario', label: 'Valor Funcionário' },
  {
    key: 'ativo',
    label: 'Status',
    render: (v) => <StatusBadge ativo={v as boolean} />,
  },
];

const renderForm = (item: Partial<Beneficio>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-2 gap-4">
    <InputField label="Código" value={item.codigo ?? ''} onChange={(v) => onChange('codigo', v)} />
    <InputField label="Nome" value={item.nome ?? ''} onChange={(v) => onChange('nome', v)} />
    <InputField label="Descrição" value={item.descricao ?? ''} onChange={(v) => onChange('descricao', v)} />
    <SelectField
      label="Tipo"
      value={item.tipo ?? ''}
      onChange={(v) => onChange('tipo', v)}
      options={[
        { value: 'VALE_TRANSPORTE', label: 'Vale Transporte' },
        { value: 'VALE_REFEICAO', label: 'Vale Refeição' },
        { value: 'VALE_ALIMENTACAO', label: 'Vale Alimentação' },
        { value: 'PLANO_SAUDE', label: 'Plano de Saúde' },
        { value: 'PLANO_ODONTOLOGICO', label: 'Plano Odontológico' },
        { value: 'SEGURO_VIDA', label: 'Seguro de Vida' },
        { value: 'OUTROS', label: 'Outros' },
      ]}
    />
    <InputField label="Valor Empresa" type="number" value={item.valorEmpresa ?? ''} onChange={(v) => onChange('valorEmpresa', v)} />
    <InputField label="Valor Funcionário" type="number" value={item.valorFuncionario ?? ''} onChange={(v) => onChange('valorFuncionario', v)} />
  </div>
);

export default function BeneficiosPage() {
  return (
    <CrudPage<Beneficio>
      title="Benefícios"
      endpoint="/rh/beneficios"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
