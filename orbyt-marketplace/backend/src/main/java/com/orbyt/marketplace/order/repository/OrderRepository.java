package com.orbyt.marketplace.order.repository;

import com.orbyt.marketplace.order.domain.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndTenantId(UUID id, UUID tenantId);
    List<Order> findByTenantIdAndSellerIdOrderByCreatedAtDesc(UUID tenantId, UUID sellerId);
    List<Order> findByTenantIdAndBuyerIdOrderByCreatedAtDesc(UUID tenantId, UUID buyerId);
    long countByTenantId(UUID tenantId);
}
