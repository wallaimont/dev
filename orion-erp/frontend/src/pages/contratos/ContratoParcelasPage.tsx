import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { ContratoParcela, Column } from '@/types';

const columns: Column<ContratoParcela>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'contratoId', label: 'Contrato ID' },
  { key: 'numeroParcela', label: 'Nº Parcela' },
  { key: 'dataVencimento', label: 'Vencimento' },
  { key: 'valor', label: 'Valor' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function ContratoParcelasPage() {
  return (
    <CrudPage<ContratoParcela>
      title="Parcelas de Contrato"
      labelSingular="Parcela"
      endpoint="/contrato-parcelas"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Contrato ID" type="number" value={item.contratoId ?? ''} onChange={(e) => set('contratoId', Number(e.target.value))} required />
          <InputField label="Número Parcela" type="number" value={item.numeroParcela ?? ''} onChange={(e) => set('numeroParcela', Number(e.target.value))} required />
          <InputField label="Data Vencimento" type="date" value={item.dataVencimento ?? ''} onChange={(e) => set('dataVencimento', e.target.value)} required />
          <InputField label="Data Pagamento" type="date" value={item.dataPagamento ?? ''} onChange={(e) => set('dataPagamento', e.target.value)} />
          <InputField label="Valor" type="number" step="0.01" value={item.valor ?? ''} onChange={(e) => set('valor', Number(e.target.value))} required />
          <InputField label="Valor Pago" type="number" step="0.01" value={item.valorPago ?? ''} onChange={(e) => set('valorPago', Number(e.target.value))} />
          <SelectField label="Status" value={item.status ?? ''} onChange={(e) => set('status', e.target.value)} options={[{ label: 'Pendente', value: 'PENDENTE' }, { label: 'Pago', value: 'PAGO' }, { label: 'Vencido', value: 'VENCIDO' }, { label: 'Cancelado', value: 'CANCELADO' }]} />
        </div>
      )}
    />
  );
}
