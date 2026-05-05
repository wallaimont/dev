import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { TabelaPreco, Column } from '@/types';

const columns: Column<TabelaPreco>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'vigenciaInicio', label: 'Vigência Início' },
  { key: 'vigenciaFim', label: 'Vigência Fim' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function TabelasPrecoPage() {
  return (
    <CrudPage<TabelaPreco>
      title="Tabelas de Preço"
      labelSingular="Tabela de Preço"
      endpoint="/cadastros/tabelas-preco"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Vigência Início" type="date" value={item.vigenciaInicio ?? ''} onChange={(e) => set('vigenciaInicio', e.target.value)} />
          <InputField label="Vigência Fim" type="date" value={item.vigenciaFim ?? ''} onChange={(e) => set('vigenciaFim', e.target.value)} />
        </div>
      )}
    />
  );
}
