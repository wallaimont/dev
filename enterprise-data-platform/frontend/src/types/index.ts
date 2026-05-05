export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  avatarUrl?: string;
  active: boolean;
  companyId?: number;
  companyName?: string;
  branchId?: number;
  branchName?: string;
  roles: string[];
  permissions: string[];
  lastLogin?: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Company {
  id: number;
  name: string;
  tradeName?: string;
  cnpj?: string;
  phone?: string;
  email?: string;
  address?: string;
  tenantCode?: string;
  industry?: string;
  subscriptionPlan?: string;
  subscriptionStatus?: string;
  onboardingStatus?: string;
  slaTier?: string;
  goLiveDate?: string;
  active: boolean;
  branchCount: number;
  createdAt: string;
}

export interface DataSource {
  id: number;
  name: string;
  description?: string;
  type: string;
  host?: string;
  port?: number;
  databaseName?: string;
  apiUrl?: string;
  syncSchedule?: string;
  active: boolean;
  status: string;
  lastSync?: string;
  lastSyncStatus?: string;
  lastSyncRecords?: number;
  companyId?: number;
  companyName?: string;
  createdAt: string;
}

export interface KpiDefinition {
  id: number;
  name: string;
  code?: string;
  description?: string;
  formula?: string;
  unit?: string;
  periodicity?: string;
  targetValue?: number;
  warningThreshold?: number;
  criticalThreshold?: number;
  thresholdDirection?: string;
  category?: string;
  module?: string;
  ownerName?: string;
  active: boolean;
  alertEnabled: boolean;
  currentValue?: number;
  currentStatus?: string;
  variationPct?: number;
  createdAt: string;
}

export interface DashboardData {
  summary: SummaryCards;
  revenueByMonth: ChartDataItem[];
  revenueByRegion: ChartDataItem[];
  topProducts: ChartDataItem[];
  topSellers: ChartDataItem[];
  salesByChannel: ChartDataItem[];
  kpiCards: KpiCardData[];
}

export interface SummaryCards {
  grossRevenue: number;
  netRevenue: number;
  grossMargin: number;
  netMargin: number;
  averageTicket: number;
  operatingCost: number;
  defaultRate: number;
  totalOrders: number;
  totalCustomers: number;
  conversionRate: number;
}

export interface ChartDataItem {
  label: string;
  value: number;
  previousValue?: number;
  variationPct?: number;
}

export interface KpiCardData {
  name: string;
  code: string;
  value: number;
  target: number;
  unit: string;
  status: string;
  variationPct: number;
  trend: string;
}

export interface IntegrationLog {
  id: number;
  dataSourceId: number;
  dataSourceName: string;
  status: string;
  recordsExtracted?: number;
  recordsTransformed?: number;
  recordsLoaded?: number;
  recordsRejected?: number;
  errorMessage?: string;
  startedAt?: string;
  finishedAt?: string;
  durationMs?: number;
  triggeredBy?: string;
  createdAt: string;
}

export interface AuditLogEntry {
  id: number;
  username: string;
  action: string;
  module: string;
  entity?: string;
  entityId?: number;
  ipAddress?: string;
  createdAt: string;
}

export interface Role {
  id: number;
  name: string;
  description?: string;
  permissions: string[];
}

export interface AlertConfiguration {
  id: number;
  name: string;
  description?: string;
  kpiId?: number;
  kpiName?: string;
  alertType?: string;
  conditionExpression?: string;
  notificationEmails?: string;
  notifyViaEmail: boolean;
  notifyInApp: boolean;
  active: boolean;
  lastTriggered?: string;
  triggerCount: number;
  companyId?: number;
  companyName?: string;
  createdAt: string;
}

export interface DataCatalogEntry {
  id: number;
  tableName: string;
  columnName?: string;
  schemaName?: string;
  businessName?: string;
  description?: string;
  dataType?: string;
  sourceSystem?: string;
  ownerName?: string;
  qualityRules?: string;
  lineageInfo?: string;
  classification?: string;
  active: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface EtlOverview {
  totalExecutions: number;
  successfulExecutions: number;
  partialExecutions: number;
  failedExecutions: number;
  runningExecutions: number;
  connectedSources: number;
}

export interface CommercialReadinessOverview {
  readinessScore: number;
  stage: string;
  activeCompanies: number;
  activeUsers: number;
  activeDataSources: number;
  connectedDataSources: number;
  activeKpis: number;
  configuredAlerts: number;
  catalogItems: number;
  etlRuns: number;
}

export interface CommercialSnapshot {
  recommendedPlan: string;
  slaTarget: string;
  onboardingDays: number;
  estimatedRoiPct: number;
  paybackMonths: number;
  revenuePotentialBand: string;
}

export interface CommercialReadinessChecklistItem {
  key: string;
  title: string;
  status: string;
  progressPct: number;
  detail: string;
  route: string;
}

export interface CommercialReadinessPriorityAction {
  title: string;
  detail: string;
  impact: string;
  route: string;
}

export interface CommercialReadinessModule {
  module: string;
  status: string;
  coveragePct: number;
  detail: string;
}

export interface CommercialServicePackage {
  name: string;
  targetSegment: string;
  priceRange: string;
  sla: string;
  recommended: boolean;
  highlights: string[];
}

export interface CommercialImplementationPhase {
  phase: string;
  timeline: string;
  objective: string;
  deliverables: string[];
}

export interface CommercialRiskSignal {
  title: string;
  severity: string;
  detail: string;
  route: string;
}

export interface CommercialReadiness {
  overview: CommercialReadinessOverview;
  snapshot: CommercialSnapshot;
  checklist: CommercialReadinessChecklistItem[];
  priorityActions: CommercialReadinessPriorityAction[];
  modules: CommercialReadinessModule[];
  servicePackages: CommercialServicePackage[];
  implementationPlan: CommercialImplementationPhase[];
  riskSignals: CommercialRiskSignal[];
}

export interface TenantOption {
  companyId: number;
  companyName: string;
  tradeName?: string;
  tenantCode?: string;
  subscriptionPlan?: string;
  subscriptionStatus?: string;
  onboardingStatus?: string;
  activeUsers: number;
  current: boolean;
}

export interface TenantContext {
  effectiveCompanyId?: number | null;
  companyName?: string | null;
  tradeName?: string | null;
  tenantCode?: string | null;
  subscriptionPlan?: string | null;
  subscriptionStatus?: string | null;
  onboardingStatus?: string | null;
  canSwitchTenant: boolean;
  availableTenants: TenantOption[];
}

export interface SubscriptionSummary {
  activeSubscriptions: number;
  monthlyRecurringRevenue: number;
  pendingRenewals: number;
  licensedUsers: number;
  activeUsers: number;
  utilizationPct: number;
}

export interface SubscriptionItem {
  id: number;
  companyId: number;
  companyName: string;
  planName: string;
  billingCycle: string;
  status: string;
  mrrValue: number;
  setupFee: number;
  licensedUsers: number;
  activeUsers: number;
  renewalDate?: string;
  autoRenew: boolean;
  billingEmail?: string;
  featureBundle?: string;
  createdAt: string;
}

export interface SubscriptionOverview {
  summary: SubscriptionSummary;
  subscriptions: SubscriptionItem[];
}

export interface ContractSummary {
  activeContracts: number;
  renewingSoon: number;
  annualRecurringRevenue: number;
  defaultSlaHours: number;
}

export interface ContractItem {
  id: number;
  companyId: number;
  companyName: string;
  contractNumber: string;
  status: string;
  commercialOwner?: string;
  legalContact?: string;
  startDate?: string;
  endDate?: string;
  annualValue: number;
  slaHours: number;
  lgpdAddendum: boolean;
  notes?: string;
  createdAt: string;
}

export interface ContractOverview {
  summary: ContractSummary;
  contracts: ContractItem[];
}

export interface OnboardingSummary {
  activePrograms: number;
  averageProgressPct: number;
  upcomingGoLives: number;
  highRiskPrograms: number;
}

export interface OnboardingItem {
  id: number;
  companyId: number;
  companyName: string;
  phase: string;
  progressPct: number;
  ownerName: string;
  kickoffDate?: string;
  targetGoLiveDate?: string;
  completedMilestones: number;
  totalMilestones: number;
  riskLevel?: string;
  nextMilestone?: string;
  blockers?: string;
  createdAt: string;
}

export interface OnboardingOverview {
  summary: OnboardingSummary;
  initiatives: OnboardingItem[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
