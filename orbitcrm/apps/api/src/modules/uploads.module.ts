import { Body, Controller, Module, Post, UseGuards } from '@nestjs/common';
import { Injectable } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';

@Injectable()
class UploadsService {
  presign(tenantId: string, body: { fileName: string; contentType?: string }) {
    const bucket = process.env.S3_BUCKET ?? 'orbitcrm';
    return {
      tenantId,
      bucket,
      fileName: body.fileName,
      contentType: body.contentType ?? 'application/octet-stream',
      uploadUrl: `${process.env.S3_ENDPOINT ?? 'http://localhost:9000'}/${bucket}/${tenantId}/${body.fileName}`,
      mode: 's3-compatible',
    };
  }
}

@ApiTags('Uploads')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('uploads')
class UploadsController {
  constructor(private readonly uploadsService: UploadsService) {}

  @Post('presign')
  presign(@CurrentUser() user: { tenantId: string }, @Body() body: { fileName: string; contentType?: string }) {
    return this.uploadsService.presign(user.tenantId, body);
  }
}

@Module({ controllers: [UploadsController], providers: [UploadsService] })
export class UploadsModule {}
