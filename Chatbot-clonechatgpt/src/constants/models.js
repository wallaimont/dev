// Single source of truth for environment API keys
export const ENV_KEYS = {
  openrouter: import.meta.env.VITE_OPENROUTER_API_KEY,
  moonshot:   import.meta.env.VITE_MOONSHOT_API_KEY,
};

// Provider registry — each provider defines its display metadata, base URL and link
export const PROVIDERS = {
  openrouter: { name: 'OpenRouter',    color: 'bg-orange-500', url: 'https://openrouter.ai/api/v1',                              link: 'openrouter.ai/keys',                                       free: true },
  moonshot:   { name: 'Moonshot/Kimi', color: 'bg-purple-600', url: 'https://api.moonshot.cn/v1',                                link: 'platform.kimi.com/console/api-keys'                                 },
  openai:     { name: 'OpenAI',        color: 'bg-green-600',  url: 'https://api.openai.com/v1',                                 link: 'platform.openai.com/api-keys'                                       },
  anthropic:  { name: 'Anthropic',     color: 'bg-amber-600',  url: 'https://api.anthropic.com',                                 link: 'console.anthropic.com/settings/keys'                                },
  google:     { name: 'Google',        color: 'bg-blue-500',   url: 'https://generativelanguage.googleapis.com/v1beta/openai',   link: 'aistudio.google.com/app/apikey'                                     },
  xai:        { name: 'xAI / Grok',   color: 'bg-zinc-500',   url: 'https://api.x.ai/v1',                                       link: 'console.x.ai'                                                       },
  deepseek:   { name: 'DeepSeek',      color: 'bg-sky-700',    url: 'https://api.deepseek.com/v1',                               link: 'platform.deepseek.com/api_keys'                                     },
  alibaba:    { name: 'Alibaba/Qwen',  color: 'bg-orange-400', url: 'https://dashscope.aliyuncs.com/compatible-mode/v1',         link: 'dashscope.aliyun.com/console/apiKey'                                },
  zhipu:      { name: 'Zhipu/GLM',    color: 'bg-indigo-500', url: 'https://open.bigmodel.cn/api/paas/v4',                      link: 'open.bigmodel.cn/usercenter/apikeys'                                },
  baidu:      { name: 'Baidu/ERNIE',  color: 'bg-blue-600',   url: 'https://qianfan.baidubce.com/v2',                           link: 'console.bce.baidu.com/qianfan/ais/apikey/list'                      },
  minimax:    { name: 'MiniMax',       color: 'bg-pink-500',   url: 'https://api.minimax.chat/v1',                               link: 'platform.minimax.io/user-center/basic-information/interface-key'    },
};

// Free models via OpenRouter — all use the openrouter provider
export const FREE_MODELS = [
  // ── OpenAI OSS ────────────────────────────────────────────────────────────
  { id: 'openrouter/owl-alpha',                                provider: 'openrouter', name: 'Owl Alpha',              desc: 'OpenRouter · Agentic · 1M ctx' },
  { id: 'openai/gpt-oss-120b:free',                            provider: 'openrouter', name: 'GPT OSS 120B',           desc: 'OpenAI · Raciocínio · 131K ctx' },
  { id: 'openai/gpt-oss-20b:free',                             provider: 'openrouter', name: 'GPT OSS 20B',            desc: 'OpenAI · Rápido · 131K ctx' },
  // ── Meta Llama ────────────────────────────────────────────────────────────
  { id: 'nousresearch/hermes-3-llama-3.1-405b:free',           provider: 'openrouter', name: 'Hermes 3 Llama 405B',    desc: 'NousResearch · 405B · 131K ctx' },
  { id: 'meta-llama/llama-3.3-70b-instruct:free',              provider: 'openrouter', name: 'Llama 3.3 70B',          desc: 'Meta · Instrução · 65K ctx' },
  { id: 'meta-llama/llama-3.2-3b-instruct:free',               provider: 'openrouter', name: 'Llama 3.2 3B',           desc: 'Meta · Ultra-rápido · 131K ctx' },
  // ── Google Gemma ──────────────────────────────────────────────────────────
  { id: 'google/gemma-4-31b-it:free',                          provider: 'openrouter', name: 'Gemma 4 31B',            desc: 'Google · Multimodal · 262K ctx' },
  { id: 'google/gemma-4-26b-a4b-it:free',                      provider: 'openrouter', name: 'Gemma 4 26B',            desc: 'Google · Compacto · 262K ctx' },
  { id: 'google/gemma-3-27b-it:free',                          provider: 'openrouter', name: 'Gemma 3 27B',            desc: 'Google · Equilibrado · 131K ctx' },
  { id: 'google/gemma-3-12b-it:free',                          provider: 'openrouter', name: 'Gemma 3 12B',            desc: 'Google · Leve · 32K ctx' },
  // ── Qwen ─────────────────────────────────────────────────────────────────
  { id: 'qwen/qwen3-coder:free',                               provider: 'openrouter', name: 'Qwen3 Coder 480B',       desc: 'Alibaba · Código · 262K ctx' },
  { id: 'qwen/qwen3-next-80b-a3b-instruct:free',               provider: 'openrouter', name: 'Qwen3 Next 80B',         desc: 'Alibaba · Raciocínio · 262K ctx' },
  // ── Mistral / Dolphin ────────────────────────────────────────────────────
  { id: 'cognitivecomputations/dolphin-mistral-24b-venice-edition:free', provider: 'openrouter', name: 'Dolphin Mistral 24B', desc: 'Uncensored · Código · 32K ctx' },
  // ── NVIDIA Nemotron ───────────────────────────────────────────────────────
  { id: 'nvidia/nemotron-3-super-120b-a12b:free',              provider: 'openrouter', name: 'Nemotron 3 Super 120B',  desc: 'NVIDIA · Multi-agente · 262K ctx' },
  { id: 'nvidia/nemotron-3-nano-omni-30b-a3b-reasoning:free',  provider: 'openrouter', name: 'Nemotron Nano Omni 30B', desc: 'NVIDIA · Raciocínio omni · 256K ctx' },
  { id: 'nvidia/nemotron-nano-12b-v2-vl:free',                 provider: 'openrouter', name: 'Nemotron Nano 12B VL',   desc: 'NVIDIA · Visão · 128K ctx' },
  { id: 'nvidia/nemotron-3-nano-30b-a3b:free',                 provider: 'openrouter', name: 'Nemotron 3 Nano 30B',    desc: 'NVIDIA · Agentic · 256K ctx' },
  { id: 'nvidia/nemotron-nano-9b-v2:free',                     provider: 'openrouter', name: 'Nemotron Nano 9B',       desc: 'NVIDIA · Raciocínio · 128K ctx' },
  // ── Outros populares ─────────────────────────────────────────────────────
  { id: 'tencent/hy3-preview:free',                            provider: 'openrouter', name: 'Tencent Hy3',            desc: 'Agentes & código · 262K ctx' },
  { id: 'minimax/minimax-m2.5:free',                           provider: 'openrouter', name: 'MiniMax M2.5',           desc: 'MiniMax · Produtividade · 197K ctx' },
  { id: 'z-ai/glm-4.5-air:free',                               provider: 'openrouter', name: 'GLM 4.5 Air',            desc: 'Zhipu · Thinking mode · 131K ctx' },
  { id: 'inclusionai/ling-2.6-1t:free',                        provider: 'openrouter', name: 'Ling 2.6 1T',            desc: 'Código & agentes · 262K ctx' },
  { id: 'poolside/laguna-m.1:free',                            provider: 'openrouter', name: 'Laguna M.1',             desc: 'Código · 131K ctx' },
  { id: 'poolside/laguna-xs.2:free',                           provider: 'openrouter', name: 'Laguna XS.2',            desc: 'Código · Compacto · 131K ctx' },
];

// Paid models — require user-configured API keys per provider
export const PAID_MODELS = [
  { id: 'gpt-4.1',                      provider: 'openai',    name: 'GPT-4.1',             desc: 'OpenAI · Mais capaz · 1M ctx' },
  { id: 'gpt-4.1-mini',                 provider: 'openai',    name: 'GPT-4.1 Mini',        desc: 'OpenAI · Rápido & barato · 1M ctx' },
  { id: 'gpt-4.1-nano',                 provider: 'openai',    name: 'GPT-4.1 Nano',        desc: 'OpenAI · Mais leve & econômico · 1M ctx' },
  { id: 'o3',                           provider: 'openai',    name: 'o3',                  desc: 'OpenAI · Raciocínio máximo' },
  { id: 'o3-pro',                       provider: 'openai',    name: 'o3 Pro',              desc: 'OpenAI · Raciocínio máximo avançado' },
  { id: 'o4-mini',                      provider: 'openai',    name: 'o4-mini',             desc: 'OpenAI · Raciocínio rápido' },
  { id: 'claude-opus-4-5',              provider: 'anthropic', name: 'Claude Opus 4.5',     desc: 'Anthropic · Mais capaz · 200K ctx' },
  { id: 'claude-sonnet-4-5',            provider: 'anthropic', name: 'Claude Sonnet 4.5',   desc: 'Anthropic · Equilibrado · 200K ctx' },
  { id: 'claude-3-7-sonnet-20250219',   provider: 'anthropic', name: 'Claude 3.7 Sonnet',   desc: 'Anthropic · Raciocínio estendido · 200K ctx' },
  { id: 'claude-haiku-3-5',             provider: 'anthropic', name: 'Claude Haiku 3.5',    desc: 'Anthropic · Rápido & barato' },
  { id: 'gemini-2.5-pro',               provider: 'google',    name: 'Gemini 2.5 Pro',      desc: 'Google · Raciocínio · 1M ctx' },
  { id: 'gemini-2.5-flash',             provider: 'google',    name: 'Gemini 2.5 Flash',    desc: 'Google · Rápido & eficiente · 1M ctx' },
  { id: 'gemini-2.0-flash',             provider: 'google',    name: 'Gemini 2.0 Flash',    desc: 'Google · Rápido · 1M ctx' },
  { id: 'grok-3',                       provider: 'xai',       name: 'Grok 3',              desc: 'xAI · Raciocínio · 131K ctx' },
  { id: 'grok-3-mini',                  provider: 'xai',       name: 'Grok 3 Mini',         desc: 'xAI · Rápido · 131K ctx' },
  { id: 'deepseek-chat',                provider: 'deepseek',  name: 'DeepSeek V3',         desc: 'DeepSeek · Chat · 64K ctx' },
  { id: 'deepseek-reasoner',            provider: 'deepseek',  name: 'DeepSeek R1',         desc: 'DeepSeek · Raciocínio · 64K ctx' },
  { id: 'kimi-k2.6',                    provider: 'moonshot',  name: 'Kimi K2.6',           desc: 'Moonshot · Raciocínio · 128K ctx' },
  { id: 'moonshot-v1-128k',             provider: 'moonshot',  name: 'Kimi v1 128K',        desc: 'Moonshot · Long context · 128K ctx' },
  { id: 'qwen-max',                     provider: 'alibaba',   name: 'Qwen Max',            desc: 'Alibaba · Mais capaz · 32K ctx' },
  { id: 'qwen-plus',                    provider: 'alibaba',   name: 'Qwen Plus',           desc: 'Alibaba · Equilibrado · 128K ctx' },
  { id: 'qwen-turbo',                   provider: 'alibaba',   name: 'Qwen Turbo',          desc: 'Alibaba · Rápido · 1M ctx' },
  { id: 'qwen3-235b-a22b',              provider: 'alibaba',   name: 'Qwen3 235B',          desc: 'Alibaba · Raciocínio máximo · 131K ctx' },
  { id: 'glm-4-plus',                   provider: 'zhipu',     name: 'GLM-4 Plus',          desc: 'Zhipu · Raciocínio avançado · 128K ctx' },
  { id: 'glm-z1-plus',                  provider: 'zhipu',     name: 'GLM-Z1 Plus',         desc: 'Zhipu · Deep thinking · 64K ctx' },
  { id: 'glm-4-long',                   provider: 'zhipu',     name: 'GLM-4 Long',          desc: 'Zhipu · Long context · 1M ctx' },
  { id: 'ernie-4.5-turbo-preview',      provider: 'baidu',     name: 'ERNIE 4.5 Turbo',     desc: 'Baidu · Raciocínio · 128K ctx' },
  { id: 'ernie-4.0-8k',                 provider: 'baidu',     name: 'ERNIE 4.0',           desc: 'Baidu · Poderoso · 8K ctx' },
  { id: 'ernie-speed-128k',             provider: 'baidu',     name: 'ERNIE Speed 128K',    desc: 'Baidu · Rápido · 128K ctx' },
  { id: 'MiniMax-Text-01',              provider: 'minimax',   name: 'MiniMax Text-01',     desc: 'MiniMax · Long context · 1M ctx' },
  { id: 'abab6.5s-chat',               provider: 'minimax',   name: 'MiniMax abab6.5s',    desc: 'MiniMax · Chat otimizado · 245K ctx' },
];

// Image generation models
export const IMAGE_MODELS = [
  {
    id: 'flux',
    provider: 'pollinations',
    name: 'FLUX (grátis)',
    desc: 'Pollinations · Gratuito · Sem API key',
    modality: 'image',
  },
  {
    id: 'flux-realism',
    provider: 'pollinations',
    name: 'FLUX Realism (grátis)',
    desc: 'Pollinations · Fotorrealismo · Sem API key',
    modality: 'image',
  },
  {
    id: 'flux-pro',
    provider: 'pollinations',
    name: 'FLUX Pro (grátis)',
    desc: 'Pollinations · Alta qualidade · Sem API key',
    modality: 'image',
  },
  {
    id: 'openai/gpt-image-1',
    provider: 'openrouter',
    name: 'GPT Image 1',
    desc: 'OpenRouter · Requer API key',
    modality: 'image',
  },
  {
    id: 'stability/stable-diffusion-3.5-large',
    provider: 'openrouter',
    name: 'Stable Diffusion 3.5',
    desc: 'OpenRouter · Requer API key',
    modality: 'image',
  },
];

export const ALL_MODELS = [...FREE_MODELS, ...PAID_MODELS, ...IMAGE_MODELS];
