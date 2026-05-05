'use client';

import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { AutomationRule } from '@orbitcrm/types';
import { Badge, Button, Card, Input, StatCard } from '@orbitcrm/ui';
import { automationRules as initialRules } from '../../../lib/demo';
import { createAutomationRule, deleteAutomationRule, getAutomationRules, updateAutomationRule } from '../../../lib/api';

export default function AutomationsPage() {
  const queryClient = useQueryClient();
  const { data: items = initialRules, isFetching } = useQuery({
    queryKey: ['automation-rules'],
    queryFn: getAutomationRules,
    initialData: initialRules,
    retry: 0,
  });

  const [name, setName] = useState('');
  const [triggerType, setTriggerType] = useState('LEAD_QUALIFIED');
  const [conditionsSummary, setConditionsSummary] = useState('score >= 80');
  const [actionsSummary, setActionsSummary] = useState('criar oportunidade + notificar SDR');

  const createMutation = useMutation({
    mutationFn: createAutomationRule,
    onSuccess: (created) => {
      queryClient.setQueryData<AutomationRule[]>(['automation-rules'], (current = initialRules) => [created, ...current.filter((item) => item.id !== created.id)]);
    },
  });

  const toggleMutation = useMutation({ mutationFn: ({ id, payload }: { id: string; payload: AutomationRule }) => updateAutomationRule(id, payload) });

  const deleteMutation = useMutation({
    mutationFn: deleteAutomationRule,
    onSuccess: (_, id) => {
      queryClient.setQueryData<AutomationRule[]>(['automation-rules'], (current = initialRules) => current.filter((item) => item.id !== id));
    },
  });

  const activeCount = useMemo(() => items.filter((item) => item.active).length, [items]);

  function addRule() {
    if (!name.trim()) return;

    createMutation.mutate({
      name,
      triggerType,
      conditionsSummary,
      actionsSummary,
      active: true,
    });

    setName('');
    setTriggerType('LEAD_QUALIFIED');
    setConditionsSummary('score >= 80');
    setActionsSummary('criar oportunidade + notificar SDR');
  }

  function toggleRule(rule: AutomationRule) {
    const nextRule = { ...rule, active: !rule.active };
    queryClient.setQueryData<AutomationRule[]>(['automation-rules'], (current = initialRules) =>
      current.map((item) => (item.id === rule.id ? nextRule : item)),
    );
    toggleMutation.mutate({ id: rule.id, payload: nextRule });
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Automações</h1>
          <p className="text-sm text-slate-500">Gatilhos, condições e ações para acelerar operação comercial e suporte.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando regras...' : 'Motor de automação conectado com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Regras totais" value={String(items.length)} helper="Playbooks ativos no tenant" />
        <StatCard label="Ativas" value={String(activeCount)} helper="Executando em produção" />
        <StatCard label="Em revisão" value={String(items.length - activeCount)} helper="Fluxos pausados ou em ajuste" />
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-4">
          <Input placeholder="Nome da regra" value={name} onChange={(e) => setName(e.target.value)} className="md:col-span-2" />
          <Input placeholder="Trigger" value={triggerType} onChange={(e) => setTriggerType(e.target.value)} />
          <Button onClick={addRule} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Nova regra'}</Button>
        </div>
        <div className="mt-3 grid gap-3 md:grid-cols-2">
          <Input placeholder="Condições" value={conditionsSummary} onChange={(e) => setConditionsSummary(e.target.value)} />
          <Input placeholder="Ações" value={actionsSummary} onChange={(e) => setActionsSummary(e.target.value)} />
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Regra</th>
                <th className="px-4 py-3">Trigger</th>
                <th className="px-4 py-3">Condições</th>
                <th className="px-4 py-3">Ações</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id} className="border-t border-slate-100">
                  <td className="px-4 py-3 font-medium text-slate-900">{item.name}</td>
                  <td className="px-4 py-3 text-slate-700">{item.triggerType}</td>
                  <td className="px-4 py-3 text-slate-700">{item.conditionsSummary}</td>
                  <td className="px-4 py-3 text-slate-700">{item.actionsSummary}</td>
                  <td className="px-4 py-3"><Badge>{item.active ? 'ativa' : 'pausada'}</Badge></td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex justify-end gap-3">
                      <button
                        type="button"
                        onClick={() => toggleRule(item)}
                        className="text-xs font-medium text-sky-700 hover:text-sky-800"
                      >
                        {item.active ? 'Pausar' : 'Ativar'}
                      </button>
                      <button
                        type="button"
                        onClick={() => deleteMutation.mutate(item.id)}
                        className="text-xs font-medium text-rose-600 hover:text-rose-700"
                      >
                        Excluir
                      </button>
                    </div>
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
