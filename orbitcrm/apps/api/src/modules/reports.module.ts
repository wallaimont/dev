import { Controller, Get, Module, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
class ReportsService {
  constructor(private readonly prisma: PrismaService) {}

  async overview(tenantId: string) {
    const [totalLeads, totalAccounts, totalOpportunities, openTickets] = await Promise.all([
      this.prisma.lead.count({ where: { tenantId, deletedAt: null } }),
      this.prisma.account.count({ where: { tenantId, deletedAt: null } }),
      this.prisma.opportunity.count({ where: { tenantId, deletedAt: null } }),
      this.prisma.ticket.count({ where: { tenantId, deletedAt: null, status: 'OPEN' } }),
    ]);

    return {
      totalLeads,
      totalAccounts,
      totalOpportunities,
      openTickets,
    };
  }

  sales() {
    return {
      months: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun'],
      values: [92000, 116000, 138000, 174000, 201000, 229000],
    };
  }

  conversion() {
    return { qualification: 48, proposal: 29, negotiation: 21, won: 12 };
  }

  tickets() {
    return { open: 7, pending: 4, resolved: 18, urgent: 2 };
  }

  teamPerformance() {
    return [
      { user: 'Rafa Sales', score: 92 },
      { user: 'Bia Lopes', score: 88 },
      { user: 'Carlos Neri', score: 83 },
    ];
  }
}

@ApiTags('Reports')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('reports')
class ReportsController {
  constructor(private readonly reportsService: ReportsService) {}

  @Get('overview')
  overview(@CurrentUser() user: { tenantId: string }) {
    return this.reportsService.overview(user.tenantId);
  }

  @Get('sales')
  sales() {
    return this.reportsService.sales();
  }

  @Get('conversion')
  conversion() {
    return this.reportsService.conversion();
  }

  @Get('tickets')
  tickets() {
    return this.reportsService.tickets();
  }

  @Get('team-performance')
  teamPerformance() {
    return this.reportsService.teamPerformance();
  }
}

@Module({ controllers: [ReportsController], providers: [ReportsService] })
export class ReportsModule {}
