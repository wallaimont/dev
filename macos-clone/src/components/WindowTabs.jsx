function WindowTabs({ items, activeId, onSelect }) {
  if (items.length === 0) return null;

  return (
    <nav className="window-tabs" aria-label="Janelas abertas">
      {items.map((item) => (
        <button
          key={item.id}
          className={`window-tab ${activeId === item.id ? 'active' : ''}`}
          onClick={() => onSelect(item.id)}
        >
          {item.title}
        </button>
      ))}
    </nav>
  );
}

export default WindowTabs;
