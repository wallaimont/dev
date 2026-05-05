package com.orbyt.marketplace.platform.api.dto;

import java.util.UUID;

public record TenantResponse(
        UUID id,
        String slug,
        String name,
        String status,
        String defaultLocale,
        String currencyCode,
        String primaryDomain
) {
}
