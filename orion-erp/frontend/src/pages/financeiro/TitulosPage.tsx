import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import { formatCurrency, formatDate } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { Titulo, Column } from '@/types';

const columns: Column<Titulo>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'numero', label: 'Número' },
  { key: 'tipo', label: 'Tipo', render: (r) => <StatusBadge value={r.tipo} /> },
  { key: 'clienteNome', label: 'Cliente/Fornecedor', render: (r) => r.clienteNome ?? r.fornecedorNome ?? '-' },
  { key: 'valorOriginal', label: 'Valor', render: (r) => formatCurrency(r.valorOriginal) },
  { key: 'valorSaldo', label: 'Saldo', render: (r) => formatCurrency(r.valorSaldo) },
  { key: 'dataVencimento', label: 'Vencimento', render: (r) => formatDate(r.dataVencimento) },
  { key: 'situacao', label: 'Situação', render: (r) => <StatusBadge value={r.situacao} /> },
];

export default function TitulosPage() {
  return (
    <CrudPage<Titulo>
      title="Títulos"
      labelSingular="Título"
      endpoint="/financeiro/titulos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'A Pagar', value: 'PAGAR' }, { label: 'A Receber', value: 'RECEBER' }]} />
          <InputField label="Número" value={item.numero ?? ''} onChange={(e) => set('numero', e.target.value)} required />
          <InputField label="ID Cliente" type="number" value={item.clienteId ?? ''} onChange={(e) => set('clienteId', Number(e.target.value))} />
          <InputField label="ID Fornecedor" type="number" value={item.fornecedorId ?? ''} onChange={(e) => set('fornecedorId', Number(e.target.value))} />
          <InputField label="Valor Original" type="number" step="0.01" value={item.valorOriginal ?? ''} onChange={(e) => set('valorOriginal', Number(e.target.value))} required />
          <InputField label="Data Emissão" type="date" value={item.dataEmissao ?? ''} onChange={(e) => set('dataEmissao', e.target.value)} required />
          <InputField label="Data Vencimento" type="date" value={item.dataVencimento ?? ''} onChange={(e) => set('dataVencimento', e.target.value)} required />
          <InputField label="ID Nat. Financeira" type="number" value={item.naturezaFinanceiraId ?? ''} onChange={(e) => set('naturezaFinanceiraId', Number(e.target.value))} required />
        </div>
      )}
    />
  );
}
