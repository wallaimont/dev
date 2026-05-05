import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Fornecedor, Column } from '@/types';

const columns: Column<Fornecedor>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'razaoSocial', label: 'Razão Social' },
  { key: 'cpfCnpj', label: 'CPF/CNPJ' },
  { key: 'tipoPessoa', label: 'Tipo', render: (r) => r.tipoPessoa === 'F' ? 'Física' : 'Jurídica' },
  { key: 'email', label: 'E-mail' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function FornecedoresPage() {
  return (
    <CrudPage<Fornecedor>
      title="Fornecedores"
      labelSingular="Fornecedor"
      endpoint="/cadastros/fornecedores"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Razão Social" value={item.razaoSocial ?? ''} onChange={(e) => set('razaoSocial', e.target.value)} required />
          <InputField label="Nome Fantasia" value={item.nomeFantasia ?? ''} onChange={(e) => set('nomeFantasia', e.target.value)} />
          <InputField label="CPF/CNPJ" value={item.cpfCnpj ?? ''} onChange={(e) => set('cpfCnpj', e.target.value)} required />
          <SelectField label="Tipo Pessoa" value={item.tipoPessoa ?? ''} onChange={(e) => set('tipoPessoa', e.target.value)} options={[{ label: 'Física', value: 'F' }, { label: 'Jurídica', value: 'J' }]} />
          <InputField label="E-mail" type="email" value={item.email ?? ''} onChange={(e) => set('email', e.target.value)} />
          <InputField label="Telefone" value={item.telefone ?? ''} onChange={(e) => set('telefone', e.target.value)} />
        </div>
      )}
    />
  );
}
