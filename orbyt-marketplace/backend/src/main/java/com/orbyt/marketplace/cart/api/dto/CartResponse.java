package com.orbyt.marketplace.cart.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID id,
    UUID userId,
    String currencyCode,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal shippingTotal,
    BigDecimal total,
    List<CartItemResponse> items
) {
    public record CartItemResponse(
        UUID id,
        UUID productId,
        UUID variantId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {}
}
