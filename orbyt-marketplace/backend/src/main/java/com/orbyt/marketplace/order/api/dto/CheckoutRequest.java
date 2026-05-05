package com.orbyt.marketplace.order.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutRequest(
        @NotBlank String tenantSlug,
        @NotNull UUID cartId,
        @NotNull UUID shippingAddressId,
        @NotBlank String paymentMethod
) {
}
