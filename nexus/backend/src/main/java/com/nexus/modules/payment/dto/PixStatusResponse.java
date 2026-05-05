package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class PixStatusResponse {
    public String txid;
    public PaymentStatus status;
    public Instant paidAt;
}
