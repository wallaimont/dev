import React, { useState } from 'react';
import { SKILLS } from '../constants/app.js';

export default function ChatInput({
  input,
  setInput,
  loading,
  attachment,
  setAttachment,
  webSearch,
  setWebSearch,
  selectedSkills,
  setSelectedSkills,
  customSkills,
  newSkillName,
  setNewSkillName,
  newSkillPrompt,
  setNewSkillPrompt,
  addCustomSkill,
  removeCustomSkill,
  getSkillMeta,
  onSend,
  onStop,
  onHandleFile,
  onCaptureScreen,
  fileInputRef,
  extrasRef,
  inputRef,
}) {
  const [showExtras, setShowExtras]     = useState(false);
  const [showSkillSub, setShowSkillSub] = useState(false);

  return (
    <div className="shrink-0 px-4 pb-5 pt-3">
      <input
        ref={fileInputRef}
        type="file"
        className="hidden"
        accept="image/*,text/*,.md,.csv,.json,.txt,.js,.ts,.jsx,.tsx,.py,.java,.cs,.cpp,.c,.html,.css"
        onChange={e => { onHandleFile?.(e.target.files?.[0]); e.target.value = ''; }}
      />

      <div className="mx-auto max-w-4xl">
        {/* Active mode badges */}
        {(webSearch || selectedSkills.length > 0) && (
          <div className="mb-3 flex flex-wrap gap-2">
            {webSearch && (
              <span className="inline-flex items-center gap-1 rounded-full border border-blue-700/50 bg-blue-900/40 px-2.5 py-0.5 text-[11px] text-blue-300">
                🌐 Busca web
                <button onClick={() => setWebSearch(false)} className="ml-0.5 leading-none hover:text-white">✕</button>
              </span>
            )}
            {selectedSkills.map(skillKey => {
              const meta = getSkillMeta(skillKey);
              if (!meta) return null;
              return (
                <span key={skillKey} className="inline-flex items-center gap-1 rounded-full border border-teal-700/50 bg-teal-900/40 px-2.5 py-0.5 text-[11px] text-teal-300">
                  {meta.icon} {meta.label}
                  <button
                    onClick={() => setSelectedSkills(prev => prev.filter(k => k !== skillKey))}
                    className="ml-0.5 leading-none hover:text-white"
                  >✕</button>
                </span>
              );
            })}
          </div>
        )}

        {/* Attachment preview */}
        {attachment && (
          <div className="mb-3 flex items-center gap-2 rounded-2xl border border-white/10 bg-white/[0.05] px-3 py-2.5 shadow-[0_14px_30px_rgba(0,0,0,0.14)] backdrop-blur-xl">
            {attachment.type === 'image' ? (
              <img src={attachment.data} alt={attachment.name} className="h-11 w-11 shrink-0 rounded-xl border border-white/10 object-cover" />
            ) : (
              <span className="shrink-0 text-lg">📄</span>
            )}
            <span className="flex-1 truncate text-xs text-zinc-300">{attachment.name}</span>
            <button onClick={() => setAttachment(null)} className="shrink-0 text-xs text-zinc-500 transition hover:text-red-400">✕</button>
          </div>
        )}

        <div className="flex items-end gap-3 rounded-[1.75rem] border border-white/10 bg-[linear-gradient(180deg,rgba(15,23,24,0.82),rgba(10,14,18,0.96))] px-3 py-3.5 shadow-[0_22px_70px_rgba(0,0,0,0.28)] backdrop-blur-2xl transition focus-within:border-emerald-400/25 focus-within:shadow-[0_22px_80px_rgba(16,185,129,0.12)]">

          {/* Extras button */}
          <div ref={extrasRef} className="relative shrink-0">
            <button
              onClick={() => { setShowExtras(v => !v); setShowSkillSub(false); }}
              className="flex h-10 w-10 items-center justify-center rounded-2xl border border-white/8 bg-white/[0.06] text-zinc-300 transition hover:border-white/12 hover:bg-white/[0.11] hover:text-white"
              title="Mais opções"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M12 5v14M5 12h14"/>
              </svg>
            </button>

            {showExtras && (
              <div className="absolute bottom-full left-0 z-50 mb-3 w-56 overflow-hidden rounded-3xl border border-white/10 bg-[#101716]/95 py-1 shadow-2xl backdrop-blur-2xl">
                <button
                  onClick={() => { fileInputRef.current?.click(); setShowExtras(false); }}
                  className="flex w-full items-center gap-3 px-4 py-2.5 text-left text-sm text-zinc-200 transition hover:bg-zinc-800"
                >
                  <span>📎</span> Adicionar arquivos ou fotos
                </button>
                <button
                  onClick={() => { onCaptureScreen?.(); setShowExtras(false); }}
                  className="flex w-full items-center gap-3 px-4 py-2.5 text-left text-sm text-zinc-200 transition hover:bg-zinc-800"
                >
                  <span>📸</span> Fazer captura de tela
                </button>
                <div className="my-1 h-px bg-zinc-800" />
                <button
                  onClick={() => setShowSkillSub(v => !v)}
                  className="flex w-full items-center justify-between gap-3 px-4 py-2.5 text-left text-sm text-zinc-200 transition hover:bg-zinc-800"
                >
                  <span className="flex items-center gap-3"><span>🧠</span> Usar skills</span>
                  <span className="text-xs text-zinc-500">{showSkillSub ? '▲' : '▶'}</span>
                </button>
                {showSkillSub && (
                  <div className="bg-zinc-800/50 px-2 pb-1">
                    <p className="px-3 pb-1 pt-2 text-[10px] uppercase tracking-[0.16em] text-zinc-500">Padrão</p>
                    {Object.entries(SKILLS).map(([key, skill]) => {
                      const active = selectedSkills.includes(key);
                      return (
                        <button
                          key={key}
                          onClick={() => setSelectedSkills(prev =>
                            prev.includes(key) ? prev.filter(k => k !== key) : [...prev, key]
                          )}
                          className={`flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-xs transition ${
                            active ? 'bg-teal-900/30 text-teal-300' : 'text-zinc-300 hover:bg-zinc-700'
                          }`}
                        >
                          <span>{skill.icon}</span> {skill.label}
                          {active && <span className="ml-auto text-teal-400">✓</span>}
                        </button>
                      );
                    })}
                    <div className="my-2 h-px bg-zinc-700/60" />
                    <p className="px-3 pb-1 pt-1 text-[10px] uppercase tracking-[0.16em] text-zinc-500">Personalizadas</p>
                    {customSkills.length === 0 && (
                      <p className="px-3 py-2 text-[11px] text-zinc-500">Nenhuma skill personalizada criada.</p>
                    )}
                    {customSkills.map(skill => {
                      const key    = `custom:${skill.id}`;
                      const active = selectedSkills.includes(key);
                      return (
                        <div key={skill.id} className="flex items-center gap-1">
                          <button
                            onClick={() => setSelectedSkills(prev =>
                              prev.includes(key) ? prev.filter(k => k !== key) : [...prev, key]
                            )}
                            className={`flex flex-1 items-center gap-2 rounded-lg px-3 py-2 text-left text-xs transition ${
                              active ? 'bg-teal-900/30 text-teal-300' : 'text-zinc-300 hover:bg-zinc-700'
                            }`}
                          >
                            <span>{skill.icon || '🧩'}</span> {skill.label}
                            {active && <span className="ml-auto text-teal-400">✓</span>}
                          </button>
                          <button
                            onClick={() => removeCustomSkill(skill.id)}
                            className="px-2 py-2 text-xs text-zinc-500 transition hover:text-red-400"
                            title="Remover skill"
                          >✕</button>
                        </div>
                      );
                    })}
                    <div className="mt-2 space-y-2 rounded-lg border border-zinc-700/60 bg-zinc-900/50 p-2.5">
                      <p className="text-[10px] uppercase tracking-[0.16em] text-zinc-500">Nova skill</p>
                      <input
                        value={newSkillName}
                        onChange={e => setNewSkillName(e.target.value)}
                        placeholder="Nome (ex: Clean Architecture)"
                        className="w-full rounded-md border border-zinc-700 bg-zinc-900 px-2 py-1.5 text-[11px] text-zinc-200 outline-none focus:border-zinc-500"
                      />
                      <textarea
                        value={newSkillPrompt}
                        onChange={e => setNewSkillPrompt(e.target.value)}
                        placeholder="Regra da skill para a LLM seguir"
                        rows={3}
                        className="w-full resize-none rounded-md border border-zinc-700 bg-zinc-900 px-2 py-1.5 text-[11px] text-zinc-200 outline-none focus:border-zinc-500"
                      />
                      <button
                        onClick={addCustomSkill}
                        disabled={!newSkillName.trim() || !newSkillPrompt.trim()}
                        className="w-full rounded-md bg-teal-600 py-1.5 text-[11px] font-semibold text-white transition hover:bg-teal-500 disabled:cursor-not-allowed disabled:opacity-40"
                      >
                        Adicionar skill
                      </button>
                    </div>
                    {selectedSkills.length > 0 && (
                      <button
                        onClick={() => setSelectedSkills([])}
                        className="mt-1 w-full rounded-lg px-3 py-2 text-left text-[11px] text-zinc-400 transition hover:bg-zinc-700"
                      >
                        Limpar skills
                      </button>
                    )}
                  </div>
                )}
                <div className="my-1 h-px bg-zinc-800" />
                <button
                  onClick={() => { setWebSearch(v => !v); setShowExtras(false); }}
                  className={`flex w-full items-center gap-3 px-4 py-2.5 text-left text-sm transition hover:bg-zinc-800 ${webSearch ? 'text-blue-300' : 'text-zinc-200'}`}
                >
                  <span>🌐</span> Busca na web
                  {webSearch && <span className="ml-auto text-xs text-blue-400">✓</span>}
                </button>
              </div>
            )}
          </div>

          {/* Textarea */}
          <textarea
            ref={inputRef}
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                onSend();
              }
            }}
            placeholder="Mensagem para Chaat IA..."
            rows={1}
            style={{ resize: 'none' }}
            className="max-h-36 flex-1 overflow-y-auto bg-transparent px-1 pt-1 text-sm leading-relaxed outline-none placeholder:text-zinc-500"
          />

          {/* Send / Stop */}
          {loading ? (
            <button
              onClick={onStop}
              className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-red-600/80 text-white shadow-lg transition hover:scale-[1.02] hover:bg-red-500"
              title="Parar geração"
            >
              <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                <rect x="4" y="4" width="16" height="16" rx="2"/>
              </svg>
            </button>
          ) : (
            <button
              onClick={() => onSend()}
              disabled={!input.trim() && !attachment}
              className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[linear-gradient(135deg,#34d399_0%,#14b8a6_55%,#0891b2_100%)] text-white shadow-[0_16px_40px_rgba(20,184,166,0.28)] transition hover:scale-[1.02] disabled:cursor-not-allowed disabled:opacity-40"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <line x1="12" y1="19" x2="12" y2="5"/>
                <polyline points="5 12 12 5 19 12"/>
              </svg>
            </button>
          )}
        </div>

        <p className="mt-2 text-center text-[10px] uppercase tracking-[0.18em] text-zinc-600">
          Enter para enviar · Shift+Enter para nova linha
        </p>
      </div>
    </div>
  );
}
