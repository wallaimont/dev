package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class CardPaymentResponse {
    public UUID paymentId;
    public PaymentStatus status;
    public String authCode;
    public String last4;
}
