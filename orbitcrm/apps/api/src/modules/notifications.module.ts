import { Controller, Get, Module, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
class NotificationsService {
  constructor(private readonly prisma: PrismaService) {}

  list(userId: string, tenantId: string) {
    return this.prisma.notification.findMany({ where: { userId, tenantId }, orderBy: { createdAt: 'desc' }, take: 50 });
  }
}

@ApiTags('Notifications')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('notifications')
class NotificationsController {
  constructor(private readonly notificationsService: NotificationsService) {}

  @Get()
  list(@CurrentUser() user: { sub: string; tenantId: string }) {
    return this.notificationsService.list(user.sub, user.tenantId);
  }
}

@Module({ controllers: [NotificationsController], providers: [NotificationsService] })
export class NotificationsModule {}
