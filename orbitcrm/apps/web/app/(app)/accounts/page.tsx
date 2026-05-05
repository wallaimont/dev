'use client';

import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Account } from '@orbitcrm/types';
import { Button, Card, Input } from '@orbitcrm/ui';
import { accounts as initialAccounts } from '../../../lib/demo';
import { createAccount, deleteAccount, getAccounts } from '../../../lib/api';

export default function AccountsPage() {
  const queryClient = useQueryClient();
  const [localItems, setLocalItems] = useState<Account[]>([]);
  const [removedIds, setRemovedIds] = useState<string[]>([]);
  const { data: serverItems = initialAccounts, isFetching } = useQuery({
    queryKey: ['accounts'],
    queryFn: getAccounts,
    initialData: initialAccounts,
    retry: 0,
  });
  const [name, setName] = useState('');
  const [segment, setSegment] = useState('');
  const [website, setWebsite] = useState('');

  const createMutation = useMutation({
    mutationFn: createAccount,
    onSuccess: (created) => {
      setLocalItems((current) => [created, ...current.filter((item) => item.id !== created.id)]);
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
    },
  });

  const deleteMutation = useMutation({ mutationFn: deleteAccount });

  const items = [
    ...localItems,
    ...serverItems.filter((item) => !removedIds.includes(item.id) && !localItems.some((local) => local.id === item.id)),
  ];

  function addAccount() {
    if (!name) return;

    createMutation.mutate({ name, segment, website });
    setName('');
    setSegment('');
    setWebsite('');
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Contas</h1>
          <p className="text-sm text-slate-500">Empresas, segmentos, carteira e contexto comercial unificado.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando com a API...' : 'Módulo comercial conectado com fallback demo.'}</p>
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-4">
          <Input placeholder="Nome da empresa" value={name} onChange={(e) => setName(e.target.value)} />
          <Input placeholder="Segmento" value={segment} onChange={(e) => setSegment(e.target.value)} />
          <Input placeholder="Website" value={website} onChange={(e) => setWebsite(e.target.value)} />
          <Button onClick={addAccount} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Adicionar conta'}</Button>
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Conta</th>
                <th className="px-4 py-3">Segmento</th>
                <th className="px-4 py-3">Porte</th>
                <th className="px-4 py-3">Website</th>
                <th className="px-4 py-3">Owner</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map((account) => (
                <tr key={account.id} className="border-t border-slate-100">
                  <td className="px-4 py-3 font-medium text-slate-900">{account.name}</td>
                  <td className="px-4 py-3 text-slate-700">{account.segment}</td>
                  <td className="px-4 py-3 text-slate-700">{account.size}</td>
                  <td className="px-4 py-3 text-slate-700">{account.website}</td>
                  <td className="px-4 py-3 text-slate-700">{account.owner}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => {
                        setLocalItems((current) => current.filter((item) => item.id !== account.id));
                        setRemovedIds((current) => [...new Set([...current, account.id])]);
                        queryClient.setQueryData<Account[]>(['accounts'], (current = initialAccounts) => current.filter((item) => item.id !== account.id));
                        deleteMutation.mutate(account.id);
                      }}
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
