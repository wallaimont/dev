import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { Copy, Check } from 'lucide-react'
import { useState } from 'react'

function CodeBlock({ language, value }) {
  const [copied, setCopied] = useState(false)
  const copy = () => {
    navigator.clipboard.writeText(value)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }
  return (
    <div style={{ position: 'relative', marginBottom: 12 }}>
      <button onClick={copy} style={{
        position: 'absolute', top: 8, right: 8, zIndex: 1,
        background: '#2d3748', border: 'none', borderRadius: 4,
        color: '#94a3b8', cursor: 'pointer', padding: '4px 8px',
        display: 'flex', alignItems: 'center', gap: 4, fontSize: 11,
      }}>
        {copied ? <Check size={12} /> : <Copy size={12} />}
        {copied ? 'Copiado!' : 'Copiar'}
      </button>
      <SyntaxHighlighter
        style={vscDarkPlus}
        language={language || 'text'}
        PreTag="div"
        customStyle={{ margin: 0, borderRadius: 8, fontSize: 13 }}
      >
        {value}
      </SyntaxHighlighter>
    </div>
  )
}

export default function MarkdownRenderer({ content }) {
  return (
    <ReactMarkdown
      remarkPlugins={[remarkGfm]}
      components={{
        code({ node, inline, className, children, ...props }) {
          const match = /language-(\w+)/.exec(className || '')
          const lang = match ? match[1] : ''
          const value = String(children).replace(/\n$/, '')
          if (!inline && (lang || value.includes('\n'))) {
            return <CodeBlock language={lang} value={value} />
          }
          return (
            <code style={{
              background: '#1e2130', padding: '2px 6px', borderRadius: 4,
              fontSize: '0.85em', color: '#a5b4fc',
            }} {...props}>
              {children}
            </code>
          )
        },
        p({ children }) {
          return <p style={{ marginBottom: 10, lineHeight: 1.7 }}>{children}</p>
        },
        h1({ children }) {
          return <h1 style={{ fontSize: 20, fontWeight: 700, marginBottom: 12, color: '#e2e8f0' }}>{children}</h1>
        },
        h2({ children }) {
          return <h2 style={{ fontSize: 17, fontWeight: 600, marginBottom: 10, color: '#cbd5e1' }}>{children}</h2>
        },
        h3({ children }) {
          return <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 8, color: '#94a3b8' }}>{children}</h3>
        },
        ul({ children }) {
          return <ul style={{ paddingLeft: 20, marginBottom: 10 }}>{children}</ul>
        },
        ol({ children }) {
          return <ol style={{ paddingLeft: 20, marginBottom: 10 }}>{children}</ol>
        },
        li({ children }) {
          return <li style={{ marginBottom: 4, lineHeight: 1.6 }}>{children}</li>
        },
        strong({ children }) {
          return <strong style={{ color: '#c4b5fd', fontWeight: 600 }}>{children}</strong>
        },
        table({ children }) {
          return (
            <div style={{ overflowX: 'auto', marginBottom: 12 }}>
              <table style={{
                borderCollapse: 'collapse', width: '100%', fontSize: 13,
              }}>
                {children}
              </table>
            </div>
          )
        },
        th({ children }) {
          return <th style={{ border: '1px solid #2a2d3e', padding: '6px 12px', background: '#1a1d2e', textAlign: 'left' }}>{children}</th>
        },
        td({ children }) {
          return <td style={{ border: '1px solid #2a2d3e', padding: '6px 12px' }}>{children}</td>
        },
      }}
    >
      {content}
    </ReactMarkdown>
  )
}
