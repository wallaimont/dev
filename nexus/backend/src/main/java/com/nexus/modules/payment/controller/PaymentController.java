package com.nexus.modules.payment.controller;

import com.nexus.modules.payment.dto.*;
import com.nexus.modules.payment.service.PaymentService;
import com.nexus.modules.payment.service.RefundService;
import com.nexus.shared.security.NexusPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Payments", description = "Payment processing - PIX, card, boleto")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    // ---- PIX ----
    @PostMapping("/api/v1/orders/{orderId}/payments/pix")
    @Operation(summary = "Generate PIX charge for order")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<PixChargeResponse> createPix(
            @PathVariable UUID orderId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPixCharge(orderId, principal.getUserId(), idempotencyKey));
    }

    @GetMapping("/api/v1/payments/pix/{txid}")
    @Operation(summary = "Poll PIX charge status")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<PixStatusResponse> getPixStatus(@PathVariable String txid) {
        return ResponseEntity.ok(paymentService.getPixStatus(txid));
    }

    // ---- Card ----
    @PostMapping("/api/v1/orders/{orderId}/payments/card")
    @Operation(summary = "Pay with credit/debit card")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CardPaymentResponse> payWithCard(
            @PathVariable UUID orderId,
            @Valid @RequestBody CardPaymentRequest req,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.payWithCard(orderId, req, principal.getUserId(), idempotencyKey));
    }

    // ---- Boleto ----
    @PostMapping("/api/v1/orders/{orderId}/payments/boleto")
    @Operation(summary = "Generate boleto for order")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BoletoResponse> createBoleto(
            @PathVariable UUID orderId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createBoleto(orderId, principal.getUserId(), idempotencyKey));
    }

    // ---- Refund ----
    @PostMapping("/api/v1/payments/{paymentId}/refund")
    @Operation(summary = "Request refund")
    @PreAuthorize("hasAnyRole('BUYER','TENANT_ADMIN')")
    public ResponseEntity<RefundResponse> requestRefund(
            @PathVariable UUID paymentId,
            @Valid @RequestBody RefundRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(refundService.requestRefund(paymentId, req, principal.getUserId()));
    }

    @PostMapping("/api/v1/payments/refunds/{refundId}/approve")
    @Operation(summary = "Approve refund (admin)")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Void> approveRefund(
            @PathVariable UUID refundId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        refundService.approve(refundId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    // ---- Seller Payouts ----
    @GetMapping("/api/v1/seller/payouts")
    @Operation(summary = "List my payouts (seller)")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> listPayouts(
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(paymentService.listSellerPayouts(principal.getUserId()));
    }

    // ---- Webhooks (no auth - validated by signature) ----
    @PostMapping("/api/v1/webhooks/pagarme")
    @Operation(summary = "Pagar.me webhook receiver")
    public ResponseEntity<Void> pagarmeWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Hub-Signature") String signature) {
        paymentService.processWebhook("PAGARME", extractEventType(payload), payload, signature);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/webhooks/gerencianet")
    @Operation(summary = "Gerencianet/EFI webhook receiver")
    public ResponseEntity<Void> gerencianetWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Webhook-Signature", required = false) String signature) {
        paymentService.processWebhook("GERENCIANET", extractEventType(payload), payload, signature);
        return ResponseEntity.ok().build();
    }

    private String extractEventType(String payload) {
        // Parse event type from JSON payload
        return "PAYMENT_CONFIRMED"; // Simplified - use Jackson in real impl
    }
}
