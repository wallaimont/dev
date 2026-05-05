import { useEffect, useState } from 'react';
import AppleMenu from './AppleMenu';

function MenuBar({ theme, onToggleTheme, onOpenSpotlight, onOpenControlCenter, controlCenterOpen, activeAppName, onLock, onOpenSettings }) {
  const [appleMenuOpen, setAppleMenuOpen] = useState(false);

  return (
    <header className="menu-bar">
      <div className="menu-left">
        <button
          className={`apple-button${appleMenuOpen ? ' active' : ''}`}
          aria-label="Menu Apple"
          onClick={() => setAppleMenuOpen((v) => !v)}
        >
          
        </button>
        {appleMenuOpen && (
          <AppleMenu
            onClose={() => setAppleMenuOpen(false)}
            onOpenSettings={() => { onOpenSettings?.(); setAppleMenuOpen(false); }}
            onLock={() => { onLock?.(); setAppleMenuOpen(false); }}
            onRestart={() => { localStorage.removeItem('novaos-booted'); window.location.reload(); }}
          />
        )}
        <span className="menu-app-name">{activeAppName || 'NovaOS'}</span>
        <nav>
          <button className="menu-item">Arquivo</button>
          <button className="menu-item">Editar</button>
          <button className="menu-item">Visualizar</button>
          <button className="menu-item">Ir</button>
          <button className="menu-item">Janela</button>
        </nav>
      </div>

      <div className="menu-right">
        <button className="theme-toggle" onClick={onOpenSpotlight} title="Buscar apps (Ctrl+K)">
          🔍
        </button>
        <button className="theme-toggle" onClick={onToggleTheme} title="Alternar tema">
          {theme === 'light' ? '🌙' : '☀️'}
        </button>
        <button
          className={`theme-toggle cc-btn${controlCenterOpen ? ' cc-btn-active' : ''}`}
          onClick={onOpenControlCenter}
          title="Central de Controle"
        >
          ⊞
        </button>
        <ClockAndDate />
      </div>
    </header>
  );
}

function ClockAndDate() {
  const [now, setNow] = useState(() => new Date());

  useEffect(() => {
    const interval = setInterval(() => {
      setNow(new Date());
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const date = now.toLocaleDateString('pt-BR', {
    weekday: 'short',
    day: '2-digit',
    month: 'short',
  });
  const time = now.toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit',
  });

  return (
    <>
      <span>{date}</span>
      <span>{time}</span>
    </>
  );
}

export default MenuBar;
