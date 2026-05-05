'use client';

import { useQuery } from '@tanstack/react-query';
import { Card, StatCard } from '@orbitcrm/ui';
import { formatCurrency } from '@orbitcrm/utils';
import { overview as demoOverview, tickets as demoTickets } from '../../../lib/demo';
import { getOverview, getTickets } from '../../../lib/api';
import { SalesChart, StageChart } from '../../../components/charts';

export default function DashboardPage() {
  const { data: overview = demoOverview } = useQuery({
    queryKey: ['dashboard-overview'],
    queryFn: getOverview,
    initialData: demoOverview,
  });

  const { data: tickets = demoTickets } = useQuery({
    queryKey: ['tickets-critical'],
    queryFn: getTickets,
    initialData: demoTickets,
  });
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-950">Dashboard principal</h1>
        <p className="text-sm text-slate-500">Visão consolidada de vendas, atendimento e operação do tenant.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Leads" value={String(overview.totalLeads)} helper="+12% nos últimos 30 dias" />
        <StatCard label="Contas" value={String(overview.totalAccounts)} helper="clientes ativos" />
        <StatCard label="Oportunidades" value={String(overview.totalOpportunities)} helper="pipeline vivo" />
        <StatCard label="Pipeline" value={formatCurrency(overview.pipelineValue)} helper="valor estimado" />
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        <Card>
          <div className="mb-4">
            <h2 className="text-lg font-semibold">Vendas por mês</h2>
            <p className="text-sm text-slate-500">Receita prevista para o semestre atual</p>
          </div>
          <SalesChart />
        </Card>
        <Card>
          <div className="mb-4">
            <h2 className="text-lg font-semibold">Oportunidades por etapa</h2>
            <p className="text-sm text-slate-500">Distribuição do pipeline comercial</p>
          </div>
          <StageChart />
        </Card>
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <Card>
          <h2 className="text-lg font-semibold">Próximas tarefas</h2>
          <div className="mt-4 space-y-3">
            {[
              'Reunião de descoberta com Atlas Supply — hoje, 15:00',
              'Follow-up proposta Vertex Energy — amanhã, 09:30',
              'Onboarding Solis Group — sexta, 10:00',
            ].map((task) => (
              <div key={task} className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 text-sm text-slate-700">{task}</div>
            ))}
          </div>
        </Card>
        <Card>
          <h2 className="text-lg font-semibold">Tickets críticos</h2>
          <div className="mt-4 space-y-3">
            {tickets.map((ticket) => (
              <div key={ticket.id} className="rounded-xl border border-slate-100 px-4 py-3">
                <p className="text-sm font-medium">{ticket.subject}</p>
                <p className="text-xs text-slate-500">{ticket.id} • {ticket.priority} • {ticket.status}</p>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
