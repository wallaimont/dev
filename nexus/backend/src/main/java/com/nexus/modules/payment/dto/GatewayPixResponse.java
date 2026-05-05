package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data public class GatewayPixResponse {
    public String gateway;
    public String txId;
    public String qrCode;
    public String qrCodeImageUrl;
    public String pixCopyPaste;
    public Instant expiresAt;
}
