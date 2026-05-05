import React, { useState } from 'react';
import { PROVIDERS, PAID_MODELS } from '../constants/models.js';

const CONFIGURABLE = Object.entries(PROVIDERS).filter(([id, p]) => id === 'openrouter' || !p.free);

export default function SettingsModal({ apiKeys, onSave, onClose }) {
  const [keys, setKeys]     = useState({ ...apiKeys });
  const [show, setShow]     = useState({});

  const update = (k, v) => setKeys(prev => ({ ...prev, [k]: v }));
  const toggle = (k) => setShow(prev => ({ ...prev, [k]: !prev[k] }));

  const handleSave = () => { onSave(keys); onClose(); };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="relative flex max-h-[90vh] w-full max-w-lg flex-col rounded-3xl border border-white/10 bg-[#0e1714] shadow-2xl">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/8 px-6 py-4">
          <h2 className="text-lg font-semibold text-white">Configurar APIs</h2>
          <button onClick={onClose} className="text-zinc-500 hover:text-zinc-200">✕</button>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto px-6 py-4 space-y-4">
          {CONFIGURABLE.map(([id, prov]) => {
            const modelsForProvider = PAID_MODELS.filter(m => m.provider === id);
            return (
              <div key={id} className="rounded-2xl border border-white/8 bg-white/[0.03] p-4">
                <div className="mb-2 flex items-center gap-2">
                  <span className="text-base">{prov.icon}</span>
                  <span className="text-sm font-medium text-zinc-200">{prov.name}</span>
                  {prov.link && (
                    <a
                      href={`https://${prov.link}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="ml-auto text-[11px] text-emerald-400/80 hover:text-emerald-400"
                    >
                      Obter chave ↗
                    </a>
                  )}
                </div>

                {/* Model badges */}
                {modelsForProvider.length > 0 && (
                  <div className="mb-3 flex flex-wrap gap-1.5">
                    {modelsForProvider.slice(0, 6).map(m => (
                      <span key={m.id} className="rounded-md bg-white/5 px-2 py-0.5 text-[10px] text-zinc-400">
                        {m.name}
                      </span>
                    ))}
                    {modelsForProvider.length > 6 && (
                      <span className="rounded-md bg-white/5 px-2 py-0.5 text-[10px] text-zinc-500">
                        +{modelsForProvider.length - 6}
                      </span>
                    )}
                  </div>
                )}

                <div className="relative">
                  <input
                    type={show[id] ? 'text' : 'password'}
                    placeholder={`${prov.name} API Key`}
                    value={keys[id] || ''}
                    onChange={e => update(id, e.target.value)}
                    className="w-full rounded-xl border border-white/8 bg-white/[0.04] px-4 py-2.5 pr-14 text-sm text-zinc-100 outline-none placeholder:text-zinc-600 focus:border-emerald-400/40"
                  />
                  <button
                    type="button"
                    onClick={() => toggle(id)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-[11px] text-zinc-500 hover:text-zinc-300"
                  >
                    {show[id] ? 'Ocultar' : 'Mostrar'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-2 border-t border-white/8 px-6 py-4">
          <button onClick={onClose} className="rounded-xl px-5 py-2.5 text-sm text-zinc-400 hover:text-zinc-200">
            Cancelar
          </button>
          <button
            onClick={handleSave}
            className="rounded-xl bg-emerald-600 px-5 py-2.5 text-sm font-medium text-white hover:bg-emerald-500"
          >
            Salvar
          </button>
        </div>
      </div>
    </div>
  );
}
