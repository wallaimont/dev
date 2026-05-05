import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { ContaBancaria, Column } from '@/types';

const columns: Column<ContaBancaria>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'agencia', label: 'Agência' },
  { key: 'conta', label: 'Conta' },
  { key: 'tipo', label: 'Tipo' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function ContasBancariasPage() {
  return (
    <CrudPage<ContaBancaria>
      title="Contas Bancárias"
      labelSingular="Conta Bancária"
      endpoint="/cadastros/contas-bancarias"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Descrição" value={item.descricao ?? ''} onChange={(e) => set('descricao', e.target.value)} required />
          <InputField label="Agência" value={item.agencia ?? ''} onChange={(e) => set('agencia', e.target.value)} required />
          <InputField label="Conta" value={item.conta ?? ''} onChange={(e) => set('conta', e.target.value)} required />
          <InputField label="Dígito" value={item.digito ?? ''} onChange={(e) => set('digito', e.target.value)} />
          <SelectField label="Tipo" value={item.tipo ?? ''} onChange={(e) => set('tipo', e.target.value)} options={['CORRENTE', 'POUPANCA']} required />
          <InputField label="Saldo Inicial" value={item.saldoInicial ?? ''} onChange={(e) => set('saldoInicial', e.target.value)} />
        </div>
      )}
    />
  );
}
