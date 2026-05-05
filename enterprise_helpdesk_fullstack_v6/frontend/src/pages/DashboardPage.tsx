
import { useEffect, useState } from 'react'
import { downloadAnalystsCsv, downloadAnalystsPdf, getExecutiveDashboard } from '../api/dashboard'
import { AnalystPerformanceTable } from '../components/AnalystPerformanceTable'
import { DashboardCharts } from '../components/DashboardCharts'
import { StatCard } from '../components/StatCard'
import { useAuth } from '../hooks/useAuth'
import { useNotify } from '../hooks/useNotify'
import type { DashboardExecutive } from '../types'

export function DashboardPage() {
  const [data, setData] = useState<DashboardExecutive | null>(null)
  const { user } = useAuth()
  const { notify } = useNotify()
  const canExport = user?.role === 'admin' || user?.role === 'manager'

  useEffect(() => {
    getExecutiveDashboard().then(setData)
  }, [])

  async function handleCsvExport() {
    await downloadAnalystsCsv()
    notify('Relatório CSV baixado.', 'success')
  }

  async function handlePdfExport() {
    await downloadAnalystsPdf()
    notify('Relatório PDF baixado.', 'success')
  }

  if (!data) return <div className="loading">Carregando dashboard...</div>

  const metrics = data.totals

  return (
    <div className="page-grid">
      <header className="page-header">
        <div>
          <h1>Dashboard Executivo</h1>
          <p className="muted">Painel corporativo com visão operacional, exportações gerenciais e produtividade dos analistas.</p>
        </div>
        {canExport && (
          <div className="actions-row">
            <button className="secondary-button" onClick={handleCsvExport}>Exportar CSV</button>
            <button className="secondary-button" onClick={handlePdfExport}>Exportar PDF</button>
          </div>
        )}
      </header>

      <section className="stats-grid">
        <StatCard label="Total de chamados" value={metrics.total_tickets} helper="Volume total registrado" />
        <StatCard label="Abertos" value={metrics.open_tickets} helper="Demandas novas" />
        <StatCard label="Em andamento" value={metrics.in_progress_tickets} helper="Fila ativa" />
        <StatCard label="Resolvidos" value={metrics.resolved_tickets} helper="Entregas concluídas" />
        <StatCard label="Atrasados" value={metrics.overdue_tickets} helper="Acima do SLA" />
        <StatCard label="Críticos" value={metrics.critical_tickets} helper="Incidentes prioritários" />
      </section>

      <DashboardCharts data={data} />
      <AnalystPerformanceTable rows={data.analyst_performance} />
    </div>
  )
}
