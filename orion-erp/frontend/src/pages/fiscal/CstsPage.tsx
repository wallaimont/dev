import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Cst, Column } from '@/types';

const columns: Column<Cst>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'tipoImposto', label: 'Tipo Imposto' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function CstsPage() {
  return (
    <CrudPage<Cst>
      title="CSTs"
      labelSingular="CST"
      endpoint="/fiscal/csts"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <InputField label="Tipo Imposto" value={item.tipoImposto ?? ''} onChange={(e) => set('tipoImposto', e.target.value)} />
        </div>
      )}
    />
  );
}
