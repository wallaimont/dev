import React, { useEffect, useMemo, useState } from 'react';
import { Database, Plus, RefreshCw, Wifi } from 'lucide-react';
import api from '../services/api';
import type { Company, DataSource, PageResponse } from '../types';
import {
  EmptyState,
  extractErrorMessage,
  formatCompactNumber,
  formatDateTime,
  getStatusLabel,
  getStatusTone,
  MetricCard,
  SectionTitle,
} from './adminShared';

type DataSourceFormState = {
  name: string;
  description: string;
  type: string;
  host: string;
  port: string;
  databaseName: string;
  apiUrl: string;
  filePath: string;
  syncSchedule: string;
  companyId: string;
};

const initialForm: DataSourceFormState = {
  name: '',
  description: '',
  type: 'ERP',
  host: '',
  port: '',
  databaseName: '',
  apiUrl: '',
  filePath: '',
  syncSchedule: '',
  companyId: '',
};

export default function DataSourcesPage() {
  const [sources, setSources] = useState<DataSource[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [busyId, setBusyId] = useState<number | null>(null);
  const [form, setForm] = useState<DataSourceFormState>(initialForm);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [sourcesResponse, companiesResponse] = await Promise.all([
        api.get<PageResponse<DataSource>>('/data-sources', { params: { size: 50 } }),
        api.get<PageResponse<Company>>('/companies', { params: { size: 100 } }),
      ]);
      setSources(sourcesResponse.data.content);
      setCompanies(companiesResponse.data.content);
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel carregar as fontes de dados.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, []);

  const stats = useMemo(
    () => ({
      connected: sources.filter((source) => source.status === 'CONNECTED').length,
      failed: sources.filter((source) => source.status === 'FAILED').length,
      pending: sources.filter((source) => source.status === 'PENDING').length,
    }),
    [sources],
  );

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');

    try {
      await api.post('/data-sources', {
        name: form.name,
        description: form.description || undefined,
        type: form.type,
        host: form.host || undefined,
        port: form.port ? Number(form.port) : undefined,
        databaseName: form.databaseName || undefined,
        apiUrl: form.apiUrl || undefined,
        filePath: form.filePath || undefined,
        syncSchedule: form.syncSchedule || undefined,
        companyId: form.companyId ? Number(form.companyId) : undefined,
      });
      setForm(initialForm);
      setMessage('Fonte de dados cadastrada com sucesso.');
      await loadData();
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel cadastrar a fonte.'));
    } finally {
      setSaving(false);
    }
  };

  const handleTest = async (sourceId: number) => {
    setBusyId(sourceId);
    try {
      await api.post(`/data-sources/${sourceId}/test-connection`);
      await loadData();
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel testar a conexao.'));
    } finally {
      setBusyId(null);
    }
  };

  const handleSync = async (sourceId: number) => {
    setBusyId(sourceId);
    try {
      await api.post(`/etl/execute/${sourceId}`);
      await loadData();
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel executar o ETL.'));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-start justify-between gap-4">
        <SectionTitle
          title="Fontes de dados"
          description="Gerencie as conexoes com sistemas externos e a camada de ingestao que sustenta o produto."
        />
        <button className="btn-secondary" onClick={() => void loadData()}>
          Atualizar
        </button>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
        <MetricCard title="Fontes cadastradas" value={sources.length} subtitle={`${stats.connected} conectadas`} icon={Database} />
        <MetricCard title="Com falha" value={stats.failed} subtitle="Conectores em atencao" icon={RefreshCw} />
        <MetricCard title="Pendentes" value={stats.pending} subtitle="Aguardando validacao" icon={Wifi} />
        <MetricCard title="Ultima carga" value={formatCompactNumber(sources.reduce((sum, source) => sum + (source.lastSyncRecords ?? 0), 0))} subtitle="Registros processados nas ultimas sincronizacoes" icon={Plus} />
      </div>

      {error ? <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">{error}</div> : null}
      {message ? <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">{message}</div> : null}

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.5fr,1fr]">
        <section className="space-y-4">
          {loading ? (
            <div className="glass-card p-6 text-sm text-surface-400">Carregando fontes...</div>
          ) : sources.length ? (
            <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
              {sources.map((source) => (
                <div key={source.id} className="glass-card p-6">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex items-start gap-3">
                      <div className="rounded-xl bg-brand-600/20 p-3 text-brand-300">
                        <Database size={20} />
                      </div>
                      <div>
                        <h3 className="font-semibold text-white">{source.name}</h3>
                        <p className="text-xs text-surface-500">{source.type} - {source.companyName || 'Global'}</p>
                      </div>
                    </div>
                    <span className={getStatusTone(source.status)}>{getStatusLabel(source.status)}</span>
                  </div>

                  <div className="mt-4 space-y-2 text-sm">
                    <div className="flex justify-between text-surface-400">
                      <span>Agendamento</span>
                      <span className="text-surface-200">{source.syncSchedule || 'Manual'}</span>
                    </div>
                    <div className="flex justify-between text-surface-400">
                      <span>Ultima sincronizacao</span>
                      <span className="text-surface-200">{formatDateTime(source.lastSync)}</span>
                    </div>
                    <div className="flex justify-between text-surface-400">
                      <span>Registros processados</span>
                      <span className="text-surface-200">{formatCompactNumber(source.lastSyncRecords)}</span>
                    </div>
                  </div>

                  <div className="mt-4 flex gap-2 border-t border-surface-700/50 pt-4">
                    <button className="btn-secondary flex-1 px-4 py-2 text-xs" disabled={busyId === source.id} onClick={() => void handleTest(source.id)}>
                      Testar
                    </button>
                    <button className="btn-primary flex-1 px-4 py-2 text-xs" disabled={busyId === source.id} onClick={() => void handleSync(source.id)}>
                      {busyId === source.id ? 'Processando...' : 'Sincronizar'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <EmptyState title="Nenhuma fonte cadastrada" description="Cadastre ERP, CRM, API, banco legado ou arquivos para comecar a operacao." />
          )}
        </section>

        <section className="glass-card space-y-4 p-6">
          <div>
            <h2 className="text-lg font-semibold text-white">Nova fonte</h2>
            <p className="text-sm text-surface-400">Cadastre uma integracao para alimentar dashboards, KPIs e alertas.</p>
          </div>

          <form className="space-y-3" onSubmit={handleSubmit}>
            <input className="input-field" placeholder="Nome da fonte" value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} required />
            <textarea className="input-field min-h-20" placeholder="Descricao" value={form.description} onChange={(event) => setForm((current) => ({ ...current, description: event.target.value }))} />
            <select className="input-field" value={form.type} onChange={(event) => setForm((current) => ({ ...current, type: event.target.value }))}>
              <option value="ERP">ERP</option>
              <option value="CRM">CRM</option>
              <option value="SQL_DATABASE">Banco SQL</option>
              <option value="REST_API">API REST</option>
              <option value="CSV">CSV</option>
              <option value="EXCEL">Excel</option>
              <option value="SPREADSHEET">Planilha</option>
              <option value="WEBHOOK">Webhook</option>
            </select>
            <select className="input-field" value={form.companyId} onChange={(event) => setForm((current) => ({ ...current, companyId: event.target.value }))}>
              <option value="">Escopo global</option>
              {companies.map((company) => (
                <option key={company.id} value={company.id}>{company.name}</option>
              ))}
            </select>
            <input className="input-field" placeholder="Host" value={form.host} onChange={(event) => setForm((current) => ({ ...current, host: event.target.value }))} />
            <input className="input-field" placeholder="Porta" value={form.port} onChange={(event) => setForm((current) => ({ ...current, port: event.target.value }))} />
            <input className="input-field" placeholder="Banco / base" value={form.databaseName} onChange={(event) => setForm((current) => ({ ...current, databaseName: event.target.value }))} />
            <input className="input-field" placeholder="URL da API" value={form.apiUrl} onChange={(event) => setForm((current) => ({ ...current, apiUrl: event.target.value }))} />
            <input className="input-field" placeholder="Caminho do arquivo" value={form.filePath} onChange={(event) => setForm((current) => ({ ...current, filePath: event.target.value }))} />
            <input className="input-field" placeholder="Expressao de agendamento" value={form.syncSchedule} onChange={(event) => setForm((current) => ({ ...current, syncSchedule: event.target.value }))} />
            <button type="submit" className="btn-primary flex w-full items-center justify-center gap-2" disabled={saving}>
              <Plus size={16} />
              {saving ? 'Salvando...' : 'Cadastrar fonte'}
            </button>
          </form>
        </section>
      </div>
    </div>
  );
}
