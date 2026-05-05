import {
  HISTORY_KEY,
  APIKEYS_KEY,
  APIKEYS_SESSION_KEY,
  CUSTOM_SKILLS_KEY_BASE,
} from '../constants/app.js';

// --- Conversation history --------------------------------------------------

export function loadHistory() {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]');
  } catch {
    return [];
  }
}

export function saveHistory(history) {
  localStorage.setItem(HISTORY_KEY, JSON.stringify(history));
}

// --- API keys (persistent across sessions) ---

export function loadApiKeys() {
  try {
    const inLocal = JSON.parse(localStorage.getItem(APIKEYS_KEY) || '{}');
    if (inLocal && typeof inLocal === 'object' && Object.keys(inLocal).length > 0) {
      return inLocal;
    }
  } catch {
    // ignore parse errors
  }

  // Migration from previous session-scoped storage to persistent local storage.
  try {
    const inSession = JSON.parse(sessionStorage.getItem(APIKEYS_SESSION_KEY) || '{}');
    if (inSession && typeof inSession === 'object' && Object.keys(inSession).length > 0) {
      localStorage.setItem(APIKEYS_KEY, JSON.stringify(inSession));
      return inSession;
    }
  } catch {
    // ignore parse errors
  }

  return {};
}

export function saveApiKeys(keys) {
  localStorage.setItem(APIKEYS_KEY, JSON.stringify(keys));
  // Keep in sync for backward compatibility with any older code path.
  sessionStorage.setItem(APIKEYS_SESSION_KEY, JSON.stringify(keys));
}

// --- Per-user custom skills ------------------------------------------------

export function loadCustomSkillsForUser(user) {
  const uid = user?.id || user?.email || 'anon';
  const key = `${CUSTOM_SKILLS_KEY_BASE}_${uid}`;
  try {
    const parsed = JSON.parse(localStorage.getItem(key) || '[]');
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export function saveCustomSkillsForUser(user, skills) {
  const uid = user?.id || user?.email || 'anon';
  const key = `${CUSTOM_SKILLS_KEY_BASE}_${uid}`;
  localStorage.setItem(key, JSON.stringify(skills));
}
