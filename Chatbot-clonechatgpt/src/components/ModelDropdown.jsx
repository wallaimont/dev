import React from 'react';
import { FREE_MODELS, PAID_MODELS, IMAGE_MODELS, PROVIDERS } from '../constants/models.js';

export default function ModelDropdown({
  show,
  menuPos,
  dropdownRef,
  selectedModel,
  modelSearch,
  setModelSearch,
  onSelect,
}) {
  if (!show) return null;

  const q             = modelSearch.toLowerCase();
  const filteredFree  = FREE_MODELS.filter(m => !q || m.name.toLowerCase().includes(q) || m.desc.toLowerCase().includes(q));
  const filteredPaid  = PAID_MODELS.filter(m => !q || m.name.toLowerCase().includes(q) || m.desc.toLowerCase().includes(q));
  const filteredImage = IMAGE_MODELS.filter(m => !q || m.name.toLowerCase().includes(q) || m.desc.toLowerCase().includes(q));

  const ModelButton = ({ m }) => (
    <button
      key={m.id}
      onMouseDown={() => onSelect(m)}
      style={{
        background: m.id === selectedModel.id ? 'rgba(39,39,42,0.8)' : 'transparent',
        color: '#e4e4e7',
        width: '100%',
      }}
      className="flex items-start gap-2 px-3 py-2 text-left transition hover:bg-zinc-800"
    >
      <span className={`mt-1.5 h-2 w-2 shrink-0 rounded-full ${PROVIDERS[m.provider]?.color}`} />
      <div>
        <div className="text-sm font-medium leading-tight">{m.name}</div>
        <div className="mt-0.5 text-[11px] leading-tight text-zinc-500">{m.desc}</div>
      </div>
      {m.id === selectedModel.id && (
        <span className="ml-auto mt-0.5 shrink-0 text-xs text-emerald-400">✓</span>
      )}
    </button>
  );

  return (
    <div
      ref={dropdownRef}
      style={{ position: 'fixed', top: menuPos.top, left: menuPos.left, zIndex: 99999 }}
      className="w-80 overflow-hidden rounded-3xl border border-white/10 shadow-2xl"
      onMouseDown={e => e.stopPropagation()}
    >
      <div className="flex max-h-[70vh] flex-col" style={{ background: '#0f1714', color: '#e4e4e7' }}>
        {/* Search */}
        <div className="shrink-0 border-b border-zinc-800 px-3 pb-2 pt-3">
          <div className="flex items-center gap-2 rounded-xl bg-zinc-800 px-3 py-1.5">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="shrink-0 text-zinc-500">
              <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
            </svg>
            <input
              autoFocus
              value={modelSearch}
              onChange={e => setModelSearch(e.target.value)}
              placeholder="Buscar modelo..."
              className="flex-1 bg-transparent text-xs text-zinc-200 outline-none placeholder:text-zinc-500"
            />
            {modelSearch && (
              <button onClick={() => setModelSearch('')} className="text-xs text-zinc-500 hover:text-zinc-300">✕</button>
            )}
          </div>
        </div>

        <div className="flex-1 overflow-y-auto">
          {filteredFree.length > 0 && (
            <>
              <div className="px-3 pb-1 pt-3">
                <p className="px-1 text-[10px] font-semibold uppercase tracking-wider text-zinc-500">Gratuitos</p>
              </div>
              {filteredFree.map(m => <ModelButton key={m.id} m={m} />)}
            </>
          )}
          {filteredPaid.length > 0 && (
            <>
              <div className="mt-1 border-t border-zinc-800 px-3 pb-1 pt-3">
                <p className="px-1 text-[10px] font-semibold uppercase tracking-wider text-zinc-500">Pagos</p>
              </div>
              {filteredPaid.map(m => <ModelButton key={m.id} m={m} />)}
            </>
          )}
          {filteredImage.length > 0 && (
            <>
              <div className="mt-1 border-t border-zinc-800 px-3 pb-1 pt-3">
                <p className="px-1 text-[10px] font-semibold uppercase tracking-wider text-zinc-500">Imagem</p>
              </div>
              {filteredImage.map(m => <ModelButton key={m.id} m={m} />)}
            </>
          )}
          {filteredFree.length === 0 && filteredPaid.length === 0 && filteredImage.length === 0 && (
            <p className="px-4 py-6 text-center text-xs text-zinc-500">Nenhum modelo encontrado</p>
          )}
        </div>
      </div>
    </div>
  );
}
