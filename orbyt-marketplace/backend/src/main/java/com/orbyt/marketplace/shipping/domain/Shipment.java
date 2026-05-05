package com.orbyt.marketplace.shipping.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "shipments")
@Getter @Setter @NoArgsConstructor
public class Shipment extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID sellerId;

    @Column(length = 50)
    private String carrierCode;

    @Column(length = 100)
    private String carrierName;

    @Column(length = 100)
    private String trackingCode;

    @Column(length = 500)
    private String trackingUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus shipmentStatus = ShipmentStatus.PENDING;

    @Column(precision = 12, scale = 2)
    private BigDecimal shippingCost;

    @Column(precision = 6, scale = 3)
    private BigDecimal weightKg;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime estimatedDeliveryAt;

    @Column(length = 200)
    private String originZipCode;

    @Column(length = 200)
    private String destinationZipCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shipment_id")
    @OrderBy("eventDate DESC")
    private List<ShipmentTracking> trackingEvents = new ArrayList<>();

    public enum ShipmentStatus {
        PENDING, LABEL_CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, RETURNED, EXCEPTION
    }

    public void addTrackingEvent(String status, String location, String description) {
        ShipmentTracking event = new ShipmentTracking();
        event.setStatus(status);
        event.setLocation(location);
        event.setDescription(description);
        event.setEventDate(LocalDateTime.now());
        trackingEvents.add(event);
    }
}
