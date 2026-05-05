package com.orbyt.marketplace.payment.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PixChargeResponse(
        UUID paymentId,
        UUID pixChargeId,
        String qrCode,
        String copyPasteCode,
        OffsetDateTime expiresAt,
        String status
) {
}
