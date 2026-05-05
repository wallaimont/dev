import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import { formatCurrency, formatDate } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { Funcionario, Column } from '@/types';

const columns: Column<Funcionario>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'nome', label: 'Nome' },
  { key: 'cpf', label: 'CPF' },
  { key: 'cargo', label: 'Cargo' },
  { key: 'departamento', label: 'Departamento' },
  { key: 'salario', label: 'Salário', render: (r) => formatCurrency(r.salario) },
  { key: 'dataAdmissao', label: 'Admissão', render: (r) => formatDate(r.dataAdmissao) },
  { key: 'situacao', label: 'Situação', render: (r) => <StatusBadge value={r.situacao} /> },
];

export default function FuncionariosPage() {
  return (
    <CrudPage<Funcionario>
      title="Funcionários"
      labelSingular="Funcionário"
      endpoint="/rh/funcionarios"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="CPF" value={item.cpf ?? ''} onChange={(e) => set('cpf', e.target.value)} required />
          <InputField label="E-mail" type="email" value={item.email ?? ''} onChange={(e) => set('email', e.target.value)} />
          <InputField label="Cargo" value={item.cargo ?? ''} onChange={(e) => set('cargo', e.target.value)} />
          <InputField label="Departamento" value={item.departamento ?? ''} onChange={(e) => set('departamento', e.target.value)} />
          <InputField label="Salário" type="number" step="0.01" value={item.salario ?? ''} onChange={(e) => set('salario', Number(e.target.value))} />
          <InputField label="Data Admissão" type="date" value={item.dataAdmissao ?? ''} onChange={(e) => set('dataAdmissao', e.target.value)} required />
          <SelectField
            label="Situação"
            value={item.situacao ?? ''}
            onChange={(e) => set('situacao', e.target.value)}
            options={[
              { label: 'Ativo', value: 'ATIVO' },
              { label: 'Afastado', value: 'AFASTADO' },
              { label: 'Férias', value: 'FERIAS' },
              { label: 'Demitido', value: 'DEMITIDO' },
            ]}
          />
        </div>
      )}
    />
  );
}
