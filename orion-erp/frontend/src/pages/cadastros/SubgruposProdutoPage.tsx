import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { SubgrupoProduto, Column } from '@/types';

const columns: Column<SubgrupoProduto>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function SubgruposProdutoPage() {
  return (
    <CrudPage<SubgrupoProduto>
      title="Subgrupos de Produto"
      labelSingular="Subgrupo de Produto"
      endpoint="/cadastros/subgrupos-produto"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Grupo ID" type="number" value={item.grupoId ?? ''} onChange={(e) => set('grupoId', Number(e.target.value))} required />
        </div>
      )}
    />
  );
}
