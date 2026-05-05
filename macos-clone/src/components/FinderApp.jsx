import { useState } from 'react';

const FS_TREE = {
  'Início': {
    icon: '🏠',
    children: {
      'Documentos': {
        icon: '📁',
        children: {
          'Relatório Q1.pdf': { icon: '📄', type: 'file', size: '1.2 MB' },
          'Apresentação.pptx': { icon: '📊', type: 'file', size: '3.8 MB' },
          'Notas.md': { icon: '📝', type: 'file', size: '14 KB' },
          'Contrato.docx': { icon: '📃', type: 'file', size: '240 KB' },
        },
      },
      'Downloads': {
        icon: '📥',
        children: {
          'app-v2.zip': { icon: '🗜', type: 'file', size: '48 MB' },
          'wallpaper.png': { icon: '🖼', type: 'file', size: '4.2 MB' },
          'video.mp4': { icon: '🎬', type: 'file', size: '210 MB' },
          'setup.dmg': { icon: '💿', type: 'file', size: '98 MB' },
        },
      },
      'Projetos': {
        icon: '💼',
        children: {
          'novaos': {
            icon: '📁',
            children: {
              'src': { icon: '📁', children: {
                'App.jsx': { icon: '⚛', type: 'file', size: '12 KB' },
                'index.css': { icon: '🎨', type: 'file', size: '18 KB' },
              }},
              'package.json': { icon: '📦', type: 'file', size: '2 KB' },
              'README.md': { icon: '📝', type: 'file', size: '4 KB' },
            },
          },
          'api-service': {
            icon: '📁',
            children: {
              'server.js': { icon: '🟨', type: 'file', size: '6 KB' },
              'routes.js': { icon: '🟨', type: 'file', size: '3 KB' },
            },
          },
        },
      },
      'Imagens': {
        icon: '🖼',
        children: {
          'Férias 2024': {
            icon: '📁',
            children: {
              'IMG_001.jpg': { icon: '🏖', type: 'file', size: '3.1 MB' },
              'IMG_002.jpg': { icon: '🌅', type: 'file', size: '2.8 MB' },
              'IMG_003.jpg': { icon: '🌄', type: 'file', size: '3.3 MB' },
            },
          },
          'Screenshot.png': { icon: '🖥', type: 'file', size: '890 KB' },
          'Avatar.jpg': { icon: '👤', type: 'file', size: '156 KB' },
        },
      },
      'Música': {
        icon: '🎵',
        children: {
          'Playlist Trabalho': {
            icon: '📁',
            children: {
              'Lofi Hip Hop.mp3': { icon: '🎵', type: 'file', size: '8.2 MB' },
              'Jazz Mix.mp3': { icon: '🎷', type: 'file', size: '12 MB' },
            },
          },
          'Podcasts': {
            icon: '📁',
            children: {
              'Tech Talk Ep42.mp3': { icon: '🎙', type: 'file', size: '45 MB' },
            },
          },
        },
      },
      'Lixeira': { icon: '🗑', children: {
        'rascunho-antigo.txt': { icon: '📄', type: 'file', size: '2 KB' },
      }},
    },
  },
  'Aplicativos': {
    icon: '📱',
    children: {
      'Terminal.app': { icon: '🖥', type: 'file', size: '—' },
      'Calculadora.app': { icon: '🧮', type: 'file', size: '—' },
      'Calendário.app': { icon: '📅', type: 'file', size: '—' },
      'Configurações.app': { icon: '⚙️', type: 'file', size: '—' },
      'TextEdit.app': { icon: '📝', type: 'file', size: '—' },
      'Fotos.app': { icon: '📸', type: 'file', size: '—' },
    },
  },
  'Área de Trabalho': {
    icon: '🖥',
    children: {
      'Atalho NovaOS.lnk': { icon: '🔗', type: 'file', size: '1 KB' },
      'Rascunho.txt': { icon: '📝', type: 'file', size: '3 KB' },
    },
  },
};

const SIDEBAR_ITEMS = [
  { label: 'Início', key: 'Início' },
  { label: 'Aplicativos', key: 'Aplicativos' },
  { label: 'Área de Trabalho', key: 'Área de Trabalho' },
];

function getNodeByPath(path) {
  let node = FS_TREE;
  for (const segment of path) {
    if (!node[segment]) return null;
    node = node[segment].children || node[segment];
  }
  return node;
}

export default function FinderApp() {
  const [path, setPath] = useState(['Início']);
  const [view, setView] = useState('grid'); // 'grid' | 'list'
  const [selected, setSelected] = useState(null);

  const currentNode = getNodeByPath(path) || {};
  const entries = Object.entries(currentNode);

  function navigate(name, node) {
    if (node.type === 'file') {
      setSelected(name);
      return;
    }
    setPath((p) => [...p, name]);
    setSelected(null);
  }

  function goBack() {
    if (path.length <= 1) return;
    setPath((p) => p.slice(0, -1));
    setSelected(null);
  }

  function goToIndex(index) {
    setPath((p) => p.slice(0, index + 1));
    setSelected(null);
  }

  function goToSidebar(key) {
    setPath([key]);
    setSelected(null);
  }

  return (
    <div className="finder-layout">
      {/* Sidebar */}
      <aside className="finder-sidebar">
        <p className="sidebar-section-title">Favoritos</p>
        <ul>
          {SIDEBAR_ITEMS.map((item) => (
            <li
              key={item.key}
              className={`sidebar-item ${path[0] === item.key ? 'sidebar-active' : ''}`}
              onClick={() => goToSidebar(item.key)}
            >
              <span className="sidebar-icon">{FS_TREE[item.key]?.icon}</span>
              {item.label}
            </li>
          ))}
        </ul>
        <p className="sidebar-section-title" style={{ marginTop: '16px' }}>Locais</p>
        <ul>
          <li className="sidebar-item">
            <span className="sidebar-icon">💻</span>NovaOS
          </li>
          <li className="sidebar-item">
            <span className="sidebar-icon">☁️</span>iCloud
          </li>
        </ul>
      </aside>

      {/* Main area */}
      <section className="finder-main">
        {/* Toolbar */}
        <div className="finder-toolbar">
          <button
            className="finder-nav-btn"
            onClick={goBack}
            disabled={path.length <= 1}
            title="Voltar"
          >
            ←
          </button>
          <div className="finder-breadcrumb">
            {path.map((segment, i) => (
              <span key={i}>
                {i > 0 && <span className="breadcrumb-sep"> › </span>}
                <button
                  className="breadcrumb-btn"
                  onClick={() => goToIndex(i)}
                >
                  {segment}
                </button>
              </span>
            ))}
          </div>
          <div className="finder-view-toggle">
            <button
              className={`view-btn ${view === 'grid' ? 'active' : ''}`}
              onClick={() => setView('grid')}
              title="Grade"
            >⊞</button>
            <button
              className={`view-btn ${view === 'list' ? 'active' : ''}`}
              onClick={() => setView('list')}
              title="Lista"
            >☰</button>
          </div>
        </div>

        {/* Files */}
        {view === 'grid' ? (
          <div className="finder-grid">
            {entries.map(([name, node]) => (
              <div
                key={name}
                className={`finder-grid-item ${selected === name ? 'selected' : ''} ${node.type !== 'file' ? 'is-folder' : ''}`}
                onClick={() => { setSelected(name); }}
                onDoubleClick={() => navigate(name, node)}
                title={node.type === 'file' ? name : `Abrir ${name}`}
              >
                <span className="finder-item-icon">{node.icon}</span>
                <span className="finder-item-name">{name}</span>
                {node.type === 'file' && <span className="finder-item-size">{node.size}</span>}
              </div>
            ))}
            {entries.length === 0 && (
              <p className="finder-empty">Pasta vazia</p>
            )}
          </div>
        ) : (
          <div className="finder-list">
            <div className="finder-list-header">
              <span>Nome</span>
              <span>Tamanho</span>
              <span>Tipo</span>
            </div>
            {entries.map(([name, node]) => (
              <div
                key={name}
                className={`finder-list-row ${selected === name ? 'selected' : ''}`}
                onClick={() => setSelected(name)}
                onDoubleClick={() => navigate(name, node)}
              >
                <span className="finder-list-name">
                  <span className="finder-item-icon-sm">{node.icon}</span>
                  {name}
                </span>
                <span>{node.type === 'file' ? node.size : '—'}</span>
                <span>{node.type === 'file' ? 'Arquivo' : 'Pasta'}</span>
              </div>
            ))}
            {entries.length === 0 && (
              <p className="finder-empty">Pasta vazia</p>
            )}
          </div>
        )}
      </section>
    </div>
  );
}
