package com.orbyt.marketplace.identity.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String tenantSlug,
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
