import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateActivityDto {
  @IsString() title!: string;
  @IsOptional() @IsString() type?: string;
  @IsOptional() @IsString() description?: string;
  @IsOptional() dueDate?: string;
  @IsOptional() @IsString() priority?: string;
  @IsOptional() @IsString() status?: string;
  @IsOptional() @IsString() leadId?: string;
  @IsOptional() @IsString() accountId?: string;
  @IsOptional() @IsString() contactId?: string;
  @IsOptional() @IsString() opportunityId?: string;
}

@Injectable()
class ActivitiesService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.activity.findMany({ where: { tenantId, deletedAt: null }, orderBy: { dueDate: 'asc' } });
  }

  create(tenantId: string, ownerId: string, dto: CreateActivityDto) {
    return this.prisma.activity.create({
      data: {
        tenantId,
        ownerId,
        ...dto,
        type: dto.type ?? 'TASK',
        priority: dto.priority ?? 'MEDIUM',
        status: dto.status ?? 'PENDING',
        dueDate: dto.dueDate ? new Date(dto.dueDate) : null,
      } as any,
    });
  }

  async getOne(tenantId: string, id: string) {
    const activity = await this.prisma.activity.findFirst({ where: { id, tenantId, deletedAt: null } });
    if (!activity) throw new NotFoundException('Atividade não encontrada');
    return activity;
  }

  async update(id: string, tenantId: string, dto: CreateActivityDto) {
    await this.getOne(tenantId, id);
    return this.prisma.activity.update({
      where: { id },
      data: { ...dto, dueDate: dto.dueDate ? new Date(dto.dueDate) : undefined } as any,
    });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.activity.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }
}

@ApiTags('Activities')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('activities')
class ActivitiesController {
  constructor(private readonly activitiesService: ActivitiesService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.activitiesService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string; sub: string }, @Body() dto: CreateActivityDto) {
    return this.activitiesService.create(user.tenantId, user.sub, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.activitiesService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateActivityDto) {
    return this.activitiesService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.activitiesService.remove(id, user.tenantId);
  }
}

@Module({ controllers: [ActivitiesController], providers: [ActivitiesService] })
export class ActivitiesModule {}
