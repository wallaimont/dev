
import type { AnalystPerformance } from '../types'

export function AnalystPerformanceTable({ rows }: { rows: AnalystPerformance[] }) {
  return (
    <section className="card table-card">
      <div className="section-header">
        <div>
          <h3>Indicadores por analista</h3>
          <p className="muted">Carteira ativa, resoluções e atrasos por responsável.</p>
        </div>
      </div>

      <div className="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Analista</th>
              <th>Atribuídos</th>
              <th>Ativos</th>
              <th>Resolvidos</th>
              <th>Atrasados</th>
              <th>Tempo médio</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.analyst_id}>
                <td>{row.analyst_name}</td>
                <td>{row.assigned_total}</td>
                <td>{row.active_load}</td>
                <td>{row.resolved_total}</td>
                <td>{row.overdue_total}</td>
                <td>{row.avg_resolution_hours}h</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
