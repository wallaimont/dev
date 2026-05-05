import type { Company, Ticket } from '../types'

const statusLabel: Record<string, string> = {
  open: 'Aberto',
  in_progress: 'Em andamento',
  waiting_client: 'Aguardando cliente',
  resolved: 'Resolvido',
  closed: 'Fechado',
}

const priorityLabel: Record<string, string> = {
  low: 'Baixa',
  medium: 'Média',
  high: 'Alta',
  critical: 'Crítica',
}

export function TicketsTable({
  tickets,
  companies,
  selectedTicketId,
  onSelect,
}: {
  tickets: Ticket[]
  companies: Company[]
  selectedTicketId: number | null
  onSelect: (ticket: Ticket) => void
}) {
  const companyNameById = Object.fromEntries(companies.map((company) => [company.id, company.name]))

  return (
    <div className="card table-card">
      <div className="section-header">
        <div>
          <h2>Chamados</h2>
          <p className="muted">Selecione uma linha para editar e colaborar no atendimento.</p>
        </div>
      </div>

      <div className="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Título</th>
              <th>Status</th>
              <th>Prioridade</th>
              <th>Empresa</th>
              <th>Comentários</th>
              <th>SLA</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((ticket) => (
              <tr
                key={ticket.id}
                onClick={() => onSelect(ticket)}
                className={selectedTicketId === ticket.id ? 'selected-row' : ''}
              >
                <td>#{ticket.id}</td>
                <td>
                  <div className="table-title">{ticket.title}</div>
                  <div className="small muted truncate">{ticket.description}</div>
                </td>
                <td><span className={`badge status-${ticket.status}`}>{statusLabel[ticket.status]}</span></td>
                <td><span className={`badge priority-${ticket.priority}`}>{priorityLabel[ticket.priority]}</span></td>
                <td>{companyNameById[ticket.company_id] ?? `Empresa ${ticket.company_id}`}</td>
                <td>{ticket.comments.length}</td>
                <td>{ticket.sla_hours}h</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
