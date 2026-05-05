export async function streamOpenAICompat(providerName, providerUrl, modelId, messages, apiKey, onDelta, signal) {
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${apiKey}`,
  };

  if (providerName === 'openrouter') {
    headers['HTTP-Referer'] = window.location.origin;
    headers['X-Title'] = 'Chaat IA';
  }

  const res = await fetch(`${providerUrl}/chat/completions`, {
    method: 'POST',
    headers,
    signal,
    body: JSON.stringify({
      model: modelId,
      messages,
      stream: true,
    }),
  });

  if (!res.ok) {
    const err = await res.text();
    throw new Error(`Erro ${res.status}: ${err}`);
  }

  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buf = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buf += decoder.decode(value, { stream: true });
    const lines = buf.split('\n');
    buf = lines.pop() || '';

    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed || trimmed === 'data: [DONE]') continue;
      if (!trimmed.startsWith('data: ')) continue;
      try {
        const json = JSON.parse(trimmed.slice(6));
        const delta = json.choices?.[0]?.delta?.content;
        if (delta) onDelta(delta);
      } catch {
        // ignore parse errors
      }
    }
  }
}

export async function streamAnthropicMessages(modelId, messages, apiKey, onDelta, signal) {
  const res = await fetch('https://api.anthropic.com/v1/messages', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'x-api-key': apiKey,
      'anthropic-version': '2023-06-01',
      'anthropic-dangerous-direct-browser-access': 'true',
    },
    signal,
    body: JSON.stringify({
      model: modelId,
      max_tokens: 8096,
      messages,
      stream: true,
    }),
  });

  if (!res.ok) {
    const err = await res.text();
    throw new Error(`Erro ${res.status}: ${err}`);
  }

  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buf = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buf += decoder.decode(value, { stream: true });
    const lines = buf.split('\n');
    buf = lines.pop() || '';

    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed || !trimmed.startsWith('data: ')) continue;
      try {
        const json = JSON.parse(trimmed.slice(6));
        if (json.type === 'content_block_delta' && json.delta?.text) {
          onDelta(json.delta.text);
        }
      } catch {
        // ignore parse errors
      }
    }
  }
}

export async function callSecureChatProxy(proxyUrl, idToken, payload, signal) {
  const res = await fetch(proxyUrl, {
    method: 'POST',
    signal,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${idToken}`,
    },
    body: JSON.stringify(payload),
  });

  const text = await res.text();
  if (!res.ok) {
    throw new Error(`SECURE_PROXY_${res.status}:${text}`);
  }

  let parsed;
  try {
    parsed = JSON.parse(text);
  } catch {
    throw new Error('SECURE_PROXY_BAD_JSON');
  }

  return parsed?.content || '';
}

export async function callSecureImageProxy(proxyUrl, idToken, payload, signal) {
  const res = await fetch(proxyUrl, {
    method: 'POST',
    signal,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${idToken}`,
    },
    body: JSON.stringify({ ...payload, mode: 'image' }),
  });

  const text = await res.text();
  if (!res.ok) {
    throw new Error(`SECURE_PROXY_${res.status}:${text}`);
  }

  let parsed;
  try {
    parsed = JSON.parse(text);
  } catch {
    throw new Error('SECURE_PROXY_BAD_JSON');
  }

  return {
    imageUrl: parsed?.imageUrl || '',
    content: parsed?.content || '',
  };
}

export async function generatePollinationsImage(modelId, prompt) {
  const encoded = encodeURIComponent(prompt);
  const url = `https://image.pollinations.ai/prompt/${encoded}?model=${encodeURIComponent(modelId)}&width=1024&height=1024&nologo=true&enhance=true`;
  // Pollinations returns the image directly from the URL — no prefetch needed
  return { imageUrl: url, content: '' };
}

export async function generateOpenAIImageCompat(providerName, providerUrl, modelId, prompt, apiKey, signal) {
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${apiKey}`,
  };

  if (providerName === 'openrouter') {
    headers['HTTP-Referer'] = window.location.origin;
    headers['X-Title'] = 'Chaat IA';
  }

  const res = await fetch(`${providerUrl}/images/generations`, {
    method: 'POST',
    headers,
    signal,
    body: JSON.stringify({
      model: modelId,
      prompt,
      size: '1024x1024',
    }),
  });

  const text = await res.text();
  if (!res.ok) {
    throw new Error(`Erro ${res.status}: ${text}`);
  }

  let parsed;
  try {
    parsed = JSON.parse(text);
  } catch {
    const raw = text.trim();
    if (raw.startsWith('http://') || raw.startsWith('https://') || raw.startsWith('data:image/')) {
      return { imageUrl: raw, content: '' };
    }
    throw new Error(`IMAGE_BAD_JSON:${raw.slice(0, 200)}`);
  }

  const data = parsed?.data?.[0] || {};
  const imageUrl =
    data.url ||
    (data.b64_json ? `data:image/png;base64,${data.b64_json}` : '') ||
    parsed?.image_url ||
    parsed?.url ||
    parsed?.images?.[0]?.url ||
    '';
  if (!imageUrl) {
    throw new Error('IMAGE_EMPTY_RESPONSE');
  }

  return {
    imageUrl,
    content: data.revised_prompt ? `Imagem gerada com prompt revisado: ${data.revised_prompt}` : '',
  };
}
