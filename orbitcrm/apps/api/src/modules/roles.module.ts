import { Body, Controller, Delete, Get, Module, NotFoundException, Param, Post, Put, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { IsBoolean, IsOptional, IsString } from 'class-validator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

class CreateRoleDto {
  @IsString() name!: string;
  @IsOptional() @IsString() description?: string;
  @IsOptional() permissionCodes?: string[];
  @IsOptional() @IsBoolean() isSystem?: boolean;
}

@Injectable()
class RolesService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.role.findMany({
      where: { OR: [{ tenantId }, { tenantId: null }] },
      include: { permissions: { include: { permission: true } } },
      orderBy: { name: 'asc' },
    });
  }

  async create(tenantId: string, dto: CreateRoleDto) {
    const role = await this.prisma.role.create({
      data: {
        tenantId,
        name: dto.name,
        description: dto.description,
        isSystem: dto.isSystem ?? false,
      },
    });

    if (dto.permissionCodes?.length) {
      const permissions = await this.prisma.permission.findMany({ where: { code: { in: dto.permissionCodes } } });
      await this.prisma.rolePermission.createMany({
        data: permissions.map((permission) => ({ roleId: role.id, permissionId: permission.id })),
        skipDuplicates: true,
      });
    }

    return this.getOne(tenantId, role.id);
  }

  async getOne(tenantId: string, id: string) {
    const role = await this.prisma.role.findFirst({
      where: { id, OR: [{ tenantId }, { tenantId: null }] },
      include: { permissions: { include: { permission: true } } },
    });
    if (!role) throw new NotFoundException('Perfil não encontrado');
    return role;
  }

  async update(id: string, tenantId: string, dto: CreateRoleDto) {
    await this.getOne(tenantId, id);
    await this.prisma.role.update({ where: { id }, data: { name: dto.name, description: dto.description, isSystem: dto.isSystem } });

    if (dto.permissionCodes) {
      await this.prisma.rolePermission.deleteMany({ where: { roleId: id } });
      const permissions = await this.prisma.permission.findMany({ where: { code: { in: dto.permissionCodes } } });
      await this.prisma.rolePermission.createMany({
        data: permissions.map((permission) => ({ roleId: id, permissionId: permission.id })),
        skipDuplicates: true,
      });
    }

    return this.getOne(tenantId, id);
  }

  async remove(id: string, tenantId: string) {
    const role = await this.getOne(tenantId, id);
    if (role.isSystem) {
      return { success: false, message: 'Perfis de sistema não podem ser removidos.' };
    }
    await this.prisma.role.delete({ where: { id } });
    return { success: true };
  }
}

@ApiTags('Roles')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('roles')
class RolesController {
  constructor(private readonly rolesService: RolesService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.rolesService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string }, @Body() dto: CreateRoleDto) {
    return this.rolesService.create(user.tenantId, dto);
  }

  @Get(':id')
  getOne(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.rolesService.getOne(user.tenantId, id);
  }

  @Put(':id')
  update(@CurrentUser() user: { tenantId: string }, @Param('id') id: string, @Body() dto: CreateRoleDto) {
    return this.rolesService.update(id, user.tenantId, dto);
  }

  @Delete(':id')
  remove(@CurrentUser() user: { tenantId: string }, @Param('id') id: string) {
    return this.rolesService.remove(id, user.tenantId);
  }
}

@Module({ controllers: [RolesController], providers: [RolesService] })
export class RolesModule {}
