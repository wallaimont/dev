package com.orbyt.marketplace.platform.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TenantUpsertRequest(
        @NotBlank String slug,
        @NotBlank String name,
        @NotBlank String defaultLocale,
        @NotBlank String currencyCode,
        String primaryDomain
) {
}
