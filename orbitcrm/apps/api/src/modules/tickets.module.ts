import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Patch, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateTicketDto {
  @IsString() subject!: string;
  @IsOptional() @IsString() description?: string;
  @IsOptional() @IsString() status?: string;
  @IsOptional() @IsString() priority?: string;
  @IsOptional() @IsString() category?: string;
  @IsOptional() @IsString() queue?: string;
  @IsOptional() @IsString() accountId?: string;
  @IsOptional() @IsString() contactId?: string;
  @IsOptional() @IsString() internalNotes?: string;
}

@Injectable()
class TicketsService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.ticket.findMany({ where: { tenantId, deletedAt: null }, orderBy: { createdAt: 'desc' } });
  }

  create(tenantId: string, ownerId: string, dto: CreateTicketDto) {
    return this.prisma.ticket.create({
      data: {
        tenantId,
        ownerId,
        ...dto,
        status: dto.status ?? 'OPEN',
        priority: dto.priority ?? 'MEDIUM',
      } as any,
    });
  }

  async getOne(tenantId: string, id: string) {
    const ticket = await this.prisma.ticket.findFirst({ where: { id, tenantId, deletedAt: null } });
    if (!ticket) throw new NotFoundException('Ticket não encontrado');
    return ticket;
  }

  async update(id: string, tenantId: string, dto: CreateTicketDto) {
    await this.getOne(tenantId, id);
    return this.prisma.ticket.update({ where: { id }, data: dto as any });
  }

  async updateAssign(id: string, tenantId: string, ownerId: string) {
    await this.getOne(tenantId, id);
    return this.prisma.ticket.update({ where: { id }, data: { ownerId } });
  }

  async updateStatus(id: string, tenantId: string, status: string) {
    await this.getOne(tenantId, id);
    return this.prisma.ticket.update({ where: { id }, data: { status } as any });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.ticket.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }
}

@ApiTags('Tickets')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('tickets')
class TicketsController {
  constructor(private readonly ticketsService: TicketsService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.ticketsService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string; sub: string }, @Body() dto: CreateTicketDto) {
    return this.ticketsService.create(user.tenantId, user.sub, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.ticketsService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateTicketDto) {
    return this.ticketsService.update(id, user.tenantId, dto);
  }

  @Patch(':id/assign')
  assign(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() body: { ownerId: string }) {
    return this.ticketsService.updateAssign(id, user.tenantId, body.ownerId);
  }

  @Patch(':id/status')
  status(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() body: { status: string }) {
    return this.ticketsService.updateStatus(id, user.tenantId, body.status);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.ticketsService.remove(id, user.tenantId);
  }
}

@Module({ controllers: [TicketsController], providers: [TicketsService] })
export class TicketsModule {}
