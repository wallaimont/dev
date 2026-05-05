import { useState, useCallback, useRef, useEffect } from 'react';
import { loadHistory, saveHistory } from '../utils/storage.js';
import { fetchHistoryFromProxy, pushHistoryToProxy } from '../services/historyService.js';
import { getFirebaseIdToken } from '../firebase.js';
import { HISTORY_PROXY_URL } from '../constants/app.js';

const DEBOUNCE_MS = 800;

export function useConversations(user) {
  const [conversations, setConversations] = useState(() => loadHistory());
  const [activeId, setActiveId] = useState(null);
  const historySyncTimerRef = useRef(null);

  // --- Persistence helpers -------------------------------------------------

  const persist = useCallback((convs) => {
    setConversations(convs);
    saveHistory(convs);
  }, []);

  const persistDebounced = useCallback((convs) => {
    persist(convs);
    clearTimeout(historySyncTimerRef.current);
    historySyncTimerRef.current = setTimeout(async () => {
      try {
        const idToken = await getFirebaseIdToken();
        if (idToken) await pushHistoryToProxy(HISTORY_PROXY_URL, idToken, convs);
      } catch {
        // Remote sync is best-effort; local history is already saved.
      }
    }, DEBOUNCE_MS);
  }, [persist]);

  // --- Sync remote history when user logs in --------------------------------

  useEffect(() => {
    if (!user) return;
    let cancelled = false;
    (async () => {
      try {
        const idToken = await getFirebaseIdToken();
        if (!idToken || cancelled) return;
        const remote = await fetchHistoryFromProxy(HISTORY_PROXY_URL, idToken);
        if (!cancelled && Array.isArray(remote) && remote.length > 0) {
          setConversations(remote);
          saveHistory(remote);
        }
      } catch {
        // best-effort
      }
    })();
    return () => { cancelled = true; };
  }, [user]);

  // Cleanup on unmount
  useEffect(() => () => clearTimeout(historySyncTimerRef.current), []);

  // --- CRUD ----------------------------------------------------------------

  const newConversation = useCallback(() => {
    const id = Date.now().toString();
    const conv = { id, title: 'Nova conversa', messages: [], createdAt: Date.now() };
    const updated = [conv, ...conversations];
    persistDebounced(updated);
    setActiveId(id);
    return id;
  }, [conversations, persistDebounced]);

  const deleteConversation = useCallback((id, e) => {
    e?.stopPropagation();
    const updated = conversations.filter(c => c.id !== id);
    persistDebounced(updated);
    setActiveId(prev => {
      if (prev !== id) return prev;
      return updated.find(c => c.id !== id)?.id ?? (updated[0]?.id || null);
    });
  }, [conversations, persistDebounced]);

  const startRename = useCallback((conv, e) => {
    e?.stopPropagation();
    return { id: conv.id, title: conv.title || '' };
  }, []);

  const commitRename = useCallback((id, title) => {
    const updated = conversations.map(c =>
      c.id === id ? { ...c, title: title.trim() || 'Nova conversa' } : c
    );
    persistDebounced(updated);
  }, [conversations, persistDebounced]);

  const exportConversation = useCallback((id) => {
    const convId = id ?? activeId;
    const conv = conversations.find(c => c.id === convId);
    if (!conv || !conv.messages.length) return;
    const md = conv.messages
      .map(m => `**${m.role === 'user' ? 'Você' : 'Chaat IA'}**: ${m.content}`)
      .join('\n\n---\n\n');
    const blob = new Blob([md], { type: 'text/markdown' });
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement('a');
    a.href     = url;
    a.download = `${conv.title || 'conversa'}.md`;
    a.click();
    URL.revokeObjectURL(url);
  }, [conversations, activeId]);

  return {
    conversations,
    setConversations,
    activeId,
    setActiveId,
    persist,
    persistDebounced,
    newConversation,
    deleteConversation,
    startRename,
    commitRename,
    exportConversation,
  };
}
