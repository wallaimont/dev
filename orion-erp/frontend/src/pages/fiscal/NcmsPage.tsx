import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Ncm, Column } from '@/types';

const columns: Column<Ncm>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'aliquotaIpi', label: 'Alíquota IPI' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function NcmsPage() {
  return (
    <CrudPage<Ncm>
      title="NCMs"
      labelSingular="NCM"
      endpoint="/fiscal/ncms"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <InputField label="Alíquota IPI" type="number" value={item.aliquotaIpi ?? ''} onChange={(e) => set('aliquotaIpi', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
