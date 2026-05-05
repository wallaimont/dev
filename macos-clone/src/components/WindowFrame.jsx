import { useMemo, useRef } from 'react';

function WindowFrame({
  id,
  title,
  state,
  onBringToFront,
  onClose,
  onMinimize,
  onToggleMaximize,
  onDrag,
  onResize,
  children,
}) {
  const dragRef = useRef(null);

  const style = useMemo(
    () => ({
      top: state.top,
      left: state.left,
      width: state.width,
      height: state.height,
      zIndex: state.z,
    }),
    [state.top, state.left, state.width, state.height, state.z]
  );

  function startDrag(event) {
    if (state.maximized) return;
    if (event.target.closest('.traffic-lights')) return;

    onBringToFront(id);

    const startX = event.clientX;
    const startY = event.clientY;
    const originLeft = state.left;
    const originTop = state.top;

    function handleMove(moveEvent) {
      const dx = moveEvent.clientX - startX;
      const dy = moveEvent.clientY - startY;
      onDrag(id, {
        left: Math.max(0, originLeft + dx),
        top: Math.max(40, originTop + dy),
      });
    }

    function handleUp() {
      window.removeEventListener('mousemove', handleMove);
      window.removeEventListener('mouseup', handleUp);
    }

    window.addEventListener('mousemove', handleMove);
    window.addEventListener('mouseup', handleUp);
  }

  function startResize(event) {
    event.preventDefault();
    event.stopPropagation();
    if (state.maximized) return;

    onBringToFront(id);

    const startX = event.clientX;
    const startY = event.clientY;
    const originWidth = state.width;
    const originHeight = state.height;

    function handleMove(moveEvent) {
      const dx = moveEvent.clientX - startX;
      const dy = moveEvent.clientY - startY;

      onResize(id, {
        width: Math.max(280, originWidth + dx),
        height: Math.max(180, originHeight + dy),
      });
    }

    function handleUp() {
      window.removeEventListener('mousemove', handleMove);
      window.removeEventListener('mouseup', handleUp);
    }

    window.addEventListener('mousemove', handleMove);
    window.addEventListener('mouseup', handleUp);
  }

  return (
    <article
      ref={dragRef}
      className={`window ${state.isOpen ? 'show' : ''}`}
      style={style}
      onMouseDown={() => onBringToFront(id)}
    >
      <header className="window-titlebar drag-handle" onMouseDown={startDrag}>
        <div className="traffic-lights">
          <button className="tl tl-close" data-action="close" aria-label="Fechar" onClick={() => onClose(id)} />
          <button
            className="tl tl-min"
            data-action="minimize"
            aria-label="Minimizar"
            onClick={() => onMinimize(id)}
          />
          <button
            className="tl tl-max"
            data-action="maximize"
            aria-label="Maximizar"
            onClick={() => onToggleMaximize(id)}
          />
        </div>
        <h2>{title}</h2>
      </header>
      <div className="window-content">{children}</div>
      <button
        className={`window-resizer ${state.maximized ? 'hidden' : ''}`}
        onMouseDown={startResize}
        aria-label={`Redimensionar ${title}`}
      />
    </article>
  );
}

export default WindowFrame;
