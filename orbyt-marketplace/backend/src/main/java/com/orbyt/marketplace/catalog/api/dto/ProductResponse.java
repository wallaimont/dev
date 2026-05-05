package com.orbyt.marketplace.catalog.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID storeId,
        UUID categoryId,
        String name,
        String sku,
        BigDecimal price,
        BigDecimal promotionalPrice,
        String currencyCode,
        String approvalStatus
) {
}
