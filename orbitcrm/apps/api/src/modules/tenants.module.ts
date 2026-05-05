import { Body, Controller, Get, Module, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
class TenantsService {
  constructor(private readonly prisma: PrismaService) {}

  current(tenantId: string) {
    return this.prisma.tenant.findUnique({ where: { id: tenantId }, include: { plan: true, subscriptions: true } });
  }

  update(tenantId: string, body: { name?: string; primaryColor?: string; settings?: Record<string, unknown> }) {
    return this.prisma.tenant.update({ where: { id: tenantId }, data: body as any });
  }
}

@ApiTags('Tenants')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('tenants')
class TenantsController {
  constructor(private readonly tenantsService: TenantsService) {}

  @Get('current')
  current(@CurrentUser() user: { tenantId: string }) {
    return this.tenantsService.current(user.tenantId);
  }

  @Put('current')
  update(@CurrentUser() user: { tenantId: string }, @Body() body: { name?: string; primaryColor?: string; settings?: Record<string, unknown> }) {
    return this.tenantsService.update(user.tenantId, body);
  }
}

@Module({ controllers: [TenantsController], providers: [TenantsService] })
export class TenantsModule {}
