import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import { formatCurrency } from '@/lib/formatters';
import StatusBadge from '@/components/StatusBadge';
import type { Apolice, Column } from '@/types';

const columns: Column<Apolice>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'numero', label: 'Número' },
  { key: 'clienteNome', label: 'Cliente' },
  { key: 'seguradoraNome', label: 'Seguradora' },
  { key: 'ramo', label: 'Ramo' },
  { key: 'vigenciaInicio', label: 'Vigência Início' },
  { key: 'vigenciaFim', label: 'Vigência Fim' },
  { key: 'premioTotal', label: 'Prêmio Total', render: (r) => formatCurrency(r.premioTotal) },
  { key: 'importanciaSegurada', label: 'Importância', render: (r) => formatCurrency(r.importanciaSegurada) },
  { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
];

const statusOptions = [
  { label: 'Ativa', value: 'ATIVA' },
  { label: 'Vencida', value: 'VENCIDA' },
  { label: 'Cancelada', value: 'CANCELADA' },
  { label: 'Suspensa', value: 'SUSPENSA' },
];

export default function ApolicesPage() {
  return (
    <CrudPage<Apolice>
      title="Apólices"
      labelSingular="Apólice"
      endpoint="/seguros/apolices"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Número" value={item.numero ?? ''} onChange={(e) => set('numero', e.target.value)} required />
          <InputField label="ID Proposta" type="number" value={item.propostaId ?? ''} onChange={(e) => set('propostaId', Number(e.target.value))} />
          <InputField label="ID Cliente" type="number" value={item.clienteId ?? ''} onChange={(e) => set('clienteId', Number(e.target.value))} required />
          <InputField label="ID Seguradora" type="number" value={item.seguradoraId ?? ''} onChange={(e) => set('seguradoraId', Number(e.target.value))} required />
          <InputField label="ID Corretora" type="number" value={item.corretoraId ?? ''} onChange={(e) => set('corretoraId', Number(e.target.value))} />
          <InputField label="Ramo" value={item.ramo ?? ''} onChange={(e) => set('ramo', e.target.value)} required />
          <SelectField label="Status" value={item.status ?? 'ATIVA'} onChange={(e) => set('status', e.target.value)} options={statusOptions} />
          <InputField label="Vigência Início" type="date" value={item.vigenciaInicio ?? ''} onChange={(e) => set('vigenciaInicio', e.target.value)} required />
          <InputField label="Vigência Fim" type="date" value={item.vigenciaFim ?? ''} onChange={(e) => set('vigenciaFim', e.target.value)} required />
          <InputField label="Prêmio Total" type="number" step="0.01" value={item.premioTotal ?? ''} onChange={(e) => set('premioTotal', Number(e.target.value))} />
          <InputField label="Importância Segurada" type="number" step="0.01" value={item.importanciaSegurada ?? ''} onChange={(e) => set('importanciaSegurada', Number(e.target.value))} />
          <InputField label="Franquia" type="number" step="0.01" value={item.franquia ?? ''} onChange={(e) => set('franquia', Number(e.target.value))} />
          <InputField label="% Comissão" type="number" step="0.01" value={item.percentualComissao ?? ''} onChange={(e) => set('percentualComissao', Number(e.target.value))} />
          <InputField label="Certificado Inclusão" value={item.certificadoInclusao ?? ''} onChange={(e) => set('certificadoInclusao', e.target.value)} />
          <div className="col-span-2">
            <InputField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} />
          </div>
        </div>
      )}
    />
  );
}
