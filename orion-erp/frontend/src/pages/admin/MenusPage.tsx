import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Column } from '@/types';

interface Menu {
  id: number;
  empresaId?: number;
  codigo: string;
  nome: string;
  modulo: string;
  url?: string;
  icone?: string;
  ordem?: number;
  parentId?: number;
  ativo: boolean;
}

const columns: Column<Menu>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'modulo', label: 'Módulo' },
  { key: 'ordem', label: 'Ordem' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function MenusPage() {
  return (
    <CrudPage<Menu>
      title="Menus"
      labelSingular="Menu"
      endpoint="/administracao/menus"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Módulo" value={item.modulo ?? ''} onChange={(e) => set('modulo', e.target.value)} required />
          <InputField label="URL" value={item.url ?? ''} onChange={(e) => set('url', e.target.value)} />
          <InputField label="Ícone" value={item.icone ?? ''} onChange={(e) => set('icone', e.target.value)} />
          <InputField label="Ordem" type="number" value={item.ordem ?? ''} onChange={(e) => set('ordem', Number(e.target.value))} />
          <InputField label="Parent ID" type="number" value={item.parentId ?? ''} onChange={(e) => set('parentId', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
