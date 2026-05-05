import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField, TextAreaField } from '@/components/FormField';
import { formatCurrency, formatDate } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { MovimentacaoEstoque, Column } from '@/types';

const columns: Column<MovimentacaoEstoque>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'tipo', label: 'Tipo', render: (r) => <StatusBadge value={r.tipo} /> },
  { key: 'produtoDescricao', label: 'Produto' },
  { key: 'armazemDescricao', label: 'Armazém' },
  { key: 'quantidade', label: 'Qtd' },
  { key: 'custoUnitario', label: 'Custo Unit.', render: (r) => formatCurrency(r.custoUnitario) },
  { key: 'dataMovimentacao', label: 'Data', render: (r) => formatDate(r.dataMovimentacao) },
];

export default function MovimentacoesPage() {
  return (
    <CrudPage<MovimentacaoEstoque>
      title="Movimentações de Estoque"
      labelSingular="Movimentação"
      endpoint="/estoque/movimentacoes"
      columns={columns}
      canDelete={false}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'Entrada', value: 'ENTRADA' }, { label: 'Saída', value: 'SAIDA' }, { label: 'Ajuste', value: 'AJUSTE' }]} />
          <InputField label="ID Produto" type="number" value={item.produtoId ?? ''} onChange={(e) => set('produtoId', Number(e.target.value))} required />
          <InputField label="ID Armazém" type="number" value={item.armazemId ?? ''} onChange={(e) => set('armazemId', Number(e.target.value))} required />
          <InputField label="Quantidade" type="number" step="0.01" value={item.quantidade ?? ''} onChange={(e) => set('quantidade', Number(e.target.value))} required />
          <InputField label="Custo Unitário" type="number" step="0.01" value={item.custoUnitario ?? ''} onChange={(e) => set('custoUnitario', Number(e.target.value))} required />
          <TextAreaField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} className="col-span-2" />
        </div>
      )}
    />
  );
}
