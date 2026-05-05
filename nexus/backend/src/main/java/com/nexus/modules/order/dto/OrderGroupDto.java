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

@Data public class OrderGroupDto {
    public UUID id;
    public UUID sellerId;
    public String storeName;
    public OrderGroupStatus status;
    public List<OrderItemDto> items;
    public BigDecimal subtotal;
    public BigDecimal shippingCost;
}
