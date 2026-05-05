package com.nexus.modules.admin.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder public class DashboardKpiResponse {
    public BigDecimal totalGmv;
    public int totalOrders;
    public int totalSellers;
    public int totalBuyers;
    public int pendingSellers;
    public int pendingProducts;
}
