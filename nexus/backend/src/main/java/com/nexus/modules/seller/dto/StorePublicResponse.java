package com.nexus.modules.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class StorePublicResponse {
    public UUID id;
    public String slug;
    public String name;
    public String description;
    public String logoUrl;
    public String bannerUrl;
    public BigDecimal avgRating;
    public Integer totalRatings;
    public Integer totalSales;
}
