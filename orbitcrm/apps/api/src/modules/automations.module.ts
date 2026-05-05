import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsBoolean, IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateAutomationRuleDto {
  @IsString() name!: string;
  @IsString() triggerType!: string;
  @IsOptional() conditionsJson?: Record<string, unknown>;
  @IsOptional() actionsJson?: Record<string, unknown>;
  @IsOptional() @IsBoolean() active?: boolean;
}

@Injectable()
class AutomationsService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.automationRule.findMany({ where: { tenantId, deletedAt: null }, orderBy: { createdAt: 'desc' } });
  }

  create(tenantId: string, dto: CreateAutomationRuleDto) {
    return this.prisma.automationRule.create({
      data: {
        tenantId,
        name: dto.name,
        triggerType: dto.triggerType,
        conditionsJson: (dto.conditionsJson ?? {}) as any,
        actionsJson: (dto.actionsJson ?? {}) as any,
        active: dto.active ?? true,
      } as any,
    });
  }

  async getOne(tenantId: string, id: string) {
    const rule = await this.prisma.automationRule.findFirst({ where: { id, tenantId, deletedAt: null } });
    if (!rule) throw new NotFoundException('Regra não encontrada');
    return rule;
  }

  async update(id: string, tenantId: string, dto: CreateAutomationRuleDto) {
    await this.getOne(tenantId, id);
    return this.prisma.automationRule.update({ where: { id }, data: dto as any });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.automationRule.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }
}

@ApiTags('Automation Rules')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('automation-rules')
class AutomationsController {
  constructor(private readonly automationsService: AutomationsService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.automationsService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string }, @Body() dto: CreateAutomationRuleDto) {
    return this.automationsService.create(user.tenantId, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.automationsService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateAutomationRuleDto) {
    return this.automationsService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.automationsService.remove(id, user.tenantId);
  }
}

@Module({ controllers: [AutomationsController], providers: [AutomationsService] })
export class AutomationsModule {}
