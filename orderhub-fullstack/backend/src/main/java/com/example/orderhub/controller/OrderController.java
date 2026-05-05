package com.example.orderhub.controller;

import com.example.orderhub.dto.OrderRequest;
import com.example.orderhub.dto.OrderStatusUpdateRequest;
import com.example.orderhub.entity.Order;
import com.example.orderhub.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Order>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(orderService.findByClientId(clientId));
    }

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.status()));
    }

    @PostMapping("/{id}/send-to-billing")
    public ResponseEntity<Map<String, String>> sendToBilling(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", orderService.sendToBilling(id)));
    }

    @GetMapping("/{id}/delivery-status")
    public ResponseEntity<Map<String, String>> deliveryStatus(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", orderService.getDeliveryStatus(id)));
    }
}
