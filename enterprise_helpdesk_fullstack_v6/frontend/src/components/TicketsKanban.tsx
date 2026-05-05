import { statusOptions } from '../api/tickets'
import type { Ticket } from '../types'

export function TicketsKanban({
  tickets,
  selectedTicketId,
  onSelect,
}: {
  tickets: Ticket[]
  selectedTicketId: number | null
  onSelect: (ticket: Ticket) => void
}) {
  return (
    <div className="kanban-grid">
      {statusOptions.map((column) => {
        const items = tickets.filter((ticket) => ticket.status === column.value)
        return (
          <section className="card kanban-column" key={column.value}>
            <div className="section-header">
              <div>
                <h3>{column.label}</h3>
                <p className="muted">{items.length} chamado(s)</p>
              </div>
            </div>
            <div className="kanban-list">
              {items.map((ticket) => (
                <button
                  type="button"
                  key={ticket.id}
                  className={`kanban-ticket ${selectedTicketId === ticket.id ? 'kanban-ticket-active' : ''}`}
                  onClick={() => onSelect(ticket)}
                >
                  <div className="kanban-ticket-top">
                    <strong>#{ticket.id}</strong>
                    <span className={`badge priority-${ticket.priority}`}>{ticket.priority}</span>
                  </div>
                  <div>{ticket.title}</div>
                  <span className="small muted truncate">{ticket.description}</span>
                </button>
              ))}
              {items.length === 0 && <p className="muted small">Sem chamados nesta etapa.</p>}
            </div>
          </section>
        )
      })}
    </div>
  )
}
