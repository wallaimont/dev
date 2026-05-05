package com.nexus.modules.review.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class ReviewResponse {
    public UUID id;
    public UUID productId;
    public UUID buyerId;
    public String buyerName;
    public Integer rating;
    public String title;
    public String comment;
    public int helpfulCount;
    public Instant createdAt;
}
