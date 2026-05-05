import React, { useEffect, useState } from 'react';
import { AlertTriangle, BarChart3, Plus, Target, TrendingUp } from 'lucide-react';
import api from '../services/api';
import type { Company, KpiDefinition, PageResponse } from '../types';
import {
  EmptyState,
  extractErrorMessage,
  formatCurrency,
  getStatusLabel,
  getStatusTone,
  MetricCard,
  SectionTitle,
} from './adminShared';

type KpiFormState = {
  name: string;
  code: string;
  unit: string;
  periodicity: string;
  targetValue: string;
  warningThreshold: string;
  criticalThreshold: string;
  category: string;
  module: string;
  ownerName: string;
  companyId: string;
  alertEnabled: boolean;
};

const initialForm: KpiFormState = {
  name: '',
  code: '',
  unit: 'R$',
  periodicity: 'MONTHLY',
  targetValue: '',
  warningThreshold: '',
  criticalThreshold: '',
  category: '',
  module: '',
  ownerName: '',
  companyId: '',
  alertEnabled: true,
};

function formatKpiValue(value?: number, unit?: string) {
  if (value === undefined || value === null) return 'N/A';
  if (unit === 'R$') return formatCurrency(value);
  return `${value}${unit || ''}`;
}

export default function KpisPage() {
  const [kpis, setKpis] = useState<KpiDefinition[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState<KpiFormState>(initialForm);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [kpiResponse, companyResponse] = await Promise.all([
        api.get<PageResponse<KpiDefinition>>('/kpis', { params: { size: 100 } }),
        api.get<PageResponse<Company>>('/companies', { params: { size: 100 } }),
      ]);
      setKpis(kpiResponse.data.content);
      setCompanies(companyResponse.data.content);
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel carregar os KPIs.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, []);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');

    try {
      await api.post('/kpis', {
        name: form.name,
        code: form.code || undefined,
        unit: form.unit || undefined,
        periodicity: form.periodicity,
        targetValue: form.targetValue ? Number(form.targetValue) : undefined,
        warningThreshold: form.warningThreshold ? Number(form.warningThreshold) : undefined,
        criticalThreshold: form.criticalThreshold ? Number(form.criticalThreshold) : undefined,
        category: form.category || undefined,
        module: form.module || undefined,
        ownerName: form.ownerName || undefined,
        companyId: form.companyId ? Number(form.companyId) : undefined,
        alertEnabled: form.alertEnabled,
      });
      setForm(initialForm);
      setMessage('KPI criado com sucesso.');
      await loadData();
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel criar o KPI.'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <SectionTitle
        title="Indicadores (KPIs)"
        description="Cadastre e monitore indicadores-chave com valor operacional e comercial para cada conta."
      />

      <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
        <MetricCard title="KPIs ativos" value={kpis.filter((kpi) => kpi.active).length} subtitle={`${kpis.length} cadastrados`} icon={BarChart3} />
        <MetricCard title="Com alertas" value={kpis.filter((kpi) => kpi.alertEnabled).length} subtitle="Prontos para monitoracao" icon={AlertTriangle} />
        <MetricCard title="No alvo" value={kpis.filter((kpi) => kpi.currentStatus === 'ON_TRACK').length} subtitle="Indicadores saudaveis" icon={Target} />
        <MetricCard title="Com variacao" value={kpis.filter((kpi) => kpi.variationPct !== undefined && kpi.variationPct !== null).length} subtitle="Acompanhamento de tendencia" icon={TrendingUp} />
      </div>

      {error ? <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">{error}</div> : null}
      {message ? <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">{message}</div> : null}

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.6fr,1fr]">
        <section className="glass-card p-6">
          <div className="mb-4 flex items-center justify-between gap-4">
            <div>
              <h2 className="text-lg font-semibold text-white">Painel de indicadores</h2>
              <p className="text-sm text-surface-400">Visibilidade dos resultados, metas e status por indicador.</p>
            </div>
            <button className="btn-secondary px-4 py-2 text-xs" onClick={() => void loadData()}>
              Atualizar
            </button>
          </div>

          {loading ? (
            <p className="text-sm text-surface-400">Carregando KPIs...</p>
          ) : kpis.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-surface-700/50">
                    <th className="px-4 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Indicador</th>
                    <th className="px-4 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Codigo</th>
                    <th className="px-4 py-3 text-right text-xs uppercase tracking-wide text-surface-400">Realizado</th>
                    <th className="px-4 py-3 text-right text-xs uppercase tracking-wide text-surface-400">Meta</th>
                    <th className="px-4 py-3 text-center text-xs uppercase tracking-wide text-surface-400">Status</th>
                    <th className="px-4 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Categoria</th>
                    <th className="px-4 py-3 text-center text-xs uppercase tracking-wide text-surface-400">Alerta</th>
                  </tr>
                </thead>
                <tbody>
                  {kpis.map((kpi) => (
                    <tr key={kpi.id} className="border-b border-surface-700/20">
                      <td className="px-4 py-3">
                        <p className="font-medium text-white">{kpi.name}</p>
                        <p className="text-xs text-surface-500">{kpi.ownerName || 'Sem owner definido'}</p>
                      </td>
                      <td className="px-4 py-3 text-surface-400">{kpi.code || 'N/A'}</td>
                      <td className="px-4 py-3 text-right font-semibold text-white">{formatKpiValue(kpi.currentValue, kpi.unit)}</td>
                      <td className="px-4 py-3 text-right text-surface-400">{formatKpiValue(kpi.targetValue, kpi.unit)}</td>
                      <td className="px-4 py-3 text-center">
                        <span className={getStatusTone(kpi.currentStatus)}>{getStatusLabel(kpi.currentStatus)}</span>
                      </td>
                      <td className="px-4 py-3 text-surface-300">{kpi.category || 'Nao categorizado'}</td>
                      <td className="px-4 py-3 text-center text-amber-300">{kpi.alertEnabled ? 'Ativo' : 'Off'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState title="Nenhum KPI cadastrado" description="Cadastre indicadores financeiros, comerciais e operacionais para enriquecer a oferta da plataforma." />
          )}
        </section>

        <section className="glass-card space-y-4 p-6">
          <div>
            <h2 className="text-lg font-semibold text-white">Novo KPI</h2>
            <p className="text-sm text-surface-400">Crie indicadores prontos para metas, alertas e acompanhamento executivo.</p>
          </div>

          <form className="space-y-3" onSubmit={handleSubmit}>
            <input className="input-field" placeholder="Nome do KPI" value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} required />
            <input className="input-field" placeholder="Codigo" value={form.code} onChange={(event) => setForm((current) => ({ ...current, code: event.target.value }))} />
            <input className="input-field" placeholder="Unidade (R$, %, dias...)" value={form.unit} onChange={(event) => setForm((current) => ({ ...current, unit: event.target.value }))} />
            <select className="input-field" value={form.periodicity} onChange={(event) => setForm((current) => ({ ...current, periodicity: event.target.value }))}>
              <option value="DAILY">Diario</option>
              <option value="WEEKLY">Semanal</option>
              <option value="MONTHLY">Mensal</option>
              <option value="QUARTERLY">Trimestral</option>
              <option value="YEARLY">Anual</option>
            </select>
            <input className="input-field" placeholder="Meta" value={form.targetValue} onChange={(event) => setForm((current) => ({ ...current, targetValue: event.target.value }))} />
            <input className="input-field" placeholder="Threshold warning" value={form.warningThreshold} onChange={(event) => setForm((current) => ({ ...current, warningThreshold: event.target.value }))} />
            <input className="input-field" placeholder="Threshold critical" value={form.criticalThreshold} onChange={(event) => setForm((current) => ({ ...current, criticalThreshold: event.target.value }))} />
            <input className="input-field" placeholder="Categoria" value={form.category} onChange={(event) => setForm((current) => ({ ...current, category: event.target.value }))} />
            <input className="input-field" placeholder="Modulo" value={form.module} onChange={(event) => setForm((current) => ({ ...current, module: event.target.value }))} />
            <input className="input-field" placeholder="Owner" value={form.ownerName} onChange={(event) => setForm((current) => ({ ...current, ownerName: event.target.value }))} />
            <select className="input-field" value={form.companyId} onChange={(event) => setForm((current) => ({ ...current, companyId: event.target.value }))}>
              <option value="">Escopo global</option>
              {companies.map((company) => (
                <option key={company.id} value={company.id}>{company.name}</option>
              ))}
            </select>
            <label className="flex items-center gap-2 text-sm text-surface-300">
              <input type="checkbox" checked={form.alertEnabled} onChange={(event) => setForm((current) => ({ ...current, alertEnabled: event.target.checked }))} />
              Habilitar alerta automatico
            </label>
            <button type="submit" className="btn-primary flex w-full items-center justify-center gap-2" disabled={saving}>
              <Plus size={16} />
              {saving ? 'Salvando...' : 'Criar KPI'}
            </button>
          </form>
        </section>
      </div>
    </div>
  );
}
