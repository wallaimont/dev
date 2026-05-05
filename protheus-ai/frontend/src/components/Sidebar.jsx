import { MessageSquare, Code2, BarChart3, FileUp, Brain } from 'lucide-react'

const NAV_ITEMS = [
  { id: 'chat', label: 'Chat IA', icon: MessageSquare, desc: 'Tire dúvidas sobre Protheus' },
  { id: 'codegen', label: 'Gerador de Código', icon: Code2, desc: 'Gere código AdvPL / TLPP' },
  { id: 'analysis', label: 'Análise', icon: BarChart3, desc: 'Análise técnica e consultoria' },
  { id: 'docs', label: 'Documentos', icon: FileUp, desc: 'Adicione PDFs à base de conhecimento' },
]

export default function Sidebar({ activePage, onNavigate }) {
  return (
    <aside style={{
      width: 240,
      background: '#13151f',
      borderRight: '1px solid #2a2d3e',
      display: 'flex',
      flexDirection: 'column',
      padding: '20px 0',
    }}>
      {/* Logo */}
      <div style={{ padding: '0 20px 24px', borderBottom: '1px solid #2a2d3e' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{
            width: 38, height: 38, borderRadius: 10,
            background: 'linear-gradient(135deg, #667eea, #764ba2)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <Brain size={20} color="#fff" />
          </div>
          <div>
            <div style={{ fontWeight: 700, fontSize: 16, color: '#e2e8f0' }}>ProtheusAI</div>
            <div style={{ fontSize: 11, color: '#64748b' }}>TOTVS Intelligence</div>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav style={{ flex: 1, padding: '16px 12px' }}>
        {NAV_ITEMS.map(item => {
          const Icon = item.icon
          const isActive = activePage === item.id
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              style={{
                width: '100%',
                display: 'flex',
                alignItems: 'center',
                gap: 12,
                padding: '10px 12px',
                marginBottom: 4,
                borderRadius: 8,
                border: 'none',
                cursor: 'pointer',
                background: isActive ? 'linear-gradient(135deg, #667eea22, #764ba222)' : 'transparent',
                borderLeft: isActive ? '2px solid #667eea' : '2px solid transparent',
                color: isActive ? '#a5b4fc' : '#94a3b8',
                textAlign: 'left',
                transition: 'all 0.2s',
              }}
            >
              <Icon size={18} style={{ flexShrink: 0 }} />
              <div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{item.label}</div>
                <div style={{ fontSize: 11, opacity: 0.7, marginTop: 1 }}>{item.desc}</div>
              </div>
            </button>
          )
        })}
      </nav>

      {/* Footer */}
      <div style={{ padding: '16px 20px', borderTop: '1px solid #2a2d3e' }}>
        <div style={{ fontSize: 11, color: '#475569', textAlign: 'center' }}>
          Powered by Ollama + LangChain
        </div>
        <div style={{ fontSize: 10, color: '#334155', textAlign: 'center', marginTop: 2 }}>
          TOTVS Protheus AI v1.0
        </div>
      </div>
    </aside>
  )
}
