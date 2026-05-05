// Firebase Authentication — Google, GitHub e outros provedores OAuth
// Para configurar, preencha as variáveis no arquivo .env.local (veja .env.example)

import { initializeApp } from 'firebase/app';
import {
  getAuth,
  onAuthStateChanged,
  GoogleAuthProvider,
  GithubAuthProvider,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  updateProfile,
  signInWithPopup,
  signOut,
} from 'firebase/auth';

const firebaseConfig = {
  apiKey:            import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain:        import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId:         import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket:     import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId:             import.meta.env.VITE_FIREBASE_APP_ID,
};

// Firebase só é inicializado se a chave principal estiver configurada
export const isFirebaseConfigured = !!firebaseConfig.apiKey && !!firebaseConfig.authDomain;

let _auth = null;
let _googleProvider = null;
let _githubProvider = null;
let _authReadyResolve = null;
let _authInitialized = false;

const _authReadyPromise = new Promise((resolve) => {
  _authReadyResolve = resolve;
});

function withTimeout(promise, timeoutMs) {
  if (!timeoutMs || timeoutMs <= 0) return promise;
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => reject(new Error('AUTH_READY_TIMEOUT')), timeoutMs);
    promise
      .then((value) => {
        clearTimeout(timer);
        resolve(value);
      })
      .catch((err) => {
        clearTimeout(timer);
        reject(err);
      });
  });
}

if (isFirebaseConfigured) {
  try {
    const app = initializeApp(firebaseConfig);
    _auth = getAuth(app);
    onAuthStateChanged(_auth, () => {
      if (_authInitialized) return;
      _authInitialized = true;
      _authReadyResolve?.();
    });
    _googleProvider = new GoogleAuthProvider();
    _googleProvider.setCustomParameters({ prompt: 'select_account' });
    _githubProvider = new GithubAuthProvider();
    _githubProvider.addScope('read:user');
    _githubProvider.addScope('user:email');
  } catch (err) {
    console.error('[Firebase] Falha ao inicializar:', err);
  }
}

export const firebaseAuth = _auth;

export async function waitForFirebaseAuthReady(timeoutMs = 7000) {
  if (!isFirebaseConfigured || !_auth || _authInitialized) return;
  try {
    await withTimeout(_authReadyPromise, timeoutMs);
  } catch {
    // Se estourar timeout, seguimos com o estado atual para não bloquear o app.
  }
}

export async function getFirebaseIdToken({ forceRefresh = false, timeoutMs = 7000 } = {}) {
  if (!isFirebaseConfigured || !_auth) return null;
  await waitForFirebaseAuthReady(timeoutMs);
  const user = _auth.currentUser;
  if (!user) return null;
  try {
    return await user.getIdToken(forceRefresh);
  } catch {
    return null;
  }
}

// Converte um FirebaseUser para o formato de sessão local
function toLocalUser(fbUser, provider) {
  const email = fbUser.email || '';
  const rawName = fbUser.displayName || email.split('@')[0] || 'Usuário';
  return {
    id:        fbUser.uid,
    name:      rawName,
    email,
    photo:     fbUser.photoURL || null,
    provider,  // 'google' | 'github'
    createdAt: Date.now(),
  };
}

export async function signInWithGoogle() {
  if (!isFirebaseConfigured || !_auth) {
    throw new Error(
      'Firebase não configurado. Preencha as variáveis VITE_FIREBASE_* no arquivo .env.local e reinicie o servidor.'
    );
  }
  const result = await signInWithPopup(_auth, _googleProvider);
  return toLocalUser(result.user, 'google');
}

export async function signInWithGitHub() {
  if (!isFirebaseConfigured || !_auth) {
    throw new Error(
      'Firebase não configurado. Preencha as variáveis VITE_FIREBASE_* no arquivo .env.local e reinicie o servidor.'
    );
  }
  try {
    const result = await signInWithPopup(_auth, _githubProvider);
    return toLocalUser(result.user, 'github');
  } catch (err) {
    if (err.code === 'auth/account-exists-with-different-credential') {
      throw new Error(
        'Este e-mail já está cadastrado via Google. Clique em "Continuar com Google" para entrar.'
      );
    }
    throw err;
  }
}

export async function signOutFirebase() {
  if (_auth) {
    try { await signOut(_auth); } catch { /* ignora erros de logout */ }
  }
}

export async function registerWithEmailPassword(name, email, password) {
  if (!isFirebaseConfigured || !_auth) {
    throw new Error(
      'Firebase não configurado. Preencha as variáveis VITE_FIREBASE_* no arquivo .env.local e reinicie o servidor.'
    );
  }
  const result = await createUserWithEmailAndPassword(_auth, email, password);
  if (name?.trim()) {
    await updateProfile(result.user, { displayName: name.trim() });
  }
  return toLocalUser({ ...result.user, displayName: name?.trim() || result.user.displayName }, 'password');
}

export async function loginWithEmailPassword(email, password) {
  if (!isFirebaseConfigured || !_auth) {
    throw new Error(
      'Firebase não configurado. Preencha as variáveis VITE_FIREBASE_* no arquivo .env.local e reinicie o servidor.'
    );
  }
  const result = await signInWithEmailAndPassword(_auth, email, password);
  return toLocalUser(result.user, 'password');
}
