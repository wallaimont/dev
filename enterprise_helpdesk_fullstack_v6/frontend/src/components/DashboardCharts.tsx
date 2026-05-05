import type { DashboardExecutive } from '../types'

function percent(value: number, total: number) {
  if (!total) return 0
  return Math.round((value / total) * 100)
}

export function DashboardCharts({ data }: { data: DashboardExecutive }) {
  const total = data.totals.total_tickets
  const maxTrend = Math.max(1, ...data.trends.flatMap((item) => [item.opened, item.resolved]))

  return (
    <div className="charts-grid">
      <section className="card chart-card">
        <div className="section-header">
          <div>
            <h3>Status dos chamados</h3>
            <p className="muted">Distribuição do volume atual por etapa de atendimento.</p>
          </div>
        </div>
        <div className="bars-list">
          {data.status_breakdown.map((item) => (
            <div key={item.status} className="bar-row">
              <div className="bar-label-line">
                <span>{item.status}</span>
                <span>{item.total} • {percent(item.total, total)}%</span>
              </div>
              <div className="bar-track">
                <div className="bar-fill" style={{ width: `${percent(item.total, total)}%` }} />
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="card chart-card">
        <div className="section-header">
          <div>
            <h3>Indicadores executivos</h3>
            <p className="muted">Métricas de SLA e resolução média para gestão.</p>
          </div>
        </div>
        <div className="kpi-ring-list">
          <article className="gauge-card">
            <div className="gauge-ring"><div className="gauge-inner"><strong>{data.avg_sla_hours}h</strong></div></div>
            <div><strong>SLA médio</strong><div className="small muted">Horas prometidas por ticket</div></div>
          </article>
          <article className="gauge-card">
            <div className="gauge-ring"><div className="gauge-inner"><strong>{data.avg_resolution_hours}h</strong></div></div>
            <div><strong>Resolução média</strong><div className="small muted">Tempo médio até concluir</div></div>
          </article>
          <article className="gauge-card">
            <div className="gauge-ring"><div className="gauge-inner"><strong>{data.totals.overdue_tickets}</strong></div></div>
            <div><strong>Chamados atrasados</strong><div className="small muted">Demandas acima do prazo</div></div>
          </article>
        </div>
      </section>

      <section className="card chart-card chart-card-wide">
        <div className="section-header">
          <div>
            <h3>Tendência de 7 dias</h3>
            <p className="muted">Aberturas e resoluções recentes para leitura operacional.</p>
          </div>
        </div>
        <div className="trend-grid">
          {data.trends.map((point) => (
            <div key={point.day} className="trend-day">
              <div className="trend-bars">
                <div className="trend-bar-group">
                  <div className="trend-bar trend-opened" style={{ height: `${Math.max(10, (point.opened / maxTrend) * 140)}px` }} />
                  <span className="small muted">{point.opened}</span>
                </div>
                <div className="trend-bar-group">
                  <div className="trend-bar trend-resolved" style={{ height: `${Math.max(10, (point.resolved / maxTrend) * 140)}px` }} />
                  <span className="small muted">{point.resolved}</span>
                </div>
              </div>
              <div className="small muted">{new Date(point.day).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })}</div>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
