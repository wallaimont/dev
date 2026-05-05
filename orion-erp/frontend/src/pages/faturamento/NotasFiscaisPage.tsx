import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { NotaFiscal, Column } from '@/types';

const columns: Column<NotaFiscal>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'serie', label: 'Série' },
  { key: 'numero', label: 'Número' },
  { key: 'dataEmissao', label: 'Data Emissão' },
  { key: 'valorTotal', label: 'Valor Total' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function NotasFiscaisPage() {
  return (
    <CrudPage<NotaFiscal>
      title="Notas Fiscais"
      labelSingular="Nota Fiscal"
      endpoint="/faturamento/notas-fiscais"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-3 gap-4">
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'Entrada', value: 'ENTRADA' }, { label: 'Saída', value: 'SAIDA' }]} />
          <InputField label="Série" value={item.serie ?? ''} onChange={(e) => set('serie', e.target.value)} required />
          <InputField label="Número" value={item.numero ?? ''} onChange={(e) => set('numero', e.target.value)} required />
          <InputField label="Modelo" value={item.modelo ?? ''} onChange={(e) => set('modelo', e.target.value)} />
          <InputField label="Data Emissão" type="date" value={item.dataEmissao ?? ''} onChange={(e) => set('dataEmissao', e.target.value)} required />
          <InputField label="Data Saída/Entrada" type="date" value={item.dataSaidaEntrada ?? ''} onChange={(e) => set('dataSaidaEntrada', e.target.value)} />
          <InputField label="Cliente ID" type="number" value={item.clienteId ?? ''} onChange={(e) => set('clienteId', Number(e.target.value))} />
          <InputField label="Fornecedor ID" type="number" value={item.fornecedorId ?? ''} onChange={(e) => set('fornecedorId', Number(e.target.value))} />
          <InputField label="Transportadora ID" type="number" value={item.transportadoraId ?? ''} onChange={(e) => set('transportadoraId', Number(e.target.value))} />
          <SelectField label="Frete por Conta" value={item.fretePorConta ?? ''} onChange={(e) => set('fretePorConta', e.target.value)} options={[{ label: 'Emitente', value: 'EMITENTE' }, { label: 'Destinatário', value: 'DESTINATARIO' }, { label: 'Terceiros', value: 'TERCEIROS' }]} />
          <InputField label="Valor Produtos" type="number" step="0.01" value={item.valorProdutos ?? ''} onChange={(e) => set('valorProdutos', Number(e.target.value))} />
          <InputField label="Valor Frete" type="number" step="0.01" value={item.valorFrete ?? ''} onChange={(e) => set('valorFrete', Number(e.target.value))} />
          <InputField label="Valor Seguro" type="number" step="0.01" value={item.valorSeguro ?? ''} onChange={(e) => set('valorSeguro', Number(e.target.value))} />
          <InputField label="Valor Desconto" type="number" step="0.01" value={item.valorDesconto ?? ''} onChange={(e) => set('valorDesconto', Number(e.target.value))} />
          <InputField label="Valor Outras Despesas" type="number" step="0.01" value={item.valorOutrasDespesas ?? ''} onChange={(e) => set('valorOutrasDespesas', Number(e.target.value))} />
          <InputField label="Informações Complementares" value={item.informacoesComplementares ?? ''} onChange={(e) => set('informacoesComplementares', e.target.value)} />
        </div>
      )}
    />
  );
}
