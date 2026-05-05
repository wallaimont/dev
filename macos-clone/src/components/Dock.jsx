import { useState } from 'react';

const DOCK_ITEMS = [
  { id: 'finderWindow',     className: 'dock-finder',    label: 'Finder' },
  { id: 'notesWindow',      className: 'dock-notes',     label: 'Notas' },
  { id: 'browserWindow',   className: 'dock-browser',   label: 'Navegador' },
  { id: 'terminalWindow',  className: 'dock-terminal',  label: 'Terminal' },
  { id: 'calculatorWindow',className: 'dock-calc',      label: 'Calculadora' },
  { id: 'calendarWindow',  className: 'dock-calendar',  label: 'Calendário' },
  { id: 'musicWindow',     className: 'dock-music',     label: 'Música' },
  { id: 'texteditWindow',  className: 'dock-textedit',  label: 'TextEdit' },
  { id: 'photosWindow',    className: 'dock-photos',    label: 'Fotos' },
  { id: 'monitorWindow',   className: 'dock-monitor',   label: 'Monitor' },
  { id: 'settingsWindow',  className: 'dock-settings',  label: 'Configurações' },
];

function getMagnifyScale(distance) {
  if (distance === 0) return 1.6;
  if (distance === 1) return 1.3;
  if (distance === 2) return 1.1;
  return 1.0;
}

function Dock({ onOpen, onToggleAll, windowsState }) {
  const [hoveredIdx, setHoveredIdx] = useState(null);

  return (
    <footer className="dock" id="dock" onMouseLeave={() => setHoveredIdx(null)}>
      {DOCK_ITEMS.map((item, idx) => {
        const isOpen = windowsState?.[item.id]?.isOpen;
        const distance = hoveredIdx === null ? null : Math.abs(idx - hoveredIdx);
        const scale = hoveredIdx === null ? 1.0 : getMagnifyScale(distance);
        return (
          <div
            key={item.id}
            className="dock-item-wrap"
            onMouseEnter={() => setHoveredIdx(idx)}
            style={{ '--dock-scale': scale }}
          >
            <span className="dock-label">{item.label}</span>
            <button
              className={`dock-item ${item.className}`}
              aria-label={item.label}
              onClick={() => onOpen(item.id)}
              title={item.label}
            />
            {isOpen && <span className="dock-dot" />}
          </div>
        );
      })}
      <span className="dock-divider" />
      <div
        className="dock-item-wrap"
        onMouseEnter={() => setHoveredIdx(DOCK_ITEMS.length)}
        style={{ '--dock-scale': hoveredIdx === DOCK_ITEMS.length ? 1.6 : 1.0 }}
      >
        <span className="dock-label">Janelas</span>
        <button
          className="dock-item dock-overview"
          aria-label="Mostrar janelas"
          onClick={onToggleAll}
          title="Mostrar todas"
        />
      </div>
    </footer>
  );
}

export default Dock;
