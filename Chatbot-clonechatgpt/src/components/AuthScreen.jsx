import React, { useState } from 'react';
import { login, register, loginWithGoogle, loginWithGitHub } from '../auth.js';

function IconGoogle() {
  return (
    <svg width="18" height="18" viewBox="0 0 48 48" className="shrink-0">
      <path fill="#FFC107" d="M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8-6.627 0-12-5.373-12-12s5.373-12 12-12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 12.955 4 4 12.955 4 24s8.955 20 20 20 20-8.955 20-20c0-1.341-.138-2.65-.389-3.917z"/>
      <path fill="#FF3D00" d="m6.306 14.691 6.571 4.819C14.655 15.108 18.961 12 24 12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 16.318 4 9.656 8.337 6.306 14.691z"/>
      <path fill="#4CAF50" d="M24 44c5.166 0 9.86-1.977 13.409-5.192l-6.19-5.238A11.91 11.91 0 0 1 24 36c-5.202 0-9.619-3.317-11.283-7.946l-6.522 5.025C9.505 39.556 16.227 44 24 44z"/>
      <path fill="#1976D2" d="M43.611 20.083H42V20H24v8h11.303a12.04 12.04 0 0 1-4.087 5.571l.003-.002 6.19 5.238C36.971 39.205 44 34 44 24c0-1.341-.138-2.65-.389-3.917z"/>
    </svg>
  );
}

function IconGitHub() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" className="shrink-0">
      <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/>
    </svg>
  );
}

export default function AuthScreen({ onAuth }) {
  const [mode, setMode]               = useState('login');
  const [name, setName]               = useState('');
  const [email, setEmail]             = useState('');
  const [password, setPassword]       = useState('');
  const [showPw, setShowPw]           = useState(false);
  const [loading, setLoading]         = useState(false);
  const [socialLoading, setSocial]    = useState('');
  const [error, setError]             = useState('');

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const user = mode === 'login'
        ? await login(email, password)
        : await register(name, email, password);
      onAuth(user);
    } catch (err) {
      setError(err.message || 'Erro desconhecido');
    } finally {
      setLoading(false);
    }
  };

  const handleSocial = async (provider) => {
    setSocial(provider);
    setError('');
    try {
      const user = provider === 'google'
        ? await loginWithGoogle()
        : await loginWithGitHub();
      onAuth(user);
    } catch (err) {
      setError(err.message || 'Erro na autenticação social');
    } finally {
      setSocial('');
    }
  };

  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-[#07110f] px-4">
      {/* Background blobs */}
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute -left-24 top-[-12rem] h-80 w-80 rounded-full bg-emerald-500/18 blur-3xl" />
        <div className="absolute right-[-10rem] top-20 h-[28rem] w-[28rem] rounded-full bg-cyan-400/10 blur-3xl" />
        <div className="absolute bottom-[-10rem] left-1/3 h-72 w-72 rounded-full bg-teal-300/10 blur-3xl" />
      </div>

      <div className="relative z-10 w-full max-w-sm">
        {/* Logo */}
        <div className="mb-8 flex flex-col items-center gap-3">
          <div className="flex h-14 w-14 items-center justify-center rounded-[1.4rem] bg-[linear-gradient(135deg,#34d399_0%,#0f766e_52%,#0f172a_100%)] text-xl font-black text-white shadow-[0_18px_50px_rgba(16,185,129,0.25)] ring-1 ring-white/10">
            C
          </div>
          <div className="text-center">
            <span className="block text-lg font-semibold tracking-[0.16em] text-zinc-300/90 uppercase">Chaat IA</span>
            <span className="block text-xs text-zinc-500">Workspace de conversas inteligentes</span>
          </div>
        </div>

        {/* Card */}
        <div className="rounded-[2rem] border border-white/8 bg-[linear-gradient(145deg,rgba(16,24,22,0.9),rgba(8,12,14,0.8))] p-8 shadow-[0_30px_90px_rgba(0,0,0,0.3)] backdrop-blur-2xl">
          <h2 className="mb-6 text-center text-xl font-semibold text-white">
            {mode === 'login' ? 'Entrar na conta' : 'Criar conta'}
          </h2>

          {/* Social buttons */}
          <div className="mb-5 flex flex-col gap-2.5">
            <button
              onClick={() => handleSocial('google')}
              disabled={!!socialLoading}
              className="flex items-center justify-center gap-3 rounded-2xl border border-white/10 bg-white/6 py-3 text-sm text-zinc-200 transition hover:bg-white/10 disabled:opacity-50"
            >
              <IconGoogle />
              {socialLoading === 'google' ? 'Aguarde...' : 'Continuar com Google'}
            </button>
            <button
              onClick={() => handleSocial('github')}
              disabled={!!socialLoading}
              className="flex items-center justify-center gap-3 rounded-2xl border border-white/10 bg-white/6 py-3 text-sm text-zinc-200 transition hover:bg-white/10 disabled:opacity-50"
            >
              <IconGitHub />
              {socialLoading === 'github' ? 'Aguarde...' : 'Continuar com GitHub'}
            </button>
          </div>

          <div className="relative mb-5 flex items-center gap-3">
            <div className="h-px flex-1 bg-white/8" />
            <span className="text-[11px] uppercase tracking-[0.16em] text-zinc-600">ou</span>
            <div className="h-px flex-1 bg-white/8" />
          </div>

          <form onSubmit={submit} className="flex flex-col gap-3">
            {mode === 'register' && (
              <input
                type="text"
                placeholder="Seu nome"
                value={name}
                onChange={e => setName(e.target.value)}
                className="rounded-xl border border-white/8 bg-white/[0.04] px-4 py-3 text-sm text-zinc-100 outline-none placeholder:text-zinc-600 focus:border-emerald-400/40"
              />
            )}
            <input
              type="email"
              placeholder="E-mail"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              className="rounded-xl border border-white/8 bg-white/[0.04] px-4 py-3 text-sm text-zinc-100 outline-none placeholder:text-zinc-600 focus:border-emerald-400/40"
            />
            <div className="relative">
              <input
                type={showPw ? 'text' : 'password'}
                placeholder="Senha"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
                className="w-full rounded-xl border border-white/8 bg-white/[0.04] px-4 py-3 pr-12 text-sm text-zinc-100 outline-none placeholder:text-zinc-600 focus:border-emerald-400/40"
              />
              <button
                type="button"
                onClick={() => setShowPw(v => !v)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-zinc-500 hover:text-zinc-300"
              >
                {showPw ? 'Ocultar' : 'Mostrar'}
              </button>
            </div>

            {error && (
              <p className="rounded-xl border border-red-500/20 bg-red-500/10 px-4 py-2.5 text-xs text-red-400">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="mt-1 rounded-2xl bg-[linear-gradient(135deg,#34d399_0%,#14b8a6_55%,#0891b2_100%)] py-3 text-sm font-semibold text-white shadow-[0_14px_40px_rgba(20,184,166,0.25)] transition hover:opacity-90 disabled:opacity-50"
            >
              {loading ? 'Aguarde...' : mode === 'login' ? 'Entrar' : 'Criar conta'}
            </button>
          </form>

          <p className="mt-5 text-center text-xs text-zinc-500">
            {mode === 'login' ? 'Não tem conta?' : 'Já tem conta?'}{' '}
            <button
              onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}
              className="text-emerald-400 hover:underline"
            >
              {mode === 'login' ? 'Criar conta' : 'Entrar'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
