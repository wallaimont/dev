import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { CondicaoPagamento, Column } from '@/types';

const columns: Column<CondicaoPagamento>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function CondicoesPagamentoPage() {
  return (
    <CrudPage<CondicaoPagamento>
      title="Condições de Pagamento"
      labelSingular="Condição de Pagamento"
      endpoint="/cadastros/condicoes-pagamento"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={['A_VISTA', 'A_PRAZO', 'PARCELADO']} required />
        </div>
      )}
    />
  );
}
