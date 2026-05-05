import CrudPage from '@/pages/CrudPage';
import { InputField, TextAreaField } from '@/components/FormField';
import { formatDate } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { Inventario, Column } from '@/types';

const columns: Column<Inventario>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'armazemDescricao', label: 'Armazém' },
  { key: 'dataInventario', label: 'Data', render: (r) => formatDate(r.dataInventario) },
  { key: 'situacao', label: 'Situação', render: (r) => <StatusBadge value={r.situacao} /> },
];

export default function InventariosPage() {
  return (
    <CrudPage<Inventario>
      title="Inventários"
      labelSingular="Inventário"
      endpoint="/estoque/inventarios"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="ID Armazém" type="number" value={item.armazemId ?? ''} onChange={(e) => set('armazemId', Number(e.target.value))} required />
          <InputField label="Data" type="date" value={item.dataInventario ?? ''} onChange={(e) => set('dataInventario', e.target.value)} required />
          <TextAreaField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} className="col-span-2" />
        </div>
      )}
    />
  );
}
