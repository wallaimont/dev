import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Contrato, Column } from '@/types';

const columns: Column<Contrato>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'numero', label: 'Número' },
  { key: 'clienteId', label: 'Cliente ID' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'valorTotal', label: 'Valor Total' },
  { key: 'valorMensal', label: 'Valor Mensal' },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

export default function ContratosPage() {
  return (
    <CrudPage<Contrato>
      title="Contratos"
      labelSingular="Contrato"
      endpoint="/contratos"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Número" value={item.numero ?? ''} onChange={(e) => set('numero', e.target.value)} required />
          <InputField label="Cliente ID" type="number" value={item.clienteId ?? ''} onChange={(e) => set('clienteId', Number(e.target.value))} required />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'Prestação de Serviço', value: 'PRESTACAO_SERVICO' }, { label: 'Locação', value: 'LOCACAO' }, { label: 'Manutenção', value: 'MANUTENCAO' }, { label: 'Licenciamento', value: 'LICENCIAMENTO' }]} />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} />
          <InputField label="Data Início" type="date" value={item.dataInicio ?? ''} onChange={(e) => set('dataInicio', e.target.value)} required />
          <InputField label="Data Fim" type="date" value={item.dataFim ?? ''} onChange={(e) => set('dataFim', e.target.value)} required />
          <InputField label="Valor Total" type="number" step="0.01" value={item.valorTotal ?? ''} onChange={(e) => set('valorTotal', Number(e.target.value))} required />
          <InputField label="Valor Mensal" type="number" step="0.01" value={item.valorMensal ?? ''} onChange={(e) => set('valorMensal', Number(e.target.value))} required />
          <InputField label="Forma Pagamento" value={item.formaPagamento ?? ''} onChange={(e) => set('formaPagamento', e.target.value)} />
          <InputField label="Dia Vencimento" type="number" value={item.diaVencimento ?? ''} onChange={(e) => set('diaVencimento', Number(e.target.value))} />
          <InputField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} />
        </div>
      )}
    />
  );
}
