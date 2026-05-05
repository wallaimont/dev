package com.nexus.modules.order.repository;

import com.nexus.modules.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndTenantId(UUID id, UUID tenantId);
    Page<Order> findByBuyerIdAndTenantId(UUID buyerId, UUID tenantId, Pageable pageable);
    Optional<Order> findByOrderNumberAndTenantId(String orderNumber, UUID tenantId);
}
