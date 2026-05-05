import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fetchHistoryFromProxy, pushHistoryToProxy } from './historyService.js';

const HISTORY_URL = 'https://example.com/api/history';
const TOKEN = 'test-token';

beforeEach(() => {
  vi.restoreAllMocks();
});

describe('fetchHistoryFromProxy', () => {
  it('retorna null quando a resposta não é ok', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false });
    const result = await fetchHistoryFromProxy(HISTORY_URL, TOKEN);
    expect(result).toBeNull();
  });

  it('retorna null quando conversations não é array', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ conversations: null }),
    });
    const result = await fetchHistoryFromProxy(HISTORY_URL, TOKEN);
    expect(result).toBeNull();
  });

  it('retorna array de conversas quando bem-sucedido', async () => {
    const convs = [{ id: 'c1', messages: [] }];
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ conversations: convs }),
    });
    const result = await fetchHistoryFromProxy(HISTORY_URL, TOKEN);
    expect(result).toEqual(convs);
  });

  it('envia header Authorization correto', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ conversations: [] }),
    });
    await fetchHistoryFromProxy(HISTORY_URL, TOKEN);
    expect(fetch).toHaveBeenCalledWith(
      HISTORY_URL,
      expect.objectContaining({
        headers: expect.objectContaining({ Authorization: `Bearer ${TOKEN}` }),
      })
    );
  });
});

describe('pushHistoryToProxy', () => {
  it('faz POST com body serializado corretamente', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: true });
    const convs = [{ id: 'c1', messages: [] }];
    await pushHistoryToProxy(HISTORY_URL, TOKEN, convs);
    expect(fetch).toHaveBeenCalledWith(
      HISTORY_URL,
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ conversations: convs }),
        headers: expect.objectContaining({ Authorization: `Bearer ${TOKEN}` }),
      })
    );
  });

  it('não lança exceção mesmo se a resposta não for ok', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false });
    await expect(pushHistoryToProxy(HISTORY_URL, TOKEN, [])).resolves.not.toThrow();
  });
});
