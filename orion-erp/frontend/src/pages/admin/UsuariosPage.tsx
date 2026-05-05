import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { Usuario, Column } from '@/types';

const columns: Column<Usuario>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'nome', label: 'Nome' },
  { key: 'login', label: 'Login' },
  { key: 'email', label: 'E-mail' },
  { key: 'perfilNome', label: 'Perfil' },
  { key: 'ativo', label: 'Status', render: (r) => <StatusBadge value={r.ativo ? 'ATIVO' : 'INATIVO'} /> },
];

export default function UsuariosPage() {
  return (
    <CrudPage<Usuario>
      title="Usuários"
      labelSingular="Usuário"
      endpoint="/admin/usuarios"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Nome" value={item.nome ?? ''} onChange={(e) => set('nome', e.target.value)} required />
          <InputField label="Login" value={item.login ?? ''} onChange={(e) => set('login', e.target.value)} required />
          <InputField label="E-mail" type="email" value={item.email ?? ''} onChange={(e) => set('email', e.target.value)} required />
          <InputField label="ID Perfil" type="number" value={item.perfilId ?? ''} onChange={(e) => set('perfilId', Number(e.target.value))} required />
          {!item.id && <InputField label="Senha" type="password" value={(item as any).senha ?? ''} onChange={(e) => set('senha', e.target.value)} required />}
        </div>
      )}
    />
  );
}
