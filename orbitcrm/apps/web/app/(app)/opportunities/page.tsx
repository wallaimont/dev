'use client';

import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Opportunity } from '@orbitcrm/types';
import { Badge, Button, Card, Input } from '@orbitcrm/ui';
import { formatCurrency } from '@orbitcrm/utils';
import { opportunities as initialOpportunities } from '../../../lib/demo';
import { createOpportunity, deleteOpportunity, getOpportunities } from '../../../lib/api';

export default function OpportunitiesPage() {
  const queryClient = useQueryClient();
  const [localItems, setLocalItems] = useState<Opportunity[]>([]);
  const [removedIds, setRemovedIds] = useState<string[]>([]);
  const { data: serverItems = initialOpportunities, isFetching } = useQuery({
    queryKey: ['opportunities'],
    queryFn: getOpportunities,
    initialData: initialOpportunities,
    retry: 0,
  });
  const [title, setTitle] = useState('');
  const [account, setAccount] = useState('');
  const [value, setValue] = useState('');
  const [expectedClose, setExpectedClose] = useState('');

  const createMutation = useMutation({
    mutationFn: createOpportunity,
    onSuccess: (created) => {
      setLocalItems((current) => [created, ...current.filter((item) => item.id !== created.id)]);
      queryClient.invalidateQueries({ queryKey: ['opportunities'] });
    },
  });

  const deleteMutation = useMutation({ mutationFn: deleteOpportunity });

  const items = [
    ...localItems,
    ...serverItems.filter((item) => !removedIds.includes(item.id) && !localItems.some((local) => local.id === item.id)),
  ];

  function addOpportunity() {
    if (!title || !value) return;

    createMutation.mutate({ title, account, value: Number(value), expectedClose });
    setTitle('');
    setAccount('');
    setValue('');
    setExpectedClose('');
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Oportunidades</h1>
          <p className="text-sm text-slate-500">Negócios ativos, valor, etapa e previsão de fechamento.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando com a API...' : 'Pipeline comercial conectado com fallback demo.'}</p>
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-5">
          <Input placeholder="Título do negócio" value={title} onChange={(e) => setTitle(e.target.value)} />
          <Input placeholder="Conta" value={account} onChange={(e) => setAccount(e.target.value)} />
          <Input placeholder="Valor" type="number" value={value} onChange={(e) => setValue(e.target.value)} />
          <Input placeholder="Fechamento previsto" type="date" value={expectedClose} onChange={(e) => setExpectedClose(e.target.value)} />
          <Button onClick={addOpportunity} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Nova oportunidade'}</Button>
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Título</th>
                <th className="px-4 py-3">Conta</th>
                <th className="px-4 py-3">Valor</th>
                <th className="px-4 py-3">Etapa</th>
                <th className="px-4 py-3">Fechamento</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id} className="border-t border-slate-100">
                  <td className="px-4 py-3 font-medium text-slate-900">{item.title}</td>
                  <td className="px-4 py-3 text-slate-700">{item.account}</td>
                  <td className="px-4 py-3 text-slate-700">{formatCurrency(item.value)}</td>
                  <td className="px-4 py-3"><Badge>{item.stage}</Badge></td>
                  <td className="px-4 py-3 text-slate-700">{item.expectedClose}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => {
                        setLocalItems((current) => current.filter((currentItem) => currentItem.id !== item.id));
                        setRemovedIds((current) => [...new Set([...current, item.id])]);
                        queryClient.setQueryData<Opportunity[]>(['opportunities'], (current = initialOpportunities) => current.filter((currentItem) => currentItem.id !== item.id));
                        deleteMutation.mutate(item.id);
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
