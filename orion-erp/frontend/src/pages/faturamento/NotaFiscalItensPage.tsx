import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import type { NotaFiscalItem, Column } from '@/types';

const columns: Column<NotaFiscalItem>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'notaFiscalId', label: 'NF ID' },
  { key: 'numeroItem', label: 'Item' },
  { key: 'produtoId', label: 'Produto ID' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'quantidade', label: 'Qtd' },
  { key: 'valorTotal', label: 'Valor Total' },
];

export default function NotaFiscalItensPage() {
  return (
    <CrudPage<NotaFiscalItem>
      title="Itens de Nota Fiscal"
      labelSingular="Item"
      endpoint="/faturamento/nota-fiscal-itens"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Nota Fiscal ID" type="number" value={item.notaFiscalId ?? ''} onChange={(e) => set('notaFiscalId', Number(e.target.value))} required />
          <InputField label="Nº Item" type="number" value={item.numeroItem ?? ''} onChange={(e) => set('numeroItem', Number(e.target.value))} required />
          <InputField label="Produto ID" type="number" value={item.produtoId ?? ''} onChange={(e) => set('produtoId', Number(e.target.value))} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <InputField label="NCM" value={item.ncm ?? ''} onChange={(e) => set('ncm', e.target.value)} />
          <InputField label="CFOP" value={item.cfop ?? ''} onChange={(e) => set('cfop', e.target.value)} />
          <InputField label="Quantidade" type="number" step="0.01" value={item.quantidade ?? ''} onChange={(e) => set('quantidade', Number(e.target.value))} required />
          <InputField label="Valor Unitário" type="number" step="0.01" value={item.valorUnitario ?? ''} onChange={(e) => set('valorUnitario', Number(e.target.value))} required />
          <InputField label="Valor Total" type="number" step="0.01" value={item.valorTotal ?? ''} onChange={(e) => set('valorTotal', Number(e.target.value))} required />
          <InputField label="Valor Desconto" type="number" step="0.01" value={item.valorDesconto ?? ''} onChange={(e) => set('valorDesconto', Number(e.target.value))} />
          <InputField label="ICMS CST" value={item.icmsCst ?? ''} onChange={(e) => set('icmsCst', e.target.value)} />
          <InputField label="ICMS Base" type="number" step="0.01" value={item.icmsBase ?? ''} onChange={(e) => set('icmsBase', Number(e.target.value))} />
          <InputField label="ICMS Alíquota" type="number" step="0.01" value={item.icmsAliquota ?? ''} onChange={(e) => set('icmsAliquota', Number(e.target.value))} />
          <InputField label="ICMS Valor" type="number" step="0.01" value={item.icmsValor ?? ''} onChange={(e) => set('icmsValor', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
