package com.orbyt.marketplace.identity.api.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID tenantId,
        String email,
        String fullName,
        String preferredLocale,
        String preferredCurrency,
        String status
) {
}
