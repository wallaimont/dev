import { useState, useCallback, useRef } from 'react';
import { saveHistory } from '../utils/storage.js';
import { PROVIDERS, FREE_MODELS } from '../constants/models.js';
import {
  streamOpenAICompat,
  streamAnthropicMessages,
  callSecureChatProxy,
  callSecureImageProxy,
  generateOpenAIImageCompat,
  generatePollinationsImage,
} from '../services/chatService.js';
import { SECURE_PROXY_URL } from '../constants/app.js';
import { getFirebaseIdToken } from '../firebase.js';

export function useChat({
  activeId,
  setActiveId,
  conversations,
  setConversations,
  persist,
  selectedModel,
  getKey,
  selectedSkills,
  getSkillMeta,
  webSearch,
  inputRef,
}) {
  const [loading, setLoading]       = useState(false);
  const [input, setInput]           = useState('');
  const [attachment, setAttachment] = useState(null);
  const [actualModel, setActualModel] = useState(null);
  const [copiedId, setCopiedId]     = useState(null);

  const abortCtrlRef = useRef(null);

  // --- Low-level streaming helpers ----------------------------------------

  const streamOpenAI = useCallback(async (model, msgs, onDelta, signal) => {
    const key = getKey(model);
    return streamOpenAICompat(
      model.provider,
      PROVIDERS[model.provider]?.url,
      model.id,
      msgs,
      key,
      onDelta,
      signal
    );
  }, [getKey]);

  const streamAnthropic = useCallback(async (model, msgs, onDelta, signal) => {
    const key = getKey(model);
    return streamAnthropicMessages(model.id, msgs, key, onDelta, signal);
  }, [getKey]);

  const callSecureProxy = useCallback(async (model, msgs, signal) => {
    const idToken = await getFirebaseIdToken();
    const payload = {
      provider: model.provider,
      model: model.id,
      messages: msgs,
    };
    return callSecureChatProxy(SECURE_PROXY_URL, idToken, payload, signal);
  }, []);

  const callSecureImage = useCallback(async (model, prompt, signal) => {
    const idToken = await getFirebaseIdToken();
    const payload = {
      provider: model.provider,
      model: model.id,
      prompt,
    };
    return callSecureImageProxy(SECURE_PROXY_URL, idToken, payload, signal);
  }, []);

  const generateImageFallback = useCallback(async (model, prompt, signal) => {
    if (model.provider === 'pollinations') {
      return generatePollinationsImage(model.id, prompt, signal);
    }
    const key = getKey(model);
    return generateOpenAIImageCompat(
      model.provider,
      PROVIDERS[model.provider]?.url,
      model.id,
      prompt,
      key,
      signal
    );
  }, [getKey]);

  // --- Clipboard copy -------------------------------------------------------

  const copyMsg = useCallback((id, content) => {
    navigator.clipboard.writeText(content).then(() => {
      setCopiedId(id);
      setTimeout(() => setCopiedId(null), 2000);
    });
  }, []);

  // --- File / screenshot attachment -----------------------------------------

  const handleFile = useCallback((file) => {
    if (!file) return;
    const reader = new FileReader();
    if (file.type.startsWith('image/')) {
      reader.onload = () => setAttachment({ type: 'image', data: reader.result, name: file.name });
      reader.readAsDataURL(file);
    } else {
      reader.onload = () => setAttachment({ type: 'text', data: reader.result, name: file.name });
      reader.readAsText(file);
    }
  }, []);

  const captureScreen = useCallback(async () => {
    try {
      const stream  = await navigator.mediaDevices.getDisplayMedia({ video: true });
      const track   = stream.getVideoTracks()[0];
      const imgCapture = new ImageCapture(track);
      const bitmap  = await imgCapture.grabFrame();
      track.stop();
      const canvas  = document.createElement('canvas');
      canvas.width  = bitmap.width;
      canvas.height = bitmap.height;
      canvas.getContext('2d').drawImage(bitmap, 0, 0);
      const dataUrl = canvas.toDataURL('image/png');
      setAttachment({ type: 'image', data: dataUrl, name: 'captura.png' });
    } catch {
      // User cancelled or browser unsupported — silent fail
    }
  }, []);

  // --- Stop generation ------------------------------------------------------

  const stopGeneration = useCallback(() => {
    abortCtrlRef.current?.abort();
  }, []);

  // --- Main send function ---------------------------------------------------

  const send = useCallback(async (text) => {
    const rawContent = (text || input).trim();
    const snap = attachment;
    if ((!rawContent && !snap) || loading) return;

    const content = rawContent || (snap?.name || 'arquivo');
    setInput('');
    setAttachment(null);

    // Ensure there is an active conversation
    let convId = activeId;
    let currentConvs = conversations;
    if (!convId) {
      const id   = Date.now().toString();
      const conv = { id, title: content.slice(0, 60), messages: [], createdAt: Date.now() };
      currentConvs = [conv, ...currentConvs];
      persist(currentConvs);
      convId = id;
      setActiveId(id);
    }

    // Add user message
    const userMsg = {
      id:      `u-${Date.now()}`,
      role:    'user',
      content: rawContent,
      ...(snap?.type === 'image' && { img: snap.data, imgName: snap.name }),
      ...(snap?.type === 'text'  && { fileCtx: `Conteúdo do arquivo "${snap.name}":\n\`\`\`\n${snap.data.slice(0, 8000)}\n\`\`\`` }),
    };
    currentConvs = currentConvs.map(c =>
      c.id === convId ? { ...c, messages: [...c.messages, userMsg] } : c
    );
    persist(currentConvs);
    setLoading(true);

    // Add assistant placeholder
    const assistantId  = `a-${Date.now()}`;
    const assistantMsg = { id: assistantId, role: 'assistant', content: '' };
    currentConvs = currentConvs.map(c =>
      c.id === convId ? { ...c, messages: [...c.messages, assistantMsg] } : c
    );
    persist(currentConvs);

    try {
      // Optional web search via DuckDuckGo
      let searchCtx = null;
      if (webSearch && rawContent) {
        try {
          const apiUrl = `https://api.duckduckgo.com/?q=${encodeURIComponent(rawContent)}&format=json&no_html=1&skip_disambig=1`;
          const res = await fetch(`https://corsproxy.io/?${encodeURIComponent(apiUrl)}`, { signal: AbortSignal.timeout(5000) });
          if (res.ok) {
            const data = await res.json();
            const parts = [];
            if (data.AbstractText) parts.push(`Resumo: ${data.AbstractText}${data.AbstractURL ? ` (${data.AbstractURL})` : ''}`);
            const related = (data.RelatedTopics || []).filter(t => t.Text).slice(0, 3);
            if (related.length) parts.push('Relacionados:\n' + related.map(t => `• ${t.Text}`).join('\n'));
            searchCtx = parts.length ? parts.join('\n\n') : null;
          }
        } catch { /* search is best-effort */ }
      }

      const conv = currentConvs.find(c => c.id === convId);
      const skillPrompts = selectedSkills
        .map(key => getSkillMeta(key)?.prompt)
        .filter(Boolean);

      const apiMsgs = [
        ...(skillPrompts.length
          ? [{ role: 'system', content: `Skills ativas:\n- ${skillPrompts.join('\n- ')}` }]
          : []),
        ...conv.messages
          .filter(m => m.id !== assistantId)
          .map(m => {
            const isCurrentMsg = m.id === userMsg.id;
            const textParts    = [];
            if (m.fileCtx) textParts.push(m.fileCtx);
            if (searchCtx && isCurrentMsg) textParts.push(`[Contexto da web]\n${searchCtx}`);
            if (m.content) textParts.push(m.content);
            const textContent = textParts.join('\n\n') || m.content;
            if (m.role === 'user' && m.img) {
              return {
                role: 'user',
                content: [
                  { type: 'image_url', image_url: { url: m.img } },
                  { type: 'text', text: textContent },
                ],
              };
            }
            return { role: m.role, content: textContent || m.content };
          }),
      ];

      // Functional updater avoids stale-closure on streaming deltas
      const onDelta = (delta) => {
        setConversations(prev => {
          const updated = prev.map(c =>
            c.id === convId
              ? { ...c, messages: c.messages.map(m =>
                  m.id === assistantId ? { ...m, content: m.content + delta } : m
                )}
              : c
          );
          saveHistory(updated);
          return updated;
        });
      };

      const abortCtrl = new AbortController();
      abortCtrlRef.current = abortCtrl;

      let usedFallback = false;
      const isImageModel = selectedModel?.modality === 'image';
      const isPollinationsImage = isImageModel && selectedModel?.provider === 'pollinations';

      try {
        if (isPollinationsImage) {
          // Pollinations: chamada direta, sem proxy
          const imageResult = await generateImageFallback(selectedModel, rawContent, abortCtrl.signal);
          setConversations(prev => {
            const updated = prev.map(c =>
              c.id === convId
                ? {
                    ...c,
                    messages: c.messages.map(m =>
                      m.id === assistantId
                        ? {
                            ...m,
                            img: imageResult.imageUrl,
                            imgName: `gerada-${Date.now()}.png`,
                            content: imageResult.content || 'Imagem gerada com sucesso.',
                          }
                        : m
                    ),
                  }
                : c
            );
            saveHistory(updated);
            return updated;
          });
        } else if (isImageModel) {
          // Outros modelos de imagem (ex: openrouter) — via proxy seguro
          const imageResult = await callSecureImage(selectedModel, rawContent, abortCtrl.signal);
          setConversations(prev => {
            const updated = prev.map(c =>
              c.id === convId
                ? {
                    ...c,
                    messages: c.messages.map(m =>
                      m.id === assistantId
                        ? {
                            ...m,
                            img: imageResult.imageUrl,
                            imgName: `gerada-${Date.now()}.png`,
                            content: imageResult.content || 'Imagem gerada com sucesso.',
                          }
                        : m
                    ),
                  }
                : c
            );
            saveHistory(updated);
            return updated;
          });
        } else {
          let proxyContent = null;

          if (selectedModel.provider === 'openrouter') {
            // Automatic fallback through free models on 429/503
            const startIdx = FREE_MODELS.findIndex(m => m.id === selectedModel.id);
            const candidates = startIdx >= 0
              ? [...FREE_MODELS.slice(startIdx), ...FREE_MODELS.slice(0, startIdx)]
              : [selectedModel, ...FREE_MODELS];

            for (const candidate of candidates) {
              try {
                proxyContent = await callSecureProxy(candidate, apiMsgs, abortCtrl.signal);
                setActualModel(candidate.id !== selectedModel.id ? candidate : null);
                break;
              } catch (e) {
                const eMsg = String(e?.message || '');
                if (eMsg.includes('SECURE_PROXY_429') || eMsg.includes('SECURE_PROXY_503')) {
                  continue; // try next candidate
                }
                throw e;
              }
            }

            if (proxyContent === null) {
              throw new Error('Erro 429: todos os modelos gratuitos atingiram a cota. Tente novamente em alguns minutos.');
            }
          } else {
            setActualModel(null);
            proxyContent = await callSecureProxy(selectedModel, apiMsgs, abortCtrl.signal);
          }

          onDelta(proxyContent);
        }
      } catch (proxyErr) {
        const msg = String(proxyErr?.message || '');
        const proxyUnavailable = msg.includes('SECURE_PROXY_404') || msg.includes('SECURE_PROXY_500') || msg.includes('SECURE_PROXY_503');
        const unauthenticated = msg.includes('SECURE_PROXY_401') || msg.includes('SECURE_PROXY_NO_AUTH');
        const proxyUnsupportedForImage = isImageModel && msg.includes('SECURE_PROXY_400');

        if (!proxyUnavailable && !unauthenticated && !proxyUnsupportedForImage) throw proxyErr;

        usedFallback = true;
        const requiresApiKey = selectedModel.provider === 'openrouter' || !PROVIDERS[selectedModel.provider]?.free;
        if (requiresApiKey && !getKey(selectedModel)) throw new Error('NO_API_KEY');

        if (isImageModel) {
          const imageResult = await generateImageFallback(selectedModel, rawContent, abortCtrl.signal);
          setConversations(prev => {
            const updated = prev.map(c =>
              c.id === convId
                ? {
                    ...c,
                    messages: c.messages.map(m =>
                      m.id === assistantId
                        ? {
                            ...m,
                            img: imageResult.imageUrl,
                            imgName: `gerada-${Date.now()}.png`,
                            content: imageResult.content || 'Imagem gerada com sucesso.',
                          }
                        : m
                    ),
                  }
                : c
            );
            saveHistory(updated);
            return updated;
          });
        } else if (selectedModel.provider === 'anthropic') {
          await streamAnthropic(selectedModel, apiMsgs, onDelta, abortCtrl.signal);
        } else {
          await streamOpenAI(selectedModel, apiMsgs, onDelta, abortCtrl.signal);
        }
      }

      if (usedFallback) {
        console.warn('[Segurança] Backend seguro indisponível — fallback legado em uso.');
      }

      // Auto-generate title on first user message
      if (conv.messages.filter(m => m.role === 'user').length === 0) {
        setConversations(prev => {
          const updated = prev.map(c =>
            c.id === convId ? { ...c, title: content.slice(0, 60) } : c
          );
          saveHistory(updated);
          return updated;
        });
      }
    } catch (err) {
      let errMsg;
      if (err.name === 'AbortError') {
        errMsg = '(cancelado)';
      } else if (err.message === 'NO_API_KEY') {
        const prov = PROVIDERS[selectedModel.provider];
        errMsg = `🔑 **Chave de API não configurada** para ${prov?.name || selectedModel.provider}.\n\nClique em **Configurar APIs** na barra lateral para adicionar sua chave.\n\n> Obtenha em: https://${prov?.link}`;
      } else if (/Erro 40[13]/.test(err.message)) {
        const prov = PROVIDERS[selectedModel.provider];
        errMsg = `🔑 **Chave de API inválida** para ${prov?.name || selectedModel.provider}.\n\nVerifique se a chave está correta em **Configurar APIs**.\n\n> Obtenha em: https://${prov?.link}`;
      } else if (err.message.includes('Erro 429') || err.message.includes('SECURE_PROXY_429')) {
        errMsg = `⚠️ **Todos os modelos gratuitos estão com cota esgotada** no momento.\n\nAguarde alguns minutos e tente novamente, ou selecione um modelo pago.`;
      } else if (err.message.includes('Erro 404')) {
        errMsg = `⚠️ **Modelo indisponível**: "${selectedModel.name}" não tem endpoints ativos no momento. Selecione outro modelo.`;
      } else if (err.message.includes('IMAGE_EMPTY_RESPONSE') || err.message.includes('IMAGE_BAD_JSON')) {
        errMsg = '⚠️ O provedor retornou um formato de imagem inesperado. Tente outro modelo de imagem.';
      } else {
        errMsg = `Erro: ${err.message}`;
      }

      setConversations(prev => {
        const updated = prev.map(c =>
          c.id === convId
            ? { ...c, messages: c.messages.map(m =>
                m.id === assistantId ? { ...m, content: errMsg } : m
              )}
            : c
        );
        saveHistory(updated);
        return updated;
      });
    } finally {
      abortCtrlRef.current = null;
      setLoading(false);
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  }, [
    input, loading, activeId, conversations, persist, setActiveId,
    selectedModel, streamOpenAI, streamAnthropic, callSecureProxy,
    callSecureImage, generateImageFallback,
    attachment, selectedSkills, webSearch, getSkillMeta, getKey,
    setConversations, inputRef,
  ]);

  return {
    loading,
    input,
    setInput,
    attachment,
    setAttachment,
    actualModel,
    setActualModel,
    copiedId,
    send,
    stopGeneration,
    copyMsg,
    handleFile,
    captureScreen,
  };
}
