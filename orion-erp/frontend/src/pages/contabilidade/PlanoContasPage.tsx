import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { PlanoContas, Column } from '@/types';

const columns: Column<PlanoContas>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'natureza', label: 'Natureza' },
  { key: 'nivel', label: 'Nível' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function PlanoContasPage() {
  return (
    <CrudPage<PlanoContas>
      title="Plano de Contas"
      labelSingular="Conta"
      endpoint="/contabilidade/plano-contas"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={[{ label: 'Sintética', value: 'SINTETICA' }, { label: 'Analítica', value: 'ANALITICA' }]} />
          <SelectField label="Natureza" value={item.natureza ?? ''} onChange={(e) => set('natureza', e.target.value)} options={[{ label: 'Devedora', value: 'DEVEDORA' }, { label: 'Credora', value: 'CREDORA' }]} />
          <InputField label="Nível" type="number" value={item.nivel ?? ''} onChange={(e) => set('nivel', Number(e.target.value))} />
          <SelectField label="Aceita Lançamento" value={item.aceitaLancamento === true ? 'true' : item.aceitaLancamento === false ? 'false' : ''} onChange={(e) => set('aceitaLancamento', e.target.value === 'true')} options={[{ label: 'Sim', value: 'true' }, { label: 'Não', value: 'false' }]} />
          <InputField label="Conta Pai ID" type="number" value={item.contaPaiId ?? ''} onChange={(e) => set('contaPaiId', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
