package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class RefundResponse {
    public UUID refundId;
    public String status;
    public BigDecimal amount;
}
