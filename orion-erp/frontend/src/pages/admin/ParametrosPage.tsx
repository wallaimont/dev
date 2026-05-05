import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import type { ParametroSistema, Column } from '@/types';

const columns: Column<ParametroSistema>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'chave', label: 'Chave' },
  { key: 'valor', label: 'Valor' },
  { key: 'descricao', label: 'Descrição' },
];

export default function ParametrosPage() {
  return (
    <CrudPage<ParametroSistema>
      title="Parâmetros"
      labelSingular="Parâmetro"
      endpoint="/admin/parametros"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Chave" value={item.chave ?? ''} onChange={(e) => set('chave', e.target.value)} required />
          <InputField label="Valor" value={item.valor ?? ''} onChange={(e) => set('valor', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} className="col-span-2" />
        </div>
      )}
    />
  );
}
