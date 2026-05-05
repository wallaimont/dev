import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Banco, Column } from '@/types';

const columns: Column<Banco>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigoBanco', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function BancosPage() {
  return (
    <CrudPage<Banco>
      title="Bancos"
      labelSingular="Banco"
      endpoint="/cadastros/bancos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código do Banco" value={item.codigoBanco ?? ''} onChange={(e) => set('codigoBanco', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
        </div>
      )}
    />
  );
}
