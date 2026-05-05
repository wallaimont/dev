package com.nexus.modules.order.dto;

import com.nexus.modules.order.domain.OrderGroupStatus;
import com.nexus.modules.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder public class OrderSummary {
    public UUID id;
    public String orderNumber;
    public OrderStatus status;
    public BigDecimal total;
    public int itemCount;
    public Instant createdAt;
}
