package com.orbyt.marketplace.order.repository;

import com.orbyt.marketplace.order.domain.OrderStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
    List<OrderStatusHistory> findByOrderIdAndTenantIdOrderByChangedAtDesc(UUID orderId, UUID tenantId);
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(UUID orderId);
}
