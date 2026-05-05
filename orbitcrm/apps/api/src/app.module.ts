import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { ThrottlerModule } from '@nestjs/throttler';
import { PrismaModule } from './prisma/prisma.module';
import { AuthModule } from './auth/auth.module';
import { UsersModule } from './users/users.module';
import { RolesModule } from './modules/roles.module';
import { TenantsModule } from './modules/tenants.module';
import { PermissionsModule } from './modules/permissions.module';
import { LeadsModule } from './modules/leads.module';
import { AccountsModule } from './modules/accounts.module';
import { ContactsModule } from './modules/contacts.module';
import { OpportunitiesModule } from './modules/opportunities.module';
import { ActivitiesModule } from './modules/activities.module';
import { TicketsModule } from './modules/tickets.module';
import { AutomationsModule } from './modules/automations.module';
import { ReportsModule } from './modules/reports.module';
import { AuditModule } from './modules/audit.module';
import { BillingModule } from './modules/billing.module';
import { NotificationsModule } from './modules/notifications.module';
import { UploadsModule } from './modules/uploads.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    ThrottlerModule.forRoot([{ ttl: 60_000, limit: 120 }]),
    PrismaModule,
    AuthModule,
    UsersModule,
    RolesModule,
    TenantsModule,
    PermissionsModule,
    LeadsModule,
    AccountsModule,
    ContactsModule,
    OpportunitiesModule,
    ActivitiesModule,
    TicketsModule,
    AutomationsModule,
    ReportsModule,
    AuditModule,
    BillingModule,
    NotificationsModule,
    UploadsModule,
  ],
})
export class AppModule {}
