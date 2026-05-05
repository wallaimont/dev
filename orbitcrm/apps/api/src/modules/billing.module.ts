import { Body, Controller, Get, Module, Post, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
class BillingService {
  constructor(private readonly prisma: PrismaService) {}

  plans() {
    return this.prisma.plan.findMany({ orderBy: { priceCents: 'asc' } });
  }

  subscription(tenantId: string) {
    return this.prisma.subscription.findFirst({ where: { tenantId }, include: { plan: true } });
  }

  checkoutSession(tenantId: string, body: { planCode: string }) {
    return {
      tenantId,
      provider: 'stripe',
      planCode: body.planCode,
      status: 'prepared',
      message: 'Integração pronta para conectar Stripe Checkout e webhooks.',
    };
  }
}

@ApiTags('Billing')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('billing')
class BillingController {
  constructor(private readonly billingService: BillingService) {}

  @Get('plans')
  plans() {
    return this.billingService.plans();
  }

  @Get('subscription')
  subscription(@CurrentUser() user: { tenantId: string }) {
    return this.billingService.subscription(user.tenantId);
  }

  @Post('checkout-session')
  checkoutSession(@CurrentUser() user: { tenantId: string }, @Body() body: { planCode: string }) {
    return this.billingService.checkoutSession(user.tenantId, body);
  }
}

@Module({ controllers: [BillingController], providers: [BillingService] })
export class BillingModule {}
