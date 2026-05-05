import { statusOptions } from '../api/tickets'
import type { Company, TicketFilters, TicketPriority, TicketsViewMode, UserRole } from '../types'

const priorities: { value: TicketPriority; label: string }[] = [
  { value: 'low', label: 'Baixa' },
  { value: 'medium', label: 'Média' },
  { value: 'high', label: 'Alta' },
  { value: 'critical', label: 'Crítica' },
]

export function TicketFiltersBar({
  filters,
  companies,
  onChange,
  viewMode,
  onChangeView,
  userRole,
}: {
  filters: TicketFilters
  companies: Company[]
  onChange: (filters: TicketFilters) => void
  viewMode: TicketsViewMode
  onChangeView: (mode: TicketsViewMode) => void
  userRole?: UserRole
}) {
  const showCompanyFilter = userRole !== 'client'

  return (
    <div className="card toolbar-card">
      <div className="toolbar-grid">
        <label>
          <span>Buscar</span>
          <input
            value={filters.search}
            onChange={(e) => onChange({ ...filters, search: e.target.value })}
            placeholder="Título, descrição ou ID"
          />
        </label>

        <label>
          <span>Status</span>
          <select value={filters.status} onChange={(e) => onChange({ ...filters, status: e.target.value as TicketFilters['status'] })}>
            <option value="all">Todos</option>
            {statusOptions.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
          </select>
        </label>

        <label>
          <span>Prioridade</span>
          <select value={filters.priority} onChange={(e) => onChange({ ...filters, priority: e.target.value as TicketFilters['priority'] })}>
            <option value="all">Todas</option>
            {priorities.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
          </select>
        </label>

        {showCompanyFilter && (
          <label>
            <span>Empresa</span>
            <select value={String(filters.companyId)} onChange={(e) => onChange({ ...filters, companyId: e.target.value === 'all' ? 'all' : Number(e.target.value) })}>
              <option value="all">Todas</option>
              {companies.map((company) => <option key={company.id} value={company.id}>{company.name}</option>)}
            </select>
          </label>
        )}
      </div>

      <div className="toolbar-actions">
        <button className={viewMode === 'table' ? 'primary-button' : 'secondary-button'} onClick={() => onChangeView('table')}>Tabela</button>
        <button className={viewMode === 'kanban' ? 'primary-button' : 'secondary-button'} onClick={() => onChangeView('kanban')}>Kanban</button>
      </div>
    </div>
  )
}
