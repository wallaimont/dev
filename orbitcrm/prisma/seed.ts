import { PrismaClient, PlanCode, SubscriptionStatus, UserStatus, LeadStatus, OpportunityStage, OpportunityStatus, ActivityType, ActivityPriority, ActivityStatus, TicketStatus, TicketPriority } from '@prisma/client';
import { hash } from 'bcryptjs';

const prisma = new PrismaClient();

async function main() {
  const plans = [
    {
      id: 'plan_starter',
      code: PlanCode.STARTER,
      name: 'Starter',
      priceCents: 14900,
      userLimit: 3,
      featureFlags: { crm: true, tickets: false, automations: false },
    },
    {
      id: 'plan_growth',
      code: PlanCode.GROWTH,
      name: 'Growth',
      priceCents: 49900,
      userLimit: null,
      featureFlags: { crm: true, tickets: true, automations: true, reports: true },
    },
    {
      id: 'plan_enterprise',
      code: PlanCode.ENTERPRISE,
      name: 'Enterprise',
      priceCents: 0,
      userLimit: null,
      featureFlags: { crm: true, tickets: true, automations: true, reports: true, audit: true, sso: true },
    },
  ];

  for (const plan of plans) {
    await prisma.plan.upsert({
      where: { code: plan.code },
      create: plan,
      update: plan,
    });
  }

  const permissionCodes = [
    'manage:all',
    'crm:read',
    'crm:write',
    'tickets:manage',
    'reports:view',
    'billing:manage',
  ];

  for (const code of permissionCodes) {
    await prisma.permission.upsert({
      where: { code },
      create: { code, description: code },
      update: { description: code },
    });
  }

  const tenant = await prisma.tenant.upsert({
    where: { slug: 'orbit-demo' },
    create: {
      id: 'tenant_demo',
      name: 'Orbit Demo Industries',
      slug: 'orbit-demo',
      primaryColor: '#0ea5e9',
      planId: 'plan_growth',
      settings: { timezone: 'America/Sao_Paulo', locale: 'pt-BR' },
    },
    update: {
      name: 'Orbit Demo Industries',
      planId: 'plan_growth',
      primaryColor: '#0ea5e9',
    },
  });

  await prisma.subscription.upsert({
    where: { id: 'sub_demo' },
    create: {
      id: 'sub_demo',
      tenantId: tenant.id,
      planId: 'plan_growth',
      status: SubscriptionStatus.ACTIVE,
      stripeCustomerId: 'cus_demo_001',
      stripeSubscriptionId: 'sub_demo_001',
      currentPeriodEnd: new Date('2026-05-10T00:00:00Z'),
    },
    update: {
      status: SubscriptionStatus.ACTIVE,
      currentPeriodEnd: new Date('2026-05-10T00:00:00Z'),
    },
  });

  const adminRole = await prisma.role.upsert({
    where: { id: 'role_admin_demo' },
    create: { id: 'role_admin_demo', tenantId: tenant.id, name: 'Admin', description: 'Administrador do tenant', isSystem: true },
    update: { name: 'Admin', description: 'Administrador do tenant' },
  });

  const salesRole = await prisma.role.upsert({
    where: { id: 'role_sales_demo' },
    create: { id: 'role_sales_demo', tenantId: tenant.id, name: 'Sales', description: 'Equipe comercial', isSystem: true },
    update: { name: 'Sales', description: 'Equipe comercial' },
  });

  const permissions = await prisma.permission.findMany({ where: { code: { in: permissionCodes } } });

  await prisma.rolePermission.createMany({
    data: permissions.map((permission) => ({ roleId: adminRole.id, permissionId: permission.id })),
    skipDuplicates: true,
  });
  await prisma.rolePermission.createMany({
    data: permissions
      .filter((permission) => ['crm:read', 'crm:write', 'reports:view'].includes(permission.code))
      .map((permission) => ({ roleId: salesRole.id, permissionId: permission.id })),
    skipDuplicates: true,
  });

  const passwordHash = await hash('Admin@123', 10);
  const salesHash = await hash('Sales@123', 10);

  const admin = await prisma.user.upsert({
    where: { email: 'admin@orbitcrm.demo' },
    create: {
      id: 'user_admin_demo',
      tenantId: tenant.id,
      roleId: adminRole.id,
      name: 'Orbit Admin',
      email: 'admin@orbitcrm.demo',
      passwordHash,
      title: 'Founder & Admin',
      status: UserStatus.ACTIVE,
    },
    update: { tenantId: tenant.id, roleId: adminRole.id, passwordHash, status: UserStatus.ACTIVE },
  });

  const salesUsers = [
    { id: 'user_sales_1', name: 'Rafa Sales', email: 'rafa@orbitcrm.demo' },
    { id: 'user_sales_2', name: 'Bia Lopes', email: 'bia@orbitcrm.demo' },
    { id: 'user_sales_3', name: 'Carlos Neri', email: 'carlos@orbitcrm.demo' },
  ];

  for (const person of salesUsers) {
    await prisma.user.upsert({
      where: { email: person.email },
      create: {
        id: person.id,
        tenantId: tenant.id,
        roleId: salesRole.id,
        name: person.name,
        email: person.email,
        passwordHash: salesHash,
        title: 'Sales Executive',
        status: UserStatus.ACTIVE,
      },
      update: { tenantId: tenant.id, roleId: salesRole.id, passwordHash: salesHash },
    });
  }

  await prisma.lead.createMany({
    data: [
      { id: 'lead_demo_1', tenantId: tenant.id, ownerId: 'user_sales_1', name: 'Marina Costa', email: 'marina@atlas.com', phone: '+55 11 99888-1010', company: 'Atlas Supply', title: 'Procurement Lead', source: 'Inbound', status: LeadStatus.QUALIFIED, score: 86, notes: 'Interesse em expansão regional.' },
      { id: 'lead_demo_2', tenantId: tenant.id, ownerId: 'user_sales_2', name: 'Paulo Freitas', email: 'paulo@vertex.com', phone: '+55 21 97777-2020', company: 'Vertex Energy', title: 'COO', source: 'Outbound', status: LeadStatus.PROPOSAL, score: 77, notes: 'Solicitou proposta enterprise.' },
      { id: 'lead_demo_3', tenantId: tenant.id, ownerId: 'user_sales_3', name: 'Lívia Moura', email: 'livia@nexa.com', phone: '+55 31 96666-3030', company: 'Nexa Health', title: 'Operations Manager', source: 'Referral', status: LeadStatus.NEW, score: 64, notes: 'Avaliar integrações futuras.' },
    ],
    skipDuplicates: true,
  });

  await prisma.account.createMany({
    data: [
      { id: 'account_demo_1', tenantId: tenant.id, ownerId: 'user_sales_1', name: 'Atlas Supply', segment: 'Distribuição', size: 'Mid-market', website: 'https://atlas.example.com', phone: '+55 11 4000-1001', address: 'São Paulo - SP', notes: 'Conta estratégica do sudeste.' },
      { id: 'account_demo_2', tenantId: tenant.id, ownerId: 'user_sales_2', name: 'Vertex Energy', segment: 'Energia', size: 'Enterprise', website: 'https://vertex.example.com', phone: '+55 21 4000-1002', address: 'Rio de Janeiro - RJ', notes: 'Operação nacional com alto potencial.' },
    ],
    skipDuplicates: true,
  });

  await prisma.contact.createMany({
    data: [
      { id: 'contact_demo_1', tenantId: tenant.id, accountId: 'account_demo_1', ownerId: 'user_sales_1', name: 'Marina Costa', email: 'marina@atlas.com', phone: '+55 11 99888-1010', title: 'Procurement Lead', linkedin: 'linkedin.com/in/marinacosta' },
      { id: 'contact_demo_2', tenantId: tenant.id, accountId: 'account_demo_2', ownerId: 'user_sales_2', name: 'Paulo Freitas', email: 'paulo@vertex.com', phone: '+55 21 97777-2020', title: 'COO', linkedin: 'linkedin.com/in/paulofreitas' },
    ],
    skipDuplicates: true,
  });

  await prisma.opportunity.createMany({
    data: [
      { id: 'opp_demo_1', tenantId: tenant.id, accountId: 'account_demo_1', contactId: 'contact_demo_1', ownerId: 'user_sales_1', title: 'Expansão regional', value: 180000, stage: OpportunityStage.QUALIFICATION, probability: 35, expectedCloseDate: new Date('2026-04-22'), source: 'Inbound', notes: 'Foco em 4 filiais', status: OpportunityStatus.OPEN },
      { id: 'opp_demo_2', tenantId: tenant.id, accountId: 'account_demo_2', contactId: 'contact_demo_2', ownerId: 'user_sales_2', title: 'Licenças enterprise', value: 320000, stage: OpportunityStage.PROPOSAL, probability: 60, expectedCloseDate: new Date('2026-04-28'), source: 'Outbound', notes: 'Proposta enviada', status: OpportunityStatus.OPEN },
      { id: 'opp_demo_3', tenantId: tenant.id, accountId: 'account_demo_2', contactId: 'contact_demo_2', ownerId: 'user_sales_3', title: 'Renovação anual', value: 540000, stage: OpportunityStage.NEGOTIATION, probability: 75, expectedCloseDate: new Date('2026-05-05'), source: 'CS Expansion', notes: 'Negociação final', status: OpportunityStatus.OPEN },
    ],
    skipDuplicates: true,
  });

  await prisma.activity.createMany({
    data: [
      { id: 'activity_demo_1', tenantId: tenant.id, ownerId: admin.id, accountId: 'account_demo_1', opportunityId: 'opp_demo_1', type: ActivityType.MEETING, title: 'Reunião de descoberta', description: 'Mapear processo comercial atual', dueDate: new Date('2026-04-10T15:00:00Z'), priority: ActivityPriority.HIGH, status: ActivityStatus.PENDING },
      { id: 'activity_demo_2', tenantId: tenant.id, ownerId: 'user_sales_2', opportunityId: 'opp_demo_2', type: ActivityType.FOLLOW_UP, title: 'Follow-up da proposta', dueDate: new Date('2026-04-11T12:00:00Z'), priority: ActivityPriority.MEDIUM, status: ActivityStatus.PENDING },
    ],
    skipDuplicates: true,
  });

  await prisma.ticket.createMany({
    data: [
      { id: 'ticket_demo_1', tenantId: tenant.id, accountId: 'account_demo_1', contactId: 'contact_demo_1', ownerId: admin.id, subject: 'Ajuste de regra de SLA', description: 'Equipe precisa rever janela de atendimento premium.', status: TicketStatus.OPEN, priority: TicketPriority.URGENT, category: 'SLA', queue: 'Suporte N1', internalNotes: 'Priorizar até o fim do dia.' },
      { id: 'ticket_demo_2', tenantId: tenant.id, accountId: 'account_demo_2', contactId: 'contact_demo_2', ownerId: admin.id, subject: 'Webhook Stripe não sincroniza', description: 'Eventos de billing não chegaram no ambiente de teste.', status: TicketStatus.PENDING, priority: TicketPriority.HIGH, category: 'Billing', queue: 'Finance Ops', internalNotes: 'Aguardar validação do endpoint.' },
    ],
    skipDuplicates: true,
  });

  await prisma.automationRule.createMany({
    data: [
      { id: 'automation_demo_1', tenantId: tenant.id, name: 'Lead qualified → criar oportunidade', triggerType: 'lead.status.changed', conditionsJson: { status: 'QUALIFIED' }, actionsJson: { createOpportunity: true }, active: true },
      { id: 'automation_demo_2', tenantId: tenant.id, name: 'SLA próximo → notificar gestor', triggerType: 'ticket.sla.warning', conditionsJson: { minutesBefore: 60 }, actionsJson: { notifyManager: true }, active: true },
    ],
    skipDuplicates: true,
  });

  await prisma.notification.createMany({
    data: [
      { id: 'notification_demo_1', tenantId: tenant.id, userId: admin.id, title: 'SLA crítico', message: 'O ticket ticket_demo_1 vence em 38 minutos.', type: 'sla', link: '/tickets' },
      { id: 'notification_demo_2', tenantId: tenant.id, userId: admin.id, title: 'Novo lead qualificado', message: 'Marina Costa avançou para QUALIFIED.', type: 'crm', link: '/leads' },
    ],
    skipDuplicates: true,
  });

  await prisma.auditLog.createMany({
    data: [
      { id: 'audit_demo_1', tenantId: tenant.id, userId: admin.id, action: 'LOGIN', entity: 'User', entityId: admin.id, metadata: { source: 'seed' }, ipAddress: '127.0.0.1' },
      { id: 'audit_demo_2', tenantId: tenant.id, userId: admin.id, action: 'UPDATE_STAGE', entity: 'Opportunity', entityId: 'opp_demo_2', metadata: { from: 'PROPOSAL', to: 'NEGOTIATION' }, ipAddress: '127.0.0.1' },
    ],
    skipDuplicates: true,
  });

  console.log('🌌 OrbitCRM seed finalizado com sucesso.');
}

main()
  .catch((error) => {
    console.error(error);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
