import { Body, Controller, Delete, Get, Param, Post, Put, UseGuards, UseInterceptors } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { UsersService } from './users.service';

@ApiTags('Users')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get()
  list(@CurrentUser() user: { tenantId: string }) {
    return this.usersService.list(user.tenantId);
  }

  @Post()
  create(@CurrentUser() user: { tenantId: string }, @Body() body: { name: string; email: string; password?: string; title?: string }) {
    return this.usersService.create(user.tenantId, body);
  }

  @Put(':id')
  update(@Param('id') id: string, @CurrentUser() user: { tenantId: string }, @Body() body: Record<string, unknown>) {
    return this.usersService.update(id, user.tenantId, body);
  }

  @Delete(':id')
  remove(@Param('id') id: string, @CurrentUser() user: { tenantId: string }) {
    return this.usersService.remove(id, user.tenantId);
  }
}
