import { useCallback, useEffect, useState } from 'react';

const BUTTONS = [
  ['AC', '+/-', '%', '÷'],
  ['7', '8', '9', '×'],
  ['4', '5', '6', '−'],
  ['1', '2', '3', '+'],
  ['0', '.', '='],
];

function Calculator() {
  const [display, setDisplay] = useState('0');
  const [prev, setPrev] = useState(null);
  const [op, setOp] = useState(null);
  const [fresh, setFresh] = useState(true);
  const [history, setHistory] = useState([]);

  const compute = useCallback(() => {
    if (prev === null || op === null) return;
    const a = parseFloat(prev);
    const b = parseFloat(display);
    let result;
    if (op === '+') result = a + b;
    else if (op === '−') result = a - b;
    else if (op === '×') result = a * b;
    else if (op === '÷') result = b === 0 ? 'Erro' : a / b;
    const str = typeof result === 'number' ? String(parseFloat(result.toFixed(10))) : result;
    setHistory((h) => [`${prev} ${op} ${b} = ${str}`, ...h.slice(0, 4)]);
    setDisplay(str);
    setPrev(null);
    setOp(null);
    setFresh(true);
  }, [prev, op, display]);

  function handleButton(label) {
    if (label === 'AC') {
      setDisplay('0'); setPrev(null); setOp(null); setFresh(true); return;
    }
    if (label === '+/-') {
      setDisplay((d) => d.startsWith('-') ? d.slice(1) : '-' + d); return;
    }
    if (label === '%') {
      setDisplay((d) => String(parseFloat(d) / 100)); return;
    }
    if (['+', '−', '×', '÷'].includes(label)) {
      setPrev(display); setOp(label); setFresh(true); return;
    }
    if (label === '=') { compute(); return; }
    if (label === '.') {
      if (fresh) { setDisplay('0.'); setFresh(false); return; }
      if (!display.includes('.')) setDisplay((d) => d + '.'); return;
    }
    if (fresh) {
      setDisplay(label === '0' ? '0' : label);
      setFresh(false);
    } else {
      setDisplay((d) => d === '0' ? label : d.length < 12 ? d + label : d);
    }
  }

  useEffect(() => {
    function handleKey(e) {
      const map = { Enter: '=', Backspace: 'BS', '*': '×', '/': '÷', '-': '−', Escape: 'AC' };
      const key = map[e.key] || e.key;
      if (key === 'BS') { setDisplay((d) => d.length > 1 ? d.slice(0, -1) : '0'); return; }
      const flat = BUTTONS.flat();
      if (flat.includes(key)) handleButton(key);
    }
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [display, prev, op, fresh, compute]);

  const isOp = (label) => ['+', '−', '×', '÷'].includes(label);

  return (
    <div className="calc-wrap">
      <div className="calc-history">
        {history.map((h, i) => <div key={i} className="calc-hist-item">{h}</div>)}
      </div>
      <div className="calc-display">{display}</div>
      <div className="calc-grid">
        {BUTTONS.map((row, ri) =>
          row.map((btn) => (
            <button
              key={btn}
              className={`calc-btn ${btn === '0' ? 'calc-zero' : ''} ${btn === '=' ? 'calc-eq' : ''} ${isOp(btn) ? 'calc-op' : ''} ${['AC', '+/-', '%'].includes(btn) ? 'calc-fn' : ''}`}
              onClick={() => handleButton(btn)}
            >
              {btn}
            </button>
          ))
        )}
      </div>
    </div>
  );
}

export default Calculator;
