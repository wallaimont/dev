import type { TicketActivity } from '../types'

const labels: Record<string, string> = {
  CREATE: 'Criação',
  UPDATE: 'Atualização',
  COMMENT: 'Comentário',
  UPLOAD: 'Upload',
  DELETE: 'Remoção',
}

export function ActivityTimeline({ activity }: { activity: TicketActivity[] }) {
  return (
    <div className="comments-block">
      <div className="section-header">
        <div>
          <h3>Histórico de atividades</h3>
          <p className="muted">Linha do tempo automática do ticket.</p>
        </div>
      </div>
      <div className="comments-list">
        {activity.length === 0 ? (
          <p className="muted">Sem movimentações registradas.</p>
        ) : (
          activity.map((item) => (
            <article key={item.id} className="comment-item">
              <div className="comment-meta">
                <strong>{labels[item.action] ?? item.action}</strong>
                <span className="small muted">{new Date(item.created_at).toLocaleString('pt-BR')}</span>
              </div>
              <p>{item.description || 'Ação executada no ticket.'}</p>
              <span className="small muted">Por: {item.performed_by}</span>
            </article>
          ))
        )}
      </div>
    </div>
  )
}
