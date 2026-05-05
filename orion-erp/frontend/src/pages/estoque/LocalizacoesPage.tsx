import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Localizacao, Column } from '@/types';

const columns: Column<Localizacao>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'armazemNome', label: 'Armazém' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function LocalizacoesPage() {
  return (
    <CrudPage<Localizacao>
      title="Localizações"
      labelSingular="Localização"
      endpoint="/estoque/localizacoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Armazém ID" type="number" value={item.armazemId ?? ''} onChange={(e) => set('armazemId', Number(e.target.value))} required />
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} />
        </div>
      )}
    />
  );
}
