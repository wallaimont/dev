import CrudPage from '@/pages/CrudPage';
import { InputField, TextAreaField } from '@/components/FormField';
import { formatCurrency, formatDate } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import api from '@/lib/api';
import toast from 'react-hot-toast';
import type { PedidoCompra, Column } from '@/types';

const columns: Column<PedidoCompra>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'numero', label: 'Número' },
  { key: 'fornecedorNome', label: 'Fornecedor' },
  { key: 'dataEmissao', label: 'Emissão', render: (r) => formatDate(r.dataEmissao) },
  { key: 'valorTotal', label: 'Valor Total', render: (r) => formatCurrency(r.valorTotal) },
  { key: 'situacao', label: 'Situação', render: (r) => <StatusBadge value={r.situacao} /> },
];

function ExtraActions({ item, reload }: { item: PedidoCompra; reload: () => void }) {
  const action = async (acao: string) => {
    try {
      await api.patch(`/compras/pedidos/${item.id}/${acao}`);
      toast.success(`Pedido ${acao} com sucesso`);
      reload();
    } catch (err: any) {
      toast.error(err.response?.data?.message ?? 'Erro na ação');
    }
  };

  if (item.situacao !== 'RASCUNHO') return null;
  return (
    <>
      <button onClick={(e) => { e.stopPropagation(); action('aprovar'); }} className="text-xs text-green-600 hover:underline">Aprovar</button>
      <button onClick={(e) => { e.stopPropagation(); action('reprovar'); }} className="text-xs text-yellow-600 hover:underline">Reprovar</button>
    </>
  );
}

export default function PedidosCompraPage() {
  return (
    <CrudPage<PedidoCompra>
      title="Pedidos de Compra"
      labelSingular="Pedido"
      endpoint="/compras/pedidos"
      columns={columns}
      extraActions={(item, reload) => <ExtraActions item={item} reload={reload} />}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Número" value={item.numero ?? ''} onChange={(e) => set('numero', e.target.value)} required />
          <InputField label="ID Fornecedor" type="number" value={item.fornecedorId ?? ''} onChange={(e) => set('fornecedorId', Number(e.target.value))} required />
          <InputField label="Data Emissão" type="date" value={item.dataEmissao ?? ''} onChange={(e) => set('dataEmissao', e.target.value)} required />
          <TextAreaField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} className="col-span-2" />
        </div>
      )}
    />
  );
}
