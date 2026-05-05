import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { CentroCusto, Column } from '@/types';

const columns: Column<CentroCusto>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'nivel', label: 'Nível' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function CentrosCustoPage() {
  return (
    <CrudPage<CentroCusto>
      title="Centros de Custo"
      labelSingular="Centro de Custo"
      endpoint="/cadastros/centros-custo"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Nível" value={item.nivel ?? ''} onChange={(e) => set('nivel', e.target.value)} />
        </div>
      )}
    />
  );
}
