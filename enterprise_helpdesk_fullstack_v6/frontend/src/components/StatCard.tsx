export function StatCard({ title, value, hint }: { title: string; value: number; hint: string }) {
  return (
    <div className="card stat-card">
      <span className="muted">{title}</span>
      <strong>{value}</strong>
      <span className="small">{hint}</span>
    </div>
  )
}
