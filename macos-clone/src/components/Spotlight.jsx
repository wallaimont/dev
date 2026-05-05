import { useEffect, useMemo, useRef, useState } from 'react';

function Spotlight({ isOpen, query, results, onChangeQuery, onClose, onOpenApp }) {
  const inputRef = useRef(null);
  const [selectedIndex, setSelectedIndex] = useState(0);

  useEffect(() => {
    if (!isOpen) {
      setSelectedIndex(0);
      return;
    }

    setSelectedIndex(0);
    const timer = setTimeout(() => {
      inputRef.current?.focus();
    }, 0);

    return () => clearTimeout(timer);
  }, [isOpen]);

  useEffect(() => {
    if (selectedIndex > results.length - 1) {
      setSelectedIndex(0);
    }
  }, [results, selectedIndex]);

  const activeItem = useMemo(() => results[selectedIndex], [results, selectedIndex]);

  function handleKeyDown(event) {
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      setSelectedIndex((value) => (results.length ? (value + 1) % results.length : 0));
      return;
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault();
      setSelectedIndex((value) => (results.length ? (value - 1 + results.length) % results.length : 0));
      return;
    }

    if (event.key === 'Enter' && activeItem) {
      event.preventDefault();
      onOpenApp(activeItem.id);
    }

    if (event.key === 'Escape') {
      event.preventDefault();
      onClose();
    }
  }

  if (!isOpen) return null;

  return (
    <div className="spotlight-backdrop" onClick={onClose}>
      <section className="spotlight" onClick={(event) => event.stopPropagation()}>
        <input
          ref={inputRef}
          className="spotlight-input"
          value={query}
          onChange={(event) => onChangeQuery(event.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Buscar apps... (Ctrl/Cmd + K)"
          aria-label="Buscar apps"
        />

        <ul className="spotlight-results">
          {results.length === 0 ? <li className="spotlight-empty">Nenhum app encontrado</li> : null}
          {results.map((item, index) => (
            <li key={item.id}>
              <button
                className={`spotlight-item ${selectedIndex === index ? 'active' : ''}`}
                onClick={() => onOpenApp(item.id)}
              >
                {item.title}
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

export default Spotlight;
