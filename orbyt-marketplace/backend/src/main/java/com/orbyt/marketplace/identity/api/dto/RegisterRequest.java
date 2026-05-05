package com.orbyt.marketplace.identity.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String tenantSlug,
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @Size(min = 8, max = 72) String password
) {
}
