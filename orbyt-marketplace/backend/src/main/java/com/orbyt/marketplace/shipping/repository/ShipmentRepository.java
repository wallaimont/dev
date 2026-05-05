package com.orbyt.marketplace.shipping.repository;

import com.orbyt.marketplace.shipping.domain.Shipment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    Optional<Shipment> findByOrderId(UUID orderId);

    List<Shipment> findBySellerIdOrderByCreatedAtDesc(UUID sellerId);

    Optional<Shipment> findByTrackingCode(String trackingCode);
}
