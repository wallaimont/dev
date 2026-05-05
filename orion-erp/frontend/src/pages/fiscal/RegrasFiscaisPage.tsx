import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { RegraFiscal, Column } from '@/types';

const columns: Column<RegraFiscal>[] = [
  { key: 'id', label: 'ID', className: 'w-20' },
  { key: 'ufOrigem', label: 'UF Orig.' },
  { key: 'ufDestino', label: 'UF Dest.' },
  { key: 'ncm', label: 'NCM' },
  { key: 'cfopCodigo', label: 'CFOP' },
  { key: 'cstIcms', label: 'CST ICMS' },
  { key: 'aliquotaIcms', label: '% ICMS', render: (r) => `${r.aliquotaIcms}%` },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function RegrasFiscaisPage() {
  return (
    <CrudPage<RegraFiscal>
      title="Regras Fiscais"
      labelSingular="Regra Fiscal"
      endpoint="/fiscal/regras-fiscais"
      columns={columns}
      keyExtractor={(r) => r.id}
      renderForm={(item, set) => (
        <div className="grid grid-cols-3 gap-4">
          <InputField label="ID Nat. Operação" type="number" value={item.naturezaOperacaoId ?? ''} onChange={(e) => set('naturezaOperacaoId', Number(e.target.value))} required />
          <InputField label="UF Origem" value={item.ufOrigem ?? ''} onChange={(e) => set('ufOrigem', e.target.value)} maxLength={2} />
          <InputField label="UF Destino" value={item.ufDestino ?? ''} onChange={(e) => set('ufDestino', e.target.value)} maxLength={2} />
          <InputField label="NCM" value={item.ncm ?? ''} onChange={(e) => set('ncm', e.target.value)} />
          <InputField label="CFOP" value={item.cfopCodigo ?? ''} onChange={(e) => set('cfopCodigo', e.target.value)} required />
          <InputField label="CST ICMS" value={item.cstIcms ?? ''} onChange={(e) => set('cstIcms', e.target.value)} />
          <InputField label="% ICMS" type="number" step="0.01" value={item.aliquotaIcms ?? ''} onChange={(e) => set('aliquotaIcms', Number(e.target.value))} />
          <InputField label="CST PIS" value={item.cstPis ?? ''} onChange={(e) => set('cstPis', e.target.value)} />
          <InputField label="% PIS" type="number" step="0.01" value={item.aliquotaPis ?? ''} onChange={(e) => set('aliquotaPis', Number(e.target.value))} />
          <InputField label="CST COFINS" value={item.cstCofins ?? ''} onChange={(e) => set('cstCofins', e.target.value)} />
          <InputField label="% COFINS" type="number" step="0.01" value={item.aliquotaCofins ?? ''} onChange={(e) => set('aliquotaCofins', Number(e.target.value))} />
        </div>
      )}
    />
  );
}
