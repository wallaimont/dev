import { useEffect, useRef, useState } from 'react';

function ControlCenter({ theme, onClose }) {
  const ref = useRef(null);
  const [wifi, setWifi] = useState(true);
  const [bluetooth, setBluetooth] = useState(true);
  const [airdrop, setAirdrop] = useState(false);
  const [volume, setVolume] = useState(62);
  const [brightness, setBrightness] = useState(80);

  useEffect(() => {
    function handleClick(e) {
      if (ref.current && !ref.current.contains(e.target)) onClose();
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, [onClose]);

  return (
    <div className="cc-panel" ref={ref}>
      <div className="cc-grid">
        <div className="cc-tile-group">
          <button className={`cc-tile ${wifi ? 'on' : ''}`} onClick={() => setWifi((v) => !v)}>
            <span className="cc-tile-icon">📶</span>
            <span className="cc-tile-label">Wi-Fi</span>
            <span className="cc-tile-sub">{wifi ? 'NovaNet' : 'Desligado'}</span>
          </button>
          <button className={`cc-tile ${bluetooth ? 'on' : ''}`} onClick={() => setBluetooth((v) => !v)}>
            <span className="cc-tile-icon">🦷</span>
            <span className="cc-tile-label">Bluetooth</span>
            <span className="cc-tile-sub">{bluetooth ? 'Ativo' : 'Desligado'}</span>
          </button>
          <button className={`cc-tile ${airdrop ? 'on' : ''}`} onClick={() => setAirdrop((v) => !v)}>
            <span className="cc-tile-icon">📡</span>
            <span className="cc-tile-label">AirDrop</span>
            <span className="cc-tile-sub">{airdrop ? 'Ativo' : 'Desligado'}</span>
          </button>
        </div>
        <div className="cc-battery">
          <span className="cc-batt-icon">🔋</span>
          <div className="cc-batt-info">
            <span>Bateria</span>
            <span className="cc-batt-pct">85%</span>
          </div>
          <div className="cc-batt-bar"><div className="cc-batt-fill" style={{width:'85%'}} /></div>
        </div>
      </div>

      <div className="cc-slider-section">
        <div className="cc-slider-row">
          <span>🔆</span>
          <input type="range" min={0} max={100} value={brightness} onChange={(e) => setBrightness(+e.target.value)} />
          <span className="cc-slider-val">{brightness}%</span>
        </div>
        <div className="cc-slider-row">
          <span>{volume === 0 ? '🔇' : volume < 40 ? '🔈' : '🔊'}</span>
          <input type="range" min={0} max={100} value={volume} onChange={(e) => setVolume(+e.target.value)} />
          <span className="cc-slider-val">{volume}%</span>
        </div>
      </div>
    </div>
  );
}

export default ControlCenter;
