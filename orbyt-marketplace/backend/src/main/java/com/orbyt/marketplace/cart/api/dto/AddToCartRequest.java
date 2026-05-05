package com.orbyt.marketplace.cart.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddToCartRequest(
    @NotNull UUID productId,
    UUID variantId,
    @Min(1) int quantity
) {}
