export type NavItem = {
  href: string;
  label: string;
  badge?: string;
};

export type Lead = {
  id: string;
  name: string;
  company: string;
  email: string;
  status: 'new' | 'qualified' | 'proposal' | 'won' | 'lost';
  score: number;
  owner: string;
};

export type Account = {
  id: string;
  name: string;
  segment: string;
  size: string;
  website: string;
  phone: string;
  owner: string;
};

export type Contact = {
  id: string;
  name: string;
  email: string;
  phone: string;
  title: string;
  account: string;
  owner: string;
};

export type ActivityType = 'task' | 'call' | 'meeting' | 'email';
export type ActivityPriority = 'low' | 'medium' | 'high';
export type ActivityStatus = 'pending' | 'in_progress' | 'done';

export type Activity = {
  id: string;
  title: string;
  type: ActivityType;
  priority: ActivityPriority;
  status: ActivityStatus;
  dueDate: string;
  relatedTo: string;
  owner: string;
};

export type AutomationRule = {
  id: string;
  name: string;
  triggerType: string;
  conditionsSummary: string;
  actionsSummary: string;
  active: boolean;
};

export type NotificationItem = {
  id: string;
  title: string;
  message: string;
  type: string;
  read: boolean;
  createdAt: string;
};

export type AuditEvent = {
  id: string;
  action: string;
  entity: string;
  actor: string;
  createdAt: string;
};

export type TenantProfile = {
  name: string;
  slug: string;
  primaryColor: string;
  planName: string;
  timezone: string;
  locale: string;
};

export type BillingPlan = {
  code: string;
  name: string;
  priceCents: number;
  userLimit: number | null;
  features: string[];
};

export type SubscriptionSummary = {
  planCode: string;
  planName: string;
  status: string;
  currentPeriodEnd: string;
  stripeCustomerId: string;
};

export type TeamUser = {
  id: string;
  name: string;
  email: string;
  title: string;
  status: string;
  lastLoginAt: string;
};

export type OpportunityStage = 'qualification' | 'discovery' | 'proposal' | 'negotiation' | 'won';

export type Opportunity = {
  id: string;
  title: string;
  account: string;
  value: number;
  stage: OpportunityStage;
  owner: string;
  expectedClose: string;
};

export type Ticket = {
  id: string;
  subject: string;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  status: 'open' | 'pending' | 'resolved';
  owner: string;
};

export type DashboardOverview = {
  totalLeads: number;
  totalAccounts: number;
  totalOpportunities: number;
  pipelineValue: number;
  wonDeals: number;
  openTickets: number;
};
