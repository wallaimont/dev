import { useState, useCallback } from 'react';
import { loadApiKeys, saveApiKeys } from '../utils/storage.js';
import { ENV_KEYS, PROVIDERS } from '../constants/models.js';

export function useApiKeys() {
  const [apiKeys, setApiKeys] = useState(() => loadApiKeys());

  const saveKeys = useCallback((keys) => {
    setApiKeys(keys);
    saveApiKeys(keys);
  }, []);

  /**
   * Returns the effective API key for a given model.
   * Priority: user-configured key → environment-variable fallback.
   */
  const getKey = useCallback((model) => {
    const provider = model?.provider;
    if (!provider) return null;

    // OpenRouter: user key takes precedence, then env fallback
    if (provider === 'openrouter') {
      return apiKeys['openrouter'] || ENV_KEYS.openrouter || null;
    }
    // Moonshot/Kimi
    if (provider === 'moonshot') {
      return apiKeys['moonshot'] || ENV_KEYS.moonshot || null;
    }
    // All other providers
    return apiKeys[provider] || null;
  }, [apiKeys]);

  /**
   * Returns the list of provider IDs that have a configured key.
   */
  const configuredProviders = useCallback(() => {
    return Object.keys(PROVIDERS).filter(p => !!getKey({ provider: p }));
  }, [getKey]);

  return { apiKeys, saveKeys, getKey, configuredProviders };
}
