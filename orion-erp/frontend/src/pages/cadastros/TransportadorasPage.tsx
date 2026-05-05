import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Transportadora, Column } from '@/types';

const columns: Column<Transportadora>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'razaoSocial', label: 'Razão Social' },
  { key: 'cpfCnpj', label: 'CPF/CNPJ' },
  { key: 'cidade', label: 'Cidade' },
  { key: 'uf', label: 'UF' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function TransportadorasPage() {
  return (
    <CrudPage<Transportadora>
      title="Transportadoras"
      labelSingular="Transportadora"
      endpoint="/cadastros/transportadoras"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Razão Social" value={item.razaoSocial ?? ''} onChange={(e) => set('razaoSocial', e.target.value)} required />
          <InputField label="Nome Fantasia" value={item.nomeFantasia ?? ''} onChange={(e) => set('nomeFantasia', e.target.value)} />
          <InputField label="CPF/CNPJ" value={item.cpfCnpj ?? ''} onChange={(e) => set('cpfCnpj', e.target.value)} required />
          <InputField label="Endereço" value={item.endereco ?? ''} onChange={(e) => set('endereco', e.target.value)} />
          <InputField label="Cidade" value={item.cidade ?? ''} onChange={(e) => set('cidade', e.target.value)} />
          <InputField label="UF" value={item.uf ?? ''} onChange={(e) => set('uf', e.target.value)} />
          <InputField label="Telefone" value={item.telefone ?? ''} onChange={(e) => set('telefone', e.target.value)} />
          <InputField label="Email" value={item.email ?? ''} onChange={(e) => set('email', e.target.value)} />
        </div>
      )}
    />
  );
}
