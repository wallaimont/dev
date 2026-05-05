// Sistema de autenticação híbrido:
// 1) Prioriza Firebase (OAuth + e-mail/senha) quando configurado.
// 2) Mantém fallback legado em localStorage para não quebrar ambientes antigos.

import {
  signInWithGoogle as fbGoogle,
  signInWithGitHub as fbGitHub,
  registerWithEmailPassword as fbRegister,
  loginWithEmailPassword as fbLogin,
  signOutFirebase,
  isFirebaseConfigured,
} from './firebase.js';

const USERS_KEY   = 'chaat_users';
const SESSION_KEY = 'chaat_session';

// Usuário admin fixo (sempre disponível, independente do localStorage)
const ADMIN_USER = {
  id: 'admin-builtin',
  name: 'Admin',
  email: 'admin@chaat.ia',
  role: 'admin',
  createdAt: 0,
};
// Hash SHA-256 de "admin123"
const ADMIN_HASH = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9';

async function hashPassword(password) {
  const encoder = new TextEncoder();
  const data = encoder.encode(password);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

function getUsers() {
  try { return JSON.parse(localStorage.getItem(USERS_KEY) || '{}'); } catch { return {}; }
}

function saveUsers(users) {
  localStorage.setItem(USERS_KEY, JSON.stringify(users));
}

export function getSession() {
  try { return JSON.parse(localStorage.getItem(SESSION_KEY) || 'null'); } catch { return null; }
}

function saveSession(user) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(user));
}

export function logout() {
  const session = getSession();
  if (session?.provider === 'google' || session?.provider === 'github' || session?.provider === 'password') {
    signOutFirebase(); // async mas não precisamos aguardar
  }
  localStorage.removeItem(SESSION_KEY);
}

// --- Login social via Firebase -----------------------------------------------

export async function loginWithGoogle() {
  const user = await fbGoogle();
  saveSession(user);
  return user;
}

export async function loginWithGitHub() {
  const user = await fbGitHub();
  saveSession(user);
  return user;
}

export async function register(name, email, password) {
  if (!name.trim() || !email.trim() || !password) throw new Error('Preencha todos os campos.');
  if (password.length < 6) throw new Error('A senha deve ter pelo menos 6 caracteres.');
  const emailNorm = email.trim().toLowerCase();

  if (isFirebaseConfigured) {
    try {
      const user = await fbRegister(name, emailNorm, password);
      saveSession(user);
      return user;
    } catch (err) {
      if (err?.code === 'auth/email-already-in-use') {
        throw new Error('Este e-mail já está cadastrado.');
      }
      if (err?.code === 'auth/invalid-email') {
        throw new Error('E-mail inválido.');
      }
      if (err?.code === 'auth/weak-password') {
        throw new Error('A senha deve ter pelo menos 6 caracteres.');
      }
      throw err;
    }
  }

  const users = getUsers();
  if (users[emailNorm]) throw new Error('Este e-mail já está cadastrado.');
  const hash = await hashPassword(password);
  const user = { id: crypto.randomUUID(), name: name.trim(), email: emailNorm, createdAt: Date.now() };
  users[emailNorm] = { ...user, hash };
  saveUsers(users);
  saveSession(user);
  return user;
}

export async function login(email, password) {
  if (!email.trim() || !password) throw new Error('Preencha todos os campos.');
  const emailNorm = email.trim().toLowerCase();

  // Admin builtin — sempre disponível, mesmo com Firebase configurado
  if (emailNorm === 'admin@chaat.ia') {
    const hash = await hashPassword(password);
    if (hash !== ADMIN_HASH) throw new Error('Senha incorreta.');
    saveSession(ADMIN_USER);
    return { ...ADMIN_USER };
  }

  if (isFirebaseConfigured) {
    try {
      const user = await fbLogin(emailNorm, password);
      saveSession(user);
      return user;
    } catch (err) {
      if (err?.code === 'auth/user-not-found' || err?.code === 'auth/invalid-credential') {
        throw new Error('E-mail ou senha inválidos.');
      }
      if (err?.code === 'auth/invalid-email') {
        throw new Error('E-mail inválido.');
      }
      throw err;
    }
  }

  const users = getUsers();
  const stored = users[emailNorm];
  if (!stored) throw new Error('E-mail não encontrado.');
  const hash = await hashPassword(password);
  if (hash !== stored.hash) throw new Error('Senha incorreta.');
  const user = { id: stored.id, name: stored.name, email: stored.email, createdAt: stored.createdAt };
  saveSession(user);
  return user;
}
