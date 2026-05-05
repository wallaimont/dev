package com.orbyt.marketplace.shipping.application;

import com.orbyt.marketplace.shared.event.DomainEventPublisher;
import com.orbyt.marketplace.shipping.domain.Shipment;
import com.orbyt.marketplace.shipping.repository.ShipmentRepository;
import com.orbyt.marketplace.shipping.repository.ShippingCarrierRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShippingCarrierRepository carrierRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ShippingService shippingService;

    @Test
    void shouldMarkShipmentAsShippedAndPublishEvent() {
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = new Shipment();
        shipment.setId(shipmentId);
        shipment.setOrderId(UUID.randomUUID());

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        Shipment updated = shippingService.markAsShipped(shipmentId, "TRACK-123");

        assertThat(updated.getTrackingCode()).isEqualTo("TRACK-123");
        assertThat(updated.getShipmentStatus()).isEqualTo(Shipment.ShipmentStatus.IN_TRANSIT);
        assertThat(updated.getShippedAt()).isNotNull();
        assertThat(updated.getTrackingEvents()).hasSize(1);
        verify(eventPublisher).publish(any());
    }
}