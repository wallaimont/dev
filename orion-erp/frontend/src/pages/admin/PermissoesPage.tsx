import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import type { Permissao, Column } from '@/types';

const columns: Column<Permissao>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'modulo', label: 'Módulo' },
  { key: 'recurso', label: 'Recurso' },
  { key: 'acao', label: 'Ação' },
  { key: 'descricao', label: 'Descrição' },
];

export default function PermissoesPage() {
  return (
    <CrudPage<Permissao>
      title="Permissões"
      labelSingular="Permissão"
      endpoint="/administracao/permissoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Módulo" value={item.modulo ?? ''} onChange={(e) => set('modulo', e.target.value)} required />
          <InputField label="Recurso" value={item.recurso ?? ''} onChange={(e) => set('recurso', e.target.value)} required />
          <InputField label="Ação" value={item.acao ?? ''} onChange={(e) => set('acao', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} />
        </div>
      )}
    />
  );
}
