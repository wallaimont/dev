import { ConflictException, Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { compare, hash } from 'bcryptjs';
import { PrismaService } from '../prisma/prisma.service';
import { ForgotPasswordDto, LoginDto, RefreshDto, RegisterDto, ResetPasswordDto } from './dto/auth.dto';

@Injectable()
export class AuthService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly jwtService: JwtService,
  ) {}

  async register(dto: RegisterDto) {
    const existing = await this.prisma.user.findUnique({ where: { email: dto.email } });
    if (existing) throw new ConflictException('E-mail já cadastrado');

    const slug = (dto.companySlug ?? dto.companyName)
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '');

    const plan = await this.prisma.plan.findFirst({ where: { code: (dto.planCode as any) || 'STARTER' } });
    const tenant = await this.prisma.tenant.create({
      data: {
        name: dto.companyName,
        slug,
        primaryColor: '#0ea5e9',
        planId: plan?.id,
      },
    });

    const role = await this.prisma.role.create({
      data: {
        tenantId: tenant.id,
        name: 'Admin',
        description: 'Administrador do tenant',
        isSystem: true,
      },
    });

    const user = await this.prisma.user.create({
      data: {
        tenantId: tenant.id,
        roleId: role.id,
        name: dto.name,
        email: dto.email,
        passwordHash: await hash(dto.password, 10),
        status: 'ACTIVE',
        title: 'Owner',
      },
    });

    return this.issueTokens(user.id, user.email, user.tenantId, ['manage:all'], user.name);
  }

  async login(dto: LoginDto) {
    const user = await this.prisma.user.findFirst({
      where: { email: dto.email, deletedAt: null },
      include: { role: { include: { permissions: { include: { permission: true } } } } },
    });

    if (!user || !(await compare(dto.password, user.passwordHash))) {
      throw new UnauthorizedException('Credenciais inválidas');
    }

    const permissions = user.role?.permissions.map((item) => item.permission.code) ?? [];
    await this.prisma.user.update({ where: { id: user.id }, data: { lastLoginAt: new Date() } });

    return this.issueTokens(user.id, user.email, user.tenantId, permissions, user.name);
  }

  async refresh(dto: RefreshDto) {
    try {
      const payload = await this.jwtService.verifyAsync(dto.refreshToken, {
        secret: process.env.JWT_REFRESH_SECRET ?? 'super-secret-refresh-token',
      });

      const user = await this.prisma.user.findUnique({ where: { id: payload.sub } });
      if (!user?.refreshTokenHash) throw new UnauthorizedException();

      const valid = await compare(dto.refreshToken, user.refreshTokenHash);
      if (!valid) throw new UnauthorizedException();

      return this.issueTokens(user.id, user.email, user.tenantId, payload.permissions ?? []);
    } catch {
      throw new UnauthorizedException('Refresh token inválido');
    }
  }

  async forgotPassword(dto: ForgotPasswordDto) {
    return {
      message: `Se o e-mail ${dto.email} existir, enviaremos um link de recuperação.`,
      provider: process.env.EMAIL_PROVIDER ?? 'resend',
    };
  }

  async resetPassword(_: ResetPasswordDto) {
    return { message: 'Fluxo de redefinição preparado para token seguro.' };
  }

  async me(user: { sub: string; email: string; tenantId: string; permissions: string[] }) {
    const profile = await this.prisma.user.findUnique({
      where: { id: user.sub },
      select: { id: true, name: true, email: true, tenantId: true, title: true },
    });

    return {
      id: profile?.id ?? user.sub,
      name: profile?.name ?? 'Orbit User',
      email: profile?.email ?? user.email,
      tenantId: profile?.tenantId ?? user.tenantId,
      title: profile?.title ?? null,
      permissions: user.permissions,
    };
  }

  async logout(userId: string) {
    await this.prisma.user.update({ where: { id: userId }, data: { refreshTokenHash: null } });
    return { success: true };
  }

  private async issueTokens(userId: string, email: string, tenantId: string, permissions: string[], name?: string) {
    const payload = { sub: userId, email, tenantId, permissions };
    const accessToken = await this.jwtService.signAsync(payload, {
      secret: process.env.JWT_SECRET ?? 'super-secret-access-token',
      expiresIn: process.env.ACCESS_TOKEN_EXPIRES_IN ?? '15m',
    });
    const refreshToken = await this.jwtService.signAsync(payload, {
      secret: process.env.JWT_REFRESH_SECRET ?? 'super-secret-refresh-token',
      expiresIn: process.env.REFRESH_TOKEN_EXPIRES_IN ?? '7d',
    });

    await this.prisma.user.update({
      where: { id: userId },
      data: { refreshTokenHash: await hash(refreshToken, 10) },
    });

    return {
      accessToken,
      refreshToken,
      user: { id: userId, name, email, tenantId, permissions },
    };
  }
}
