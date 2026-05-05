package com.orbyt.marketplace.catalog.api.dto;

import java.util.UUID;

public record StoreResponse(
        UUID id,
        UUID tenantId,
        UUID sellerId,
        String name,
        String slug,
        String description,
        String status
) {
}
