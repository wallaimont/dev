'use client';

import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Activity } from '@orbitcrm/types';
import { Badge, Button, Card, Input } from '@orbitcrm/ui';
import { activities as initialActivities } from '../../../lib/demo';
import { createActivity, deleteActivity, getActivities } from '../../../lib/api';

export default function ActivitiesPage() {
  const queryClient = useQueryClient();
  const { data: items = initialActivities, isFetching } = useQuery({
    queryKey: ['activities'],
    queryFn: getActivities,
    initialData: initialActivities,
    retry: 0,
  });

  const [title, setTitle] = useState('');
  const [type, setType] = useState<Activity['type']>('task');
  const [priority, setPriority] = useState<Activity['priority']>('medium');
  const [dueDate, setDueDate] = useState('');
  const [relatedTo, setRelatedTo] = useState('');

  const createMutation = useMutation({
    mutationFn: createActivity,
    onSuccess: (created) => {
      queryClient.setQueryData<Activity[]>(['activities'], (current = initialActivities) => [created, ...current]);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteActivity,
    onSuccess: (_, id) => {
      queryClient.setQueryData<Activity[]>(['activities'], (current = initialActivities) => current.filter((item) => item.id !== id));
    },
  });

  function addActivity() {
    if (!title) return;

    createMutation.mutate({ title, type, priority, dueDate, relatedTo });
    setTitle('');
    setType('task');
    setPriority('medium');
    setDueDate('');
    setRelatedTo('');
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Atividades</h1>
          <p className="text-sm text-slate-500">Tarefas, reuniões, ligações e follow-ups vinculados ao contexto do cliente.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando agenda operacional...' : 'Atividades conectadas com fallback demo.'}</p>
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-6">
          <Input placeholder="Título da atividade" value={title} onChange={(e) => setTitle(e.target.value)} className="md:col-span-2" />
          <select value={type} onChange={(e) => setType(e.target.value as Activity['type'])} className="h-11 rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-700">
            <option value="task">Tarefa</option>
            <option value="call">Ligação</option>
            <option value="meeting">Reunião</option>
            <option value="email">E-mail</option>
          </select>
          <select value={priority} onChange={(e) => setPriority(e.target.value as Activity['priority'])} className="h-11 rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-700">
            <option value="low">Baixa</option>
            <option value="medium">Média</option>
            <option value="high">Alta</option>
          </select>
          <Input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
          <Input placeholder="Conta ou negócio" value={relatedTo} onChange={(e) => setRelatedTo(e.target.value)} />
        </div>
        <div className="mt-3 flex justify-end">
          <Button onClick={addActivity} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Adicionar atividade'}</Button>
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Atividade</th>
                <th className="px-4 py-3">Tipo</th>
                <th className="px-4 py-3">Prioridade</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Prazo</th>
                <th className="px-4 py-3">Contexto</th>
                <th className="px-4 py-3">Owner</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id} className="border-t border-slate-100">
                  <td className="px-4 py-3 font-medium text-slate-900">{item.title}</td>
                  <td className="px-4 py-3 text-slate-700">{item.type}</td>
                  <td className="px-4 py-3"><Badge>{item.priority}</Badge></td>
                  <td className="px-4 py-3"><Badge>{item.status}</Badge></td>
                  <td className="px-4 py-3 text-slate-700">{item.dueDate}</td>
                  <td className="px-4 py-3 text-slate-700">{item.relatedTo}</td>
                  <td className="px-4 py-3 text-slate-700">{item.owner}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => deleteMutation.mutate(item.id)}
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
