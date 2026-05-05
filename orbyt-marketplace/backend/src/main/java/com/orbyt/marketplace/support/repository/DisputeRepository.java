package com.orbyt.marketplace.support.repository;

import com.orbyt.marketplace.support.domain.Dispute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    List<Dispute> findByTenantIdAndOrderId(UUID tenantId, UUID orderId);
    List<Dispute> findByTenantIdAndBuyerId(UUID tenantId, UUID buyerId);
}
