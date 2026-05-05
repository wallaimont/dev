/**
 * Verifica quais modelos da lista FREE_MODELS existem no catálogo do OpenRouter.
 * Não precisa de chave API — usa o endpoint público /api/v1/models.
 * Uso: node scripts/check-free-models.mjs
 */

const FREE_MODELS = [
  { id: 'deepseek/deepseek-r1:free',                          name: 'DeepSeek R1' },
  { id: 'deepseek/deepseek-r1-0528:free',                     name: 'DeepSeek R1 0528' },
  { id: 'deepseek/deepseek-chat-v3-0324:free',                name: 'DeepSeek V3 0324' },
  { id: 'meta-llama/llama-4-maverick:free',                   name: 'Llama 4 Maverick' },
  { id: 'meta-llama/llama-4-scout:free',                      name: 'Llama 4 Scout' },
  { id: 'meta-llama/llama-3.3-70b-instruct:free',             name: 'Llama 3.3 70B' },
  { id: 'google/gemini-2.5-flash-preview:free',               name: 'Gemini 2.5 Flash' },
  { id: 'google/gemini-2.5-pro-exp-03-25:free',               name: 'Gemini 2.5 Pro Exp' },
  { id: 'google/gemma-4-31b-it:free',                         name: 'Gemma 4 31B' },
  { id: 'google/gemma-4-26b-a4b-it:free',                     name: 'Gemma 4 26B' },
  { id: 'qwen/qwen3-235b-a22b:free',                          name: 'Qwen3 235B' },
  { id: 'qwen/qwen3-30b-a3b:free',                            name: 'Qwen3 30B' },
  { id: 'qwen/qwen3-coder:free',                              name: 'Qwen3 Coder 480B' },
  { id: 'qwen/qwen3-next-80b-a3b-instruct:free',              name: 'Qwen3 Next 80B' },
  { id: 'mistralai/mistral-small-3.2-24b-instruct:free',      name: 'Mistral Small 3.2' },
  { id: 'mistralai/devstral-small:free',                      name: 'Mistral DevStral' },
  { id: 'mistralai/mistral-7b-instruct:free',                 name: 'Mistral 7B' },
  { id: 'microsoft/phi-4:free',                               name: 'Phi-4' },
  { id: 'microsoft/phi-4-reasoning:free',                     name: 'Phi-4 Reasoning' },
  { id: 'microsoft/mai-ds-r1:free',                           name: 'MAI DeepSeek R1' },
  { id: 'openrouter/owl-alpha',                               name: 'Owl Alpha' },
  { id: 'openai/gpt-oss-120b:free',                           name: 'GPT OSS 120B' },
  { id: 'openai/gpt-oss-20b:free',                            name: 'GPT OSS 20B' },
  { id: 'nvidia/nemotron-3-super-120b-a12b:free',             name: 'Nemotron 3 Super' },
  { id: 'nvidia/nemotron-3-nano-omni-30b-a3b-reasoning:free', name: 'Nemotron Nano Omni' },
  { id: 'nvidia/nemotron-nano-12b-v2-vl:free',                name: 'Nemotron Nano 12B VL' },
  { id: 'nvidia/nemotron-3-nano-30b-a3b:free',                name: 'Nemotron 3 Nano 30B' },
  { id: 'nvidia/nemotron-nano-9b-v2:free',                    name: 'Nemotron Nano 9B' },
  { id: 'tencent/hy3-preview:free',                           name: 'Tencent Hy3' },
  { id: 'minimax/minimax-m2.5:free',                          name: 'MiniMax M2.5' },
  { id: 'z-ai/glm-4.5-air:free',                              name: 'GLM 4.5 Air' },
  { id: 'inclusionai/ling-2.6-1t:free',                       name: 'Ling 2.6 1T' },
  { id: 'poolside/laguna-m.1:free',                           name: 'Laguna M.1' },
  { id: 'poolside/laguna-xs.2:free',                          name: 'Laguna XS.2' },
];

console.log('Buscando catálogo do OpenRouter...\n');

const res = await fetch('https://openrouter.ai/api/v1/models', {
  headers: { 'HTTP-Referer': 'https://localhost', 'X-Title': 'model-check' },
});

if (!res.ok) {
  console.error('Erro ao buscar modelos:', res.status, await res.text());
  process.exit(1);
}

const { data: catalog } = await res.json();
const catalogIds = new Set(catalog.map(m => m.id));

const ok = [];
const notFound = [];

for (const m of FREE_MODELS) {
  if (catalogIds.has(m.id)) {
    ok.push(m);
  } else {
    notFound.push(m);
  }
}

// Mostra resultado
console.log(`✅ ENCONTRADOS (${ok.length}):`);
ok.forEach(m => console.log(`   ${m.id.padEnd(60)} ${m.name}`));

console.log(`\n❌ NÃO ENCONTRADOS (${notFound.length}):`);
if (notFound.length === 0) {
  console.log('   Nenhum! Todos os modelos estão no catálogo.');
} else {
  notFound.forEach(m => console.log(`   ${m.id.padEnd(60)} ${m.name}`));
}

// Sugestão: modelos free populares no catálogo que não estão na lista
const ourIds = new Set(FREE_MODELS.map(m => m.id));
const freeCatalog = catalog.filter(m => m.id.endsWith(':free') && !ourIds.has(m.id));
if (freeCatalog.length > 0) {
  console.log(`\n💡 OUTROS MODELOS :free DISPONÍVEIS NO OPENROUTER (${freeCatalog.length}):`);
  // Ordena por context_length desc para mostrar os mais capazes primeiro
  freeCatalog
    .sort((a, b) => (b.context_length || 0) - (a.context_length || 0))
    .slice(0, 20)
    .forEach(m => console.log(`   ${m.id.padEnd(60)} ctx:${m.context_length || '?'}`));
}
