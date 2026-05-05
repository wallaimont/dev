import { useState } from 'react';

const STORAGE_KEY = 'novaos-notes-v2';

function loadNotes() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) return JSON.parse(raw);
  } catch {/* */}
  return [
    { id: 1, title: 'Checklist de hoje', content: '- Revisar backlog\n- Fazer deploy\n- Atualizar documentação', updatedAt: Date.now() },
    { id: 2, title: 'Ideias', content: 'Melhorar UX da tela de login\nAdicionar modo escuro no app mobile', updatedAt: Date.now() - 86400000 },
    { id: 3, title: 'Links úteis', content: 'https://react.dev\nhttps://developer.mozilla.org\nhttps://css-tricks.com', updatedAt: Date.now() - 172800000 },
  ];
}

let noteCounter = 10;

function formatDate(ts) {
  const d = new Date(ts);
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
}

export default function NotesApp() {
  const [notes, setNotes] = useState(loadNotes);
  const [activeId, setActiveId] = useState(loadNotes()[0]?.id ?? null);

  const activeNote = notes.find((n) => n.id === activeId) || null;

  function saveNotes(updated) {
    setNotes(updated);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
  }

  function newNote() {
    const id = ++noteCounter;
    const note = { id, title: 'Nova nota', content: '', updatedAt: Date.now() };
    const updated = [note, ...notes];
    saveNotes(updated);
    setActiveId(id);
  }

  function deleteNote(id) {
    const updated = notes.filter((n) => n.id !== id);
    saveNotes(updated);
    if (activeId === id) setActiveId(updated[0]?.id ?? null);
  }

  function updateContent(value) {
    const updated = notes.map((n) =>
      n.id === activeId ? { ...n, content: value, updatedAt: Date.now() } : n
    );
    saveNotes(updated);
  }

  function updateTitle(value) {
    const updated = notes.map((n) =>
      n.id === activeId ? { ...n, title: value, updatedAt: Date.now() } : n
    );
    saveNotes(updated);
  }

  return (
    <div className="notes-layout">
      {/* Sidebar */}
      <aside className="notes-sidebar">
        <div className="notes-sidebar-header">
          <span>Notas</span>
          <button className="notes-new-btn" onClick={newNote} title="Nova nota">+</button>
        </div>
        <ul className="notes-list">
          {notes.map((note) => (
            <li
              key={note.id}
              className={`notes-list-item ${activeId === note.id ? 'active' : ''}`}
              onClick={() => setActiveId(note.id)}
            >
              <div className="notes-list-title">{note.title || 'Sem título'}</div>
              <div className="notes-list-preview">
                <span className="notes-list-date">{formatDate(note.updatedAt)}</span>
                <span>{note.content.split('\n')[0].slice(0, 30) || 'Sem conteúdo'}</span>
              </div>
              <button
                className="notes-delete-btn"
                onClick={(e) => { e.stopPropagation(); deleteNote(note.id); }}
                title="Deletar nota"
              >×</button>
            </li>
          ))}
          {notes.length === 0 && (
            <li className="notes-empty">Nenhuma nota</li>
          )}
        </ul>
      </aside>

      {/* Editor */}
      <div className="notes-editor-panel">
        {activeNote ? (
          <>
            <input
              className="notes-title-input"
              value={activeNote.title}
              onChange={(e) => updateTitle(e.target.value)}
              placeholder="Título"
            />
            <div className="notes-meta">
              {formatDate(activeNote.updatedAt)} · {activeNote.content.length} caracteres
            </div>
            <textarea
              className="notes-textarea"
              value={activeNote.content}
              onChange={(e) => updateContent(e.target.value)}
              placeholder="Comece a escrever..."
            />
          </>
        ) : (
          <div className="notes-no-selection">
            <span>📝</span>
            <p>Selecione ou crie uma nota</p>
            <button className="notes-new-btn-large" onClick={newNote}>Nova nota</button>
          </div>
        )}
      </div>
    </div>
  );
}
