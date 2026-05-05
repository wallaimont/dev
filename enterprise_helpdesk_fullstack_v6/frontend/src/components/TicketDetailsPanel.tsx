
import { useEffect, useMemo, useState } from 'react'
import { statusOptions } from '../api/tickets'
import type { CreateCommentPayload, Ticket, TicketActivity, TicketPriority, TicketStatus, UpdateTicketPayload, User, UserRole } from '../types'
import { ActivityTimeline } from './ActivityTimeline'
import { AttachmentManager } from './AttachmentManager'

const priorities: { value: TicketPriority; label: string; sla: number }[] = [
  { value: 'low', label: 'Baixa', sla: 72 },
  { value: 'medium', label: 'Média', sla: 24 },
  { value: 'high', label: 'Alta', sla: 8 },
  { value: 'critical', label: 'Crítica', sla: 4 },
]

export function TicketDetailsPanel({
  ticket,
  analysts,
  activity,
  onSave,
  onAddComment,
  onAddAttachment,
  onRemoveAttachment,
  onRequestClose,
  onApproveClose,
  saving,
  commenting,
  currentUserRole,
}: {
  ticket: Ticket | null
  analysts: User[]
  activity: TicketActivity[]
  onSave: (payload: UpdateTicketPayload) => Promise<void>
  onAddComment: (payload: CreateCommentPayload) => Promise<void>
  onAddAttachment: (file: File) => void
  onRemoveAttachment: (id: number) => void
  onRequestClose: () => Promise<void>
  onApproveClose: () => Promise<void>
  saving: boolean
  commenting: boolean
  currentUserRole?: UserRole
}) {
  const [form, setForm] = useState<UpdateTicketPayload>({})
  const [comment, setComment] = useState('')
  const canEditTicket = currentUserRole !== 'client'
  const canAssign = ['admin', 'manager', 'analyst'].includes(currentUserRole ?? '')
  const canApprove = ['admin', 'manager'].includes(currentUserRole ?? '')
  const canRequestClose = !!ticket && ticket.status !== 'closed'
  const closureRequested = !!ticket?.closure_requested_at

  useEffect(() => {
    if (!ticket) return
    setForm({
      title: ticket.title,
      description: ticket.description,
      status: ticket.status,
      priority: ticket.priority,
      assigned_to_id: ticket.assigned_to_id ?? null,
      sla_hours: ticket.sla_hours,
    })
    setComment('')
  }, [ticket])

  const createdAtLabel = useMemo(() => ticket ? new Date(ticket.created_at).toLocaleString('pt-BR') : '', [ticket])

  if (!ticket) {
    return (
      <section className="card details-card empty-state">
        <h2>Detalhes do chamado</h2>
        <p className="muted">Selecione um ticket na tabela ou no kanban para editar, anexar evidências, aprovar encerramento e registrar comentários.</p>
      </section>
    )
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    if (!canEditTicket) return
    await onSave(form)
  }

  async function handleCommentSubmit(event: React.FormEvent) {
    event.preventDefault()
    if (!comment.trim()) return
    await onAddComment({ content: comment.trim() })
    setComment('')
  }

  return (
    <section className="card details-card">
      <div className="section-header">
        <div>
          <h2>Chamado #{ticket.id}</h2>
          <p className="muted">Criado em {createdAtLabel}</p>
        </div>
        <span className={`badge status-${ticket.status}`}>{statusOptions.find((item) => item.value === ticket.status)?.label}</span>
      </div>

      <div className="actions-row">
        {canRequestClose && <button type="button" className="secondary-button" onClick={onRequestClose}>Solicitar encerramento</button>}
        {canApprove && closureRequested && ticket.status !== 'closed' && (
          <button type="button" className="primary-button" onClick={onApproveClose}>Aprovar encerramento</button>
        )}
      </div>

      {closureRequested && (
        <div className="inline-info">
          <strong>Fluxo de encerramento:</strong> solicitado em {ticket.closure_requested_at ? new Date(ticket.closure_requested_at).toLocaleString('pt-BR') : '-'}
          {ticket.closure_approved_at && <> • aprovado em {new Date(ticket.closure_approved_at).toLocaleString('pt-BR')}</>}
        </div>
      )}

      <form className="details-form" onSubmit={handleSubmit}>
        <label>
          <span>Título</span>
          <input value={form.title ?? ''} onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))} minLength={5} required disabled={!canEditTicket} />
        </label>

        <label>
          <span>Descrição</span>
          <textarea value={form.description ?? ''} onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))} minLength={10} rows={4} required disabled={!canEditTicket} />
        </label>

        <div className="form-grid compact">
          <label>
            <span>Status</span>
            <select value={form.status ?? ticket.status} onChange={(e) => setForm((prev) => ({ ...prev, status: e.target.value as TicketStatus }))} disabled={!canEditTicket}>
              {statusOptions.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
            </select>
          </label>

          <label>
            <span>Prioridade</span>
            <select value={form.priority ?? ticket.priority} onChange={(e) => { const value = e.target.value as TicketPriority; const found = priorities.find((p) => p.value === value); setForm((prev) => ({ ...prev, priority: value, sla_hours: found?.sla ?? prev.sla_hours })) }} disabled={!canEditTicket}>
              {priorities.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
            </select>
          </label>

          <label>
            <span>Responsável</span>
            <select value={form.assigned_to_id ?? ''} onChange={(e) => setForm((prev) => ({ ...prev, assigned_to_id: e.target.value ? Number(e.target.value) : null }))} disabled={!canAssign}>
              <option value="">Não atribuído</option>
              {analysts.map((analyst) => <option key={analyst.id} value={analyst.id}>{analyst.full_name}</option>)}
            </select>
          </label>

          <label>
            <span>SLA (horas)</span>
            <input type="number" value={form.sla_hours ?? ticket.sla_hours} onChange={(e) => setForm((prev) => ({ ...prev, sla_hours: Number(e.target.value) }))} min={1} max={720} disabled={!canEditTicket || currentUserRole === 'client'} />
          </label>
        </div>

        <div className="actions-row">
          <button className="primary-button" type="submit" disabled={saving || !canEditTicket}>{saving ? 'Salvando...' : 'Salvar alterações'}</button>
        </div>
      </form>

      <AttachmentManager ticket={ticket} onAddAttachment={onAddAttachment} onRemoveAttachment={onRemoveAttachment} />
      <ActivityTimeline items={activity} />

      <form className="comment-form" onSubmit={handleCommentSubmit}>
        <label>
          <span>Novo comentário</span>
          <textarea rows={3} value={comment} onChange={(e) => setComment(e.target.value)} placeholder="Registre contexto, evidências ou atualização para a equipe." />
        </label>
        <button className="secondary-button" type="submit" disabled={commenting}>{commenting ? 'Enviando...' : 'Adicionar comentário'}</button>
      </form>
    </section>
  )
}
