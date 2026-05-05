import { useState } from 'react'
import axios from 'axios'
import { Code2, Loader2, Copy, Check, Wand2 } from 'lucide-react'
import MarkdownRenderer from '../components/MarkdownRenderer'

const MODULES = [
  'Geral', 'SIGAFIN - Financeiro', 'SIGAFIS - Fiscal', 'SIGACON - Contábil',
  'SIGAEST - Estoque', 'SIGACOM - Compras', 'SIGAVND - Vendas',
  'SIGAPON - Ponto/RH', 'SIGAGPE - Gestão de Pessoal',
]

const EXAMPLES = [
  { lang: 'advpl', module: 'SIGAVND - Vendas', desc: 'Função para buscar pedidos de venda em aberto de um cliente' },
  { lang: 'tlpp', module: 'SIGAFIN - Financeiro', desc: 'Endpoint REST para consultar saldo de contas a receber' },
  { lang: 'advpl', module: 'SIGAEST - Estoque', desc: 'Relatório de produtos com estoque abaixo do mínimo' },
]

export default function CodeGenPage() {
  const [description, setDescription] = useState('')
  const [language, setLanguage] = useState('advpl')
  const [module, setModule] = useState('Geral')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)
  const [copied, setCopied] = useState(false)

  const generate = async () => {
    if (!description.trim() || loading) return
    setLoading(true)
    setResult(null)
    setError(null)
    try {
      const res = await axios.post('/api/codegen', {
        description: description.trim(),
        language,
        module: module === 'Geral' ? null : module,
      })
      setResult(res.data)
    } catch (err) {
      setError(err.response?.data?.detail || err.message)
    } finally {
      setLoading(false)
    }
  }

  const copyCode = () => {
    if (!result?.code) return
    navigator.clipboard.writeText(result.code)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const loadExample = (ex) => {
    setDescription(ex.desc)
    setLanguage(ex.lang)
    setModule(ex.module)
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: '#0f1117', overflowY: 'auto' }}>
      {/* Header */}
      <div style={{ padding: '20px 24px', borderBottom: '1px solid #1e2130' }}>
        <h1 style={{ fontSize: 18, fontWeight: 700, color: '#e2e8f0', marginBottom: 2 }}>Gerador de Código AdvPL / TLPP</h1>
        <p style={{ fontSize: 12, color: '#64748b' }}>Descreva o que deseja e a IA gerará o código automaticamente</p>
      </div>

      <div style={{ padding: '24px', display: 'flex', gap: 24, flex: 1 }}>
        {/* Left Panel - Form */}
        <div style={{ flex: '0 0 380px', display: 'flex', flexDirection: 'column', gap: 16 }}>

          {/* Examples */}
          <div>
            <p style={{ fontSize: 12, color: '#64748b', marginBottom: 8 }}>Exemplos rápidos:</p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
              {EXAMPLES.map((ex, i) => (
                <button key={i} onClick={() => loadExample(ex)} style={{
                  background: '#13151f', border: '1px solid #2a2d3e',
                  borderRadius: 8, padding: '8px 12px', cursor: 'pointer',
                  textAlign: 'left', color: '#94a3b8', fontSize: 12,
                  transition: 'border-color 0.2s',
                }}>
                  <span style={{ color: '#a5b4fc', fontWeight: 600 }}>{ex.lang.toUpperCase()}</span>
                  {' · '}{ex.desc}
                </button>
              ))}
            </div>
          </div>

          {/* Language */}
          <div>
            <label style={{ fontSize: 12, fontWeight: 600, color: '#94a3b8', display: 'block', marginBottom: 6 }}>
              Linguagem
            </label>
            <div style={{ display: 'flex', gap: 8 }}>
              {['advpl', 'tlpp'].map(lang => (
                <button
                  key={lang}
                  onClick={() => setLanguage(lang)}
                  style={{
                    flex: 1, padding: '10px', borderRadius: 8, border: 'none', cursor: 'pointer',
                    background: language === lang
                      ? 'linear-gradient(135deg, #667eea, #764ba2)'
                      : '#13151f',
                    color: language === lang ? '#fff' : '#64748b',
                    fontWeight: 700, fontSize: 14,
                    border: language === lang ? 'none' : '1px solid #2a2d3e',
                    transition: 'all 0.2s',
                  }}
                >
                  {lang.toUpperCase()}
                </button>
              ))}
            </div>
          </div>

          {/* Module */}
          <div>
            <label style={{ fontSize: 12, fontWeight: 600, color: '#94a3b8', display: 'block', marginBottom: 6 }}>
              Módulo
            </label>
            <select
              value={module}
              onChange={e => setModule(e.target.value)}
              style={{
                width: '100%', background: '#13151f', border: '1px solid #2a2d3e',
                borderRadius: 8, color: '#e2e8f0', padding: '10px 12px',
                fontSize: 13, outline: 'none',
              }}
            >
              {MODULES.map(m => <option key={m} value={m}>{m}</option>)}
            </select>
          </div>

          {/* Description */}
          <div>
            <label style={{ fontSize: 12, fontWeight: 600, color: '#94a3b8', display: 'block', marginBottom: 6 }}>
              Descrição do Código
            </label>
            <textarea
              value={description}
              onChange={e => setDescription(e.target.value)}
              placeholder="Ex: Função AdvPL que busca todos os pedidos de venda em aberto de um cliente, filtra por período e retorna um array com código, cliente e valor total..."
              rows={6}
              style={{
                width: '100%', background: '#13151f', border: '1px solid #2a2d3e',
                borderRadius: 8, color: '#e2e8f0', padding: '12px',
                fontSize: 13, outline: 'none', resize: 'vertical', lineHeight: 1.5,
                fontFamily: 'inherit',
              }}
            />
          </div>

          <button
            onClick={generate}
            disabled={!description.trim() || loading}
            style={{
              background: !description.trim() || loading
                ? '#1e2130'
                : 'linear-gradient(135deg, #667eea, #764ba2)',
              border: 'none', borderRadius: 10, color: '#fff', cursor: !description.trim() || loading ? 'not-allowed' : 'pointer',
              padding: '12px', fontWeight: 700, fontSize: 14,
              display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
              opacity: !description.trim() || loading ? 0.5 : 1,
              transition: 'all 0.2s',
            }}
          >
            {loading ? <><Loader2 size={16} style={{ animation: 'spin 1s linear infinite' }} /> Gerando...</> : <><Wand2 size={16} /> Gerar Código</>}
          </button>
        </div>

        {/* Right Panel - Output */}
        <div style={{ flex: 1, minWidth: 0 }}>
          {!result && !loading && !error && (
            <div style={{
              height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexDirection: 'column', gap: 12, color: '#334155',
            }}>
              <Code2 size={48} strokeWidth={1} />
              <p>O código gerado aparecerá aqui</p>
            </div>
          )}

          {loading && (
            <div style={{
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              gap: 12, color: '#64748b', height: 200,
            }}>
              <Loader2 size={20} style={{ animation: 'spin 1s linear infinite' }} />
              Gerando código {language.toUpperCase()}...
            </div>
          )}

          {error && (
            <div style={{
              background: '#1a0a0a', border: '1px solid #7f1d1d', borderRadius: 12,
              padding: 20, color: '#f87171',
            }}>
              ❌ {error}
            </div>
          )}

          {result && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              {/* Code block */}
              <div style={{ background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 12, overflow: 'hidden' }}>
                <div style={{
                  padding: '10px 16px', borderBottom: '1px solid #2a2d3e',
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                }}>
                  <span style={{ fontSize: 12, color: '#64748b', fontFamily: 'monospace' }}>
                    {result.language?.toUpperCase() || language.toUpperCase()} — Código Gerado
                  </span>
                  <button onClick={copyCode} style={{
                    background: 'transparent', border: '1px solid #2a2d3e', borderRadius: 6,
                    color: '#94a3b8', cursor: 'pointer', padding: '4px 10px',
                    display: 'flex', alignItems: 'center', gap: 4, fontSize: 11,
                  }}>
                    {copied ? <Check size={12} /> : <Copy size={12} />}
                    {copied ? 'Copiado!' : 'Copiar'}
                  </button>
                </div>
                <pre style={{
                  padding: '16px', overflowX: 'auto', fontSize: 13,
                  lineHeight: 1.6, color: '#e2e8f0', fontFamily: 'Consolas, "Courier New", monospace',
                  maxHeight: 500,
                }}>
                  <code>{result.code}</code>
                </pre>
              </div>

              {/* Explanation */}
              {result.explanation && (
                <div style={{ background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 12, padding: 20 }}>
                  <h3 style={{ fontSize: 13, fontWeight: 600, color: '#94a3b8', marginBottom: 10 }}>📋 Explicação</h3>
                  <div style={{ fontSize: 13, color: '#cbd5e1' }}>
                    <MarkdownRenderer content={result.explanation} />
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <style>{`
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        select option { background: #13151f; }
      `}</style>
    </div>
  )
}
