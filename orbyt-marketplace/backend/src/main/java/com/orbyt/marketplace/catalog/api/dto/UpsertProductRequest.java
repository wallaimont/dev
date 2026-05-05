package com.orbyt.marketplace.catalog.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record UpsertProductRequest(
        @NotNull UUID storeId,
        @NotNull UUID categoryId,
        @NotBlank String sku,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price,
        BigDecimal promotionalPrice,
        @NotBlank String currencyCode
) {
}
