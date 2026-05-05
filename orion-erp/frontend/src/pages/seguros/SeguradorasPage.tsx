import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Seguradora, Column } from '@/types';

const columns: Column<Seguradora>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'codigo', label: 'Código' },
  { key: 'nome', label: 'Nome' },
  { key: 'cnpj', label: 'CNPJ' },
  { key: 'registroSusep', label: 'SUSEP' },
  { key: 'email', label: 'E-mail' },
  { key: 'telefone', label: 'Telefone' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function SeguradorasPage() {
  return (
    <CrudPage<Seguradora>
      title="Seguradoras"
      labelSingular="Seguradora"
      endpoint="/seguros/seguradoras"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Código" value={item.codigo ?? ''} onChange={(e) => set('codigo', e.target.value)} required />
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="CNPJ" value={item.cnpj ?? ''} onChange={(e) => set('cnpj', e.target.value)} required />
          <InputField label="Registro SUSEP" value={item.registroSusep ?? ''} onChange={(e) => set('registroSusep', e.target.value)} />
          <InputField label="E-mail" type="email" value={item.email ?? ''} onChange={(e) => set('email', e.target.value)} />
          <InputField label="Telefone" value={item.telefone ?? ''} onChange={(e) => set('telefone', e.target.value)} />
          <InputField label="Contato" value={item.contato ?? ''} onChange={(e) => set('contato', e.target.value)} />
          <InputField label="Endereço" value={item.endereco ?? ''} onChange={(e) => set('endereco', e.target.value)} />
          <InputField label="Cidade" value={item.cidade ?? ''} onChange={(e) => set('cidade', e.target.value)} />
          <InputField label="UF" value={item.uf ?? ''} onChange={(e) => set('uf', e.target.value)} />
          <InputField label="CEP" value={item.cep ?? ''} onChange={(e) => set('cep', e.target.value)} />
          <div className="col-span-2">
            <InputField label="Observação" value={item.observacao ?? ''} onChange={(e) => set('observacao', e.target.value)} />
          </div>
        </div>
      )}
    />
  );
}
