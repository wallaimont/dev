import React from 'react';
import { PROVIDERS } from '../constants/models.js';

export default function ChatHeader({
  onToggleSidebar,
  selectedModel,
  modelBtnRef,
  showModelMenu,
  onToggleModelMenu,
  actualModel,
  hasMessages,
  onExport,
}) {
  const providerColor = PROVIDERS[selectedModel?.provider]?.color ?? 'bg-zinc-500';

  return (
    <header className="relative z-50 shrink-0 border-b border-white/8 bg-black/10 px-4 py-4 backdrop-blur-xl sm:px-6">
      <div className="flex items-center gap-3">
        {/* Hamburger (mobile) */}
        <button
          onClick={onToggleSidebar}
          className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-white/10 bg-white/6 text-zinc-300 transition hover:bg-white/10 md:hidden"
          title="Menu"
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <line x1="3" y1="6" x2="21" y2="6"/>
            <line x1="3" y1="12" x2="21" y2="12"/>
            <line x1="3" y1="18" x2="21" y2="18"/>
          </svg>
        </button>

        {/* Model selector button */}
        <button
          ref={modelBtnRef}
          onClick={onToggleModelMenu}
          className="flex items-center gap-2 rounded-2xl border border-white/10 bg-white/6 px-4 py-2 text-sm shadow-[0_10px_30px_rgba(0,0,0,0.16)] transition hover:border-white/14 hover:bg-white/10"
        >
          <span className={`h-2 w-2 rounded-full ${providerColor}`} />
          <span className="font-medium">{selectedModel?.name}</span>
          <svg
            width="12" height="12" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" strokeWidth="2.5"
            className={`transition-transform ${showModelMenu ? 'rotate-180' : ''}`}
          >
            <polyline points="6 9 12 15 18 9"/>
          </svg>
        </button>

        {/* Right section */}
        <div className="ml-auto flex items-center gap-2">
          {actualModel && (
            <span className="hidden items-center gap-1 rounded-full border border-amber-700/30 bg-amber-900/30 px-2.5 py-1 text-[11px] text-amber-300 sm:inline-flex">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M9 14L4 9l5-5"/><path d="M4 9h10a6 6 0 0 1 0 12h-1"/>
              </svg>
              {actualModel.name}
            </span>
          )}
          {hasMessages && (
            <button
              onClick={onExport}
              className="hidden h-8 items-center gap-1.5 rounded-xl border border-white/10 bg-white/6 px-3 text-xs text-zinc-400 transition hover:bg-white/10 hover:text-white sm:flex"
              title="Exportar como Markdown"
            >
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
              Exportar
            </button>
          )}
          <div className="hidden items-center gap-3 sm:flex">
            <div className="rounded-full border border-emerald-400/15 bg-emerald-500/10 px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-emerald-300">
              Assistente pronto
            </div>
            <p className="max-w-[16rem] text-right text-xs leading-5 text-zinc-500">
              {selectedModel?.desc}
            </p>
          </div>
        </div>
      </div>
    </header>
  );
}
