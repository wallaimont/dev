package com.orbyt.marketplace.order.repository;

import com.orbyt.marketplace.order.domain.OrderGroup;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, UUID> {
    Optional<OrderGroup> findByIdAndTenantId(UUID id, UUID tenantId);
    List<OrderGroup> findByTenantIdAndBuyerIdOrderByCreatedAtDesc(UUID tenantId, UUID buyerId);
}
