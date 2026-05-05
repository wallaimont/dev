package com.orbyt.marketplace.support.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateTicketRequest(
    @NotBlank String subject,
    String description,
    String category,
    String priority,
    UUID orderId
) {}
