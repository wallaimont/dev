'use client';

import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Badge, Card, StatCard } from '@orbitcrm/ui';
import { auditEvents as initialAuditEvents } from '../../../lib/demo';
import { getAuditEvents } from '../../../lib/api';

export default function AuditPage() {
  const { data: items = initialAuditEvents, isFetching } = useQuery({
    queryKey: ['audit-events'],
    queryFn: getAuditEvents,
    initialData: initialAuditEvents,
    retry: 0,
  });

  const entityCount = useMemo(() => new Set(items.map((item) => item.entity)).size, [items]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Auditoria</h1>
          <p className="text-sm text-slate-500">Rastreamento de logins, mudanças críticas e eventos operacionais.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando trilha de auditoria...' : 'Auditoria conectada com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Eventos" value={String(items.length)} helper="últimos registros" />
        <StatCard label="Entidades" value={String(entityCount)} helper="objetos auditados" />
        <StatCard label="Origem" value="Tenant atual" helper="escopo multi-tenant" />
      </div>

      <Card>
        <div className="space-y-3">
          {items.map((item) => (
            <div key={item.id} className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-slate-900">{item.action}</p>
                  <p className="mt-1 text-sm text-slate-600">{item.actor} alterou `{item.entity}`</p>
                </div>
                <Badge>{item.entity}</Badge>
              </div>
              <p className="mt-2 text-xs text-slate-500">{item.createdAt}</p>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
