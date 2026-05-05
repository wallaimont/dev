import { useState, useRef, useEffect } from 'react'
import axios from 'axios'
import { FileUp, Link, Loader2, CheckCircle, AlertCircle, Database, Upload, Globe } from 'lucide-react'

export default function DocsPage() {
  const [activeTab, setActiveTab] = useState('upload')
  const [url, setUrl] = useState('')
  const [uploading, setUploading] = useState(false)
  const [ingesting, setIngesting] = useState(false)
  const [uploadResult, setUploadResult] = useState(null)
  const [urlResult, setUrlResult] = useState(null)
  const [error, setError] = useState(null)
  const [stats, setStats] = useState(null)
  const [dragOver, setDragOver] = useState(false)
  const fileInputRef = useRef(null)

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    try {
      const res = await axios.get('/api/docs/stats')
      setStats(res.data)
    } catch {}
  }

  const handleFileUpload = async (file) => {
    if (!file) return
    if (!file.name.endsWith('.pdf')) {
      setError('Apenas arquivos PDF são aceitos.')
      return
    }
    if (file.size > 50 * 1024 * 1024) {
      setError('Arquivo muito grande. Máximo 50MB.')
      return
    }
    setError(null)
    setUploadResult(null)
    setUploading(true)
    try {
      const formData = new FormData()
      formData.append('file', file)
      const res = await axios.post('/api/docs/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      setUploadResult(res.data)
      loadStats()
    } catch (err) {
      setError(err.response?.data?.detail || err.message)
    } finally {
      setUploading(false)
    }
  }

  const handleUrlIngest = async () => {
    if (!url.trim() || ingesting) return
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      setError('URL deve começar com http:// ou https://')
      return
    }
    setError(null)
    setUrlResult(null)
    setIngesting(true)
    try {
      const res = await axios.post('/api/docs/ingest-url', { url: url.trim() })
      setUrlResult(res.data)
      setUrl('')
      loadStats()
    } catch (err) {
      setError(err.response?.data?.detail || err.message)
    } finally {
      setIngesting(false)
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: '#0f1117', overflowY: 'auto' }}>
      {/* Header */}
      <div style={{ padding: '20px 24px', borderBottom: '1px solid #1e2130' }}>
        <h1 style={{ fontSize: 18, fontWeight: 700, color: '#e2e8f0', marginBottom: 2 }}>Base de Conhecimento</h1>
        <p style={{ fontSize: 12, color: '#64748b' }}>Adicione documentos PDF ou URLs para enriquecer a IA</p>
      </div>

      <div style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: 20 }}>
        {/* Stats */}
        <div style={{
          background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 12, padding: 20,
          display: 'flex', alignItems: 'center', gap: 16,
        }}>
          <div style={{
            width: 48, height: 48, borderRadius: 12,
            background: 'linear-gradient(135deg, #667eea22, #764ba222)',
            border: '1px solid #667eea33',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <Database size={22} color="#667eea" />
          </div>
          <div>
            <div style={{ fontSize: 24, fontWeight: 700, color: '#e2e8f0' }}>
              {stats?.total_documents ?? '—'}
            </div>
            <div style={{ fontSize: 12, color: '#64748b' }}>chunks de conhecimento indexados</div>
          </div>
          <div style={{ marginLeft: 'auto' }}>
            <button onClick={loadStats} style={{
              background: 'transparent', border: '1px solid #2a2d3e',
              borderRadius: 8, color: '#64748b', cursor: 'pointer',
              padding: '6px 12px', fontSize: 11,
            }}>
              Atualizar
            </button>
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: 'flex', gap: 2, background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 10, padding: 4 }}>
          {[
            { id: 'upload', label: 'Upload PDF', icon: Upload },
            { id: 'url', label: 'Importar URL', icon: Globe },
          ].map(tab => {
            const Icon = tab.icon
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                style={{
                  flex: 1, padding: '8px', borderRadius: 8, border: 'none', cursor: 'pointer',
                  background: activeTab === tab.id ? 'linear-gradient(135deg, #667eea, #764ba2)' : 'transparent',
                  color: activeTab === tab.id ? '#fff' : '#64748b',
                  fontWeight: 600, fontSize: 13,
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
                  transition: 'all 0.2s',
                }}
              >
                <Icon size={14} />
                {tab.label}
              </button>
            )
          })}
        </div>

        {/* Error */}
        {error && (
          <div style={{
            background: '#1a0a0a', border: '1px solid #7f1d1d', borderRadius: 12,
            padding: '12px 16px', color: '#f87171', display: 'flex', alignItems: 'center', gap: 8,
          }}>
            <AlertCircle size={16} />
            {error}
          </div>
        )}

        {/* Upload Tab */}
        {activeTab === 'upload' && (
          <div>
            <div
              onClick={() => !uploading && fileInputRef.current?.click()}
              onDrop={e => {
                e.preventDefault()
                setDragOver(false)
                const f = e.dataTransfer.files[0]
                if (f) handleFileUpload(f)
              }}
              onDragOver={e => { e.preventDefault(); setDragOver(true) }}
              onDragLeave={() => setDragOver(false)}
              style={{
                border: `2px dashed ${dragOver ? '#667eea' : '#2a2d3e'}`,
                borderRadius: 16, padding: '48px 24px',
                textAlign: 'center', cursor: uploading ? 'not-allowed' : 'pointer',
                background: dragOver ? '#667eea11' : 'transparent',
                transition: 'all 0.2s',
              }}
            >
              {uploading ? (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 12 }}>
                  <Loader2 size={40} color="#667eea" style={{ animation: 'spin 1s linear infinite' }} />
                  <p style={{ color: '#667eea', fontWeight: 600 }}>Processando PDF...</p>
                  <p style={{ fontSize: 12, color: '#64748b' }}>Indexando chunks na base de conhecimento</p>
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 12 }}>
                  <FileUp size={40} color="#64748b" strokeWidth={1.5} />
                  <div>
                    <p style={{ color: '#94a3b8', fontWeight: 600 }}>Arraste seu PDF aqui ou clique para selecionar</p>
                    <p style={{ fontSize: 12, color: '#64748b', marginTop: 4 }}>
                      Apenas arquivos PDF · Máximo 50MB
                    </p>
                  </div>
                </div>
              )}
            </div>

            <input
              ref={fileInputRef}
              type="file"
              accept=".pdf"
              style={{ display: 'none' }}
              onChange={e => handleFileUpload(e.target.files[0])}
            />

            {uploadResult && (
              <div style={{
                marginTop: 16, background: '#0d1a0d', border: '1px solid #14532d',
                borderRadius: 12, padding: 16, display: 'flex', gap: 12, alignItems: 'flex-start',
              }}>
                <CheckCircle size={20} color="#4ade80" style={{ flexShrink: 0, marginTop: 1 }} />
                <div>
                  <p style={{ color: '#4ade80', fontWeight: 600, marginBottom: 4 }}>PDF importado com sucesso!</p>
                  <p style={{ color: '#86efac', fontSize: 13 }}>
                    {uploadResult.chunks_added} chunks adicionados à base de conhecimento.
                  </p>
                  {uploadResult.message && (
                    <p style={{ color: '#6ee7b7', fontSize: 12, marginTop: 4 }}>{uploadResult.message}</p>
                  )}
                </div>
              </div>
            )}
          </div>
        )}

        {/* URL Tab */}
        {activeTab === 'url' && (
          <div>
            <div style={{ background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 12, padding: 20 }}>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#94a3b8', display: 'block', marginBottom: 8 }}>
                URL da Documentação
              </label>
              <div style={{ display: 'flex', gap: 8 }}>
                <input
                  type="url"
                  value={url}
                  onChange={e => setUrl(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && handleUrlIngest()}
                  placeholder="https://tdn.totvs.com/display/public/mp/..."
                  style={{
                    flex: 1, background: '#0f1117', border: '1px solid #2a2d3e',
                    borderRadius: 8, color: '#e2e8f0', padding: '10px 12px',
                    fontSize: 13, outline: 'none',
                  }}
                />
                <button
                  onClick={handleUrlIngest}
                  disabled={!url.trim() || ingesting}
                  style={{
                    background: !url.trim() || ingesting ? '#1e2130' : 'linear-gradient(135deg, #667eea, #764ba2)',
                    border: 'none', borderRadius: 8, color: '#fff', cursor: !url.trim() || ingesting ? 'not-allowed' : 'pointer',
                    padding: '10px 16px', fontWeight: 600, fontSize: 13,
                    display: 'flex', alignItems: 'center', gap: 6,
                    opacity: !url.trim() || ingesting ? 0.5 : 1,
                    whiteSpace: 'nowrap',
                  }}
                >
                  {ingesting ? <Loader2 size={14} style={{ animation: 'spin 1s linear infinite' }} /> : <Link size={14} />}
                  {ingesting ? 'Importando...' : 'Importar'}
                </button>
              </div>
              <p style={{ fontSize: 11, color: '#475569', marginTop: 8 }}>
                Exemplos: páginas do TDN TOTVS, documentação do Protheus, wikis técnicas
              </p>
            </div>

            {urlResult && (
              <div style={{
                marginTop: 16, background: '#0d1a0d', border: '1px solid #14532d',
                borderRadius: 12, padding: 16, display: 'flex', gap: 12, alignItems: 'flex-start',
              }}>
                <CheckCircle size={20} color="#4ade80" style={{ flexShrink: 0, marginTop: 1 }} />
                <div>
                  <p style={{ color: '#4ade80', fontWeight: 600, marginBottom: 4 }}>URL importada com sucesso!</p>
                  <p style={{ color: '#86efac', fontSize: 13 }}>
                    {urlResult.chunks_added} chunks adicionados à base de conhecimento.
                  </p>
                </div>
              </div>
            )}

            {/* URL Suggestions */}
            <div style={{ marginTop: 16 }}>
              <p style={{ fontSize: 12, color: '#64748b', marginBottom: 8 }}>URLs sugeridas:</p>
              {[
                'https://tdn.totvs.com/display/public/mp/AdvPL',
                'https://tdn.totvs.com/display/public/mp/TLPP',
                'https://tdn.totvs.com/display/public/mp/Financeiro',
              ].map(u => (
                <button key={u} onClick={() => setUrl(u)} style={{
                  display: 'block', width: '100%', textAlign: 'left',
                  background: 'transparent', border: 'none', color: '#667eea',
                  fontSize: 12, cursor: 'pointer', padding: '4px 0',
                  textDecoration: 'underline',
                }}>
                  {u}
                </button>
              ))}
            </div>
          </div>
        )}
      </div>

      <style>{`
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
      `}</style>
    </div>
  )
}
