package com.nexus.modules.admin.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder public class GmvResponse {
    public BigDecimal totalGmv;
    public List<GmvDataPoint> dataPoints;

    @Data @Builder public static class GmvDataPoint {
        public String date;
        public BigDecimal amount;
    }
}
