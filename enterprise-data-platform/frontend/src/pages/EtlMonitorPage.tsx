import React, { useEffect, useState } from 'react';
import { AlertTriangle, CheckCircle2, Clock3, Layers, RotateCcw } from 'lucide-react';
import api from '../services/api';
import type { EtlOverview, IntegrationLog, PageResponse } from '../types';
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

export default function EtlMonitorPage() {
  const [overview, setOverview] = useState<EtlOverview | null>(null);
  const [logs, setLogs] = useState<IntegrationLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState<number | null>(null);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [overviewResponse, logsResponse] = await Promise.all([
        api.get<EtlOverview>('/etl/overview'),
        api.get<PageResponse<IntegrationLog>>('/etl/logs', { params: { size: 20 } }),
      ]);
      setOverview(overviewResponse.data);
      setLogs(logsResponse.data.content);
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel carregar o monitor de ETL.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, []);

  const handleReplay = async (dataSourceId: number) => {
    setBusyId(dataSourceId);
    try {
      await api.post(`/etl/execute/${dataSourceId}`);
      await loadData();
    } catch (requestError) {
      setError(extractErrorMessage(requestError, 'Nao foi possivel reprocessar a carga.'));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <SectionTitle
        title="Monitor ETL"
        description="Acompanhe a extracao, transformacao e carga com dados reais da operacao."
      />

      <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
        <MetricCard title="Execucoes" value={overview?.totalExecutions ?? 0} subtitle="Historico acumulado" icon={Layers} />
        <MetricCard title="Sucesso" value={overview?.successfulExecutions ?? 0} subtitle="Cargas completas" icon={CheckCircle2} />
        <MetricCard title="Falhas" value={overview?.failedExecutions ?? 0} subtitle="Operacoes com erro" icon={AlertTriangle} />
        <MetricCard title="Em execucao" value={overview?.runningExecutions ?? 0} subtitle="Filas e processos ativos" icon={Clock3} />
      </div>

      {error ? <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">{error}</div> : null}

      <section className="glass-card overflow-hidden">
        <div className="flex items-center justify-between border-b border-surface-700/50 p-5">
          <div>
            <h2 className="font-semibold text-white">Historico de execucoes</h2>
            <p className="text-sm text-surface-400">Ultimas cargas processadas por fonte.</p>
          </div>
          <button className="btn-secondary px-4 py-2 text-xs" onClick={() => void loadData()}>
            Atualizar
          </button>
        </div>

        {loading ? (
          <div className="p-6 text-sm text-surface-400">Carregando historico...</div>
        ) : logs.length ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-surface-700/50">
                  <th className="px-5 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Fonte</th>
                  <th className="px-5 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Status</th>
                  <th className="px-5 py-3 text-right text-xs uppercase tracking-wide text-surface-400">Extraidos</th>
                  <th className="px-5 py-3 text-right text-xs uppercase tracking-wide text-surface-400">Carregados</th>
                  <th className="px-5 py-3 text-right text-xs uppercase tracking-wide text-surface-400">Rejeitados</th>
                  <th className="px-5 py-3 text-right text-xs uppercase tracking-wide text-surface-400">Duracao</th>
                  <th className="px-5 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Trigger</th>
                  <th className="px-5 py-3 text-left text-xs uppercase tracking-wide text-surface-400">Data</th>
                  <th className="px-5 py-3 text-center text-xs uppercase tracking-wide text-surface-400">Acao</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id} className="border-b border-surface-700/20">
                    <td className="px-5 py-3 text-white">{log.dataSourceName}</td>
                    <td className="px-5 py-3"><span className={getStatusTone(log.status)}>{getStatusLabel(log.status)}</span></td>
                    <td className="px-5 py-3 text-right text-surface-300">{formatCompactNumber(log.recordsExtracted)}</td>
                    <td className="px-5 py-3 text-right text-surface-300">{formatCompactNumber(log.recordsLoaded)}</td>
                    <td className="px-5 py-3 text-right text-red-300">{formatCompactNumber(log.recordsRejected)}</td>
                    <td className="px-5 py-3 text-right text-surface-300">{log.durationMs ? `${(log.durationMs / 1000).toFixed(1)}s` : 'N/A'}</td>
                    <td className="px-5 py-3 text-surface-400">{log.triggeredBy || 'N/A'}</td>
                    <td className="px-5 py-3 text-surface-400">{formatDateTime(log.createdAt)}</td>
                    <td className="px-5 py-3 text-center">
                      <button className="btn-secondary px-3 py-2 text-xs" disabled={busyId === log.dataSourceId} onClick={() => void handleReplay(log.dataSourceId)}>
                        <RotateCcw size={14} className="inline" /> {busyId === log.dataSourceId ? '...' : 'Reprocessar'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="p-6">
            <EmptyState title="Sem execucoes registradas" description="Execute uma carga em Fontes de Dados ou na Central de Integracoes para preencher o historico." />
          </div>
        )}
      </section>
    </div>
  );
}
