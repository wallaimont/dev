package com.nexus.modules.review.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data public class CreateReviewRequest {
    @NotNull @Min(1) @Max(5) public Integer rating;
    public String title;
    public String comment;
    public UUID orderItemId;
}
