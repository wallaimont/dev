const ICONS = [
  { id: 'finderWindow',     label: 'Finder',        className: 'icon-folder' },
  { id: 'notesWindow',      label: 'Notas',          className: 'icon-note' },
  { id: 'terminalWindow',   label: 'Terminal',       className: 'icon-terminal' },
  { id: 'browserWindow',   label: 'Navegador',      className: 'icon-browser' },
  { id: 'calculatorWindow',label: 'Calculadora',    className: 'icon-calc' },
  { id: 'calendarWindow',  label: 'Calendário',     className: 'icon-calendar' },
  { id: 'musicWindow',     label: 'Música',         className: 'icon-music' },
  { id: 'settingsWindow',  label: 'Configurações',  className: 'icon-settings' },
];

function DesktopIcons({ onOpen }) {
  return (
    <main className="desktop" id="desktop">
      {ICONS.map((icon) => (
        <button
          key={icon.id}
          className="desktop-icon"
          onDoubleClick={() => onOpen(icon.id)}
          onClick={() => onOpen(icon.id)}
        >
          <span className={`desktop-icon-image ${icon.className}`} aria-hidden="true"></span>
          <span className="desktop-icon-label">{icon.label}</span>
        </button>
      ))}
    </main>
  );
}

export default DesktopIcons;
