import { Card, StatCard } from '@orbitcrm/ui';
import { formatCurrency } from '@orbitcrm/utils';

export default function ReportsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-950">Relatórios e analytics</h1>
        <p className="text-sm text-slate-500">Indicadores executivos para vendas, suporte e produtividade.</p>
      </div>
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Receita prevista" value={formatCurrency(1284000)} helper="próximos 90 dias" />
        <StatCard label="Conversão média" value="24%" helper="por etapa" />
        <StatCard label="Tickets no prazo" value="93%" helper="compliance SLA" />
        <StatCard label="Produtividade" value="118" helper="atividades/semana" />
      </div>
      <Card>
        <ul className="grid gap-3 text-sm text-slate-700 md:grid-cols-2">
          <li>• Vendas por período</li>
          <li>• Conversão por etapa</li>
          <li>• Origem de leads</li>
          <li>• Receita prevista</li>
          <li>• Tickets por prioridade</li>
          <li>• Desempenho por equipe</li>
        </ul>
      </Card>
    </div>
  );
}
