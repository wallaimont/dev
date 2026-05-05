import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Perfil, Column } from '@/types';

const columns: Column<Perfil>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'nome', label: 'Nome' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function PerfisPage() {
  return (
    <CrudPage<Perfil>
      title="Perfis"
      labelSingular="Perfil"
      endpoint="/admin/perfis"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} />
        </div>
      )}
    />
  );
}
