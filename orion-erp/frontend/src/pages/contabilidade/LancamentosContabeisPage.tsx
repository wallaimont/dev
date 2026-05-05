import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { LancamentoContabil, Column } from '@/types';

const columns: Column<LancamentoContabil>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'lote', label: 'Lote' },
  { key: 'numero', label: 'Número' },
  { key: 'dataLancamento', label: 'Data' },
  { key: 'valor', label: 'Valor' },
  { key: 'historico', label: 'Histórico' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function LancamentosContabeisPage() {
  return (
    <CrudPage<LancamentoContabil>
      title="Lançamentos Contábeis"
      labelSingular="Lançamento"
      endpoint="/contabilidade/lancamentos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Lote" value={item.lote ?? ''} onChange={(e) => set('lote', e.target.value)} required />
          <InputField label="Sublote" value={item.sublote ?? ''} onChange={(e) => set('sublote', e.target.value)} />
          <InputField label="Número" type="number" value={item.numero ?? ''} onChange={(e) => set('numero', Number(e.target.value))} required />
          <InputField label="Data Lançamento" type="date" value={item.dataLancamento ?? ''} onChange={(e) => set('dataLancamento', e.target.value)} required />
          <InputField label="Conta Débito ID" type="number" value={item.contaDebitoId ?? ''} onChange={(e) => set('contaDebitoId', Number(e.target.value))} required />
          <InputField label="Conta Crédito ID" type="number" value={item.contaCreditoId ?? ''} onChange={(e) => set('contaCreditoId', Number(e.target.value))} required />
          <InputField label="Valor" type="number" step="0.01" value={item.valor ?? ''} onChange={(e) => set('valor', Number(e.target.value))} required />
          <InputField label="Histórico" value={item.historico ?? ''} onChange={(e) => set('historico', e.target.value)} required />
          <InputField label="Documento" value={item.documento ?? ''} onChange={(e) => set('documento', e.target.value)} />
          <InputField label="Centro Custo ID" type="number" value={item.centroCustoId ?? ''} onChange={(e) => set('centroCustoId', Number(e.target.value))} />
          <InputField label="Centro Resultado ID" type="number" value={item.centroResultadoId ?? ''} onChange={(e) => set('centroResultadoId', Number(e.target.value))} />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'Manual', value: 'MANUAL' }, { label: 'Automático', value: 'AUTOMATICO' }]} />
          <SelectField label="Status" value={item.status ?? ''} onChange={(e) => set('status', e.target.value)} options={[{ label: 'Provisório', value: 'PROVISORIO' }, { label: 'Definitivo', value: 'DEFINITIVO' }]} />
        </div>
      )}
    />
  );
}
