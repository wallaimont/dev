import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { TabelaPrecoItem, Column } from '@/types';

const columns: Column<TabelaPrecoItem>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'tabelaPrecoId', label: 'Tabela ID' },
  { key: 'produtoId', label: 'Produto ID' },
  { key: 'preco', label: 'Preço' },
  { key: 'precoPromocional', label: 'Preço Promocional' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function TabelaPrecoItensPage() {
  return (
    <CrudPage<TabelaPrecoItem>
      title="Itens de Tabela de Preço"
      labelSingular="Item"
      endpoint="/vendas/tabela-preco-itens"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Tabela Preço ID" type="number" value={item.tabelaPrecoId ?? ''} onChange={(e) => set('tabelaPrecoId', Number(e.target.value))} required />
          <InputField label="Produto ID" type="number" value={item.produtoId ?? ''} onChange={(e) => set('produtoId', Number(e.target.value))} required />
          <InputField label="Preço" type="number" value={item.preco ?? ''} onChange={(e) => set('preco', Number(e.target.value))} required />
          <InputField label="Preço Promocional" type="number" value={item.precoPromocional ?? ''} onChange={(e) => set('precoPromocional', Number(e.target.value))} />
          <InputField label="Qtd Mínima" type="number" value={item.quantidadeMinima ?? ''} onChange={(e) => set('quantidadeMinima', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
