import { useState } from 'react'
import axios from 'axios'
import { BarChart3, Loader2, Lightbulb, BookOpen } from 'lucide-react'
import MarkdownRenderer from '../components/MarkdownRenderer'

const MODULES = [
  'Geral', 'SIGAFIN - Financeiro', 'SIGAFIS - Fiscal', 'SIGACON - Contábil',
  'SIGAEST - Estoque', 'SIGACOM - Compras', 'SIGAVND - Vendas',
  'SIGAPON - Ponto/RH', 'SIGAGPE - Gestão de Pessoal',
]

const ANALYSIS_EXAMPLES = [
  'Como estruturar um projeto de customização de NF de saída com ponto de entrada?',
  'Quais as melhores práticas para otimização de queries no Protheus?',
  'Como implementar autenticação OAuth em uma API REST TLPP?',
  'Estratégia de migração de AdvPL legado para TLPP moderno',
  'Como implementar um workflow de aprovação de pedidos integrado ao Fluig?',
]

export default function AnalysisPage() {
  const [question, setQuestion] = useState('')
  const [module, setModule] = useState('Geral')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const analyze = async () => {
    if (!question.trim() || loading) return
    setLoading(true)
    setResult(null)
    setError(null)
    try {
      const res = await axios.post('/api/analysis', {
        question: question.trim(),
        module: module === 'Geral' ? null : module,
      })
      setResult(res.data)
    } catch (err) {
      setError(err.response?.data?.detail || err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: '#0f1117', overflowY: 'auto' }}>
      {/* Header */}
      <div style={{ padding: '20px 24px', borderBottom: '1px solid #1e2130' }}>
        <h1 style={{ fontSize: 18, fontWeight: 700, color: '#e2e8f0', marginBottom: 2 }}>Análise Técnica</h1>
        <p style={{ fontSize: 12, color: '#64748b' }}>Consultoria especializada e análise aprofundada sobre o Protheus</p>
      </div>

      <div style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: 20 }}>
        {/* Question area */}
        <div style={{ background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 12, padding: 20 }}>
          <div style={{ display: 'flex', gap: 16, marginBottom: 16 }}>
            <div style={{ flex: 1 }}>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#94a3b8', display: 'block', marginBottom: 6 }}>
                Módulo (opcional)
              </label>
              <select
                value={module}
                onChange={e => setModule(e.target.value)}
                style={{
                  width: '100%', background: '#0f1117', border: '1px solid #2a2d3e',
                  borderRadius: 8, color: '#e2e8f0', padding: '8px 12px',
                  fontSize: 13, outline: 'none',
                }}
              >
                {MODULES.map(m => <option key={m} value={m}>{m}</option>)}
              </select>
            </div>
          </div>

          <label style={{ fontSize: 12, fontWeight: 600, color: '#94a3b8', display: 'block', marginBottom: 6 }}>
            Sua pergunta ou desafio técnico
          </label>
          <textarea
            value={question}
            onChange={e => setQuestion(e.target.value)}
            placeholder="Descreva detalhadamente sua dúvida, desafio ou cenário técnico para análise..."
            rows={5}
            style={{
              width: '100%', background: '#0f1117', border: '1px solid #2a2d3e',
              borderRadius: 8, color: '#e2e8f0', padding: '12px',
              fontSize: 13, outline: 'none', resize: 'vertical', lineHeight: 1.5,
              fontFamily: 'inherit', marginBottom: 12,
            }}
          />

          <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
            <button
              onClick={analyze}
              disabled={!question.trim() || loading}
              style={{
                background: !question.trim() || loading ? '#1e2130' : 'linear-gradient(135deg, #667eea, #764ba2)',
                border: 'none', borderRadius: 10, color: '#fff',
                cursor: !question.trim() || loading ? 'not-allowed' : 'pointer',
                padding: '10px 20px', fontWeight: 700, fontSize: 14,
                display: 'flex', alignItems: 'center', gap: 8,
                opacity: !question.trim() || loading ? 0.5 : 1,
                transition: 'all 0.2s',
              }}
            >
              {loading
                ? <><Loader2 size={16} style={{ animation: 'spin 1s linear infinite' }} /> Analisando...</>
                : <><BarChart3 size={16} /> Analisar</>
              }
            </button>
          </div>
        </div>

        {/* Examples */}
        <div>
          <p style={{ fontSize: 12, color: '#64748b', marginBottom: 8 }}>Exemplos de análise:</p>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {ANALYSIS_EXAMPLES.map(ex => (
              <button key={ex} onClick={() => setQuestion(ex)} style={{
                background: '#13151f', border: '1px solid #2a2d3e',
                borderRadius: 20, color: '#94a3b8', cursor: 'pointer',
                padding: '5px 12px', fontSize: 11,
              }}>
                {ex}
              </button>
            ))}
          </div>
        </div>

        {/* Error */}
        {error && (
          <div style={{ background: '#1a0a0a', border: '1px solid #7f1d1d', borderRadius: 12, padding: 20, color: '#f87171' }}>
            ❌ {error}
          </div>
        )}

        {/* Result */}
        {result && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {/* Analysis */}
            <div style={{ background: '#13151f', border: '1px solid #2a2d3e', borderRadius: 12, padding: 20 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 14 }}>
                <BookOpen size={16} color="#667eea" />
                <h3 style={{ fontSize: 14, fontWeight: 600, color: '#a5b4fc' }}>Análise Técnica</h3>
              </div>
              <div style={{ fontSize: 14, color: '#cbd5e1', lineHeight: 1.7 }}>
                <MarkdownRenderer content={result.analysis} />
              </div>
            </div>

            {/* Recommendations */}
            {result.recommendations?.length > 0 && (
              <div style={{ background: '#0d1a0d', border: '1px solid #14532d', borderRadius: 12, padding: 20 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 14 }}>
                  <Lightbulb size={16} color="#4ade80" />
                  <h3 style={{ fontSize: 14, fontWeight: 600, color: '#4ade80' }}>Recomendações</h3>
                </div>
                <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: 8 }}>
                  {result.recommendations.map((rec, i) => (
                    <li key={i} style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                      <span style={{
                        background: '#166534', color: '#4ade80', borderRadius: 50,
                        width: 22, height: 22, display: 'flex', alignItems: 'center',
                        justifyContent: 'center', fontSize: 11, fontWeight: 700, flexShrink: 0,
                      }}>{i + 1}</span>
                      <span style={{ fontSize: 13, color: '#86efac', lineHeight: 1.6 }}>{rec}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {/* Sources */}
            {result.sources?.length > 0 && (
              <div style={{ fontSize: 11, color: '#475569', background: '#13151f', borderRadius: 8, padding: '8px 12px' }}>
                📚 Fontes consultadas: {result.sources.join(', ')}
              </div>
            )}
          </div>
        )}
      </div>

      <style>{`
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        select option { background: #0f1117; }
      `}</style>
    </div>
  )
}
