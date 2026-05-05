'use client';

import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Badge, Card, StatCard } from '@orbitcrm/ui';
import { activities as initialActivities } from '../../../lib/demo';
import { getActivities } from '../../../lib/api';

export default function CalendarPage() {
  const { data: items = initialActivities, isFetching } = useQuery({
    queryKey: ['activities'],
    queryFn: getActivities,
    initialData: initialActivities,
    retry: 0,
  });

  const pendingCount = useMemo(() => items.filter((item) => item.status !== 'done').length, [items]);
  const meetingCount = useMemo(() => items.filter((item) => item.type === 'meeting').length, [items]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Calendário</h1>
          <p className="text-sm text-slate-500">Visão semanal de compromissos, reuniões e follow-ups críticos.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando agenda...' : 'Agenda conectada às atividades do CRM.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Compromissos" value={String(items.length)} helper="agenda da semana" />
        <StatCard label="Pendentes" value={String(pendingCount)} helper="ações em aberto" />
        <StatCard label="Reuniões" value={String(meetingCount)} helper="slots comerciais" />
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-xs uppercase tracking-wide text-slate-500">{item.dueDate}</p>
                  <p className="mt-1 text-sm font-medium text-slate-900">{item.title}</p>
                  <p className="mt-1 text-sm text-slate-600">{item.relatedTo}</p>
                </div>
                <Badge>{item.type}</Badge>
              </div>
              <div className="mt-2 flex items-center justify-between text-xs text-slate-500">
                <span>Owner: {item.owner}</span>
                <span>Status: {item.status}</span>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
