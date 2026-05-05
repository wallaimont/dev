package com.orbyt.marketplace.payment.api.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PixChargeRequest(
        @NotNull UUID orderGroupId,
        @NotNull UUID buyerId,
        @NotNull BigDecimal amount
) {}
