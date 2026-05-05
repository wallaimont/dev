'use client';

import { useQuery } from '@tanstack/react-query';
import { Badge, Card, StatCard } from '@orbitcrm/ui';
import { getCurrentTenant, getCurrentUser } from '../../../lib/api';

const fallbackUser = {
  name: 'Orbit Admin',
  email: 'admin@orbitcrm.demo',
  title: 'Founder & Admin',
  tenantId: 'tenant_demo',
  permissions: ['manage:all'],
};

export default function ProfilePage() {
  const { data: user = fallbackUser, isFetching: fetchingUser } = useQuery({
    queryKey: ['current-user'],
    queryFn: getCurrentUser,
    initialData: fallbackUser,
    retry: 0,
  });

  const { data: tenant, isFetching: fetchingTenant } = useQuery({
    queryKey: ['tenant-profile'],
    queryFn: getCurrentTenant,
    retry: 0,
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Perfil do usuário</h1>
          <p className="text-sm text-slate-500">Preferências pessoais, cargo, acesso e segurança de sessão.</p>
        </div>
        <p className="text-xs text-slate-500">{fetchingUser || fetchingTenant ? 'Sincronizando perfil...' : 'Perfil conectado com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Cargo" value={user?.title ?? 'Admin'} helper="função atual" />
        <StatCard label="Tenant" value={tenant?.name ?? 'Orbit Demo Industries'} helper="organização ativa" />
        <StatCard label="Permissões" value={String(user?.permissions?.length ?? 1)} helper="escopos concedidos" />
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-2">
          <div className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 text-sm">Nome: {user?.name ?? 'Orbit Admin'}</div>
          <div className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 text-sm">E-mail: {user?.email ?? 'admin@orbitcrm.demo'}</div>
          <div className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 text-sm">Tenant ID: {user?.tenantId ?? 'tenant_demo'}</div>
          <div className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 text-sm">Plano: {tenant?.planName ?? 'Growth'}</div>
        </div>
        <div className="mt-4 flex flex-wrap gap-2">
          {(user?.permissions ?? ['manage:all']).map((permission) => (
            <Badge key={permission}>{permission}</Badge>
          ))}
        </div>
      </Card>
    </div>
  );
}
