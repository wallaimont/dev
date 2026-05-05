import type {
  Account,
  Activity,
  AuditEvent,
  AutomationRule,
  BillingPlan,
  Contact,
  DashboardOverview,
  Lead,
  NotificationItem,
  Opportunity,
  SubscriptionSummary,
  TeamUser,
  TenantProfile,
  Ticket,
} from '@orbitcrm/types';
import {
  accounts as demoAccounts,
  activities as demoActivities,
  auditEvents as demoAuditEvents,
  automationRules as demoAutomationRules,
  billingPlans as demoBillingPlans,
  contacts as demoContacts,
  currentSubscription as demoCurrentSubscription,
  notifications as demoNotifications,
  overview as demoOverview,
  leads as demoLeads,
  opportunities as demoOpportunities,
  teamUsers as demoTeamUsers,
  tenantProfile as demoTenantProfile,
  tickets as demoTickets,
} from './demo';
import { getStoredUser } from './auth';

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:3001';

type CurrentUser = {
  id?: string;
  name?: string;
  email?: string;
  tenantId?: string;
  title?: string | null;
  permissions?: string[];
};

function readSessionToken() {
  if (typeof document === 'undefined') return null;
  const match = document.cookie.match(/(?:^|; )orbitcrm_session=([^;]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

function summarizeObject(value: unknown, fallback: string) {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return fallback;
  const entries = Object.entries(value as Record<string, unknown>);
  if (entries.length === 0) return fallback;
  return entries.map(([key, entryValue]) => `${key}: ${String(entryValue)}`).join(' • ');
}

async function requestJson<T>(path: string, init?: RequestInit, fallback?: T): Promise<T> {
  try {
    const token = readSessionToken();
    const headers = new Headers(init?.headers ?? {});

    if (token && token !== 'demo-session') {
      headers.set('Authorization', `Bearer ${token}`);
    }

    if (init?.body && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }

    const response = await fetch(`${API_URL}${path}`, {
      ...init,
      headers,
      cache: 'no-store',
    });

    if (!response.ok) {
      throw new Error(`Request failed for ${path}`);
    }

    if (response.status === 204) {
      return fallback as T;
    }

    return (await response.json()) as T;
  } catch {
    return fallback as T;
  }
}

export async function getCurrentUser(): Promise<CurrentUser | null> {
  const fallback = getStoredUser();
  return requestJson<CurrentUser | null>('/auth/me', undefined, fallback);
}

export async function getOverview(): Promise<DashboardOverview> {
  const data = await requestJson<Partial<DashboardOverview>>('/reports/overview', undefined, demoOverview);
  return {
    ...demoOverview,
    ...data,
  };
}

export async function getCurrentTenant(): Promise<TenantProfile> {
  const data = await requestJson<any>('/tenants/current', undefined, null);
  if (!data) {
    return demoTenantProfile;
  }

  return {
    name: data.name ?? demoTenantProfile.name,
    slug: data.slug ?? demoTenantProfile.slug,
    primaryColor: data.primaryColor ?? demoTenantProfile.primaryColor,
    planName: data.plan?.name ?? demoTenantProfile.planName,
    timezone: data.settings?.timezone ?? demoTenantProfile.timezone,
    locale: data.settings?.locale ?? demoTenantProfile.locale,
  };
}

export async function updateCurrentTenant(input: Omit<TenantProfile, 'planName'> & { planName?: string }): Promise<TenantProfile> {
  const fallback: TenantProfile = {
    name: input.name,
    slug: input.slug,
    primaryColor: input.primaryColor,
    planName: input.planName ?? demoTenantProfile.planName,
    timezone: input.timezone,
    locale: input.locale,
  };

  const data = await requestJson<any>(
    '/tenants/current',
    {
      method: 'PUT',
      body: JSON.stringify({
        name: input.name,
        primaryColor: input.primaryColor,
        settings: {
          timezone: input.timezone,
          locale: input.locale,
          slug: input.slug,
        },
      }),
    },
    fallback as any,
  );

  return {
    name: data?.name ?? fallback.name,
    slug: data?.slug ?? fallback.slug,
    primaryColor: data?.primaryColor ?? fallback.primaryColor,
    planName: data?.plan?.name ?? fallback.planName,
    timezone: data?.settings?.timezone ?? fallback.timezone,
    locale: data?.settings?.locale ?? fallback.locale,
  };
}

export async function getUsers(): Promise<TeamUser[]> {
  const data = await requestJson<any[]>('/users', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoTeamUsers;
  }

  return data.map((item) => ({
    id: item.id,
    name: item.name ?? 'Orbit User',
    email: item.email ?? 'sem-email@demo.local',
    title: item.title ?? 'User',
    status: String(item.status ?? 'active').toLowerCase(),
    lastLoginAt: item.lastLoginAt ? String(item.lastLoginAt).slice(0, 16).replace('T', ' ') : '—',
  }));
}

export async function createUser(input: { name: string; email: string; title: string }): Promise<TeamUser> {
  const fallback: TeamUser = {
    id: `USR-${Date.now()}`,
    name: input.name,
    email: input.email,
    title: input.title || 'User',
    status: 'active',
    lastLoginAt: '—',
  };

  const data = await requestJson<any>(
    '/users',
    {
      method: 'POST',
      body: JSON.stringify({
        name: input.name,
        email: input.email,
        title: input.title,
        password: 'Temp@1234',
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    name: data?.name ?? fallback.name,
    email: data?.email ?? fallback.email,
    title: data?.title ?? fallback.title,
    status: String(data?.status ?? fallback.status).toLowerCase(),
    lastLoginAt: data?.lastLoginAt ? String(data.lastLoginAt).slice(0, 16).replace('T', ' ') : fallback.lastLoginAt,
  };
}

export async function deleteUser(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/users/${id}`, { method: 'DELETE' }, { success: true });
}

export async function getLeads(): Promise<Lead[]> {
  const data = await requestJson<any[]>('/leads', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoLeads;
  }

  return data.map((item) => ({
    id: item.id,
    name: item.name,
    company: item.company ?? 'Sem empresa',
    email: item.email ?? 'sem-email@demo.local',
    status: String(item.status ?? 'new').toLowerCase() as Lead['status'],
    score: Number(item.score ?? 0),
    owner: item.owner?.name ?? 'Orbit Team',
  }));
}

export async function createLead(input: { name: string; company: string; email: string }): Promise<Lead> {
  const fallback: Lead = {
    id: `LE-${Date.now()}`,
    name: input.name,
    company: input.company,
    email: input.email,
    status: 'new',
    score: 70,
    owner: 'Orbit Admin',
  };

  const data = await requestJson<any>(
    '/leads',
    {
      method: 'POST',
      body: JSON.stringify({
        name: input.name,
        company: input.company,
        email: input.email,
        source: 'Web app',
        status: 'NEW',
        score: 70,
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    name: data?.name ?? fallback.name,
    company: data?.company ?? fallback.company,
    email: data?.email ?? fallback.email,
    status: String(data?.status ?? fallback.status).toLowerCase() as Lead['status'],
    score: Number(data?.score ?? fallback.score),
    owner: data?.owner?.name ?? fallback.owner,
  };
}

export async function deleteLead(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/leads/${id}`, { method: 'DELETE' }, { success: true });
}

export async function getAccounts(): Promise<Account[]> {
  const data = await requestJson<any[]>('/accounts', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoAccounts;
  }

  return data.map((item) => ({
    id: item.id,
    name: item.name ?? 'Conta sem nome',
    segment: item.segment ?? 'Não informado',
    size: item.size ?? 'Não informado',
    website: item.website ?? '—',
    phone: item.phone ?? '—',
    owner: item.owner?.name ?? 'Orbit Team',
  }));
}

export async function createAccount(input: { name: string; segment: string; website: string }): Promise<Account> {
  const fallback: Account = {
    id: `AC-${Date.now()}`,
    name: input.name,
    segment: input.segment || 'Não informado',
    size: 'Growth',
    website: input.website || '—',
    phone: '—',
    owner: 'Orbit Admin',
  };

  const data = await requestJson<any>(
    '/accounts',
    {
      method: 'POST',
      body: JSON.stringify({
        name: input.name,
        segment: input.segment,
        website: input.website,
        size: 'Growth',
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    name: data?.name ?? fallback.name,
    segment: data?.segment ?? fallback.segment,
    size: data?.size ?? fallback.size,
    website: data?.website ?? fallback.website,
    phone: data?.phone ?? fallback.phone,
    owner: data?.owner?.name ?? fallback.owner,
  };
}

export async function deleteAccount(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/accounts/${id}`, { method: 'DELETE' }, { success: true });
}

export async function getContacts(): Promise<Contact[]> {
  const data = await requestJson<any[]>('/contacts', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoContacts;
  }

  return data.map((item) => ({
    id: item.id,
    name: item.name ?? 'Contato sem nome',
    email: item.email ?? 'sem-email@demo.local',
    phone: item.phone ?? '—',
    title: item.title ?? 'Não informado',
    account: item.account?.name ?? 'Sem conta',
    owner: item.owner?.name ?? 'Orbit Team',
  }));
}

export async function createContact(input: { name: string; email: string; title: string; account: string }): Promise<Contact> {
  const fallback: Contact = {
    id: `CT-${Date.now()}`,
    name: input.name,
    email: input.email,
    phone: '—',
    title: input.title || 'Contato',
    account: input.account || 'Sem conta',
    owner: 'Orbit Admin',
  };

  const data = await requestJson<any>(
    '/contacts',
    {
      method: 'POST',
      body: JSON.stringify({
        name: input.name,
        email: input.email,
        title: input.title,
        notes: `Conta informada: ${input.account}`,
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    name: data?.name ?? fallback.name,
    email: data?.email ?? fallback.email,
    phone: data?.phone ?? fallback.phone,
    title: data?.title ?? fallback.title,
    account: data?.account?.name ?? fallback.account,
    owner: data?.owner?.name ?? fallback.owner,
  };
}

export async function deleteContact(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/contacts/${id}`, { method: 'DELETE' }, { success: true });
}

export async function getOpportunities(): Promise<Opportunity[]> {
  const data = await requestJson<any[]>('/opportunities', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoOpportunities;
  }

  return data.map((item) => ({
    id: item.id,
    title: item.title ?? 'Oportunidade',
    account: item.account?.name ?? 'Sem conta',
    value: Number(item.value ?? 0),
    stage: String(item.stage ?? 'qualification').toLowerCase() as Opportunity['stage'],
    owner: item.owner?.name ?? 'Orbit Team',
    expectedClose: item.expectedCloseDate ? String(item.expectedCloseDate).slice(0, 10) : '—',
  }));
}

export async function createOpportunity(input: { title: string; account: string; value: number; expectedClose: string }): Promise<Opportunity> {
  const fallback: Opportunity = {
    id: `OP-${Date.now()}`,
    title: input.title,
    account: input.account || 'Sem conta',
    value: input.value,
    stage: 'qualification',
    owner: 'Orbit Admin',
    expectedClose: input.expectedClose || '—',
  };

  const data = await requestJson<any>(
    '/opportunities',
    {
      method: 'POST',
      body: JSON.stringify({
        title: input.title,
        value: input.value,
        expectedCloseDate: input.expectedClose || undefined,
        stage: 'QUALIFICATION',
        status: 'OPEN',
        notes: `Conta informada: ${input.account}`,
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    title: data?.title ?? fallback.title,
    account: data?.account?.name ?? fallback.account,
    value: Number(data?.value ?? fallback.value),
    stage: String(data?.stage ?? fallback.stage).toLowerCase() as Opportunity['stage'],
    owner: data?.owner?.name ?? fallback.owner,
    expectedClose: data?.expectedCloseDate ? String(data.expectedCloseDate).slice(0, 10) : fallback.expectedClose,
  };
}

export async function deleteOpportunity(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/opportunities/${id}`, { method: 'DELETE' }, { success: true });
}

export async function updateOpportunityStage(id: string, stage: Opportunity['stage']): Promise<{ success: boolean }> {
  await requestJson(`/opportunities/${id}/stage`, {
    method: 'PATCH',
    body: JSON.stringify({ stage: stage.toUpperCase() }),
  }, { success: true } as any);

  return { success: true };
}

export async function getActivities(): Promise<Activity[]> {
  const data = await requestJson<any[]>('/activities', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoActivities;
  }

  return data.map((item) => ({
    id: item.id,
    title: item.title ?? 'Atividade',
    type: String(item.type ?? 'task').toLowerCase() as Activity['type'],
    priority: String(item.priority ?? 'medium').toLowerCase() as Activity['priority'],
    status: String(item.status ?? 'pending').toLowerCase().replace('in-progress', 'in_progress') as Activity['status'],
    dueDate: item.dueDate ? String(item.dueDate).slice(0, 10) : '—',
    relatedTo: item.account?.name ?? item.contact?.name ?? item.lead?.name ?? item.opportunity?.title ?? 'Geral',
    owner: item.owner?.name ?? 'Orbit Team',
  }));
}

export async function createActivity(input: { title: string; type: Activity['type']; priority: Activity['priority']; dueDate: string; relatedTo: string }): Promise<Activity> {
  const fallback: Activity = {
    id: `AT-${Date.now()}`,
    title: input.title,
    type: input.type,
    priority: input.priority,
    status: 'pending',
    dueDate: input.dueDate || '—',
    relatedTo: input.relatedTo || 'Geral',
    owner: 'Orbit Admin',
  };

  const data = await requestJson<any>(
    '/activities',
    {
      method: 'POST',
      body: JSON.stringify({
        title: input.title,
        type: input.type.toUpperCase(),
        priority: input.priority.toUpperCase(),
        dueDate: input.dueDate || undefined,
        description: `Relacionada a: ${input.relatedTo || 'Geral'}`,
        status: 'PENDING',
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    title: data?.title ?? fallback.title,
    type: String(data?.type ?? fallback.type).toLowerCase() as Activity['type'],
    priority: String(data?.priority ?? fallback.priority).toLowerCase() as Activity['priority'],
    status: String(data?.status ?? fallback.status).toLowerCase().replace('in-progress', 'in_progress') as Activity['status'],
    dueDate: data?.dueDate ? String(data.dueDate).slice(0, 10) : fallback.dueDate,
    relatedTo: data?.account?.name ?? data?.contact?.name ?? data?.lead?.name ?? data?.opportunity?.title ?? fallback.relatedTo,
    owner: data?.owner?.name ?? fallback.owner,
  };
}

export async function deleteActivity(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/activities/${id}`, { method: 'DELETE' }, { success: true });
}

export async function getAutomationRules(): Promise<AutomationRule[]> {
  const data = await requestJson<any[]>('/automation-rules', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoAutomationRules;
  }

  return data.map((item) => ({
    id: item.id,
    name: item.name ?? 'Regra sem nome',
    triggerType: item.triggerType ?? 'CUSTOM_TRIGGER',
    conditionsSummary: summarizeObject(item.conditionsJson, 'Sem condições'),
    actionsSummary: summarizeObject(item.actionsJson, 'Sem ações'),
    active: Boolean(item.active ?? true),
  }));
}

export async function createAutomationRule(input: {
  name: string;
  triggerType: string;
  conditionsSummary: string;
  actionsSummary: string;
  active: boolean;
}): Promise<AutomationRule> {
  const fallback: AutomationRule = {
    id: `AR-${Date.now()}`,
    name: input.name,
    triggerType: input.triggerType,
    conditionsSummary: input.conditionsSummary || 'Sem condições',
    actionsSummary: input.actionsSummary || 'Sem ações',
    active: input.active,
  };

  const data = await requestJson<any>(
    '/automation-rules',
    {
      method: 'POST',
      body: JSON.stringify({
        name: input.name,
        triggerType: input.triggerType,
        conditionsJson: { summary: input.conditionsSummary || 'Sem condições' },
        actionsJson: { summary: input.actionsSummary || 'Sem ações' },
        active: input.active,
      }),
    },
    fallback as any,
  );

  return {
    id: data?.id ?? fallback.id,
    name: data?.name ?? fallback.name,
    triggerType: data?.triggerType ?? fallback.triggerType,
    conditionsSummary: summarizeObject(data?.conditionsJson, fallback.conditionsSummary),
    actionsSummary: summarizeObject(data?.actionsJson, fallback.actionsSummary),
    active: Boolean(data?.active ?? fallback.active),
  };
}

export async function updateAutomationRule(
  id: string,
  input: { name: string; triggerType: string; conditionsSummary: string; actionsSummary: string; active: boolean },
): Promise<{ success: boolean }> {
  await requestJson(
    `/automation-rules/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify({
        name: input.name,
        triggerType: input.triggerType,
        conditionsJson: { summary: input.conditionsSummary },
        actionsJson: { summary: input.actionsSummary },
        active: input.active,
      }),
    },
    { success: true } as any,
  );

  return { success: true };
}

export async function deleteAutomationRule(id: string): Promise<{ success: boolean }> {
  return requestJson<{ success: boolean }>(`/automation-rules/${id}`, { method: 'DELETE' }, { success: true });
}

export async function getTickets(): Promise<Ticket[]> {
  const data = await requestJson<any[]>('/tickets', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoTickets;
  }

  return data.map((item) => ({
    id: item.id,
    subject: item.subject,
    priority: String(item.priority ?? 'medium').toLowerCase() as Ticket['priority'],
    status: String(item.status ?? 'open').toLowerCase() as Ticket['status'],
    owner: item.owner?.name ?? 'Orbit Team',
  }));
}

export async function getNotifications(): Promise<NotificationItem[]> {
  const data = await requestJson<any[]>('/notifications', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoNotifications;
  }

  return data.map((item) => ({
    id: item.id,
    title: item.title ?? 'Notificação',
    message: item.message ?? 'Sem detalhes',
    type: String(item.type ?? 'info').toLowerCase(),
    read: Boolean(item.readAt),
    createdAt: item.createdAt ? String(item.createdAt).slice(0, 16).replace('T', ' ') : '—',
  }));
}

export async function getBillingPlans(): Promise<BillingPlan[]> {
  const data = await requestJson<any[]>('/billing/plans', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoBillingPlans;
  }

  return data.map((item) => ({
    code: String(item.code ?? 'STARTER'),
    name: item.name ?? 'Plano',
    priceCents: Number(item.priceCents ?? 0),
    userLimit: item.userLimit ?? null,
    features: Object.entries((item.featureFlags ?? {}) as Record<string, unknown>)
      .filter(([, enabled]) => Boolean(enabled))
      .map(([feature]) => feature),
  }));
}

export async function getCurrentSubscription(): Promise<SubscriptionSummary> {
  const data = await requestJson<any>('/billing/subscription', undefined, null);
  if (!data) {
    return demoCurrentSubscription;
  }

  return {
    planCode: data.plan?.code ?? demoCurrentSubscription.planCode,
    planName: data.plan?.name ?? demoCurrentSubscription.planName,
    status: String(data.status ?? demoCurrentSubscription.status).toLowerCase(),
    currentPeriodEnd: data.currentPeriodEnd ? String(data.currentPeriodEnd).slice(0, 10) : demoCurrentSubscription.currentPeriodEnd,
    stripeCustomerId: data.stripeCustomerId ?? demoCurrentSubscription.stripeCustomerId,
  };
}

export async function prepareCheckoutSession(planCode: string): Promise<{ provider: string; status: string; message: string }> {
  const data = await requestJson<any>(
    '/billing/checkout-session',
    {
      method: 'POST',
      body: JSON.stringify({ planCode }),
    },
    {
      provider: 'stripe',
      status: 'prepared',
      message: 'Integração pronta para conectar Stripe Checkout e webhooks.',
    } as any,
  );

  return {
    provider: data?.provider ?? 'stripe',
    status: data?.status ?? 'prepared',
    message: data?.message ?? 'Checkout preparado.',
  };
}

export async function getAuditEvents(): Promise<AuditEvent[]> {
  const data = await requestJson<any[]>('/audit-logs', undefined, []);
  if (!Array.isArray(data) || data.length === 0) {
    return demoAuditEvents;
  }

  return data.map((item) => ({
    id: item.id,
    action: item.action ?? 'event.logged',
    entity: item.entity ?? 'system',
    actor: item.user?.name ?? 'Sistema OrbitCRM',
    createdAt: item.createdAt ? String(item.createdAt).slice(0, 16).replace('T', ' ') : '—',
  }));
}
