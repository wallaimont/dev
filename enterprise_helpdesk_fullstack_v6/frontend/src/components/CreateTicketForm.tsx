import { useEffect, useState } from 'react'
import { getCompanies } from '../api/companies'
import { getAssignableUsers } from '../api/users'
import { useAuth } from '../hooks/useAuth'
import type { Company, CreateTicketPayload, TicketPriority, User } from '../types'

const priorities: { value: TicketPriority; label: string; sla: number }[] = [
  { value: 'low', label: 'Baixa', sla: 72 },
  { value: 'medium', label: 'Média', sla: 24 },
  { value: 'high', label: 'Alta', sla: 8 },
  { value: 'critical', label: 'Crítica', sla: 4 },
]

export function CreateTicketForm({
  onSubmit,
  submitting,
}: {
  onSubmit: (payload: CreateTicketPayload) => Promise<void>
  submitting: boolean
}) {
  const { user } = useAuth()
  const [companies, setCompanies] = useState<Company[]>([])
  const [assignees, setAssignees] = useState<User[]>([])
  const [form, setForm] = useState<CreateTicketPayload>({
    title: '',
    description: '',
    priority: 'medium',
    company_id: 1,
    assigned_to_id: null,
    sla_hours: 24,
  })

  useEffect(() => {
    getCompanies().then((data) => {
      setCompanies(data)
      if (data.length && !data.some((company) => company.id === form.company_id)) {
        setForm((prev) => ({ ...prev, company_id: data[0].id }))
      }
    })
  }, [])

  useEffect(() => {
    if (user?.role === 'client') return
    getAssignableUsers(form.company_id).then(setAssignees).catch(() => setAssignees([]))
  }, [form.company_id, user?.role])

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    await onSubmit(form)
    setForm((prev) => ({ ...prev, title: '', description: '', priority: 'medium', assigned_to_id: null, sla_hours: 24 }))
  }

  const canAssign = user?.role && user.role !== 'client'

  return (
    <form className="card form-card" onSubmit={handleSubmit}>
      <div className="section-header">
        <div>
          <h2>Novo chamado</h2>
          <p className="muted">Crie tickets com SLA padrão por prioridade e atribuição inicial.</p>
        </div>
      </div>

      <label>
        <span>Título</span>
        <input
          value={form.title}
          onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))}
          placeholder="Ex.: Erro na integração de pedidos"
          minLength={5}
          required
        />
      </label>

      <label>
        <span>Descrição</span>
        <textarea
          value={form.description}
          onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))}
          placeholder="Descreva o problema com detalhes"
          minLength={10}
          rows={5}
          required
        />
      </label>

      <div className="form-grid">
        <label>
          <span>Prioridade</span>
          <select value={form.priority} onChange={(e) => { const value = e.target.value as TicketPriority; const found = priorities.find((p) => p.value === value); setForm((prev) => ({ ...prev, priority: value, sla_hours: found?.sla ?? prev.sla_hours }))}}>
            {priorities.map((priority) => (
              <option key={priority.value} value={priority.value}>{priority.label}</option>
            ))}
          </select>
        </label>

        <label>
          <span>Empresa</span>
          <select value={form.company_id} onChange={(e) => setForm((prev) => ({ ...prev, company_id: Number(e.target.value), assigned_to_id: null }))}>
            {companies.map((company) => (
              <option key={company.id} value={company.id}>{company.name}</option>
            ))}
          </select>
        </label>

        <label>
          <span>SLA (horas)</span>
          <input type="number" min={1} max={720} value={form.sla_hours ?? 24} onChange={(e) => setForm((prev) => ({ ...prev, sla_hours: Number(e.target.value) }))} />
        </label>

        {canAssign ? (
          <label>
            <span>Responsável inicial</span>
            <select value={form.assigned_to_id ?? ''} onChange={(e) => setForm((prev) => ({ ...prev, assigned_to_id: e.target.value ? Number(e.target.value) : null }))}>
              <option value="">Não atribuído</option>
              {assignees.map((analyst) => (
                <option key={analyst.id} value={analyst.id}>{analyst.full_name}</option>
              ))}
            </select>
          </label>
        ) : null}
      </div>

      <button className="primary-button" type="submit" disabled={submitting}>
        {submitting ? 'Salvando...' : 'Criar chamado'}
      </button>
    </form>
  )
}
