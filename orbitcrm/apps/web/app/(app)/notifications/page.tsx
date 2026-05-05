'use client';

import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Badge, Card, StatCard } from '@orbitcrm/ui';
import { notifications as initialNotifications } from '../../../lib/demo';
import { getNotifications } from '../../../lib/api';

export default function NotificationsPage() {
  const { data: items = initialNotifications, isFetching } = useQuery({
    queryKey: ['notifications'],
    queryFn: getNotifications,
    initialData: initialNotifications,
    retry: 0,
  });

  const unreadCount = useMemo(() => items.filter((item) => !item.read).length, [items]);
  const urgentCount = useMemo(() => items.filter((item) => item.type === 'urgent').length, [items]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Central de notificações</h1>
          <p className="text-sm text-slate-500">Alertas operacionais, comerciais e financeiros em um só lugar.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando notificações...' : 'Feed conectado com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Total" value={String(items.length)} helper="eventos recentes" />
        <StatCard label="Não lidas" value={String(unreadCount)} helper="exigem atenção" />
        <StatCard label="Urgentes" value={String(urgentCount)} helper="prioridade máxima" />
      </div>

      <Card>
        <div className="space-y-3">
          {items.map((item) => (
            <div key={item.id} className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-slate-900">{item.title}</p>
                  <p className="mt-1 text-sm text-slate-600">{item.message}</p>
                </div>
                <Badge>{item.read ? 'lida' : 'nova'}</Badge>
              </div>
              <div className="mt-2 flex items-center justify-between text-xs text-slate-500">
                <span>Tipo: {item.type}</span>
                <span>{item.createdAt}</span>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
