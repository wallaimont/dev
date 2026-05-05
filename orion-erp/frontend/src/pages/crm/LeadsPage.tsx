import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField, TextAreaField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Lead, Column } from '@/types';

const columns: Column<Lead>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'nome', label: 'Nome' },
  { key: 'empresa', label: 'Empresa' },
  { key: 'email', label: 'E-mail' },
  { key: 'telefone', label: 'Telefone' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
  { key: 'origem', label: 'Origem' },
];

export default function LeadsPage() {
  return (
    <CrudPage<Lead>
      title="Leads"
      labelSingular="Lead"
      endpoint="/crm/leads"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Empresa" value={item.empresa ?? ''} onChange={(e) => set('empresa', e.target.value)} />
          <InputField label="E-mail" type="email" value={item.email ?? ''} onChange={(e) => set('email', e.target.value)} />
          <InputField label="Telefone" value={item.telefone ?? ''} onChange={(e) => set('telefone', e.target.value)} />
          <SelectField
            label="Status"
            value={item.status ?? ''}
            onChange={(e) => set('status', e.target.value)}
            options={[
              { label: 'Novo', value: 'NOVO' },
              { label: 'Contatado', value: 'CONTATADO' },
              { label: 'Qualificado', value: 'QUALIFICADO' },
              { label: 'Convertido', value: 'CONVERTIDO' },
              { label: 'Perdido', value: 'PERDIDO' },
            ]}
          />
          <InputField label="Origem" value={item.origem ?? ''} onChange={(e) => set('origem', e.target.value)} />
          <TextAreaField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} className="col-span-2" />
        </div>
      )}
    />
  );
}
