import { useEffect, useRef, useState } from 'react';

export default function LockScreen({ onUnlock, wallpaper }) {
  const [input, setInput] = useState('');
  const [shake, setShake] = useState(false);
  const [time, setTime] = useState(new Date());
  const inputRef = useRef(null);

  useEffect(() => {
    const t = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(t);
  }, []);

  useEffect(() => {
    // Focus input when lock screen appears
    setTimeout(() => inputRef.current?.focus(), 200);
  }, []);

  function handleSubmit(e) {
    e.preventDefault();
    if (input.length > 0) {
      onUnlock();
    } else {
      setShake(true);
      setTimeout(() => setShake(false), 500);
    }
  }

  const fmt = (n) => String(n).padStart(2, '0');
  const hours = fmt(time.getHours());
  const mins = fmt(time.getMinutes());
  const days = ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'];
  const months = ['janeiro', 'fevereiro', 'março', 'abril', 'maio', 'junho', 'julho', 'agosto', 'setembro', 'outubro', 'novembro', 'dezembro'];
  const dateStr = `${days[time.getDay()]}, ${time.getDate()} de ${months[time.getMonth()]}`;

  return (
    <div className="lock-screen" style={{ background: wallpaper }}>
      <div className="lock-blur" />
      <div className="lock-content">
        <div className="lock-time">{hours}:{mins}</div>
        <div className="lock-date">{dateStr}</div>

        <div className="lock-user">
          <div className="lock-avatar">🧑‍💻</div>
          <div className="lock-username">Usuário</div>
        </div>

        <form onSubmit={handleSubmit} className={`lock-form ${shake ? 'shake' : ''}`}>
          <input
            ref={inputRef}
            type="password"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Digite a senha…"
            className="lock-input"
            autoComplete="off"
          />
          <button type="submit" className="lock-enter-btn" title="Desbloquear">➜</button>
        </form>
        <p className="lock-hint">Digite qualquer senha para desbloquear</p>
      </div>
    </div>
  );
}
