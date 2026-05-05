import { useEffect, useRef } from 'react';

function ContextMenu({ x, y, onClose, onOpenApp, onOpenSettings, onNewNote }) {
  const ref = useRef(null);

  useEffect(() => {
    function handleClick(e) {
      if (ref.current && !ref.current.contains(e.target)) onClose();
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, [onClose]);

  const items = [
    { label: '📁  Nova Pasta', action: () => { onClose(); } },
    { label: '📝  Nova Nota', action: () => { onNewNote(); onClose(); } },
    { divider: true },
    { label: '🖼  Mudar Papel de Parede', action: () => { onOpenSettings(); onClose(); } },
    { divider: true },
    { label: '⚙️  Configurações', action: () => { onOpenSettings(); onClose(); } },
    { label: 'ℹ️  Sobre NovaOS', action: () => { onOpenApp('settingsWindow'); onClose(); } },
  ];

  // Clamp position to stay within viewport
  const style = { top: Math.min(y, window.innerHeight - 240), left: Math.min(x, window.innerWidth - 220) };

  return (
    <div className="ctx-menu" style={style} ref={ref}>
      {items.map((item, i) =>
        item.divider
          ? <div key={i} className="ctx-divider" />
          : <button key={i} className="ctx-item" onClick={item.action}>{item.label}</button>
      )}
    </div>
  );
}

export default ContextMenu;
