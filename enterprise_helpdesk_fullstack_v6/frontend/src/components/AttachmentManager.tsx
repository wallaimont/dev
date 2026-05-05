import type { TicketAttachment } from '../types'

function formatSize(bytes: number) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

const API_ROOT = (import.meta.env.VITE_API_URL || 'http://localhost:8000/api/v1').replace('/api/v1', '')

export function AttachmentManager({
  attachments,
  onAdd,
  onRemove,
  canEdit,
}: {
  attachments: TicketAttachment[]
  onAdd: (file: File) => void
  onRemove: (id: number) => void
  canEdit: boolean
}) {
  function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const files = event.target.files
    if (!files || files.length === 0) return
    onAdd(files[0])
    event.target.value = ''
  }

  return (
    <div className="attachments-card">
      <div className="section-header">
        <div>
          <h3>Anexos</h3>
          <p className="muted">Upload real no back-end com armazenamento local da demonstração.</p>
        </div>
        {canEdit && <label className="secondary-button file-button">Adicionar<input type="file" hidden onChange={handleFileChange} /></label>}
      </div>

      <div className="attachments-list">
        {attachments.length === 0 ? (
          <p className="muted">Nenhum anexo enviado.</p>
        ) : (
          attachments.map((attachment) => (
            <div key={attachment.id} className="attachment-item">
              <div>
                <strong>{attachment.original_name}</strong>
                <div className="small muted">{formatSize(attachment.size_bytes)} • {new Date(attachment.created_at).toLocaleString('pt-BR')}</div>
              </div>
              <div className="attachment-actions">
                <a className="secondary-button" href={`${API_ROOT}/${attachment.file_path}`} target="_blank" rel="noreferrer">Abrir</a>
                {canEdit && <button className="secondary-button" type="button" onClick={() => onRemove(attachment.id)}>Remover</button>}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
