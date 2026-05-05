import { useState } from 'react';

const WALLPAPERS = [
  { id: 'sonoma',    label: 'Sonoma',    css: 'linear-gradient(130deg, #8fc3ff, #d4e8ff 45%, #fff3d8)' },
  { id: 'sequoia',   label: 'Sequoia',   css: 'linear-gradient(130deg, #1a3a2a, #2e5e3a 45%, #4a7c50)' },
  { id: 'ventura',   label: 'Ventura',   css: 'linear-gradient(130deg, #6a3fa8, #a06bcf 45%, #e88fc0)' },
  { id: 'monterey',  label: 'Monterey',  css: 'linear-gradient(130deg, #1a3060, #2a6080 45%, #3ab8c8)' },
  { id: 'big-sur',   label: 'Big Sur',   css: 'linear-gradient(130deg, #f4a44a, #f5d98c 45%, #aae4ff)' },
  { id: 'mojave',    label: 'Mojave',    css: 'linear-gradient(130deg, #2e1a0e, #6a3020 45%, #c06028)' },
  { id: 'aurora',    label: 'Aurora',    css: 'linear-gradient(130deg, #08204a, #0b4f68 45%, #0f8f7a)' },
  { id: 'sunset',    label: 'Pôr do Sol',css: 'linear-gradient(130deg, #ff6b35, #f7c59f 45%, #efefd0)' },
];

const TABS = ['Aparência', 'Sobre'];

function Settings({ wallpaper, onWallpaperChange, theme, onToggleTheme }) {
  const [tab, setTab] = useState('Aparência');

  return (
    <div className="settings-wrap">
      <nav className="settings-tabs">
        {TABS.map((t) => (
          <button key={t} className={`settings-tab ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>
            {t}
          </button>
        ))}
      </nav>

      {tab === 'Aparência' && (
        <div className="settings-section">
          <h3>Papel de Parede</h3>
          <div className="wallpaper-grid">
            {WALLPAPERS.map((w) => (
              <button
                key={w.id}
                className={`wallpaper-thumb ${wallpaper === w.id ? 'selected' : ''}`}
                onClick={() => onWallpaperChange(w.id)}
                style={{ background: w.css }}
                title={w.label}
              >
                <span>{w.label}</span>
              </button>
            ))}
          </div>

          <h3>Tema</h3>
          <div className="settings-row">
            <span>Modo {theme === 'light' ? 'Claro' : 'Escuro'}</span>
            <button className="toggle-switch" onClick={onToggleTheme} data-on={theme === 'dark'}>
              <span className="toggle-knob" />
            </button>
          </div>
        </div>
      )}

      {tab === 'Sobre' && (
        <div className="settings-section about-section">
          <div className="about-logo">⌘</div>
          <h2>NovaOS</h2>
          <p>Versão 2.0.0</p>
          <p className="about-text">Sistema operacional simulado em React + Vite com glassmorphism, janelas arrastáveis, apps funcionais e muito mais.</p>
          <div className="about-specs">
            <div className="spec-row"><span>Motor</span><span>React 19 + Vite 5</span></div>
            <div className="spec-row"><span>Tema</span><span>{theme === 'light' ? 'Claro' : 'Escuro'}</span></div>
            <div className="spec-row"><span>Apps</span><span>9 aplicativos</span></div>
            <div className="spec-row"><span>Copyright</span><span>© 2026 NovaOS</span></div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Settings;
export { WALLPAPERS };
