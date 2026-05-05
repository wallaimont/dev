package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class PixChargeResponse {
    public UUID    paymentId;
    public String  txid;
    public String  qrCode;
    public String  qrCodeUrl;
    public String  pixCopyPaste;
    public BigDecimal amount;
    public PaymentStatus status;
    public Instant expiresAt;
}
