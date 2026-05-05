import { Injectable } from '@nestjs/common';
import { hash } from 'bcryptjs';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class UsersService {
  constructor(private readonly prisma: PrismaService) {}

  list(tenantId: string) {
    return this.prisma.user.findMany({
      where: { tenantId, deletedAt: null },
      select: { id: true, name: true, email: true, title: true, status: true, lastLoginAt: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async create(tenantId: string, data: { name: string; email: string; password?: string; title?: string }) {
    return this.prisma.user.create({
      data: {
        tenantId,
        name: data.name,
        email: data.email,
        title: data.title ?? 'User',
        status: 'ACTIVE',
        passwordHash: await hash(data.password ?? 'Temp@1234', 10),
      },
    });
  }

  update(id: string, tenantId: string, data: Record<string, unknown>) {
    return this.prisma.user.update({ where: { id, tenantId }, data });
  }

  async remove(id: string, tenantId: string) {
    await this.prisma.user.update({ where: { id, tenantId }, data: { deletedAt: new Date() } });
    return { success: true };
  }
}
