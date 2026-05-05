import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import type { FolhaPagamento, Column } from '@/types';

const columns: Column<FolhaPagamento>[] = [
  { key: 'id', label: 'ID' },
  { key: 'funcionarioId', label: 'Funcionário ID' },
  { key: 'competencia', label: 'Competência' },
  { key: 'salarioBase', label: 'Salário Base' },
  { key: 'totalProventos', label: 'Total Proventos' },
  { key: 'totalDescontos', label: 'Total Descontos' },
  { key: 'salarioLiquido', label: 'Salário Líquido' },
  { key: 'status', label: 'Status' },
];

const renderForm = (item: Partial<FolhaPagamento>, onChange: (field: string, value: unknown) => void) => (
  <div className="grid grid-cols-3 gap-4">
    <InputField label="Funcionário ID" type="number" value={item.funcionarioId ?? ''} onChange={(v) => onChange('funcionarioId', v)} />
    <InputField label="Competência" value={item.competencia ?? ''} onChange={(v) => onChange('competencia', v)} />
    <InputField label="Salário Base" type="number" value={item.salarioBase ?? ''} onChange={(v) => onChange('salarioBase', v)} />
    <InputField label="Horas Trabalhadas" type="number" value={item.horasTrabalhadas ?? ''} onChange={(v) => onChange('horasTrabalhadas', v)} />
    <InputField label="Horas Extras" type="number" value={item.horasExtras ?? ''} onChange={(v) => onChange('horasExtras', v)} />
    <InputField label="Valor Horas Extras" type="number" value={item.valorHorasExtras ?? ''} onChange={(v) => onChange('valorHorasExtras', v)} />
    <InputField label="Total Proventos" type="number" value={item.totalProventos ?? ''} onChange={(v) => onChange('totalProventos', v)} />
    <InputField label="INSS" type="number" value={item.inss ?? ''} onChange={(v) => onChange('inss', v)} />
    <InputField label="IRRF" type="number" value={item.irrf ?? ''} onChange={(v) => onChange('irrf', v)} />
    <InputField label="FGTS" type="number" value={item.fgts ?? ''} onChange={(v) => onChange('fgts', v)} />
    <InputField label="Total Descontos" type="number" value={item.totalDescontos ?? ''} onChange={(v) => onChange('totalDescontos', v)} />
    <InputField label="Salário Líquido" type="number" value={item.salarioLiquido ?? ''} onChange={(v) => onChange('salarioLiquido', v)} />
    <SelectField
      label="Status"
      value={item.status ?? ''}
      onChange={(v) => onChange('status', v)}
      options={[
        { value: 'ABERTA', label: 'Aberta' },
        { value: 'CALCULADA', label: 'Calculada' },
        { value: 'FECHADA', label: 'Fechada' },
      ]}
    />
  </div>
);

export default function FolhaPagamentoPage() {
  return (
    <CrudPage<FolhaPagamento>
      title="Folha de Pagamento"
      endpoint="/rh/folha-pagamento"
      columns={columns}
      renderForm={renderForm}
    />
  );
}
