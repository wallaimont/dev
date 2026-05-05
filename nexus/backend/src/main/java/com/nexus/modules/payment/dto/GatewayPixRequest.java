package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder @Data public class GatewayPixRequest {
    public String txId;
    public BigDecimal amount;
    public String payerName;
    public String payerCpf;
    public int expiresInSeconds;
    public String description;
}
