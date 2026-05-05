function AppSwitcher({ isOpen, items, activeIndex }) {
  if (!isOpen || items.length === 0) return null;

  return (
    <div className="app-switcher-overlay" aria-live="polite">
      <section className="app-switcher-panel" aria-label="Alternador de apps">
        {items.map((item, index) => (
          <article key={item.id} className={`app-switcher-card ${activeIndex === index ? 'active' : ''}`}>
            <div className={`app-switcher-icon ${item.iconClass}`}></div>
            <strong>{item.title}</strong>
          </article>
        ))}
      </section>
    </div>
  );
}

export default AppSwitcher;
