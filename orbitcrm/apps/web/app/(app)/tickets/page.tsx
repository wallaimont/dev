'use client';

import { useQuery } from '@tanstack/react-query';
import { Badge, Card } from '@orbitcrm/ui';
import { tickets as demoTickets } from '../../../lib/demo';
import { getTickets } from '../../../lib/api';

export default function TicketsPage() {
  const { data: tickets = demoTickets } = useQuery({
    queryKey: ['tickets'],
    queryFn: getTickets,
    initialData: demoTickets,
  });
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-950">Tickets de suporte</h1>
        <p className="text-sm text-slate-500">Atendimento com fila, prioridade, responsável e visão de SLA.</p>
      </div>
      <div className="grid gap-4 md:grid-cols-3">
        {tickets.map((ticket) => (
          <Card key={ticket.id}>
            <div className="flex items-center justify-between">
              <p className="text-sm font-semibold text-slate-950">{ticket.id}</p>
              <Badge>{ticket.priority}</Badge>
            </div>
            <p className="mt-3 text-sm text-slate-700">{ticket.subject}</p>
            <p className="mt-2 text-xs text-slate-500">Responsável: {ticket.owner}</p>
            <p className="text-xs text-slate-500">Status: {ticket.status}</p>
          </Card>
        ))}
      </div>
    </div>
  );
}
