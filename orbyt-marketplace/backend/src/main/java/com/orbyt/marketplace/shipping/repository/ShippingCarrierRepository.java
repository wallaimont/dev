package com.orbyt.marketplace.shipping.repository;

import com.orbyt.marketplace.shipping.domain.ShippingCarrier;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingCarrierRepository extends JpaRepository<ShippingCarrier, UUID> {

    List<ShippingCarrier> findByTenantIdAndActiveTrue(UUID tenantId);
}
