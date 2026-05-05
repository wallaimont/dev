package com.orbyt.marketplace.shipping.application;

import com.orbyt.marketplace.shared.event.DomainEvent;
import com.orbyt.marketplace.shared.event.DomainEventPublisher;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import com.orbyt.marketplace.shipping.domain.Shipment;
import com.orbyt.marketplace.shipping.domain.ShippingCarrier;
import com.orbyt.marketplace.shipping.repository.ShipmentRepository;
import com.orbyt.marketplace.shipping.repository.ShippingCarrierRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingCarrierRepository carrierRepository;
    private final DomainEventPublisher eventPublisher;

    public List<ShippingCarrier> getAvailableCarriers() {
        return carrierRepository.findByTenantIdAndActiveTrue(TenantContext.require());
    }

    @Transactional
    public Shipment createShipment(UUID orderId, UUID sellerId, String carrierCode,
                                    String originZip, String destZip, BigDecimal weightKg) {
        Shipment shipment = new Shipment();
        shipment.setTenantId(TenantContext.require());
        shipment.setOrderId(orderId);
        shipment.setSellerId(sellerId);
        shipment.setCarrierCode(carrierCode);
        shipment.setOriginZipCode(originZip);
        shipment.setDestinationZipCode(destZip);
        shipment.setWeightKg(weightKg);
        shipment.setShipmentStatus(Shipment.ShipmentStatus.PENDING);
        shipment.setEstimatedDeliveryAt(LocalDateTime.now().plusDays(7));
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment markAsShipped(UUID shipmentId, String trackingCode) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        shipment.setTrackingCode(trackingCode);
        shipment.setShipmentStatus(Shipment.ShipmentStatus.IN_TRANSIT);
        shipment.setShippedAt(LocalDateTime.now());
        shipment.addTrackingEvent("IN_TRANSIT", "Origin", "Package shipped");

        eventPublisher.publish(new DomainEvent("order.shipped", "Shipment",
            shipment.getId(), Map.of("orderId", shipment.getOrderId(), "tracking", trackingCode)));

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment updateTracking(UUID shipmentId, String status, String location, String description) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        shipment.addTrackingEvent(status, location, description);
        if ("DELIVERED".equals(status)) {
            shipment.setShipmentStatus(Shipment.ShipmentStatus.DELIVERED);
            shipment.setDeliveredAt(LocalDateTime.now());
        }
        return shipmentRepository.save(shipment);
    }

    public Shipment getByOrderId(UUID orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
    }

    public Shipment getByTrackingCode(String trackingCode) {
        return shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
    }
}
