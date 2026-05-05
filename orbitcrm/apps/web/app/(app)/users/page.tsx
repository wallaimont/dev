'use client';

import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { TeamUser } from '@orbitcrm/types';
import { Badge, Button, Card, Input, StatCard } from '@orbitcrm/ui';
import { teamUsers as initialUsers } from '../../../lib/demo';
import { createUser, deleteUser, getUsers } from '../../../lib/api';

export default function UsersPage() {
  const queryClient = useQueryClient();
  const { data: users = initialUsers, isFetching } = useQuery({
    queryKey: ['team-users'],
    queryFn: getUsers,
    initialData: initialUsers,
    retry: 0,
  });

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [title, setTitle] = useState('Sales Executive');

  const createMutation = useMutation({
    mutationFn: createUser,
    onSuccess: (created) => {
      queryClient.setQueryData<TeamUser[]>(['team-users'], (current = initialUsers) => [created, ...current.filter((item) => item.id !== created.id)]);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteUser,
    onSuccess: (_, id) => {
      queryClient.setQueryData<TeamUser[]>(['team-users'], (current = initialUsers) => current.filter((item) => item.id !== id));
    },
  });

  function addUser() {
    if (!name || !email) return;
    createMutation.mutate({ name, email, title });
    setName('');
    setEmail('');
    setTitle('Sales Executive');
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Usuários, perfis e permissões</h1>
          <p className="text-sm text-slate-500">RBAC com escopos, papéis e governança por tenant.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando usuários...' : 'Gestão de usuários conectada com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Usuários" value={String(users.length)} helper="membros do tenant" />
        <StatCard label="Ativos" value={String(users.filter((user) => user.status === 'active').length)} helper="acesso liberado" />
        <StatCard label="Perfis" value={String(new Set(users.map((user) => user.title)).size)} helper="cargos mapeados" />
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-4">
          <Input placeholder="Nome" value={name} onChange={(e) => setName(e.target.value)} />
          <Input placeholder="E-mail" value={email} onChange={(e) => setEmail(e.target.value)} />
          <Input placeholder="Cargo" value={title} onChange={(e) => setTitle(e.target.value)} />
          <Button onClick={addUser} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Adicionar usuário'}</Button>
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Usuário</th>
                <th className="px-4 py-3">Cargo</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Último acesso</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id} className="border-t border-slate-100">
                  <td className="px-4 py-3">
                    <p className="font-medium text-slate-900">{user.name}</p>
                    <p className="text-xs text-slate-500">{user.email}</p>
                  </td>
                  <td className="px-4 py-3 text-slate-700">{user.title}</td>
                  <td className="px-4 py-3"><Badge>{user.status}</Badge></td>
                  <td className="px-4 py-3 text-slate-700">{user.lastLoginAt}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => deleteMutation.mutate(user.id)}
                      className="text-xs font-medium text-rose-600 hover:text-rose-700"
                    >
                      Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}
