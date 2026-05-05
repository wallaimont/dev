const res = await fetch('https://openrouter.ai/api/v1/models', { headers: { 'HTTP-Referer': 'https://localhost' } });
const { data } = await res.json();
const free = data.filter(m => m.id.endsWith(':free'));

const keywords = ['deepseek','llama-4','gemini','qwen3','mistral','phi-4','devstral','mai-ds'];
for (const kw of keywords) {
  const found = free.filter(m => m.id.toLowerCase().includes(kw));
  if (found.length) {
    console.log(`\n[${kw}]`);
    found.forEach(m => console.log('  ', m.id, '| ctx:', m.context_length));
  }
}
