import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import type { CommercialReadiness, DashboardData, KpiCardData } from '../types';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, AreaChart, Area,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from 'recharts';
import {
  TrendingUp, TrendingDown, DollarSign, ShoppingCart, Users,
  BarChart3, Target, AlertTriangle, ArrowUpRight, ArrowDownRight, Minus
} from 'lucide-react';

const CHART_COLORS = ['#3b82f6', '#8b5cf6', '#06b6d4', '#10b981', '#f59e0b', '#ef4444'];

const defaultDashboard: DashboardData = {
  summary: {
    grossRevenue: 2847500, netRevenue: 2350000, grossMargin: 32.5, netMargin: 24.8,
    averageTicket: 485, operatingCost: 1580000, defaultRate: 3.2,
    totalOrders: 4850, totalCustomers: 1230, conversionRate: 68.5
  },
  revenueByMonth: [
    { label: 'Jan', value: 185000, previousValue: 165000, variationPct: 12.1 },
    { label: 'Fev', value: 210000, previousValue: 190000, variationPct: 10.5 },
    { label: 'Mar', value: 195000, previousValue: 180000, variationPct: 8.3 },
    { label: 'Abr', value: 245000, previousValue: 220000, variationPct: 11.4 },
    { label: 'Mai', value: 230000, previousValue: 215000, variationPct: 7.0 },
    { label: 'Jun', value: 265000, previousValue: 240000, variationPct: 10.4 },
    { label: 'Jul', value: 280000, previousValue: 255000, variationPct: 9.8 },
    { label: 'Ago', value: 250000, previousValue: 235000, variationPct: 6.4 },
    { label: 'Set', value: 310000, previousValue: 285000, variationPct: 8.8 },
    { label: 'Out', value: 295000, previousValue: 270000, variationPct: 9.3 },
    { label: 'Nov', value: 340000, previousValue: 310000, variationPct: 9.7 },
    { label: 'Dez', value: 320000, previousValue: 295000, variationPct: 8.5 },
  ],
  revenueByRegion: [
    { label: 'Sudeste', value: 1250000, variationPct: 13.6 },
    { label: 'Sul', value: 680000, variationPct: 9.7 },
    { label: 'Nordeste', value: 450000, variationPct: 9.8 },
    { label: 'C-Oeste', value: 320000, variationPct: 10.3 },
    { label: 'Norte', value: 147500, variationPct: 13.5 },
  ],
  topProducts: [
    { label: 'Produto Premium A', value: 485000, variationPct: 12.5 },
    { label: 'Produto Standard B', value: 362000, variationPct: 8.3 },
    { label: 'Serviço Gold C', value: 298000, variationPct: 15.1 },
    { label: 'Produto Entry D', value: 245000, variationPct: -2.4 },
    { label: 'Pacote Enterprise E', value: 210000, variationPct: 22.7 },
  ],
  topSellers: [
    { label: 'Carlos Silva', value: 520000, variationPct: 4.0 },
    { label: 'Ana Santos', value: 485000, variationPct: 7.8 },
    { label: 'Roberto Lima', value: 410000, variationPct: -2.4 },
    { label: 'Maria Oliveira', value: 380000, variationPct: 11.8 },
    { label: 'Pedro Costa', value: 325000, variationPct: 4.8 },
  ],
  salesByChannel: [
    { label: 'E-commerce', value: 980000, variationPct: 18.5 },
    { label: 'Loja Física', value: 750000, variationPct: 3.2 },
    { label: 'Televendas', value: 520000, variationPct: -5.1 },
    { label: 'Representantes', value: 410000, variationPct: 12.0 },
    { label: 'Marketplace', value: 187500, variationPct: 45.3 },
  ],
  kpiCards: [
    { name: 'Faturamento Bruto', code: 'FAT_BRUTO', value: 2847500, target: 3000000, unit: 'R$', status: 'WARNING', variationPct: 5.2, trend: 'up' },
    { name: 'Margem Líquida', code: 'MARG_LIQ', value: 24.8, target: 25, unit: '%', status: 'WARNING', variationPct: -0.5, trend: 'stable' },
    { name: 'Ticket Médio', code: 'TKT_MEDIO', value: 485, target: 450, unit: 'R$', status: 'ON_TRACK', variationPct: 7.8, trend: 'up' },
    { name: 'Inadimplência', code: 'INADIMP', value: 3.2, target: 5, unit: '%', status: 'ON_TRACK', variationPct: -12, trend: 'down' },
    { name: 'Giro Estoque', code: 'GIRO_EST', value: 4.5, target: 5, unit: 'x', status: 'WARNING', variationPct: 2.3, trend: 'up' },
    { name: 'Conversão', code: 'CONV', value: 68.5, target: 70, unit: '%', status: 'WARNING', variationPct: 3.1, trend: 'up' },
    { name: 'Prazo Entrega', code: 'PME', value: 3.2, target: 3, unit: 'dias', status: 'WARNING', variationPct: -5, trend: 'down' },
    { name: 'Ruptura Estoque', code: 'RUPT', value: 2.1, target: 3, unit: '%', status: 'ON_TRACK', variationPct: -15, trend: 'down' },
  ],
};

const formatCurrency = (v: number) => {
  if (v >= 1000000) return `R$ ${(v / 1000000).toFixed(1)}M`;
  if (v >= 1000) return `R$ ${(v / 1000).toFixed(0)}K`;
  return `R$ ${v.toFixed(0)}`;
};

const formatNumber = (v: number) => v.toLocaleString('pt-BR');

function KpiCard({ kpi }: { kpi: KpiCardData }) {
  const isGood = kpi.status === 'ON_TRACK';
  const pct = kpi.target > 0 ? (kpi.value / kpi.target) * 100 : 0;

  return (
    <div className="glass-card-hover p-5">
      <div className="flex items-start justify-between mb-3">
        <p className="text-xs text-surface-400 font-medium uppercase tracking-wider">{kpi.name}</p>
        <span className={`${isGood ? 'badge-success' : 'badge-warning'}`}>
          {kpi.status === 'ON_TRACK' ? 'No alvo' : 'Atenção'}
        </span>
      </div>
      <div className="flex items-end gap-2 mb-3">
        <p className="text-2xl font-bold text-white">
          {kpi.unit === 'R$' ? formatCurrency(kpi.value) : `${kpi.value}${kpi.unit}`}
        </p>
        <div className={`flex items-center gap-0.5 text-xs font-medium mb-1 ${
          kpi.variationPct >= 0 ? 'text-emerald-400' : 'text-red-400'
        }`}>
          {kpi.variationPct >= 0 ? <ArrowUpRight size={14} /> : <ArrowDownRight size={14} />}
          {Math.abs(kpi.variationPct)}%
        </div>
      </div>
      <div className="w-full bg-surface-700/50 rounded-full h-1.5">
        <div
          className={`h-1.5 rounded-full transition-all duration-500 ${
            isGood ? 'bg-gradient-to-r from-emerald-500 to-emerald-400' : 'bg-gradient-to-r from-amber-500 to-amber-400'
          }`}
          style={{ width: `${Math.min(pct, 100)}%` }}
        />
      </div>
      <p className="text-xs text-surface-500 mt-1.5">
        Meta: {kpi.unit === 'R$' ? formatCurrency(kpi.target) : `${kpi.target}${kpi.unit}`} ({pct.toFixed(0)}%)
      </p>
    </div>
  );
}

function SummaryCard({ title, value, icon: Icon, color, subtitle }: any) {
  return (
    <div className="glass-card-hover p-5 group">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs text-surface-400 font-medium uppercase tracking-wider mb-1">{title}</p>
          <p className="text-2xl font-bold text-white">{value}</p>
          {subtitle && <p className="text-xs text-surface-500 mt-1">{subtitle}</p>}
        </div>
        <div className={`p-3 rounded-xl ${color} transition-transform duration-300 group-hover:scale-110`}>
          <Icon size={22} className="text-white" />
        </div>
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const [data, setData] = useState<DashboardData>(defaultDashboard);
  const [readiness, setReadiness] = useState<CommercialReadiness | null>(null);

  useEffect(() => {
    api.get('/dashboards/executive')
      .then(res => setData(res.data))
      .catch(() => { /* use default data */ });

    api.get<CommercialReadiness>('/commercial/readiness')
      .then(res => setReadiness(res.data))
      .catch(() => { /* keep dashboard focused on analytics */ });
  }, []);

  const s = data.summary;

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Dashboard Executivo</h1>
          <p className="text-sm text-surface-400 mt-1">Visão consolidada dos indicadores estratégicos</p>
        </div>
        <select className="input-field w-48 text-sm py-2">
          <option>Mês Atual</option>
          <option>Último Trimestre</option>
          <option>Ano Atual</option>
          <option>Personalizado</option>
        </select>
      </div>

      {readiness ? (
        <div className="glass-card border border-brand-500/20 bg-gradient-to-r from-brand-500/10 via-cyan-500/5 to-surface-900 p-5">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.25em] text-brand-300">Pulso comercial</p>
              <h2 className="mt-1 text-lg font-semibold text-white">{readiness.overview.stage}</h2>
              <p className="mt-1 text-sm text-surface-300">
                {readiness.snapshot.recommendedPlan} • {readiness.snapshot.slaTarget} • payback medio em {readiness.snapshot.paybackMonths} meses.
              </p>
            </div>
            <Link
              to="/settings"
              className="inline-flex items-center rounded-xl border border-brand-400/30 bg-surface-900/60 px-4 py-2 text-sm font-medium text-white transition hover:border-brand-300 hover:bg-surface-900"
            >
              Abrir plano comercial
            </Link>
          </div>
          <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-4">
            <div className="rounded-xl bg-surface-900/50 px-3 py-2 text-sm text-surface-300">
              <span className="block text-xs uppercase text-surface-500">Score</span>
              <span className="font-semibold text-white">{readiness.overview.readinessScore}%</span>
            </div>
            <div className="rounded-xl bg-surface-900/50 px-3 py-2 text-sm text-surface-300">
              <span className="block text-xs uppercase text-surface-500">Onboarding</span>
              <span className="font-semibold text-white">{readiness.snapshot.onboardingDays} dias</span>
            </div>
            <div className="rounded-xl bg-surface-900/50 px-3 py-2 text-sm text-surface-300">
              <span className="block text-xs uppercase text-surface-500">ROI estimado</span>
              <span className="font-semibold text-white">{readiness.snapshot.estimatedRoiPct}%</span>
            </div>
            <div className="rounded-xl bg-surface-900/50 px-3 py-2 text-sm text-surface-300">
              <span className="block text-xs uppercase text-surface-500">Receita potencial</span>
              <span className="font-semibold text-white">{readiness.snapshot.revenuePotentialBand}</span>
            </div>
          </div>
        </div>
      ) : null}

      {/* Summary cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 gap-4">
        <SummaryCard title="Faturamento Bruto" value={formatCurrency(s.grossRevenue)} icon={DollarSign} color="bg-gradient-to-br from-brand-600 to-brand-500" subtitle={`Líquido: ${formatCurrency(s.netRevenue)}`} />
        <SummaryCard title="Margem Bruta" value={`${s.grossMargin}%`} icon={TrendingUp} color="bg-gradient-to-br from-emerald-600 to-emerald-500" subtitle={`Líquida: ${s.netMargin}%`} />
        <SummaryCard title="Ticket Médio" value={formatCurrency(s.averageTicket)} icon={ShoppingCart} color="bg-gradient-to-br from-violet-600 to-violet-500" subtitle={`${formatNumber(s.totalOrders)} pedidos`} />
        <SummaryCard title="Clientes Ativos" value={formatNumber(s.totalCustomers)} icon={Users} color="bg-gradient-to-br from-cyan-600 to-cyan-500" subtitle={`Conversão: ${s.conversionRate}%`} />
        <SummaryCard title="Inadimplência" value={`${s.defaultRate}%`} icon={AlertTriangle} color="bg-gradient-to-br from-amber-600 to-amber-500" subtitle={`Custo Op: ${formatCurrency(s.operatingCost)}`} />
      </div>

      {/* KPI Cards */}
      <div>
        <h3 className="text-lg font-semibold text-white mb-3">Indicadores-Chave (KPIs)</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
          {data.kpiCards.map((kpi, i) => (
            <KpiCard key={i} kpi={kpi} />
          ))}
        </div>
      </div>

      {/* Charts row 1 */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* Revenue by month */}
        <div className="glass-card p-6">
          <h3 className="text-base font-semibold text-white mb-4">Faturamento Mensal</h3>
          <ResponsiveContainer width="100%" height={280}>
            <AreaChart data={data.revenueByMonth}>
              <defs>
                <linearGradient id="gradBlue" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3} />
                  <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis dataKey="label" stroke="#64748b" fontSize={12} />
              <YAxis stroke="#64748b" fontSize={12} tickFormatter={(v) => `${(v/1000).toFixed(0)}K`} />
              <Tooltip
                contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '12px' }}
                labelStyle={{ color: '#fff' }}
                formatter={(value: number) => [formatCurrency(value), 'Faturamento']}
              />
              <Area type="monotone" dataKey="previousValue" stroke="#475569" fill="none" strokeDasharray="5 5" name="Anterior" />
              <Area type="monotone" dataKey="value" stroke="#3b82f6" fill="url(#gradBlue)" strokeWidth={2} name="Atual" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Revenue by region */}
        <div className="glass-card p-6">
          <h3 className="text-base font-semibold text-white mb-4">Faturamento por Região</h3>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={data.revenueByRegion} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis type="number" stroke="#64748b" fontSize={12} tickFormatter={(v) => `${(v/1000).toFixed(0)}K`} />
              <YAxis type="category" dataKey="label" stroke="#64748b" fontSize={12} width={80} />
              <Tooltip
                contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '12px' }}
                formatter={(value: number) => [formatCurrency(value), 'Faturamento']}
              />
              <Bar dataKey="value" radius={[0, 6, 6, 0]}>
                {data.revenueByRegion.map((_, i) => (
                  <Cell key={i} fill={CHART_COLORS[i]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts row 2 */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        {/* Top products */}
        <div className="glass-card p-6">
          <h3 className="text-base font-semibold text-white mb-4">Top Produtos</h3>
          <div className="space-y-4">
            {data.topProducts.map((item, i) => (
              <div key={i} className="flex items-center justify-between">
                <div className="flex items-center gap-3 flex-1 min-w-0">
                  <span className="w-7 h-7 flex items-center justify-center rounded-lg bg-surface-700/50 text-xs font-bold text-brand-400">
                    {i + 1}
                  </span>
                  <span className="text-sm text-surface-200 truncate">{item.label}</span>
                </div>
                <div className="text-sm font-medium text-white flex items-center gap-2">
                  {formatCurrency(item.value)}
                  <span className={`text-xs ${(item.variationPct ?? 0) >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                    {(item.variationPct ?? 0) >= 0 ? '+' : ''}{item.variationPct}%
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Top sellers */}
        <div className="glass-card p-6">
          <h3 className="text-base font-semibold text-white mb-4">Top Vendedores</h3>
          <div className="space-y-4">
            {data.topSellers.map((item, i) => (
              <div key={i} className="flex items-center justify-between">
                <div className="flex items-center gap-3 flex-1 min-w-0">
                  <div className={`w-7 h-7 flex items-center justify-center rounded-lg text-xs font-bold text-white ${
                    i === 0 ? 'bg-amber-500' : i === 1 ? 'bg-surface-400' : i === 2 ? 'bg-amber-700' : 'bg-surface-600'
                  }`}>
                    {i + 1}
                  </div>
                  <span className="text-sm text-surface-200 truncate">{item.label}</span>
                </div>
                <div className="text-sm font-medium text-white flex items-center gap-2">
                  {formatCurrency(item.value)}
                  <span className={`text-xs ${(item.variationPct ?? 0) >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                    {(item.variationPct ?? 0) >= 0 ? '+' : ''}{item.variationPct}%
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Sales by channel - pie chart */}
        <div className="glass-card p-6">
          <h3 className="text-base font-semibold text-white mb-4">Vendas por Canal</h3>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie
                data={data.salesByChannel}
                cx="50%" cy="50%"
                innerRadius={55} outerRadius={85}
                dataKey="value" nameKey="label"
                strokeWidth={0}
              >
                {data.salesByChannel.map((_, i) => (
                  <Cell key={i} fill={CHART_COLORS[i]} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '12px' }}
                formatter={(value: number) => [formatCurrency(value)]}
              />
            </PieChart>
          </ResponsiveContainer>
          <div className="mt-2 space-y-1.5">
            {data.salesByChannel.map((ch, i) => (
              <div key={i} className="flex items-center justify-between text-xs">
                <div className="flex items-center gap-2">
                  <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: CHART_COLORS[i] }} />
                  <span className="text-surface-300">{ch.label}</span>
                </div>
                <span className="text-surface-400">{formatCurrency(ch.value)}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
