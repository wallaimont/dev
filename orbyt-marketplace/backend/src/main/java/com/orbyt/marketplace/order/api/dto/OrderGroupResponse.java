package com.orbyt.marketplace.order.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderGroupResponse(
    UUID id,
    UUID buyerId,
    String currencyCode,
    BigDecimal subtotal,
    BigDecimal shippingTotal,
    BigDecimal discountTotal,
    BigDecimal grandTotal,
    String paymentMethod,
    String status,
    List<OrderResponse> orders,
    String createdAt
) {
    public record OrderResponse(
        UUID id,
        UUID sellerId,
        UUID storeId,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal total,
        BigDecimal commissionAmount,
        BigDecimal sellerNet,
        String status,
        String trackingCode,
        String shippingMethod,
        List<OrderItemResponse> items
    ) {}

    public record OrderItemResponse(
        UUID id,
        UUID productId,
        String sku,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {}
}
