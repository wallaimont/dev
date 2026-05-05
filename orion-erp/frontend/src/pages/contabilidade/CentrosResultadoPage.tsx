import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { CentroResultado, Column } from '@/types';

const columns: Column<CentroResultado>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'responsavel', label: 'Responsável' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function CentrosResultadoPage() {
  return (
    <CrudPage<CentroResultado>
      title="Centros de Resultado"
      labelSingular="Centro de Resultado"
      endpoint="/contabilidade/centros-resultado"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <InputField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} />
          <InputField label="Responsável" value={item.responsavel ?? ''} onChange={(e) => set('responsavel', e.target.value)} />
        </div>
      )}
    />
  );
}
