package com.orbyt.marketplace.engagement.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
    @NotBlank String content,
    String messageType
) {}
