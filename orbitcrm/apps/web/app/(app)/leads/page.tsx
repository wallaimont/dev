'use client';

import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Lead } from '@orbitcrm/types';
import { Badge, Button, Card, Input } from '@orbitcrm/ui';
import { leads as initialLeads } from '../../../lib/demo';
import { createLead, deleteLead, getLeads } from '../../../lib/api';

export default function LeadsPage() {
  const queryClient = useQueryClient();
  const [localItems, setLocalItems] = useState<Lead[]>([]);
  const [removedLeadIds, setRemovedLeadIds] = useState<string[]>([]);
  const { data: serverItems = initialLeads, isFetching } = useQuery({
    queryKey: ['leads'],
    queryFn: getLeads,
    initialData: initialLeads,
    retry: 0,
  });
  const [name, setName] = useState('');
  const [company, setCompany] = useState('');
  const [email, setEmail] = useState('');

  const createMutation = useMutation({
    mutationFn: createLead,
    onSuccess: (createdLead) => {
      setLocalItems((current) => [createdLead, ...current.filter((item) => item.id !== createdLead.id)]);
      queryClient.invalidateQueries({ queryKey: ['leads'] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteLead,
    onSuccess: (_, leadId) => {
      setLocalItems((current) => current.filter((item) => item.id !== leadId));
      setRemovedLeadIds((current) => [...new Set([...current, leadId])]);
      queryClient.setQueryData<Lead[]>(['leads'], (current = initialLeads) => current.filter((item) => item.id !== leadId));
    },
  });

  const items = [
    ...localItems,
    ...serverItems.filter(
      (item) => !removedLeadIds.includes(item.id) && !localItems.some((local) => local.id === item.id),
    ),
  ];

  function addLead() {
    if (!name || !company || !email) return;

    const optimisticLead: Lead = {
      id: `LE-${Date.now()}`,
      name,
      company,
      email,
      owner: 'Orbit Admin',
      score: 70,
      status: 'new',
    };

    setLocalItems((current) => [optimisticLead, ...current]);
    createMutation.mutate({ name, company, email });
    setName('');
    setCompany('');
    setEmail('');
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Leads</h1>
          <p className="text-sm text-slate-500">Captação, qualificação e conversão com segregação multi-tenant.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando com a API...' : 'Fluxo ativo com fallback demo quando necessário.'}</p>
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-4">
          <Input placeholder="Nome do lead" value={name} onChange={(e) => setName(e.target.value)} />
          <Input placeholder="Empresa" value={company} onChange={(e) => setCompany(e.target.value)} />
          <Input placeholder="E-mail" value={email} onChange={(e) => setEmail(e.target.value)} />
          <Button onClick={addLead} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Adicionar lead'}</Button>
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Lead</th>
                <th className="px-4 py-3">Empresa</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Score</th>
                <th className="px-4 py-3">Owner</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map((lead) => (
                <tr key={lead.id} className="border-t border-slate-100">
                  <td className="px-4 py-3">
                    <p className="font-medium text-slate-900">{lead.name}</p>
                    <p className="text-xs text-slate-500">{lead.email}</p>
                  </td>
                  <td className="px-4 py-3 text-slate-700">{lead.company}</td>
                  <td className="px-4 py-3"><Badge>{lead.status}</Badge></td>
                  <td className="px-4 py-3 text-slate-700">{lead.score}</td>
                  <td className="px-4 py-3 text-slate-700">{lead.owner}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => {
                        setLocalItems((current) => current.filter((item) => item.id !== lead.id));
                        setRemovedLeadIds((current) => [...new Set([...current, lead.id])]);
                        queryClient.setQueryData<Lead[]>(['leads'], (current = initialLeads) => current.filter((item) => item.id !== lead.id));
                        deleteMutation.mutate(lead.id);
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
