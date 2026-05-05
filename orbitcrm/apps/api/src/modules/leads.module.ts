import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Post, Put, Query, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { Injectable } from '@nestjs/common';
import { IsEmail, IsInt, IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateLeadDto {
  @IsString() name!: string;
  @IsOptional() @IsEmail() email?: string;
  @IsOptional() @IsString() phone?: string;
  @IsOptional() @IsString() company?: string;
  @IsOptional() @IsString() title?: string;
  @IsOptional() @IsString() source?: string;
  @IsOptional() @IsString() status?: string;
  @IsOptional() @IsInt() score?: number;
  @IsOptional() @IsString() notes?: string;
}

@Injectable()
class LeadsService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string, search?: string) {
    return this.prisma.lead.findMany({
      where: {
        tenantId,
        deletedAt: null,
        ...(search ? {
          OR: [
            { name: { contains: search, mode: 'insensitive' } },
            { company: { contains: search, mode: 'insensitive' } },
            { email: { contains: search, mode: 'insensitive' } },
          ],
        } : {}),
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  create(tenantId: string, ownerId: string, dto: CreateLeadDto) {
    return this.prisma.lead.create({ data: { tenantId, ownerId, ...dto, status: dto.status ?? 'NEW', score: dto.score ?? 0 } as any });
  }

  async getOne(tenantId: string, id: string) {
    const lead = await this.prisma.lead.findFirst({ where: { id, tenantId, deletedAt: null } });
    if (!lead) throw new NotFoundException('Lead não encontrado');
    return lead;
  }

  async update(id: string, tenantId: string, dto: CreateLeadDto) {
    await this.getOne(tenantId, id);
    return this.prisma.lead.update({ where: { id }, data: dto as any });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.lead.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }

  async convert(id: string, tenantId: string, ownerId: string) {
    const lead = await this.getOne(tenantId, id);

    return this.prisma.$transaction(async (tx) => {
      const account = await tx.account.create({
        data: {
          tenantId,
          ownerId,
          name: lead.company ?? `${lead.name} Company`,
          phone: lead.phone,
          notes: lead.notes,
        },
      });

      const contact = await tx.contact.create({
        data: {
          tenantId,
          accountId: account.id,
          ownerId,
          name: lead.name,
          email: lead.email,
          phone: lead.phone,
          title: lead.title,
          notes: lead.notes,
        },
      });

      const opportunity = await tx.opportunity.create({
        data: {
          tenantId,
          accountId: account.id,
          contactId: contact.id,
          ownerId,
          title: `Nova oportunidade • ${lead.company ?? lead.name}`,
          value: 0,
          stage: 'QUALIFICATION',
          probability: 25,
          source: lead.source,
          notes: lead.notes,
          status: 'OPEN',
        },
      });

      await tx.lead.update({ where: { id }, data: { status: 'QUALIFIED' } });
      return { account, contact, opportunity };
    });
  }
}

@ApiTags('Leads')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('leads')
class LeadsController {
  constructor(private readonly leadsService: LeadsService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }, @Query('search') search?: string) {
    return this.leadsService.list(user.tenantId, search);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string; sub: string }, @Body() dto: CreateLeadDto) {
    return this.leadsService.create(user.tenantId, user.sub, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.leadsService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateLeadDto) {
    return this.leadsService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.leadsService.remove(id, user.tenantId);
  }

  @Post(':id/convert')
  convert(@CurrentUser() user: { tenantId: string; sub: string }, @Param('id') id: string) {
    return this.leadsService.convert(id, user.tenantId, user.sub);
  }
}

@Module({
  controllers: [LeadsController],
  providers: [LeadsService],
})
export class LeadsModule {}
