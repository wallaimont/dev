import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsEmail, IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateContactDto {
  @IsOptional() @IsString() accountId?: string;
  @IsString() name!: string;
  @IsOptional() @IsEmail() email?: string;
  @IsOptional() @IsString() phone?: string;
  @IsOptional() @IsString() title?: string;
  @IsOptional() @IsString() linkedin?: string;
  @IsOptional() @IsString() notes?: string;
}

@Injectable()
class ContactsService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.contact.findMany({ where: { tenantId, deletedAt: null }, include: { account: true }, orderBy: { createdAt: 'desc' } });
  }

  create(tenantId: string, ownerId: string, dto: CreateContactDto) {
    return this.prisma.contact.create({ data: { tenantId, ownerId, ...dto } });
  }

  async getOne(tenantId: string, id: string) {
    const contact = await this.prisma.contact.findFirst({ where: { id, tenantId, deletedAt: null }, include: { account: true } });
    if (!contact) throw new NotFoundException('Contato não encontrado');
    return contact;
  }

  async update(id: string, tenantId: string, dto: CreateContactDto) {
    await this.getOne(tenantId, id);
    return this.prisma.contact.update({ where: { id }, data: dto });
  }

  async remove(id: string, tenantId: string) {
    await this.getOne(tenantId, id);
    await this.prisma.contact.update({ where: { id }, data: { deletedAt: new Date() } });
    return { success: true };
  }
}

@ApiTags('Contacts')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('contacts')
class ContactsController {
  constructor(private readonly contactsService: ContactsService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.contactsService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string; sub: string }, @Body() dto: CreateContactDto) {
    return this.contactsService.create(user.tenantId, user.sub, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.contactsService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateContactDto) {
    return this.contactsService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.contactsService.remove(id, user.tenantId);
  }
}

@Module({ controllers: [ContactsController], providers: [ContactsService] })
export class ContactsModule {}
