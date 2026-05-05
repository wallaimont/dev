'use client';

import { useMemo } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Opportunity, OpportunityStage } from '@orbitcrm/types';
import { Badge, Card } from '@orbitcrm/ui';
import { formatCurrency } from '@orbitcrm/utils';
import { opportunities as initialOpportunities } from '../../../lib/demo';
import { getOpportunities, updateOpportunityStage } from '../../../lib/api';

const columns: OpportunityStage[] = ['qualification', 'discovery', 'proposal', 'negotiation', 'won'];

export default function PipelinePage() {
  const queryClient = useQueryClient();
  const { data: items = initialOpportunities, isFetching } = useQuery({
    queryKey: ['opportunities'],
    queryFn: getOpportunities,
    initialData: initialOpportunities,
    retry: 0,
  });

  const moveMutation = useMutation({
    mutationFn: ({ id, stage }: { id: string; stage: OpportunityStage }) => updateOpportunityStage(id, stage),
  });

  function moveOpportunity(id: string, stage: OpportunityStage) {
    queryClient.setQueryData<Opportunity[]>(['opportunities'], (current = initialOpportunities) =>
      current.map((item) => (item.id === id ? { ...item, stage } : item)),
    );
    moveMutation.mutate({ id, stage });
  }

  const totals = useMemo(
    () =>
      columns.map((column) => ({
        column,
        value: items.filter((item) => item.stage === column).reduce((sum, item) => sum + item.value, 0),
      })),
    [items],
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Pipeline kanban</h1>
          <p className="text-sm text-slate-500">Arraste os cards entre colunas para atualizar o estágio comercial.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando pipeline...' : 'Kanban conectado ao backend com fallback demo.'}</p>
      </div>
      <div className="grid gap-4 xl:grid-cols-5">
        {columns.map((column) => (
          <div
            key={column}
            onDragOver={(event) => event.preventDefault()}
            onDrop={(event) => {
              const id = event.dataTransfer.getData('text/plain');
              if (id) moveOpportunity(id, column);
            }}
            className="rounded-2xl border border-slate-200 bg-slate-100/80 p-3"
          >
            <div className="mb-3">
              <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-700">{column}</h2>
              <p className="text-xs text-slate-500">{formatCurrency(totals.find((item) => item.column === column)?.value ?? 0)}</p>
            </div>
            <div className="space-y-3">
              {items.filter((item) => item.stage === column).map((item) => (
                <Card
                  key={item.id}
                  draggable
                  onDragStart={(event) => event.dataTransfer.setData('text/plain', item.id)}
                  className="cursor-grab p-4"
                >
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <p className="text-sm font-semibold text-slate-950">{item.title}</p>
                      <p className="text-xs text-slate-500">{item.account}</p>
                    </div>
                    <Badge>{item.stage}</Badge>
                  </div>
                  <p className="mt-2 text-sm text-slate-700">{formatCurrency(item.value)}</p>
                  <p className="text-xs text-slate-500">Owner: {item.owner}</p>
                  <p className="text-xs text-slate-500">Close: {item.expectedClose}</p>
                </Card>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
