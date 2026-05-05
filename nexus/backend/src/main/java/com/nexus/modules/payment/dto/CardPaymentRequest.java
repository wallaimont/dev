package com.nexus.modules.payment.dto;

import com.nexus.modules.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data public class CardPaymentRequest {
    public String cardToken;
    public int installments;
    public String holderName;
    public String billingZip;
}
