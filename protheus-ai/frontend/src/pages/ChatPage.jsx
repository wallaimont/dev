import { useState, useRef, useEffect } from 'react'
import axios from 'axios'
import { Send, Bot, User, Loader2, Trash2 } from 'lucide-react'
import MarkdownRenderer from '../components/MarkdownRenderer'

const SUGGESTIONS = [
  'Como criar um relatório customizado no Protheus?',
  'Explique como funciona o ponto de entrada A140INCL',
  'Como fazer uma query SQL no AdvPL?',
  'Quais são os principais módulos do Protheus?',
  'Como configurar uma API REST no Protheus?',
  'Diferença entre AdvPL e TLPP',
]

export default function ChatPage() {
  const [messages, setMessages] = useState([{
    role: 'assistant',
    content: '👋 Olá! Sou o **ProtheusAI**, seu assistente especializado em TOTVS Protheus.\n\nPosso ajudá-lo com:\n- Módulos do Protheus (Financeiro, Fiscal, RH, Estoque...)\n- Programação em **AdvPL** e **TLPP**\n- APIs REST e integrações\n- Configuração de ambiente\n- Pontos de entrada e customizações\n\nComo posso ajudá-lo hoje?',
  }])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [sessionId] = useState(() => crypto.randomUUID())
  const bottomRef = useRef(null)
  const inputRef = useRef(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const sendMessage = async (text) => {
    const msg = (text || input).trim()
    if (!msg || loading) return
    setInput('')
    setMessages(prev => [...prev, { role: 'user', content: msg }])
    setLoading(true)
    try {
      const res = await axios.post('/api/chat', {
        message: msg,
        session_id: sessionId,
      })
      setMessages(prev => [...prev, {
        role: 'assistant',
        content: res.data.answer,
        sources: res.data.sources,
      }])
    } catch (err) {
      setMessages(prev => [...prev, {
        role: 'assistant',
        content: `❌ **Erro ao conectar com a API:** ${err.response?.data?.detail || err.message}\n\nVerifique se o backend está rodando em \`http://localhost:8000\`.`,
      }])
    } finally {
      setLoading(false)
      inputRef.current?.focus()
    }
  }

  const clearChat = () => {
    setMessages([{
      role: 'assistant',
      content: 'Conversa reiniciada. Como posso ajudá-lo?',
    }])
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: '#0f1117' }}>
      {/* Header */}
      <div style={{
        padding: '16px 24px',
        borderBottom: '1px solid #1e2130',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        <div>
          <h1 style={{ fontSize: 18, fontWeight: 700, color: '#e2e8f0' }}>Chat ProtheusAI</h1>
          <p style={{ fontSize: 12, color: '#64748b' }}>Assistente especializado em TOTVS Protheus</p>
        </div>
        <button
          onClick={clearChat}
          style={{
            background: 'transparent', border: '1px solid #2a2d3e',
            borderRadius: 8, color: '#64748b', cursor: 'pointer',
            padding: '6px 12px', display: 'flex', alignItems: 'center', gap: 6, fontSize: 12,
          }}
        >
          <Trash2 size={14} /> Limpar
        </button>
      </div>

      {/* Messages */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '24px' }}>
        {messages.map((msg, i) => (
          <div key={i} style={{
            display: 'flex',
            gap: 12,
            marginBottom: 20,
            flexDirection: msg.role === 'user' ? 'row-reverse' : 'row',
          }}>
            {/* Avatar */}
            <div style={{
              width: 36, height: 36, borderRadius: 10, flexShrink: 0,
              background: msg.role === 'user'
                ? 'linear-gradient(135deg, #3b82f6, #1d4ed8)'
                : 'linear-gradient(135deg, #667eea, #764ba2)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              {msg.role === 'user' ? <User size={18} color="#fff" /> : <Bot size={18} color="#fff" />}
            </div>

            {/* Bubble */}
            <div style={{
              maxWidth: '72%',
              background: msg.role === 'user' ? '#1e3a5f' : '#13151f',
              border: `1px solid ${msg.role === 'user' ? '#2563eb44' : '#2a2d3e'}`,
              borderRadius: msg.role === 'user' ? '16px 4px 16px 16px' : '4px 16px 16px 16px',
              padding: '12px 16px',
              fontSize: 14,
              color: '#e2e8f0',
              lineHeight: 1.6,
            }}>
              <MarkdownRenderer content={msg.content} />
              {msg.sources?.length > 0 && (
                <div style={{ marginTop: 8, paddingTop: 8, borderTop: '1px solid #2a2d3e' }}>
                  <div style={{ fontSize: 11, color: '#475569' }}>
                    📚 Fontes: {msg.sources.join(', ')}
                  </div>
                </div>
              )}
            </div>
          </div>
        ))}

        {loading && (
          <div style={{ display: 'flex', gap: 12, marginBottom: 20 }}>
            <div style={{
              width: 36, height: 36, borderRadius: 10,
              background: 'linear-gradient(135deg, #667eea, #764ba2)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <Bot size={18} color="#fff" />
            </div>
            <div style={{
              background: '#13151f', border: '1px solid #2a2d3e',
              borderRadius: '4px 16px 16px 16px', padding: '14px 18px',
              display: 'flex', alignItems: 'center', gap: 8, color: '#64748b',
            }}>
              <Loader2 size={16} style={{ animation: 'spin 1s linear infinite' }} />
              ProtheusAI está pensando...
            </div>
          </div>
        )}

        {/* Suggestions (only at start) */}
        {messages.length === 1 && (
          <div style={{ marginTop: 24 }}>
            <p style={{ fontSize: 12, color: '#475569', marginBottom: 12 }}>Sugestões de perguntas:</p>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {SUGGESTIONS.map(s => (
                <button
                  key={s}
                  onClick={() => sendMessage(s)}
                  style={{
                    background: '#13151f', border: '1px solid #2a2d3e',
                    borderRadius: 20, color: '#94a3b8', cursor: 'pointer',
                    padding: '6px 14px', fontSize: 12,
                    transition: 'all 0.2s',
                  }}
                >
                  {s}
                </button>
              ))}
            </div>
          </div>
        )}

        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <div style={{ padding: '16px 24px', borderTop: '1px solid #1e2130' }}>
        <div style={{
          display: 'flex', gap: 10,
          background: '#13151f',
          border: '1px solid #2a2d3e',
          borderRadius: 12,
          padding: '4px 4px 4px 16px',
        }}>
          <textarea
            ref={inputRef}
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault()
                sendMessage()
              }
            }}
            placeholder="Pergunte algo sobre TOTVS Protheus... (Enter para enviar)"
            rows={2}
            style={{
              flex: 1, background: 'transparent', border: 'none', outline: 'none',
              color: '#e2e8f0', fontSize: 14, resize: 'none', paddingTop: 8,
              lineHeight: 1.5,
            }}
          />
          <button
            onClick={() => sendMessage()}
            disabled={!input.trim() || loading}
            style={{
              background: input.trim() && !loading
                ? 'linear-gradient(135deg, #667eea, #764ba2)'
                : '#1e2130',
              border: 'none', borderRadius: 10,
              color: input.trim() && !loading ? '#fff' : '#475569',
              cursor: input.trim() && !loading ? 'pointer' : 'not-allowed',
              padding: '10px 14px',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              transition: 'all 0.2s',
              alignSelf: 'flex-end', marginBottom: 4,
            }}
          >
            <Send size={18} />
          </button>
        </div>
        <p style={{ fontSize: 11, color: '#334155', textAlign: 'center', marginTop: 8 }}>
          Shift+Enter para nova linha · Enter para enviar
        </p>
      </div>

      <style>{`
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
      `}</style>
    </div>
  )
}
