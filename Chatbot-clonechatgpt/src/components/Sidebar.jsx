import React, { useState } from 'react';

export default function Sidebar({
  user,
  conversations,
  activeId,
  showSidebar,
  setShowSidebar,
  convSearch,
  setConvSearch,
  onSelectConv,
  onNewConversation,
  onDeleteConversation,
  onCommitRename,
  onLogout,
  onOpenSettings,
}) {
  const [editingConvId, setEditingConvId] = useState(null);
  const [editingTitle, setEditingTitle]   = useState('');

  const startRename = (conv, e) => {
    e?.stopPropagation();
    setEditingConvId(conv.id);
    setEditingTitle(conv.title || '');
  };

  const commitRename = (id) => {
    onCommitRename(id, editingTitle);
    setEditingConvId(null);
    setEditingTitle('');
  };

  const userInitial = (user?.name || user?.email || 'U').charAt(0).toUpperCase();

  const filtered = conversations.filter(c =>
    !convSearch || (c.title || '').toLowerCase().includes(convSearch.toLowerCase())
  );

  return (
    <aside
      className={`fixed inset-y-0 left-0 z-30 w-72 transition-transform duration-300 border-r border-white/8 bg-zinc-950/95 backdrop-blur-xl flex flex-col ${
        showSidebar ? 'translate-x-0' : '-translate-x-full'
      } md:relative md:z-10 md:translate-x-0 md:inset-auto`}
    >
      {/* Top: logo + new chat */}
      <div className="border-b border-white/8 px-5 py-5">
        <div className="mb-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-[linear-gradient(135deg,#34d399_0%,#0f766e_52%,#0f172a_100%)] text-sm font-black text-white shadow-[0_12px_30px_rgba(16,185,129,0.25)] ring-1 ring-white/10">
              C
            </div>
            <div>
              <span className="block text-sm font-semibold tracking-[0.16em] text-zinc-300/90 uppercase">Chaat IA</span>
              <span className="block text-[11px] text-zinc-500">Workspace de conversas inteligentes</span>
            </div>
          </div>
          <span className="rounded-full border border-emerald-400/20 bg-emerald-500/10 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-[0.18em] text-emerald-300">
            Live
          </span>
        </div>

        <button
          onClick={onNewConversation}
          className="flex w-full items-center justify-between rounded-2xl border border-white/10 bg-white/6 px-4 py-3 text-left shadow-[0_14px_40px_rgba(0,0,0,0.18)] transition hover:border-emerald-400/30 hover:bg-white/10"
          title="Nova conversa"
        >
          <div>
            <span className="block text-sm font-semibold text-white">Nova conversa</span>
            <span className="block text-xs text-zinc-400">Abra um novo contexto em segundos</span>
          </div>
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-500 text-zinc-950 shadow-lg shadow-emerald-950/30">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M12 5v14M5 12h14"/>
            </svg>
          </span>
        </button>
      </div>

      {/* Search */}
      <div className="px-3 pt-2 pb-1">
        <div className="flex items-center gap-2 rounded-xl border border-white/8 bg-white/[0.04] px-3 py-1.5">
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="shrink-0 text-zinc-500">
            <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
          </svg>
          <input
            value={convSearch}
            onChange={e => setConvSearch(e.target.value)}
            placeholder="Buscar conversas..."
            className="flex-1 bg-transparent text-xs text-zinc-300 outline-none placeholder:text-zinc-600"
          />
          {convSearch && (
            <button onClick={() => setConvSearch('')} className="text-xs leading-none text-zinc-500 hover:text-zinc-300">✕</button>
          )}
        </div>
      </div>

      {/* Conversation list */}
      <div className="flex-1 overflow-y-auto px-3 py-2 space-y-1.5">
        {conversations.length === 0 ? (
          <p className="mt-8 px-4 text-center text-xs leading-6 text-zinc-500">
            Nenhuma conversa ainda.<br/>Clique em + para começar.
          </p>
        ) : (
          filtered.map(conv => (
            <div
              key={conv.id}
              onClick={() => {
                onSelectConv(conv.id);
                if (typeof window !== 'undefined' && window.innerWidth < 768) setShowSidebar(false);
              }}
              className={`group flex w-full cursor-pointer items-center justify-between gap-1 rounded-2xl border px-3.5 py-3 text-left text-sm transition ${
                conv.id === activeId
                  ? 'border-emerald-400/20 bg-emerald-500/10 text-white shadow-[0_10px_30px_rgba(16,185,129,0.08)]'
                  : 'border-transparent bg-white/0 text-zinc-400 hover:border-white/8 hover:bg-white/6 hover:text-zinc-200'
              }`}
            >
              {editingConvId === conv.id ? (
                <input
                  autoFocus
                  value={editingTitle}
                  onChange={e => setEditingTitle(e.target.value)}
                  onBlur={() => commitRename(conv.id)}
                  onKeyDown={e => {
                    if (e.key === 'Enter') commitRename(conv.id);
                    if (e.key === 'Escape') { setEditingConvId(null); setEditingTitle(''); }
                  }}
                  onClick={e => e.stopPropagation()}
                  className="min-w-0 flex-1 border-b border-emerald-400/60 bg-transparent text-sm text-white outline-none"
                />
              ) : (
                <span
                  className="flex-1 truncate font-medium"
                  onDoubleClick={e => startRename(conv, e)}
                  title="Duplo-clique para renomear"
                >
                  {conv.title || 'Nova conversa'}
                </span>
              )}
              <div className="flex shrink-0 items-center gap-0.5 opacity-0 transition group-hover:opacity-100">
                <span
                  onClick={e => startRename(conv, e)}
                  className="cursor-pointer rounded-lg p-1 text-xs text-zinc-600 hover:bg-zinc-700/60 hover:text-zinc-300"
                  title="Renomear"
                >✏</span>
                <span
                  onClick={e => onDeleteConversation(conv.id, e)}
                  className="cursor-pointer rounded-lg p-1 text-xs text-zinc-600 hover:bg-red-500/10 hover:text-red-300"
                  title="Excluir"
                >✕</span>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Footer */}
      <div className="space-y-2 border-t border-white/8 bg-black/10 px-4 py-4">
        <button
          onClick={onOpenSettings}
          className="flex w-full items-center gap-2 rounded-2xl border border-white/8 bg-white/6 px-3 py-2.5 text-xs text-zinc-300 transition hover:border-white/12 hover:bg-white/10 hover:text-white"
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
          </svg>
          Configurar APIs
        </button>

        <div className="flex items-center justify-between rounded-2xl border border-white/8 bg-white/5 px-3 py-3">
          <div className="flex min-w-0 items-center gap-2">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-[linear-gradient(135deg,#34d399_0%,#0f766e_100%)] text-xs font-bold text-white shadow-lg shadow-emerald-950/40">
              {userInitial}
            </div>
            <div className="min-w-0">
              <span className="block truncate text-xs font-medium text-zinc-200">{user?.name || user?.email}</span>
              <span className="block truncate text-[10px] uppercase tracking-[0.16em] text-zinc-500">Conta ativa</span>
            </div>
          </div>
          <button
            onClick={onLogout}
            className="ml-1 shrink-0 rounded-xl p-2 text-xs text-zinc-500 transition hover:bg-red-500/10 hover:text-red-300"
            title="Sair"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
          </button>
        </div>
      </div>
    </aside>
  );
}
