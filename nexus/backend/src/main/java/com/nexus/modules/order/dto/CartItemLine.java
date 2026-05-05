package com.nexus.modules.order.dto;

import com.nexus.modules.order.domain.OrderGroupStatus;
import com.nexus.modules.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CartItemLine(UUID variantId, UUID productId, UUID storeId, UUID sellerId,
                    String productName, String sku, Map<String, String> attributes,
                    int quantity, BigDecimal unitPrice) {
    public CartItemLine(com.nexus.modules.cart.domain.CartItem item) {
        this(item.getVariantId(), item.getProductId(), null, item.getSellerId(),
             "", item.getSku(), null, item.getQuantity(), item.getUnitPrice());
    }
}
