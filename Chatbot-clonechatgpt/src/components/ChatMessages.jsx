import React, { useState } from 'react';
import MsgContent from './MsgContent.jsx';
import { SUGGESTIONS } from '../constants/app.js';

function GeneratedImage({ src, alt }) {
  const [status, setStatus] = useState('loading'); // loading | loaded | error

  return (
    <div className="mb-3 relative min-h-[80px]">
      {status === 'loading' && (
        <div className="flex flex-col items-center justify-center gap-2 rounded-2xl border border-white/10 bg-white/5 px-6 py-8 text-zinc-400 text-xs">
          <svg className="h-5 w-5 animate-spin text-emerald-400" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
          Gerando imagem…
        </div>
      )}
      {status === 'error' && (
        <div className="flex flex-col items-center justify-center gap-1 rounded-2xl border border-red-500/20 bg-red-500/10 px-6 py-6 text-red-400 text-xs">
          <span>⚠ Não foi possível carregar a imagem.</span>
          <a href={src} target="_blank" rel="noreferrer" className="underline text-red-300">Abrir link direto</a>
        </div>
      )}
      <a
        href={src}
        target="_blank"
        rel="noreferrer"
        className={status !== 'loaded' ? 'sr-only' : undefined}
      >
        <img
          src={src}
          alt={alt}
          className="max-h-[28rem] max-w-full rounded-2xl border border-white/10 object-contain"
          onLoad={() => setStatus('loaded')}
          onError={() => setStatus('error')}
        />
      </a>
    </div>
  );
}

export default function ChatMessages({
  messages,
  loading,
  selectedModel,
  copiedId,
  onCopyMsg,
  onSend,
  firstName,
  bottomRef,
  userInitial,
}) {
  return (
    <div className="flex-1 overflow-y-auto">
      {messages.length === 0 ? (
        <div className="flex h-full flex-col items-center justify-center px-6 py-12">
          <div className="w-full max-w-4xl rounded-[2rem] border border-white/8 bg-[linear-gradient(145deg,rgba(16,24,22,0.88),rgba(8,12,14,0.74))] p-8 shadow-[0_30px_90px_rgba(0,0,0,0.28)] backdrop-blur-2xl sm:p-12">
            <div className="mb-8 flex flex-wrap items-center gap-4">
              <div className="flex h-16 w-16 items-center justify-center rounded-[1.5rem] bg-[linear-gradient(135deg,#34d399_0%,#14b8a6_45%,#0f172a_100%)] text-2xl shadow-[0_18px_50px_rgba(16,185,129,0.25)] ring-1 ring-white/10">
                ✦
              </div>
              <div>
                <span className="mb-2 inline-flex rounded-full border border-white/10 bg-white/5 px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.2em] text-zinc-300">AI workspace</span>
                <h2 className="text-3xl font-semibold tracking-tight text-white sm:text-4xl">
                  Olá, {firstName}.
                </h2>
                <p className="mt-2 max-w-2xl text-sm leading-7 text-zinc-400 sm:text-base">
                  Um espaço de conversa com acabamento mais editorial, pronto para pesquisa, escrita, arquivos e respostas multimodais.
                </p>
              </div>
            </div>

            <div className="mb-6 grid gap-3 sm:grid-cols-3">
              <div className="rounded-2xl border border-white/8 bg-white/[0.04] px-4 py-4">
                <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-zinc-500">Modelo ativo</p>
                <p className="mt-2 text-sm font-medium text-white">{selectedModel?.name}</p>
                <p className="mt-1 text-xs leading-5 text-zinc-400">{selectedModel?.desc}</p>
              </div>
              <div className="rounded-2xl border border-white/8 bg-white/[0.04] px-4 py-4">
                <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-zinc-500">Fluxo</p>
                <p className="mt-2 text-sm font-medium text-white">Arquivos, busca web e estilos</p>
                <p className="mt-1 text-xs leading-5 text-zinc-400">Tudo acessível no composer sem poluir a tela.</p>
              </div>
              <div className="rounded-2xl border border-white/8 bg-white/[0.04] px-4 py-4">
                <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-zinc-500">Experiência</p>
                <p className="mt-2 text-sm font-medium text-white">Foco em clareza e ritmo visual</p>
                <p className="mt-1 text-xs leading-5 text-zinc-400">Contraste mais limpo, profundidade e navegação mais legível.</p>
              </div>
            </div>

            <div className="grid w-full grid-cols-1 gap-3 sm:grid-cols-2">
              {SUGGESTIONS.map(s => (
                <button
                  key={s}
                  onClick={() => onSend(s)}
                  className="group rounded-2xl border border-white/8 bg-white/[0.04] px-4 py-4 text-left text-sm text-zinc-300 transition hover:-translate-y-0.5 hover:border-emerald-400/20 hover:bg-white/[0.07]"
                >
                  <span className="mb-3 inline-flex rounded-full border border-white/10 bg-black/20 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-[0.18em] text-zinc-500">Prompt</span>
                  <span className="block font-medium text-zinc-100">{s}</span>
                  <span className="mt-3 block text-xs text-zinc-500 transition group-hover:text-zinc-400">Clique para começar</span>
                </button>
              ))}
            </div>
          </div>
        </div>
      ) : (
        <div className="mx-auto max-w-4xl space-y-7 px-5 py-8">
          {messages.map(msg => (
            <div key={msg.id} className={`flex gap-3 ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
              {msg.role === 'assistant' && (
                <div className="mt-1 flex h-9 w-9 shrink-0 items-center justify-center rounded-2xl bg-[linear-gradient(135deg,#34d399_0%,#14b8a6_45%,#0f172a_100%)] text-xs font-bold text-white shadow-[0_14px_35px_rgba(16,185,129,0.22)] ring-1 ring-white/10">
                  C
                </div>
              )}

              <div className={`group max-w-[85%] ${msg.role === 'user' ? 'order-first' : ''}`}>
                {msg.role === 'user' ? (
                  <div className="rounded-[1.6rem] rounded-tr-md border border-emerald-300/10 bg-[linear-gradient(135deg,rgba(16,185,129,0.2),rgba(21,24,28,0.92))] px-4 py-3 text-sm leading-relaxed text-zinc-100 shadow-[0_18px_45px_rgba(0,0,0,0.16)]">
                    {msg.img && (
                      <img
                        src={msg.img}
                        alt={msg.imgName || 'imagem'}
                        className="mb-2 max-h-52 max-w-full rounded-2xl border border-white/10 object-contain"
                      />
                    )}
                    {msg.content && <span>{msg.content}</span>}
                  </div>
                ) : (
                  <div className="rounded-[1.75rem] border border-white/8 bg-white/[0.04] px-5 py-4 text-sm leading-relaxed text-zinc-200 shadow-[0_18px_45px_rgba(0,0,0,0.14)] backdrop-blur-xl">
                    {msg.img && (
                      <GeneratedImage src={msg.img} alt={msg.imgName || 'imagem gerada'} />
                    )}
                    {msg.content ? (
                      <MsgContent content={msg.content} />
                    ) : (
                      <span className="mt-1 inline-flex gap-1">
                        <span className="h-1.5 w-1.5 animate-bounce rounded-full bg-emerald-500" style={{ animationDelay: '0ms' }} />
                        <span className="h-1.5 w-1.5 animate-bounce rounded-full bg-emerald-500" style={{ animationDelay: '150ms' }} />
                        <span className="h-1.5 w-1.5 animate-bounce rounded-full bg-emerald-500" style={{ animationDelay: '300ms' }} />
                      </span>
                    )}
                    {msg.content && (
                      <button
                        onClick={() => onCopyMsg(msg.id, msg.content)}
                        className="mt-3 flex items-center gap-1.5 text-[11px] text-zinc-500 opacity-0 transition hover:text-zinc-200 group-hover:opacity-100"
                      >
                        {copiedId === msg.id ? (
                          <><span>✓</span><span>Copiado</span></>
                        ) : (
                          <><span>⎘</span><span>Copiar</span></>
                        )}
                      </button>
                    )}
                  </div>
                )}
              </div>

              {msg.role === 'user' && (
                <div className="mt-1 flex h-9 w-9 shrink-0 items-center justify-center rounded-2xl bg-white/8 text-xs font-bold text-zinc-100 ring-1 ring-white/8 backdrop-blur-xl">
                  {userInitial}
                </div>
              )}
            </div>
          ))}
          <div ref={bottomRef} />
        </div>
      )}
    </div>
  );
}
