import React, { useState, useCallback } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';

const BROWSER_LANGS = new Set(['html', 'html5', 'javascript', 'js', 'jsx', 'tsx', 'typescript', 'ts', 'css']);

const PISTON_LANG_MAP = {
  python: 'python', py: 'python',
  java: 'java',
  c: 'c',
  cpp: 'c++', 'c++': 'c++',
  go: 'go', golang: 'go',
  ruby: 'ruby', rb: 'ruby',
  php: 'php',
  rust: 'rust', rs: 'rust',
  kotlin: 'kotlin', kt: 'kotlin',
  swift: 'swift',
  r: 'r',
  bash: 'bash', sh: 'bash', shell: 'bash',
  lua: 'lua',
  perl: 'perl',
  dart: 'dart',
  cs: 'csharp', csharp: 'csharp',
};

async function runWithPiston(language, code) {
  const lang = PISTON_LANG_MAP[(language || '').toLowerCase()];
  if (!lang) throw new Error(`Linguagem "${language}" não suportada para execução online`);
  const res = await fetch('https://emkc.org/api/v2/piston/execute', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ language: lang, version: '*', files: [{ content: code }] }),
  });
  if (!res.ok) throw new Error('Erro na API de execução (Piston)');
  const data = await res.json();
  const out = data.run?.stdout || '';
  const err = data.run?.stderr || '';
  return out + (err ? `\n[stderr]\n${err}` : '') || '(sem saída)';
}

function buildSrcdoc(language, code) {
  const lang = (language || '').toLowerCase();
  if (lang === 'html' || lang === 'html5') return code;
  return `<!DOCTYPE html>
<html>
<head><meta charset="utf-8"><style>body{margin:0;background:#111;color:#eee;font-family:sans-serif;}</style></head>
<body><script>\n${code}\n<\/script></body>
</html>`;
}

function CodeBlock({ language, code }) {
  const [copied, setCopied] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const [running, setRunning] = useState(false);
  const [sandboxKey, setSandboxKey] = useState(0);
  const [output, setOutput] = useState(null);
  const [execLoading, setExecLoading] = useState(false);
  const [execError, setExecError] = useState(null);
  const lang = (language || '').toLowerCase();
  const isBrowser = BROWSER_LANGS.has(lang);
  const canExecute = isBrowser || lang in PISTON_LANG_MAP;

  const handleRun = useCallback(async () => {
    if (isBrowser) {
      setRunning(v => !v);
      setSandboxKey(k => k + 1);
      return;
    }
    if (running) {
      setRunning(false);
      setOutput(null);
      setExecError(null);
      return;
    }
    setRunning(true);
    setExecLoading(true);
    setOutput(null);
    setExecError(null);
    try {
      const result = await runWithPiston(language, code);
      setOutput(result);
    } catch (e) {
      setExecError(e.message);
    } finally {
      setExecLoading(false);
    }
  }, [isBrowser, running, language, code]);

  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(code).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  }, [code]);

  const handleExport = useCallback(() => {
    const lang = (language || '').toLowerCase();
    const ext = lang === 'html' || lang === 'html5' ? 'html'
      : lang === 'js' || lang === 'javascript' || lang === 'jsx' ? 'js'
      : lang === 'ts' || lang === 'typescript' || lang === 'tsx' ? 'ts'
      : lang === 'css' ? 'css'
      : lang === 'py' || lang === 'python' ? 'py'
      : lang === 'java' ? 'java'
      : lang === 'json' ? 'json'
      : 'txt';
    const blob = new Blob([code], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `codigo.${ext}`;
    a.click();
    URL.revokeObjectURL(url);
  }, [code, language]);

  const block = (
    <div className={fullscreen ? 'flex flex-col h-full overflow-y-auto' : 'relative my-2 rounded-xl overflow-hidden'}>
      {/* toolbar */}
      <div className="flex items-center justify-between bg-zinc-800/90 px-3 py-1.5 text-xs text-zinc-400">
        <span className="font-mono text-zinc-500">{language}</span>
        <div className="flex items-center gap-2">
          <button
            onClick={handleExport}
            className="flex items-center gap-1 rounded px-2 py-0.5 hover:bg-zinc-700 hover:text-zinc-200 transition-colors"
            title="Exportar arquivo"
          >
            <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            Exportar
          </button>
          {canExecute && <button
            onClick={handleRun}
            className={`flex items-center gap-1 rounded px-2 py-0.5 transition-colors ${
              running
                ? 'bg-emerald-700/40 text-emerald-300 hover:bg-zinc-700'
                : 'hover:bg-emerald-700/30 hover:text-emerald-300'
            }`}
            title={running ? 'Fechar preview' : 'Executar código'}
          >
            {running ? (
              <><svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>Fechar</>
            ) : (
              <><svg className="h-3.5 w-3.5 text-emerald-400" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg><span className="text-emerald-400">Executar</span></>
            )}
          </button>}
          <button
            onClick={handleCopy}
            className="flex items-center gap-1 rounded px-2 py-0.5 hover:bg-zinc-700 hover:text-zinc-200 transition-colors"
            title="Copiar código"
          >
            {copied ? (
              <>
                <svg className="h-3.5 w-3.5 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <span className="text-emerald-400">Copiado!</span>
              </>
            ) : (
              <>
                <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <rect x="9" y="9" width="13" height="13" rx="2" ry="2" />
                  <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
                </svg>
                Copiar
              </>
            )}
          </button>
          <button
            onClick={() => setFullscreen(v => !v)}
            className="flex items-center gap-1 rounded px-2 py-0.5 hover:bg-zinc-700 hover:text-zinc-200 transition-colors"
            title={fullscreen ? 'Sair da tela cheia' : 'Tela cheia'}
          >
            {fullscreen ? (
              <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 9L4 4m0 0h5m-5 0v5M15 9l5-5m0 0h-5m5 0v5M9 15l-5 5m0 0h5m-5 0v-5M15 15l5 5m0 0h-5m5 0v-5" />
              </svg>
            ) : (
              <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 8V4m0 0h4M4 4l5 5M20 8V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5M20 16v4m0 0h-4m4 0l-5-5" />
              </svg>
            )}
          </button>
        </div>
      </div>
      <SyntaxHighlighter
        style={oneDark}
        language={language}
        PreTag="div"
        className={fullscreen ? (running ? 'max-h-[45vh] overflow-auto !m-0 !rounded-none text-xs' : 'flex-1 overflow-auto !m-0 !rounded-none text-xs') : '!m-0 !rounded-t-none !rounded-b-xl text-xs max-h-[480px] overflow-auto'}
        customStyle={{ margin: 0, borderRadius: fullscreen ? 0 : '0 0 0.75rem 0.75rem' }}
      >
        {code}
      </SyntaxHighlighter>
      {running && (
        <div className="border-t border-white/10">
          {isBrowser ? (
            <>
              <div className="flex items-center justify-between bg-zinc-900/80 px-3 py-1.5">
                <span className="text-[11px] text-emerald-400 font-semibold tracking-wide">▶ Preview ao vivo</span>
                <button
                  onClick={() => setSandboxKey(k => k + 1)}
                  className="flex items-center gap-1 rounded px-2 py-0.5 text-xs text-zinc-400 hover:bg-zinc-700 hover:text-zinc-200 transition-colors"
                  title="Recarregar"
                >
                  <svg className="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" /></svg>
                  Recarregar
                </button>
              </div>
              <iframe
                key={sandboxKey}
                srcDoc={buildSrcdoc(language, code)}
                sandbox="allow-scripts"
                title="Preview do código"
                className="w-full bg-[#111]"
                style={{ height: '420px', border: 'none', display: 'block' }}
              />
            </>
          ) : (
            <div className="bg-zinc-950">
              <div className="flex items-center justify-between px-3 py-1.5 bg-zinc-900/80">
                <span className="text-[11px] text-emerald-400 font-semibold tracking-wide">▶ Saída</span>
                {execLoading && <span className="text-[11px] text-zinc-400 animate-pulse">Executando...</span>}
              </div>
              <div className="px-3 py-3 font-mono text-xs min-h-[60px]">
                {execLoading ? (
                  <span className="text-zinc-400 animate-pulse">Aguardando resposta do servidor...</span>
                ) : execError ? (
                  <span className="text-red-400 whitespace-pre-wrap">{execError}</span>
                ) : output !== null ? (
                  <pre className="text-emerald-300 whitespace-pre-wrap leading-relaxed">{output}</pre>
                ) : null}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );

  if (fullscreen) {
    return (
      <div
        className="fixed inset-0 z-50 flex flex-col bg-zinc-900"
        onKeyDown={(e) => e.key === 'Escape' && setFullscreen(false)}
        tabIndex={-1}
      >
        {block}
        <button
          onClick={() => setFullscreen(false)}
          className="absolute right-4 top-10 rounded-full bg-zinc-700 p-1.5 text-zinc-300 hover:bg-zinc-600"
          title="Fechar (Esc)"
        >
          <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    );
  }

  return block;
}

export default function MsgContent({ content }) {
  return (
    <ReactMarkdown
      remarkPlugins={[remarkGfm]}
      components={{
        code({ node, inline, className, children, ...props }) {
          const match = /language-(\w+)/.exec(className || '');
          const code = String(children).replace(/\n$/, '');
          return !inline && match ? (
            <CodeBlock language={match[1]} code={code} />
          ) : (
            <code className="rounded bg-zinc-800 px-1.5 py-0.5 text-xs font-mono text-emerald-300" {...props}>
              {children}
            </code>
          );
        },
        p: ({ children }) => <p className="mb-3 last:mb-0">{children}</p>,
        ul: ({ children }) => <ul className="mb-3 ml-4 list-disc space-y-1">{children}</ul>,
        ol: ({ children }) => <ol className="mb-3 ml-4 list-decimal space-y-1">{children}</ol>,
        li: ({ children }) => <li className="text-sm leading-relaxed">{children}</li>,
        h1: ({ children }) => <h1 className="mb-3 text-xl font-semibold text-white">{children}</h1>,
        h2: ({ children }) => <h2 className="mb-2 text-lg font-semibold text-white">{children}</h2>,
        h3: ({ children }) => <h3 className="mb-2 text-base font-semibold text-zinc-200">{children}</h3>,
        blockquote: ({ children }) => (
          <blockquote className="my-2 border-l-4 border-emerald-500/50 pl-4 text-sm italic text-zinc-400">
            {children}
          </blockquote>
        ),
        table: ({ children }) => (
          <div className="my-3 overflow-x-auto rounded-xl border border-white/10">
            <table className="min-w-full text-xs">{children}</table>
          </div>
        ),
        th: ({ children }) => (
          <th className="border-b border-white/10 bg-white/5 px-3 py-2 text-left font-semibold text-zinc-300">
            {children}
          </th>
        ),
        td: ({ children }) => (
          <td className="border-b border-white/5 px-3 py-2 text-zinc-400">{children}</td>
        ),
        a: ({ href, children }) => (
          <a
            href={href}
            target="_blank"
            rel="noopener noreferrer"
            className="text-emerald-400 underline underline-offset-2 hover:text-emerald-300"
          >
            {children}
          </a>
        ),
        hr: () => <hr className="my-4 border-white/10" />,
      }}
    >
      {content}
    </ReactMarkdown>
  );
}
