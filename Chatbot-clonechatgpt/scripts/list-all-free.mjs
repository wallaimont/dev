// Lista TODOS os modelos free disponíveis no OpenRouter
const res = await fetch('https://openrouter.ai/api/v1/models', { headers: { 'HTTP-Referer': 'https://localhost' } });
const { data } = await res.json();
const free = data.filter(m => m.id.endsWith(':free'));
free
  .sort((a, b) => (b.context_length || 0) - (a.context_length || 0))
  .forEach(m => console.log(m.id.padEnd(65), 'ctx:', String(m.context_length || '?').padStart(7)));
console.log('\nTotal:', free.length);
