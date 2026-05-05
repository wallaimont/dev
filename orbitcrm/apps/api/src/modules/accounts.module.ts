import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateAccountDto {
  @IsString() name!: string;
  @IsOptional() @IsString() segment?: string;
  @IsOptional() @IsString() size?: string;
  @IsOptional() @IsString() website?: string;
  @IsOptional() @IsString() phone?: string;
  @IsOptional() @IsString() address?: string;
  @IsOptional() @IsString() notes?: string;
}

@Injectable()
class AccountsService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.account.findMany({ where: { tenantId, deletedAt: null }, orderBy: { createdAt: 'desc' } });
  }

  create(tenantId: string, ownerId: string, dto: CreateAccountDto) {
    return this.prisma.account.create({ data: { tenantId, ownerId, ...dto } });
  }

  async getOne(tenantId: string, id: string) {
    const account = await this.prisma.account.findFirst({ where: { id, tenantId, deletedAt: null }, include: { contacts: true, opportunities: true } });
    if (!account) throw new NotFoundException('Conta não encontrada');
    return account;
  }

  async update(id: string, tenantId: string, dto: CreateAccountDto) {
    await this.getOne(tenantId, id);
    return this.prisma.account.update({ where: { id }, data: dto });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.account.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }
}

@ApiTags('Accounts')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('accounts')
class AccountsController {
  constructor(private readonly accountsService: AccountsService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.accountsService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string; sub: string }, @Body() dto: CreateAccountDto) {
    return this.accountsService.create(user.tenantId, user.sub, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.accountsService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateAccountDto) {
    return this.accountsService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.accountsService.remove(id, user.tenantId);
  }
}

@Module({ controllers: [AccountsController], providers: [AccountsService] })
export class AccountsModule {}
