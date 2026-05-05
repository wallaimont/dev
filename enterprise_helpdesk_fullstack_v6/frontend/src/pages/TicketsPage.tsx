import { useEffect, useMemo, useState } from 'react'
import { getCompanies } from '../api/companies'
import { getAssignableUsers } from '../api/users'
import { addComment, approveCloseTicket, createTicket, deleteAttachment, getTicket, getTicketActivity, getTickets, requestCloseTicket, updateTicket, uploadAttachment } from '../api/tickets'
import { CreateTicketForm } from '../components/CreateTicketForm'
import { TicketDetailsPanel } from '../components/TicketDetailsPanel'
import { TicketsTable } from '../components/TicketsTable'
import { TicketFiltersBar } from '../components/TicketFiltersBar'
import { TicketsKanban } from '../components/TicketsKanban'
import { useAuth } from '../hooks/useAuth'
import { useNotify } from '../hooks/useNotify'
import { useRealtime } from '../hooks/useRealtime'
import type { Company, CreateCommentPayload, CreateTicketPayload, Ticket, TicketActivity, TicketFilters, TicketsViewMode, UpdateTicketPayload, User } from '../types'

const PAGE_SIZE = 8

export function TicketsPage() {
  const { user } = useAuth()
  const { notify } = useNotify()
  const [tickets, setTickets] = useState<Ticket[]>([])
  const [companies, setCompanies] = useState<Company[]>([])
  const [analysts, setAnalysts] = useState<User[]>([])
  const [activity, setActivity] = useState<TicketActivity[]>([])
  const [selectedTicketId, setSelectedTicketId] = useState<number | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [saving, setSaving] = useState(false)
  const [commenting, setCommenting] = useState(false)
  const [filters, setFilters] = useState<TicketFilters>({ search: '', status: 'all', priority: 'all', companyId: 'all' })
  const [page, setPage] = useState(1)
  const [viewMode, setViewMode] = useState<TicketsViewMode>('table')

  const selectedTicket = useMemo(() => tickets.find((ticket) => ticket.id === selectedTicketId) ?? null, [tickets, selectedTicketId])

  async function loadBase(keepSelection = true) {
    const [ticketData, companyData] = await Promise.all([getTickets(), getCompanies()])
    setTickets(ticketData)
    setCompanies(companyData)
    setSelectedTicketId((prev) => keepSelection ? (prev ?? ticketData[0]?.id ?? null) : (ticketData[0]?.id ?? null))
  }

  async function refreshSelected(ticketId: number) {
    const [data, activityData] = await Promise.all([getTicket(ticketId), getTicketActivity(ticketId)])
    setTickets((prev) => prev.map((ticket) => (ticket.id === ticketId ? data : ticket)))
    setActivity(activityData)
    if (user?.role !== 'client') {
      const team = await getAssignableUsers(data.company_id).catch(() => [])
      setAnalysts(team)
    }
  }

  useEffect(() => {
    loadBase()
  }, [])

  useEffect(() => {
    if (selectedTicketId) refreshSelected(selectedTicketId)
  }, [selectedTicketId])

  useRealtime(async (event) => {
    const impactedId = typeof event.ticket_id === 'number' ? event.ticket_id : Number(event.ticket_id)
    if (Number.isFinite(impactedId) && impactedId) {
      await loadBase()
      if (selectedTicketId === impactedId) await refreshSelected(impactedId)
    }
  })

  useEffect(() => { setPage(1) }, [filters])

  async function handleCreateTicket(payload: CreateTicketPayload) {
    setSubmitting(true)
    try {
      const created = await createTicket(payload)
      await loadBase()
      setSelectedTicketId(created.id)
      notify('Chamado criado com sucesso.', 'success')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleSave(payload: UpdateTicketPayload) {
    if (!selectedTicketId) return
    setSaving(true)
    try {
      await updateTicket(selectedTicketId, payload)
      await refreshSelected(selectedTicketId)
      setTickets(await getTickets())
      notify('Chamado atualizado.', 'success')
    } finally {
      setSaving(false)
    }
  }

  async function handleAddComment(payload: CreateCommentPayload) {
    if (!selectedTicketId) return
    setCommenting(true)
    try {
      await addComment(selectedTicketId, payload)
      await refreshSelected(selectedTicketId)
      setTickets(await getTickets())
      notify('Comentário adicionado.', 'success')
    } finally {
      setCommenting(false)
    }
  }

  async function handleAddAttachment(file: File) {
    if (!selectedTicketId) return
    await uploadAttachment(selectedTicketId, file)
    await refreshSelected(selectedTicketId)
    setTickets(await getTickets())
    notify('Anexo enviado para o servidor.', 'success')
  }

  async function handleRemoveAttachment(id: number) {
    if (!selectedTicketId) return
    await deleteAttachment(selectedTicketId, id)
    await refreshSelected(selectedTicketId)
    setTickets(await getTickets())
    notify('Anexo removido do servidor.', 'info')
  }

  const filteredTickets = useMemo(() => {
    const search = filters.search.trim().toLowerCase()
    return tickets.filter((ticket) => {
      const matchesSearch = !search || `${ticket.id}`.includes(search) || ticket.title.toLowerCase().includes(search) || ticket.description.toLowerCase().includes(search)
      const matchesStatus = filters.status === 'all' || ticket.status === filters.status
      const matchesPriority = filters.priority === 'all' || ticket.priority === filters.priority
      const matchesCompany = filters.companyId === 'all' || ticket.company_id === filters.companyId
      return matchesSearch && matchesStatus && matchesPriority && matchesCompany
    })
  }, [tickets, filters])

  const totalPages = Math.max(1, Math.ceil(filteredTickets.length / PAGE_SIZE))
  const pagedTickets = useMemo(() => filteredTickets.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE), [filteredTickets, page])

  useEffect(() => { if (page > totalPages) setPage(totalPages) }, [page, totalPages])

  const selectionPool = viewMode === 'table' ? pagedTickets : filteredTickets
  useEffect(() => {
    if (selectedTicketId && filteredTickets.some((ticket) => ticket.id === selectedTicketId)) return
    setSelectedTicketId(selectionPool[0]?.id ?? null)
  }, [selectedTicketId, filteredTickets, selectionPool])

  return (
    <div className="page-grid tickets-layout">
      <div className="tickets-main-column">
        <CreateTicketForm onSubmit={handleCreateTicket} submitting={submitting} />
        <TicketFiltersBar filters={filters} companies={companies} onChange={setFilters} viewMode={viewMode} onChangeView={setViewMode} userRole={user?.role} />

        {viewMode === 'table' ? (
          <>
            <TicketsTable tickets={pagedTickets} companies={companies} selectedTicketId={selectedTicketId} onSelect={(ticket) => setSelectedTicketId(ticket.id)} />
            <div className="pagination-row">
              <span className="muted small">{filteredTickets.length} resultado(s)</span>
              <div className="pagination-actions">
                <button className="secondary-button" disabled={page === 1} onClick={() => setPage((prev) => Math.max(1, prev - 1))}>Anterior</button>
                <span className="small muted">Página {page} de {totalPages}</span>
                <button className="secondary-button" disabled={page === totalPages} onClick={() => setPage((prev) => Math.min(totalPages, prev + 1))}>Próxima</button>
              </div>
            </div>
          </>
        ) : (
          <TicketsKanban tickets={filteredTickets} selectedTicketId={selectedTicketId} onSelect={(ticket) => setSelectedTicketId(ticket.id)} />
        )}
      </div>
      <TicketDetailsPanel
        ticket={selectedTicket}
        analysts={analysts}
        activity={activity}
        onSave={handleSave}
        onAddComment={handleAddComment}
        onAddAttachment={handleAddAttachment}
        onRemoveAttachment={handleRemoveAttachment}
        saving={saving}
        commenting={commenting}
        currentUserRole={user?.role}
      />
    </div>
  )
}
