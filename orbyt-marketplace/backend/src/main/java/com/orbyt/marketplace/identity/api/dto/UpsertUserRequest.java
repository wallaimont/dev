package com.orbyt.marketplace.identity.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertUserRequest(
        @Email @NotBlank String email,
        @NotBlank String fullName,
        @Size(min = 8, max = 72) String password,
        @NotBlank String preferredLocale,
        @NotBlank String preferredCurrency
) {
}
