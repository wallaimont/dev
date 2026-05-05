export default function AppleMenu({ onClose, onOpenSettings, onLock, onRestart }) {
  const items = [
    { label: 'Sobre o NovaOS', icon: '', action: null },
    { divider: true },
    { label: 'Preferências do Sistema…', icon: '⚙', action: onOpenSettings },
    { divider: true },
    { label: 'Bloquear Tela', icon: '🔒', action: onLock },
    { label: 'Reiniciar NovaOS', icon: '🔄', action: onRestart },
  ];

  return (
    <>
      <div className="apple-menu-backdrop" onClick={onClose} />
      <div className="apple-menu-dropdown">
        {items.map((item, i) => {
          if (item.divider) return <div key={i} className="apple-menu-divider" />;
          return (
            <button
              key={i}
              className={`apple-menu-item ${!item.action ? 'disabled' : ''}`}
              onClick={() => {
                if (item.action) { item.action(); onClose(); }
              }}
            >
              {item.icon && <span className="apple-menu-item-icon">{item.icon}</span>}
              {item.label}
            </button>
          );
        })}
      </div>
    </>
  );
}
