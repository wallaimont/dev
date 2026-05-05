package com.orbyt.marketplace.support.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AddMessageRequest(
    @NotBlank String message,
    boolean internal
) {}
