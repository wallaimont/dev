import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Empresa, Column } from '@/types';

const columns: Column<Empresa>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'razaoSocial', label: 'Razão Social' },
  { key: 'nomeFantasia', label: 'Nome Fantasia' },
  { key: 'cnpj', label: 'CNPJ' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function EmpresasPage() {
  return (
    <CrudPage<Empresa>
      title="Empresas"
      labelSingular="Empresa"
      endpoint="/admin/empresas"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Razão Social" value={item.razaoSocial ?? ''} onChange={(e) => set('razaoSocial', e.target.value)} required />
          <InputField label="Nome Fantasia" value={item.nomeFantasia ?? ''} onChange={(e) => set('nomeFantasia', e.target.value)} />
          <InputField label="CNPJ" value={item.cnpj ?? ''} onChange={(e) => set('cnpj', e.target.value)} required />
          <InputField label="Inscrição Estadual" value={item.inscricaoEstadual ?? ''} onChange={(e) => set('inscricaoEstadual', e.target.value)} />
        </div>
      )}
    />
  );
}
