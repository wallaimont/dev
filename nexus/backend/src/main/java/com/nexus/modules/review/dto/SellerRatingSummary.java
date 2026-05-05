package com.nexus.modules.review.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class SellerRatingSummary {
    public UUID sellerId;
    public BigDecimal avgRating;
    public int totalReviews;
    public int fiveStars;
    public int fourStars;
    public int threeStars;
    public int twoStars;
    public int oneStar;
}
