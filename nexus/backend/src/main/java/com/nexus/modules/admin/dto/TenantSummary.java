package com.nexus.modules.admin.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder public class TenantSummary {
    public UUID id;
    public String slug;
    public String name;
    public String status;
    public String plan;
    public Instant createdAt;
}
