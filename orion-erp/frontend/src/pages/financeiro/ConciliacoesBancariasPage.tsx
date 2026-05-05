import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { ConciliacaoBancaria, Column } from '@/types';

const columns: Column<ConciliacaoBancaria>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'contaBancariaId', label: 'Conta Bancária ID' },
  { key: 'dataInicio', label: 'Data Início' },
  { key: 'dataFim', label: 'Data Fim' },
  { key: 'saldoInicial', label: 'Saldo Inicial' },
  { key: 'saldoFinal', label: 'Saldo Final' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function ConciliacoesBancariasPage() {
  return (
    <CrudPage<ConciliacaoBancaria>
      title="Conciliações Bancárias"
      labelSingular="Conciliação"
      endpoint="/financeiro/conciliacoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Conta Bancária ID" type="number" value={item.contaBancariaId ?? ''} onChange={(e) => set('contaBancariaId', Number(e.target.value))} required />
          <InputField label="Data Início" type="date" value={item.dataInicio ?? ''} onChange={(e) => set('dataInicio', e.target.value)} required />
          <InputField label="Data Fim" type="date" value={item.dataFim ?? ''} onChange={(e) => set('dataFim', e.target.value)} required />
          <InputField label="Saldo Inicial" type="number" value={item.saldoInicial ?? ''} onChange={(e) => set('saldoInicial', Number(e.target.value))} />
          <InputField label="Saldo Final" type="number" value={item.saldoFinal ?? ''} onChange={(e) => set('saldoFinal', Number(e.target.value))} />
          <SelectField
            label="Status"
            value={item.status ?? ''}
            onChange={(e) => set('status', e.target.value)}
            options={[
              { value: 'EM_ANDAMENTO', label: 'Em Andamento' },
              { value: 'CONCLUIDA', label: 'Concluída' },
              { value: 'CANCELADA', label: 'Cancelada' },
            ]}
          />
        </div>
      )}
    />
  );
}
