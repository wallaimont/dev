import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Comissao, Column } from '@/types';

const columns: Column<Comissao>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'vendedorId', label: 'Vendedor ID' },
  { key: 'pedidoVendaId', label: 'Pedido ID' },
  { key: 'percentual', label: 'Percentual' },
  { key: 'valorComissao', label: 'Valor Comissão' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function ComissoesPage() {
  return (
    <CrudPage<Comissao>
      title="Comissões"
      labelSingular="Comissão"
      endpoint="/vendas/comissoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Vendedor ID" type="number" value={item.vendedorId ?? ''} onChange={(e) => set('vendedorId', Number(e.target.value))} required />
          <InputField label="Pedido Venda ID" type="number" value={item.pedidoVendaId ?? ''} onChange={(e) => set('pedidoVendaId', Number(e.target.value))} required />
          <InputField label="Percentual" type="number" value={item.percentual ?? ''} onChange={(e) => set('percentual', Number(e.target.value))} required />
          <InputField label="Valor Base" type="number" value={item.valorBase ?? ''} onChange={(e) => set('valorBase', Number(e.target.value))} />
          <InputField label="Valor Comissão" type="number" value={item.valorComissao ?? ''} onChange={(e) => set('valorComissao', Number(e.target.value))} />
          <SelectField
            label="Status"
            value={item.status ?? ''}
            onChange={(e) => set('status', e.target.value)}
            options={[
              { value: 'PENDENTE', label: 'Pendente' },
              { value: 'PAGO', label: 'Pago' },
              { value: 'CANCELADO', label: 'Cancelado' },
            ]}
          />
          <InputField label="Data Pagamento" type="date" value={item.dataPagamento ?? ''} onChange={(e) => set('dataPagamento', e.target.value)} />
        </div>
      )}
    />
  );
}
