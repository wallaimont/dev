package com.orbyt.marketplace.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpsertStoreRequest(
        @NotNull UUID sellerId,
        @NotBlank String name,
        @NotBlank String slug,
        String description
) {
}
