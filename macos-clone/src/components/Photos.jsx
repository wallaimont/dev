import { useState } from 'react';

const ALBUMS = [
  { id: 'all', name: 'Todas as Fotos', icon: '🖼' },
  { id: 'ferias', name: 'Férias 2024', icon: '🏖' },
  { id: 'trabalho', name: 'Trabalho', icon: '💼' },
  { id: 'natureza', name: 'Natureza', icon: '🌿' },
  { id: 'cidade', name: 'Cidade', icon: '🏙' },
  { id: 'favoritos', name: 'Favoritos', icon: '⭐' },
];

const PHOTOS = [
  { id: 1, album: 'ferias', emoji: '🏖', label: 'Praia do Sol', gradient: 'linear-gradient(135deg,#f9c74f,#f4a261)', date: '12 Jan 2024', fav: true },
  { id: 2, album: 'ferias', emoji: '🌅', label: 'Pôr do sol', gradient: 'linear-gradient(135deg,#e76f51,#f4a261)', date: '13 Jan 2024', fav: false },
  { id: 3, album: 'ferias', emoji: '🌊', label: 'Ondas', gradient: 'linear-gradient(135deg,#48cae4,#023e8a)', date: '14 Jan 2024', fav: true },
  { id: 4, album: 'ferias', emoji: '🌴', label: 'Palmeiras', gradient: 'linear-gradient(135deg,#52b788,#1b4332)', date: '15 Jan 2024', fav: false },
  { id: 5, album: 'natureza', emoji: '🌲', label: 'Floresta', gradient: 'linear-gradient(135deg,#74c69d,#1b4332)', date: '03 Mar 2024', fav: false },
  { id: 6, album: 'natureza', emoji: '🏔', label: 'Montanha', gradient: 'linear-gradient(135deg,#dee2e6,#495057)', date: '15 Apr 2024', fav: true },
  { id: 7, album: 'natureza', emoji: '🌸', label: 'Flores', gradient: 'linear-gradient(135deg,#ffb3c1,#c77dff)', date: '20 Apr 2024', fav: false },
  { id: 8, album: 'natureza', emoji: '🌻', label: 'Girassóis', gradient: 'linear-gradient(135deg,#ffd60a,#f77f00)', date: '22 Apr 2024', fav: false },
  { id: 9, album: 'cidade', emoji: '🌉', label: 'Ponte noturna', gradient: 'linear-gradient(135deg,#212529,#6c757d)', date: '05 May 2024', fav: true },
  { id: 10, album: 'cidade', emoji: '🏙', label: 'Skyline', gradient: 'linear-gradient(135deg,#7400b8,#48cae4)', date: '06 May 2024', fav: false },
  { id: 11, album: 'cidade', emoji: '☕', label: 'Café da manhã', gradient: 'linear-gradient(135deg,#8d6e63,#d7b4a0)', date: '10 May 2024', fav: false },
  { id: 12, album: 'trabalho', emoji: '💻', label: 'Home office', gradient: 'linear-gradient(135deg,#2d6a4f,#95d5b2)', date: '01 Jun 2024', fav: false },
  { id: 13, album: 'trabalho', emoji: '📊', label: 'Apresentação', gradient: 'linear-gradient(135deg,#023e8a,#90e0ef)', date: '12 Jun 2024', fav: false },
  { id: 14, album: 'favoritos', emoji: '🌟', label: 'Favorito especial', gradient: 'linear-gradient(135deg,#f72585,#7209b7)', date: '25 Jun 2024', fav: true },
];

export default function Photos() {
  const [activeAlbum, setActiveAlbum] = useState('all');
  const [preview, setPreview] = useState(null);
  const [photos, setPhotos] = useState(PHOTOS);

  const filtered = activeAlbum === 'all'
    ? photos
    : activeAlbum === 'favoritos'
    ? photos.filter((p) => p.fav)
    : photos.filter((p) => p.album === activeAlbum);

  function toggleFav(id) {
    setPhotos((prev) => prev.map((p) => p.id === id ? { ...p, fav: !p.fav } : p));
  }

  return (
    <div className="photos-layout">
      {/* Sidebar */}
      <aside className="photos-sidebar">
        <p className="sidebar-section-title">Biblioteca</p>
        <ul>
          {ALBUMS.map((album) => {
            const count = album.id === 'all' ? photos.length
              : album.id === 'favoritos' ? photos.filter((p) => p.fav).length
              : photos.filter((p) => p.album === album.id).length;
            return (
              <li
                key={album.id}
                className={`sidebar-item ${activeAlbum === album.id ? 'sidebar-active' : ''}`}
                onClick={() => setActiveAlbum(album.id)}
              >
                <span className="sidebar-icon">{album.icon}</span>
                <span className="sidebar-label">{album.name}</span>
                <span className="sidebar-count">{count}</span>
              </li>
            );
          })}
        </ul>
      </aside>

      {/* Grid */}
      <div className="photos-main">
        <div className="photos-header">
          <h3>{ALBUMS.find((a) => a.id === activeAlbum)?.name}</h3>
          <span>{filtered.length} fotos</span>
        </div>
        <div className="photos-grid">
          {filtered.map((photo) => (
            <div
              key={photo.id}
              className="photo-card"
              style={{ background: photo.gradient }}
              onClick={() => setPreview(photo)}
              title={photo.label}
            >
              <span className="photo-emoji">{photo.emoji}</span>
              {photo.fav && <span className="photo-fav-badge">⭐</span>}
            </div>
          ))}
          {filtered.length === 0 && (
            <p className="finder-empty">Nenhuma foto neste álbum</p>
          )}
        </div>
      </div>

      {/* Preview modal */}
      {preview && (
        <div className="photo-preview-overlay" onClick={() => setPreview(null)}>
          <div className="photo-preview-card" style={{ background: preview.gradient }} onClick={(e) => e.stopPropagation()}>
            <span className="photo-preview-emoji">{preview.emoji}</span>
            <div className="photo-preview-info">
              <strong>{preview.label}</strong>
              <span>{preview.date}</span>
            </div>
            <div className="photo-preview-actions">
              <button
                className="photo-fav-btn"
                onClick={() => toggleFav(preview.id)}
                title={preview.fav ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}
              >
                {photos.find((p) => p.id === preview.id)?.fav ? '⭐' : '☆'}
              </button>
              <button className="photo-close-btn" onClick={() => setPreview(null)}>✕</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
