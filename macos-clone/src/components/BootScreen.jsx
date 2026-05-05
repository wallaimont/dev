import { useEffect, useState } from 'react';

function BootScreen({ onDone }) {
  const [progress, setProgress] = useState(0);
  const [fadeOut, setFadeOut] = useState(false);

  useEffect(() => {
    const start = performance.now();
    const duration = 2200;

    function step(now) {
      const elapsed = now - start;
      const pct = Math.min(100, (elapsed / duration) * 100);
      setProgress(Math.round(pct));
      if (pct < 100) {
        requestAnimationFrame(step);
      } else {
        setTimeout(() => {
          setFadeOut(true);
          setTimeout(onDone, 500);
        }, 300);
      }
    }

    requestAnimationFrame(step);
  }, [onDone]);

  return (
    <div className={`boot-screen ${fadeOut ? 'fade-out' : ''}`}>
      <div className="boot-content">
        <div className="boot-logo">
          <span className="boot-logo-icon">⌘</span>
        </div>
        <p className="boot-name">NovaOS</p>
        <div className="boot-bar-track">
          <div className="boot-bar-fill" style={{ width: `${progress}%` }} />
        </div>
      </div>
    </div>
  );
}

export default BootScreen;
