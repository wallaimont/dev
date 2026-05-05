import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Filial, Column } from '@/types';

const columns: Column<Filial>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'nome', label: 'Nome' },
  { key: 'cnpj', label: 'CNPJ' },
  { key: 'empresaNome', label: 'Empresa' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function FiliaisPage() {
  return (
    <CrudPage<Filial>
      title="Filiais"
      labelSingular="Filial"
      endpoint="/admin/filiais"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="CNPJ" value={item.cnpj ?? ''} onChange={(e) => set('cnpj', e.target.value)} required />
          <InputField label="ID Empresa" type="number" value={item.empresaId ?? ''} onChange={(e) => set('empresaId', Number(e.target.value))} required />
        </div>
      )}
    />
  );
}
