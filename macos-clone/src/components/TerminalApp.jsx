import { useEffect, useRef, useState } from 'react';

const FS = {
  '~': { type: 'dir', children: ['documentos', 'downloads', 'projetos', '.config'] },
  '~/documentos': { type: 'dir', children: ['relatorio.txt', 'notas.md'] },
  '~/downloads': { type: 'dir', children: ['app-v2.zip', 'wallpaper.png'] },
  '~/projetos': { type: 'dir', children: ['novaos/', 'api-service/'] },
  '~/.config': { type: 'dir', children: ['settings.json'] },
};

function processCommand(input, cwd) {
  const parts = input.trim().split(/\s+/);
  const cmd = parts[0];
  const args = parts.slice(1);

  switch (cmd) {
    case 'help':
      return { out: `Comandos disponíveis:
  help       — mostra esta ajuda
  ls [dir]   — lista arquivos
  cd <dir>   — navega para diretório
  pwd        — diretório atual
  echo <msg> — imprime mensagem
  date       — data e hora atual
  whoami     — usuário atual
  uname      — informações do sistema
  clear      — limpa o terminal
  neofetch   — informações do sistema
  `, cwd };

    case 'ls': {
      const target = args[0] ? `~/${args[0]}`.replace('~/~', '~') : cwd;
      const dir = FS[target] || FS[cwd];
      if (!dir) return { out: `ls: ${target}: Diretório não encontrado`, cwd };
      return { out: dir.children.join('  '), cwd };
    }

    case 'cd': {
      if (!args[0] || args[0] === '~') return { out: '', cwd: '~' };
      const next = args[0] === '..' ? '~' : `${cwd}/${args[0]}`.replace('~~', '~');
      if (!FS[next] && !FS[`~/${args[0]}`]) return { out: `cd: ${args[0]}: Não é um diretório`, cwd };
      return { out: '', cwd: FS[next] ? next : `~/${args[0]}` };
    }

    case 'pwd':
      return { out: `/Users/nova${cwd.replace('~', '')}`, cwd };

    case 'echo':
      return { out: args.join(' '), cwd };

    case 'date':
      return { out: new Date().toLocaleString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' }), cwd };

    case 'whoami':
      return { out: 'nova', cwd };

    case 'uname':
      return { out: 'NovaOS 2.0.0 (React 19 / Vite 5)', cwd };

    case 'neofetch':
      return {
        out: `
     ⌘⌘⌘⌘⌘     nova@novaos
    ⌘⌘⌘⌘⌘⌘⌘    ──────────────────
   ⌘⌘⌘⌘⌘⌘⌘⌘⌘   OS: NovaOS 2.0.0
   ⌘⌘⌘⌘⌘⌘⌘⌘⌘   Host: Web Browser
    ⌘⌘⌘⌘⌘⌘⌘    Shell: bash 5.2
     ⌘⌘⌘⌘⌘     Resolution: ${window.innerWidth}x${window.innerHeight}
                Theme: glassmorphism
                Icons: SF-style
                Memory: React 19`, cwd };

    case 'clear':
      return { out: null, clear: true, cwd };

    case '':
      return { out: '', cwd };

    default:
      return { out: `bash: ${cmd}: command not found`, cwd };
  }
}

function TerminalApp() {
  const [lines, setLines] = useState([
    { type: 'system', text: 'NovaOS Terminal — versão 2.0.0' },
    { type: 'system', text: 'Digite "help" para ver os comandos disponíveis.' },
    { type: 'system', text: '' },
  ]);
  const [input, setInput] = useState('');
  const [cwd, setCwd] = useState('~');
  const [history, setHistory] = useState([]);
  const [histIdx, setHistIdx] = useState(-1);
  const bottomRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [lines]);

  function submit(e) {
    e.preventDefault();
    const cmd = input.trim();
    const prompt = `nova@novaos:${cwd}$`;

    const result = processCommand(cmd, cwd);

    if (result.clear) {
      setLines([{ type: 'system', text: '' }]);
      setInput('');
      setHistIdx(-1);
      return;
    }

    const newLines = [{ type: 'prompt', text: `${prompt} ${cmd}` }];
    if (result.out) {
      result.out.split('\n').forEach((line) => newLines.push({ type: 'output', text: line }));
    }

    setLines((prev) => [...prev, ...newLines]);
    setCwd(result.cwd);
    if (cmd) setHistory((h) => [cmd, ...h.slice(0, 49)]);
    setInput('');
    setHistIdx(-1);
  }

  function handleKeyDown(e) {
    if (e.key === 'ArrowUp') {
      e.preventDefault();
      const idx = Math.min(histIdx + 1, history.length - 1);
      setHistIdx(idx);
      setInput(history[idx] || '');
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      const idx = Math.max(histIdx - 1, -1);
      setHistIdx(idx);
      setInput(idx === -1 ? '' : history[idx]);
    }
  }

  return (
    <div className="term-wrap" onClick={() => inputRef.current?.focus()}>
      <div className="term-output">
        {lines.map((line, i) => (
          <div key={i} className={`term-line term-${line.type}`}>{line.text}</div>
        ))}
        <div ref={bottomRef} />
      </div>
      <form className="term-input-row" onSubmit={submit}>
        <span className="term-prompt">nova@novaos:{cwd}$</span>
        <input
          ref={inputRef}
          className="term-input"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          autoFocus
          autoComplete="off"
          spellCheck={false}
        />
      </form>
    </div>
  );
}

export default TerminalApp;
