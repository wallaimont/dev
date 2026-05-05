import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import { formatCurrency } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { Produto, Column } from '@/types';

const columns: Column<Produto>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'unidadeMedida', label: 'UN' },
  { key: 'precoVenda', label: 'Preço Venda', render: (r) => formatCurrency(r.precoVenda) },
  { key: 'precoCusto', label: 'Preço Custo', render: (r) => formatCurrency(r.precoCusto) },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function ProdutosPage() {
  return (
    <CrudPage<Produto>
      title="Produtos"
      labelSingular="Produto"
      endpoint="/cadastros/produtos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <InputField label="Unidade de Medida" value={item.unidadeMedida ?? ''} onChange={(e) => set('unidadeMedida', e.target.value)} required />
          <InputField label="NCM" value={item.ncm ?? ''} onChange={(e) => set('ncm', e.target.value)} />
          <InputField label="Preço de Venda" type="number" step="0.01" value={item.precoVenda ?? ''} onChange={(e) => set('precoVenda', Number(e.target.value))} />
          <InputField label="Preço de Custo" type="number" step="0.01" value={item.precoCusto ?? ''} onChange={(e) => set('precoCusto', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
