import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { NaturezaOperacao, Column } from '@/types';

const columns: Column<NaturezaOperacao>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'tipo', label: 'Tipo', render: (r) => <StatusBadge value={r.tipo} /> },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function NaturezasOperacaoPage() {
  return (
    <CrudPage<NaturezaOperacao>
      title="Naturezas de Operação"
      labelSingular="Natureza de Operação"
      endpoint="/fiscal/naturezas-operacao"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'Entrada', value: 'ENTRADA' }, { label: 'Saída', value: 'SAIDA' }]} />
        </div>
      )}
    />
  );
}
