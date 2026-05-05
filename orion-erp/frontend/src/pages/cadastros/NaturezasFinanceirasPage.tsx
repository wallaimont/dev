import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { NaturezaFinanceira, Column } from '@/types';

const columns: Column<NaturezaFinanceira>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function NaturezasFinanceirasPage() {
  return (
    <CrudPage<NaturezaFinanceira>
      title="Naturezas Financeiras"
      labelSingular="Natureza Financeira"
      endpoint="/cadastros/naturezas-financeiras"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={['RECEITA', 'DESPESA']} required />
        </div>
      )}
    />
  );
}
