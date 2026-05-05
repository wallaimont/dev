import type {
  Account,
  Activity,
  AuditEvent,
  AutomationRule,
  BillingPlan,
  Contact,
  DashboardOverview,
  Lead,
  NavItem,
  NotificationItem,
  Opportunity,
  SubscriptionSummary,
  TeamUser,
  TenantProfile,
  Ticket,
} from '@orbitcrm/types';

export const publicNav = [
  { href: '/features', label: 'Funcionalidades' },
  { href: '/pricing', label: 'Planos' },
  { href: '/login', label: 'Entrar' },
];

export const appNav: NavItem[] = [
  { href: '/dashboard', label: 'Dashboard' },
  { href: '/leads', label: 'Leads' },
  { href: '/accounts', label: 'Contas' },
  { href: '/contacts', label: 'Contatos' },
  { href: '/opportunities', label: 'Oportunidades' },
  { href: '/pipeline', label: 'Pipeline' },
  { href: '/activities', label: 'Atividades' },
  { href: '/calendar', label: 'Calendário' },
  { href: '/tickets', label: 'Tickets', badge: '5' },
  { href: '/automations', label: 'Automações' },
  { href: '/reports', label: 'Relatórios' },
  { href: '/integrations', label: 'Integrações' },
  { href: '/users', label: 'Usuários' },
  { href: '/billing', label: 'Billing' },
  { href: '/audit', label: 'Auditoria' },
  { href: '/settings', label: 'Configurações' },
];

export const overview: DashboardOverview = {
  totalLeads: 184,
  totalAccounts: 42,
  totalOpportunities: 28,
  pipelineValue: 1284000,
  wonDeals: 12,
  openTickets: 7,
};

export const tenantProfile: TenantProfile = {
  name: 'Orbit Demo Industries',
  slug: 'orbit-demo',
  primaryColor: '#0ea5e9',
  planName: 'Growth',
  timezone: 'America/Sao_Paulo',
  locale: 'pt-BR',
};

export const billingPlans: BillingPlan[] = [
  {
    code: 'STARTER',
    name: 'Starter',
    priceCents: 14900,
    userLimit: 3,
    features: ['CRM essencial', 'Até 3 usuários', 'Pipeline inicial'],
  },
  {
    code: 'GROWTH',
    name: 'Growth',
    priceCents: 49900,
    userLimit: null,
    features: ['Automações', 'Relatórios', 'Tickets e colaboração'],
  },
  {
    code: 'ENTERPRISE',
    name: 'Enterprise',
    priceCents: 0,
    userLimit: null,
    features: ['Auditoria', 'Branding', 'Governança ampliada'],
  },
];

export const currentSubscription: SubscriptionSummary = {
  planCode: 'GROWTH',
  planName: 'Growth',
  status: 'active',
  currentPeriodEnd: '2026-05-10',
  stripeCustomerId: 'cus_demo_001',
};

export const teamUsers: TeamUser[] = [
  {
    id: 'USR-1',
    name: 'Orbit Admin',
    email: 'admin@orbitcrm.demo',
    title: 'Founder & Admin',
    status: 'active',
    lastLoginAt: '2026-04-10 09:12',
  },
  {
    id: 'USR-2',
    name: 'Rafa Sales',
    email: 'rafa@orbitcrm.demo',
    title: 'Sales Executive',
    status: 'active',
    lastLoginAt: '2026-04-10 08:45',
  },
  {
    id: 'USR-3',
    name: 'Bia Lopes',
    email: 'bia@orbitcrm.demo',
    title: 'Sales Executive',
    status: 'active',
    lastLoginAt: '2026-04-10 08:22',
  },
  {
    id: 'USR-4',
    name: 'Carlos Neri',
    email: 'carlos@orbitcrm.demo',
    title: 'Sales Executive',
    status: 'active',
    lastLoginAt: '2026-04-09 17:40',
  },
];

export const leads: Lead[] = [
  { id: 'LE-1001', name: 'Marina Costa', company: 'Atlas Supply', email: 'marina@atlas.com', status: 'qualified', score: 86, owner: 'Rafa Sales' },
  { id: 'LE-1002', name: 'Paulo Freitas', company: 'Vertex Energy', email: 'paulo@vertex.com', status: 'proposal', score: 77, owner: 'Bia Lopes' },
  { id: 'LE-1003', name: 'Lívia Moura', company: 'Nexa Health', email: 'livia@nexa.com', status: 'new', score: 64, owner: 'Carlos Neri' },
];

export const accounts: Account[] = [
  { id: 'AC-1001', name: 'Atlas Supply', segment: 'Distribuição', size: 'Mid-market', website: 'atlas.example.com', phone: '+55 11 4000-1001', owner: 'Rafa Sales' },
  { id: 'AC-1002', name: 'Vertex Energy', segment: 'Energia', size: 'Enterprise', website: 'vertex.example.com', phone: '+55 21 4000-1002', owner: 'Bia Lopes' },
  { id: 'AC-1003', name: 'Nexa Health', segment: 'Healthtech', size: 'Growth', website: 'nexa.example.com', phone: '+55 31 4000-1003', owner: 'Carlos Neri' },
];

export const contacts: Contact[] = [
  { id: 'CT-1001', name: 'Marina Costa', email: 'marina@atlas.com', phone: '+55 11 99888-1010', title: 'Procurement Lead', account: 'Atlas Supply', owner: 'Rafa Sales' },
  { id: 'CT-1002', name: 'Paulo Freitas', email: 'paulo@vertex.com', phone: '+55 21 97777-2020', title: 'COO', account: 'Vertex Energy', owner: 'Bia Lopes' },
  { id: 'CT-1003', name: 'Lívia Moura', email: 'livia@nexa.com', phone: '+55 31 96666-3030', title: 'Operations Manager', account: 'Nexa Health', owner: 'Carlos Neri' },
];

export const opportunities: Opportunity[] = [
  { id: 'OP-2001', title: 'Expansão regional', account: 'Atlas Supply', value: 180000, stage: 'qualification', owner: 'Rafa Sales', expectedClose: '2026-04-22' },
  { id: 'OP-2002', title: 'Licenças enterprise', account: 'Vertex Energy', value: 320000, stage: 'proposal', owner: 'Bia Lopes', expectedClose: '2026-04-28' },
  { id: 'OP-2003', title: 'Renovação anual', account: 'Nexa Health', value: 540000, stage: 'negotiation', owner: 'Carlos Neri', expectedClose: '2026-05-05' },
  { id: 'OP-2004', title: 'Onboarding multiunidade', account: 'Solis Group', value: 210000, stage: 'won', owner: 'Ana Prado', expectedClose: '2026-04-12' },
];

export const activities: Activity[] = [
  { id: 'AT-4001', title: 'Reunião de diagnóstico', type: 'meeting', priority: 'high', status: 'pending', dueDate: '2026-04-12', relatedTo: 'Atlas Supply', owner: 'Rafa Sales' },
  { id: 'AT-4002', title: 'Enviar proposta comercial', type: 'email', priority: 'high', status: 'in_progress', dueDate: '2026-04-13', relatedTo: 'Vertex Energy', owner: 'Bia Lopes' },
  { id: 'AT-4003', title: 'Follow-up pós-demo', type: 'call', priority: 'medium', status: 'done', dueDate: '2026-04-09', relatedTo: 'Nexa Health', owner: 'Carlos Neri' },
];

export const automationRules: AutomationRule[] = [
  {
    id: 'AR-5001',
    name: 'Lead qualificado → criar oportunidade',
    triggerType: 'LEAD_QUALIFIED',
    conditionsSummary: 'score >= 80',
    actionsSummary: 'criar oportunidade + notificar SDR',
    active: true,
  },
  {
    id: 'AR-5002',
    name: 'Deal won → iniciar onboarding',
    triggerType: 'DEAL_WON',
    conditionsSummary: 'stage = won',
    actionsSummary: 'abrir ticket + enviar e-mail de boas-vindas',
    active: true,
  },
  {
    id: 'AR-5003',
    name: 'Cliente inativo → follow-up',
    triggerType: 'ACCOUNT_IDLE',
    conditionsSummary: '30 dias sem atividade',
    actionsSummary: 'criar tarefa para CS',
    active: false,
  },
];

export const tickets: Ticket[] = [
  { id: 'TK-301', subject: 'Ajuste de regra de SLA', priority: 'urgent', status: 'open', owner: 'Suporte N1' },
  { id: 'TK-302', subject: 'Webhook Stripe não sincroniza', priority: 'high', status: 'pending', owner: 'Finance Ops' },
  { id: 'TK-303', subject: 'Importação de contatos CSV', priority: 'medium', status: 'resolved', owner: 'CS Team' },
];

export const notifications: NotificationItem[] = [
  {
    id: 'NT-6001',
    title: 'SLA próximo do vencimento',
    message: 'O ticket TK-301 vence em 38 minutos e precisa de resposta.',
    type: 'urgent',
    read: false,
    createdAt: '2026-04-10 09:12',
  },
  {
    id: 'NT-6002',
    title: 'Novo lead qualificado',
    message: 'Um lead enterprise foi qualificado automaticamente pelo formulário.',
    type: 'sales',
    read: false,
    createdAt: '2026-04-10 08:47',
  },
  {
    id: 'NT-6003',
    title: 'Webhook confirmado',
    message: 'A assinatura Growth foi sincronizada com sucesso via webhook.',
    type: 'billing',
    read: true,
    createdAt: '2026-04-09 17:10',
  },
];

export const auditEvents: AuditEvent[] = [
  {
    id: 'AU-7001',
    action: 'permission.updated',
    entity: 'role',
    actor: 'Orbit Admin',
    createdAt: '2026-04-10 09:12',
  },
  {
    id: 'AU-7002',
    action: 'opportunity.stage_changed',
    entity: 'opportunity',
    actor: 'Bia Lopes',
    createdAt: '2026-04-10 08:47',
  },
  {
    id: 'AU-7003',
    action: 'branding.updated',
    entity: 'tenant',
    actor: 'Orbit Admin',
    createdAt: '2026-04-09 11:03',
  },
];

export const monthlySales = [
  { month: 'Jan', value: 92000 },
  { month: 'Fev', value: 116000 },
  { month: 'Mar', value: 138000 },
  { month: 'Abr', value: 174000 },
  { month: 'Mai', value: 201000 },
  { month: 'Jun', value: 229000 },
];
