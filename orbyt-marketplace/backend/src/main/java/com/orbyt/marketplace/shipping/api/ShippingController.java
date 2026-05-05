package com.orbyt.marketplace.shipping.api;

import com.orbyt.marketplace.shipping.application.ShippingService;
import com.orbyt.marketplace.shipping.domain.Shipment;
import com.orbyt.marketplace.shipping.domain.ShippingCarrier;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping("/carriers")
    public List<ShippingCarrier> getCarriers() {
        return shippingService.getAvailableCarriers();
    }

    @PostMapping("/shipments")
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment createShipment(@RequestBody CreateShipmentRequest req) {
        return shippingService.createShipment(req.orderId(), req.sellerId(),
                req.carrierCode(), req.originZip(), req.destZip(), req.weightKg());
    }

    @PatchMapping("/shipments/{id}/ship")
    public Shipment markShipped(@PathVariable UUID id, @RequestParam String trackingCode) {
        return shippingService.markAsShipped(id, trackingCode);
    }

    @PostMapping("/shipments/{id}/tracking")
    public Shipment addTracking(@PathVariable UUID id, @RequestBody TrackingEventRequest req) {
        return shippingService.updateTracking(id, req.status(), req.location(), req.description());
    }

    @GetMapping("/track/{trackingCode}")
    public Shipment track(@PathVariable String trackingCode) {
        return shippingService.getByTrackingCode(trackingCode);
    }

    @GetMapping("/orders/{orderId}")
    public Shipment getByOrder(@PathVariable UUID orderId) {
        return shippingService.getByOrderId(orderId);
    }

    public record CreateShipmentRequest(UUID orderId, UUID sellerId, String carrierCode,
                                         String originZip, String destZip, BigDecimal weightKg) {}

    public record TrackingEventRequest(String status, String location, String description) {}
}
