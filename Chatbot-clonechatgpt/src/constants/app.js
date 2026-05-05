// Storage keys
export const HISTORY_KEY           = 'chaat_history';
export const APIKEYS_KEY           = 'chatbot_api_keys';          // legado (localStorage)
export const APIKEYS_SESSION_KEY   = 'chatbot_api_keys_session';
export const CUSTOM_SKILLS_KEY_BASE = 'chaat_custom_skills';

// Backend proxy URLs — can be overridden via environment variables
export const SECURE_PROXY_URL  = import.meta.env.VITE_SECURE_PROXY_URL  || '/api/chat';
export const HISTORY_PROXY_URL = import.meta.env.VITE_HISTORY_PROXY_URL || '/api/history';

// Welcome screen quick-start suggestions
export const SUGGESTIONS = [
  'Explique como funciona inteligência artificial',
  'Escreva um poema curto sobre o Brasil',
  'Crie um componente React de botão animado',
  'Quais são os melhores livros de programação?',
];

// Built-in skill definitions — each injects a system-level prompt instruction
export const SKILLS = {
  planning: {
    label: 'Planejamento',
    icon: '🧭',
    prompt: 'Trabalhe como engenheiro sênior: antes de codar, apresente um plano curto em etapas, valide suposições e depois implemente com foco no objetivo do usuário.',
  },
  frontend: {
    label: 'Frontend',
    icon: '🎯',
    prompt: 'Ao gerar UI, preserve consistência visual, acessibilidade (labels, foco, contraste), responsividade mobile-first e componentes reutilizáveis.',
  },
  backend: {
    label: 'Backend',
    icon: '🧱',
    prompt: 'Ao gerar backend, priorize validação de entrada, tratamento de erros, contratos estáveis, separação de camadas e observabilidade mínima (logs úteis).',
  },
  debugging: {
    label: 'Debug',
    icon: '🛠️',
    prompt: 'Em debugging, identifique causa raiz, explique por que ocorre, proponha correção mínima e descreva como validar que o problema foi resolvido.',
  },
  testing: {
    label: 'Testes',
    icon: '✅',
    prompt: 'Inclua estratégia de testes objetiva: casos principais, bordas e regressão. Se possível, sugira testes automatizados com exemplos curtos.',
  },
  security: {
    label: 'Segurança',
    icon: '🔒',
    prompt: 'Aplique práticas seguras por padrão: sanitização de entradas, proteção de segredos, princípio do menor privilégio e evitar exposição de dados sensíveis.',
  },
};
