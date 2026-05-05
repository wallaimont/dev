import { useEffect, useRef, useState } from 'react';

const PROCESSES = [
  { name: 'kernel_task', cpu: 3.2, mem: 512 },
  { name: 'WindowServer', cpu: 8.1, mem: 340 },
  { name: 'novaos', cpu: 12.4, mem: 420 },
  { name: 'Finder', cpu: 1.8, mem: 95 },
  { name: 'MusicPlayer', cpu: 4.2, mem: 128 },
  { name: 'Terminal', cpu: 2.1, mem: 64 },
  { name: 'Safari', cpu: 6.7, mem: 280 },
  { name: 'Spotlight', cpu: 0.9, mem: 48 },
  { name: 'coreservicesd', cpu: 0.4, mem: 32 },
  { name: 'mdworker', cpu: 1.5, mem: 56 },
];

function randomVariation(base, range) {
  return Math.min(99, Math.max(0, base + (Math.random() - 0.5) * range));
}

function AnimatedBar({ value, color }) {
  return (
    <div className="monitor-bar-track">
      <div
        className="monitor-bar-fill"
        style={{ width: `${value}%`, background: color }}
      />
      <span className="monitor-bar-label">{value.toFixed(1)}%</span>
    </div>
  );
}

function MiniGraph({ history, color }) {
  const canvasRef = useRef(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const w = canvas.width;
    const h = canvas.height;
    ctx.clearRect(0, 0, w, h);
    ctx.strokeStyle = color;
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    history.forEach((v, i) => {
      const x = (i / (history.length - 1)) * w;
      const y = h - (v / 100) * h;
      i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
    });
    ctx.stroke();
    // Fill
    ctx.lineTo(w, h); ctx.lineTo(0, h); ctx.closePath();
    ctx.fillStyle = color + '22';
    ctx.fill();
  }, [history, color]);

  return <canvas ref={canvasRef} width={200} height={48} className="monitor-canvas" />;
}

export default function SystemMonitor() {
  const [tab, setTab] = useState('cpu');
  const [cpu, setCpu] = useState(28);
  const [ram, setRam] = useState(62);
  const [disk, setDisk] = useState(45);
  const [net, setNet] = useState(12);
  const [cpuHistory, setCpuHistory] = useState(() => Array(30).fill(28));
  const [ramHistory, setRamHistory] = useState(() => Array(30).fill(62));
  const [processes, setProcesses] = useState(PROCESSES);

  useEffect(() => {
    const interval = setInterval(() => {
      setCpu((v) => { const n = randomVariation(v, 12); setCpuHistory((h) => [...h.slice(1), n]); return n; });
      setRam((v) => randomVariation(v, 5));
      setDisk((v) => randomVariation(v, 2));
      setNet((v) => randomVariation(v, 20));
      setProcesses((prev) => prev.map((p) => ({ ...p, cpu: randomVariation(p.cpu, 2) })));
    }, 1200);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="monitor-layout">
      {/* Tabs */}
      <div className="monitor-tabs">
        {['cpu', 'memória', 'disco', 'rede', 'processos'].map((t) => (
          <button
            key={t}
            className={`monitor-tab ${tab === t ? 'active' : ''}`}
            onClick={() => setTab(t)}
          >
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      <div className="monitor-body">
        {tab === 'cpu' && (
          <div className="monitor-section">
            <div className="monitor-metric-title">
              <span>CPU Total</span>
              <span className="monitor-big-value">{cpu.toFixed(1)}%</span>
            </div>
            <AnimatedBar value={cpu} color="#34c759" />
            <MiniGraph history={cpuHistory} color="#34c759" />
            <div className="monitor-cores">
              {[0, 1, 2, 3].map((i) => (
                <div key={i} className="monitor-core">
                  <span>Core {i + 1}</span>
                  <AnimatedBar value={randomVariation(cpu, 15)} color="#30d158" />
                </div>
              ))}
            </div>
            <div className="monitor-info-grid">
              <div className="monitor-info-item"><span>Processos</span><strong>127</strong></div>
              <div className="monitor-info-item"><span>Threads</span><strong>1.284</strong></div>
              <div className="monitor-info-item"><span>Uptime</span><strong>4h 32m</strong></div>
              <div className="monitor-info-item"><span>Modelo</span><strong>NovaChip M3</strong></div>
            </div>
          </div>
        )}

        {tab === 'memória' && (
          <div className="monitor-section">
            <div className="monitor-metric-title">
              <span>Memória RAM</span>
              <span className="monitor-big-value">{ram.toFixed(1)}%</span>
            </div>
            <AnimatedBar value={ram} color="#007aff" />
            <MiniGraph history={ramHistory} color="#007aff" />
            <div className="monitor-info-grid">
              <div className="monitor-info-item"><span>Usada</span><strong>{(ram * 0.16).toFixed(1)} GB</strong></div>
              <div className="monitor-info-item"><span>Total</span><strong>16 GB</strong></div>
              <div className="monitor-info-item"><span>Livre</span><strong>{((100 - ram) * 0.16).toFixed(1)} GB</strong></div>
              <div className="monitor-info-item"><span>Swap</span><strong>0 B</strong></div>
            </div>
          </div>
        )}

        {tab === 'disco' && (
          <div className="monitor-section">
            <div className="monitor-metric-title">
              <span>Disco (SSD 512GB)</span>
              <span className="monitor-big-value">{(disk * 5.12).toFixed(0)} GB usados</span>
            </div>
            <AnimatedBar value={disk} color="#ff9f0a" />
            <div className="monitor-info-grid">
              <div className="monitor-info-item"><span>Usado</span><strong>{(disk * 5.12).toFixed(0)} GB</strong></div>
              <div className="monitor-info-item"><span>Livre</span><strong>{((100 - disk) * 5.12).toFixed(0)} GB</strong></div>
              <div className="monitor-info-item"><span>Leitura</span><strong>120 MB/s</strong></div>
              <div className="monitor-info-item"><span>Escrita</span><strong>80 MB/s</strong></div>
            </div>
          </div>
        )}

        {tab === 'rede' && (
          <div className="monitor-section">
            <div className="monitor-metric-title">
              <span>Rede (Wi-Fi)</span>
              <span className="monitor-big-value">{net.toFixed(1)} Mbps</span>
            </div>
            <AnimatedBar value={net} color="#bf5af2" />
            <div className="monitor-info-grid">
              <div className="monitor-info-item"><span>Download</span><strong>{net.toFixed(1)} Mbps</strong></div>
              <div className="monitor-info-item"><span>Upload</span><strong>{(net * 0.3).toFixed(1)} Mbps</strong></div>
              <div className="monitor-info-item"><span>IP Local</span><strong>192.168.1.5</strong></div>
              <div className="monitor-info-item"><span>Sinal</span><strong>Excelente</strong></div>
            </div>
          </div>
        )}

        {tab === 'processos' && (
          <div className="monitor-section">
            <div className="monitor-processes-header">
              <span>Processo</span>
              <span>CPU%</span>
              <span>Mem (MB)</span>
            </div>
            <div className="monitor-processes-list">
              {[...processes].sort((a, b) => b.cpu - a.cpu).map((p) => (
                <div key={p.name} className="monitor-process-row">
                  <span className="monitor-process-name">{p.name}</span>
                  <span className={`monitor-process-cpu ${p.cpu > 8 ? 'high' : ''}`}>
                    {p.cpu.toFixed(1)}
                  </span>
                  <span>{p.mem}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
