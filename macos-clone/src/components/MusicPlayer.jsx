import { useEffect, useRef, useState } from 'react';

const SONGS = [
  { id: 1, title: 'Aurora Boreal', artist: 'Nova Collective', duration: 214, color: '#5e8ff7' },
  { id: 2, title: 'Madrugada Digital', artist: 'Synth Waves', duration: 187, color: '#c87ee0' },
  { id: 3, title: 'Horizonte Índigo', artist: 'Nova Collective', duration: 243, color: '#45c99a' },
  { id: 4, title: 'Cidade de Vidro', artist: 'LoFi Dreams', duration: 198, color: '#f0a64b' },
  { id: 5, title: 'Corrente Elétrica', artist: 'Synth Waves', duration: 225, color: '#e06080' },
  { id: 6, title: 'Neon Boulevard', artist: 'LoFi Dreams', duration: 201, color: '#7ad4f0' },
];

function fmt(s) {
  return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
}

function MusicPlayer() {
  const [current, setCurrent] = useState(0);
  const [playing, setPlaying] = useState(false);
  const [progress, setProgress] = useState(0);
  const [volume, setVolume] = useState(70);
  const intervalRef = useRef(null);

  const song = SONGS[current];

  useEffect(() => {
    clearInterval(intervalRef.current);
    if (playing) {
      intervalRef.current = setInterval(() => {
        setProgress((p) => {
          if (p >= song.duration) {
            next(); return 0;
          }
          return p + 1;
        });
      }, 1000);
    }
    return () => clearInterval(intervalRef.current);
  }, [playing, current]);

  function play(idx) {
    setCurrent(idx); setProgress(0); setPlaying(true);
  }

  function toggle() { setPlaying((p) => !p); }

  function prev() {
    setProgress(0);
    setCurrent((c) => (c - 1 + SONGS.length) % SONGS.length);
  }

  function next() {
    setProgress(0);
    setCurrent((c) => (c + 1) % SONGS.length);
  }

  return (
    <div className="music-wrap">
      <div className="music-player">
        <div className="music-art" style={{ background: `linear-gradient(135deg, ${song.color}88, ${song.color})` }}>
          <span className="music-art-note">{playing ? '♫' : '♪'}</span>
        </div>
        <div className="music-info">
          <p className="music-title">{song.title}</p>
          <p className="music-artist">{song.artist}</p>
        </div>
        <div className="music-progress-row">
          <span>{fmt(progress)}</span>
          <input
            type="range" className="music-seek"
            min={0} max={song.duration} value={progress}
            onChange={(e) => setProgress(Number(e.target.value))}
          />
          <span>{fmt(song.duration)}</span>
        </div>
        <div className="music-controls">
          <button className="music-btn" onClick={prev}>⏮</button>
          <button className="music-btn music-play" onClick={toggle}>{playing ? '⏸' : '▶'}</button>
          <button className="music-btn" onClick={next}>⏭</button>
        </div>
        <div className="music-volume-row">
          <span>🔈</span>
          <input type="range" className="music-vol" min={0} max={100} value={volume} onChange={(e) => setVolume(Number(e.target.value))} />
          <span>🔊</span>
        </div>
      </div>
      <div className="music-playlist">
        <p className="music-playlist-title">Playlist</p>
        {SONGS.map((s, i) => (
          <button
            key={s.id}
            className={`music-track ${i === current ? 'active' : ''}`}
            onClick={() => play(i)}
          >
            <span className="track-color" style={{ background: s.color }} />
            <span className="track-info">
              <span className="track-name">{s.title}</span>
              <span className="track-artist">{s.artist}</span>
            </span>
            <span className="track-dur">{fmt(s.duration)}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

export default MusicPlayer;
