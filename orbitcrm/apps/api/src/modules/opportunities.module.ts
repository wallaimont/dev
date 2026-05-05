import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Patch, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsInt, IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateOpportunityDto {
  @IsOptional() @IsString() accountId?: string;
  @IsOptional() @IsString() contactId?: string;
  @IsString() title!: string;
  @IsOptional() value?: number;
  @IsOptional() @IsString() stage?: string;
  @IsOptional() @IsInt() probability?: number;
  @IsOptional() expectedCloseDate?: string;
  @IsOptional() @IsString() source?: string;
  @IsOptional() @IsString() notes?: string;
  @IsOptional() @IsString() status?: string;
}

@Injectable()
class OpportunitiesService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.opportunity.findMany({ where: { tenantId, deletedAt: null }, include: { account: true, contact: true }, orderBy: { createdAt: 'desc' } });
  }

  create(tenantId: string, ownerId: string, dto: CreateOpportunityDto) {
    return this.prisma.opportunity.create({
      data: {
        tenantId,
        ownerId,
        accountId: dto.accountId,
        contactId: dto.contactId,
        title: dto.title,
        value: Number(dto.value ?? 0),
        stage: dto.stage ?? 'QUALIFICATION',
        probability: dto.probability ?? 25,
        expectedCloseDate: dto.expectedCloseDate ? new Date(dto.expectedCloseDate) : null,
        source: dto.source,
        notes: dto.notes,
        status: dto.status ?? 'OPEN',
      } as any,
    });
  }

  async getOne(tenantId: string, id: string) {
    const opportunity = await this.prisma.opportunity.findFirst({ where: { id, tenantId, deletedAt: null }, include: { account: true, contact: true } });
    if (!opportunity) throw new NotFoundException('Oportunidade não encontrada');
    return opportunity;
  }

  async update(id: string, tenantId: string, dto: CreateOpportunityDto) {
    await this.getOne(tenantId, id);
    return this.prisma.opportunity.update({
      where: { id },
      data: {
        ...dto,
        value: dto.value !== undefined ? Number(dto.value) : undefined,
        expectedCloseDate: dto.expectedCloseDate ? new Date(dto.expectedCloseDate) : undefined,
      } as any,
    });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.opportunity.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }

  async updateStage(id: string, tenantId: string, stage: string) {
    await this.getOne(tenantId, id);
    return this.prisma.opportunity.update({ where: { id }, data: { stage } as any });
  }
}

@ApiTags('Opportunities')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('opportunities')
class OpportunitiesController {
  constructor(private readonly opportunitiesService: OpportunitiesService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.opportunitiesService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string; sub: string }, @Body() dto: CreateOpportunityDto) {
    return this.opportunitiesService.create(user.tenantId, user.sub, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.opportunitiesService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateOpportunityDto) {
    return this.opportunitiesService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.opportunitiesService.remove(id, user.tenantId);
  }

  @Patch(':id/stage')
  updateStage(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() body: { stage: string }) {
    return this.opportunitiesService.updateStage(id, user.tenantId, body.stage);
  }
}

@Module({ controllers: [OpportunitiesController], providers: [OpportunitiesService] })
export class OpportunitiesModule {}
