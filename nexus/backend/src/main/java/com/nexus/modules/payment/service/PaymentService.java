package com.nexus.modules.payment.service;

import com.nexus.modules.order.domain.Order;
import com.nexus.modules.payment.domain.*;
import com.nexus.modules.payment.dto.*;
import com.nexus.modules.payment.events.PaymentApprovedEvent;
import com.nexus.modules.payment.events.PaymentFailedEvent;
import com.nexus.modules.payment.repository.*;
import com.nexus.shared.events.OutboxEventPublisher;
import com.nexus.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Payment Service — handles PIX, card, boleto, split payment.
 * All payment operations are idempotent via idempotency_key.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PixChargeRepository pixChargeRepository;
    private final PaymentSplitRepository splitRepository;
    private final PaymentGatewayClient gatewayClient;
    private final OutboxEventPublisher eventPublisher;

    // --------------------------------------------------------
    // PIX PAYMENT FLOW
    // --------------------------------------------------------

    /**
     * Creates a PIX charge for an order.
     * Returns QR code, copy-paste code, and expiry.
     * Idempotent: same idempotency_key returns same charge.
     */
    public PixChargeResponse createPixCharge(UUID orderId, UUID buyerId, String idempotencyKey) {
        UUID tenantId = TenantContext.getTenantId();

        // Idempotency check
        return paymentRepository.findByIdempotencyKey(idempotencyKey)
            .map(existing -> buildPixResponse(existing, getPixCharge(existing.getId())))
            .orElseGet(() -> doCreatePixCharge(orderId, buyerId, tenantId, idempotencyKey));
    }

    private PixChargeResponse doCreatePixCharge(UUID orderId, UUID buyerId,
                                                  UUID tenantId, String idempotencyKey) {
        // Fetch order — validation
        Order order = fetchAndValidateOrder(orderId, buyerId, tenantId);

        // Create payment record
        Payment payment = new Payment();
        payment.setTenantId(tenantId);
        payment.setOrderId(orderId);
        payment.setIdempotencyKey(idempotencyKey);
        payment.setMethod(PaymentMethod.PIX);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotal());
        payment.setCurrencyCode(order.getCurrencyCode());
        payment = paymentRepository.save(payment);

        // Call gateway (Pagar.me / Gerencianet / EFI Bank)
        GatewayPixResponse gwResp = gatewayClient.createPixCharge(
            GatewayPixRequest.builder()
                .txId(payment.getId().toString().replace("-", "").substring(0, 26))
                .amount(order.getTotal())
                .payerName("Buyer-" + order.getBuyerId())
                .payerCpf("00000000000")
                .expiresInSeconds(3600)
                .description("Pedido #" + order.getOrderNumber())
                .build()
        );

        // Update payment with gateway data
        payment.setGateway(gwResp.getGateway());
        payment.setGatewayTxnId(gwResp.getTxId());
        payment.setExpiresAt(Instant.now().plusSeconds(3600));
        paymentRepository.save(payment);

        // Persist PIX charge
        PixCharge pixCharge = new PixCharge();
        pixCharge.setPaymentId(payment.getId());
        pixCharge.setTenantId(tenantId);
        pixCharge.setTxid(gwResp.getTxId());
        pixCharge.setQrCode(gwResp.getQrCode());
        pixCharge.setQrCodeUrl(gwResp.getQrCodeImageUrl());
        pixCharge.setPixCopyPaste(gwResp.getPixCopyPaste());
        pixCharge.setAmount(order.getTotal());
        pixCharge.setStatus(PixStatus.ACTIVE);
        pixCharge.setExpiresAt(Instant.now().plusSeconds(3600));
        pixCharge = pixChargeRepository.save(pixCharge);

        log.info("PIX charge created: txid={} orderId={}", gwResp.getTxId(), orderId);

        return buildPixResponse(payment, pixCharge);
    }

    // --------------------------------------------------------
    // WEBHOOK PROCESSING (idempotent)
    // --------------------------------------------------------

    /**
     * Processes incoming gateway webhook.
     * Must be idempotent — gateway may send duplicates.
     */
    public void processWebhook(String gateway, String eventType, Object payload, String signature) {
        // Validate webhook signature
        gatewayClient.validateSignature(gateway, payload, signature);

        String eventId = extractEventId(gateway, payload);

        // Idempotency — check if already processed
        if (pixChargeRepository.existsByProcessedWebhookId(eventId)) {
            log.info("Webhook already processed, skipping: {}", eventId);
            return;
        }

        switch (eventType) {
            case "PAYMENT_CONFIRMED", "pix.payment.received" -> handlePixConfirmed(payload, eventId);
            case "PAYMENT_FAILED"                            -> handlePaymentFailed(payload, eventId);
            case "CHARGE_EXPIRED"                            -> handlePixExpired(payload, eventId);
            default -> log.warn("Unknown webhook event: {}", eventType);
        }
    }

    private void handlePixConfirmed(Object payload, String eventId) {
        String txid = extractTxId(payload);
        PixCharge charge = pixChargeRepository.findByTxid(txid)
            .orElseThrow(() -> new PaymentNotFoundException("PixCharge not found for txid: " + txid));

        if (charge.getStatus() == PixStatus.COMPLETED) return; // Already processed

        charge.setProcessedWebhookId(eventId);
        charge.setStatus(PixStatus.COMPLETED);
        charge.setPaidAt(Instant.now());
        pixChargeRepository.save(charge);

        Payment payment = paymentRepository.findById(charge.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(Instant.now());
        paymentRepository.save(payment);

        // Publish domain event — order module will listen and confirm order
        eventPublisher.publish(
            new PaymentApprovedEvent(payment.getTenantId(), payment.getId(), payment.getOrderId()),
            "pix-confirmed-" + eventId
        );

        log.info("PIX confirmed: txid={} paymentId={}", txid, payment.getId());
    }

    private void handlePaymentFailed(Object payload, String eventId) {
        String txid = extractTxId(payload);
        pixChargeRepository.findByTxid(txid).ifPresent(charge -> {
            charge.setProcessedWebhookId(eventId);
            charge.setStatus(PixStatus.CANCELLED);
            pixChargeRepository.save(charge);

            Payment payment = paymentRepository.findById(charge.getPaymentId()).orElseThrow();
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            eventPublisher.publish(
                new PaymentFailedEvent(payment.getTenantId(), payment.getId(), payment.getOrderId()),
                "pix-failed-" + eventId
            );
        });
    }

    private void handlePixExpired(Object payload, String eventId) {
        String txid = extractTxId(payload);
        pixChargeRepository.findByTxid(txid).ifPresent(charge -> {
            if (charge.getStatus() == PixStatus.ACTIVE) {
                charge.setProcessedWebhookId(eventId);
                charge.setStatus(PixStatus.EXPIRED);
                pixChargeRepository.save(charge);

                Payment payment = paymentRepository.findById(charge.getPaymentId()).orElseThrow();
                payment.setStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
            }
        });
    }

    // --------------------------------------------------------
    // SPLIT PAYMENT
    // --------------------------------------------------------

    /**
     * Creates split payment records for each seller in the order.
     * Platform fee is deducted from each seller's portion.
     */
    public void createSplits(UUID paymentId, Order order) {
        for (var group : order.getOrderGroups()) {
            PaymentSplit split = new PaymentSplit();
            split.setPaymentId(paymentId);
            split.setOrderGroupId(group.getId());
            split.setSellerId(group.getSellerId());
            split.setTotalAmount(group.getSubtotal().add(group.getShippingCost()));
            split.setPlatformFee(group.getCommissionAmt());
            split.setSellerAmount(group.getSellerAmount());
            split.setStatus(SplitStatus.PENDING);

            // Get seller's PIX key for transfer
            String pixKey = fetchSellerPixKey(group.getSellerId());
            split.setRecipientKey(pixKey);

            splitRepository.save(split);
        }
    }

    // --------------------------------------------------------
    // ---- Additional payment methods ----

    public PixStatusResponse getPixStatus(String txid) {
        PixCharge charge = pixChargeRepository.findByTxid(txid)
                .orElseThrow(() -> new PaymentNotFoundException("PIX charge not found for txid: " + txid));
        Payment payment = paymentRepository.findById(charge.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return PixStatusResponse.builder()
                .txid(charge.getTxid())
                .status(payment.getStatus())
                .build();
    }

    public CardPaymentResponse payWithCard(UUID orderId, CardPaymentRequest req, UUID userId, String idempotencyKey) {
        // TODO: implement card payment flow
        return CardPaymentResponse.builder().build();
    }

    public BoletoResponse createBoleto(UUID orderId, UUID userId, String idempotencyKey) {
        // TODO: implement boleto generation
        return BoletoResponse.builder().build();
    }

    public java.util.List<?> listSellerPayouts(UUID userId) {
        // TODO: implement seller payouts listing
        return java.util.Collections.emptyList();
    }

    // HELPERS
    // --------------------------------------------------------

    private PixChargeResponse buildPixResponse(Payment payment, PixCharge charge) {
        return PixChargeResponse.builder()
            .paymentId(payment.getId())
            .txid(charge.getTxid())
            .qrCode(charge.getQrCode())
            .qrCodeUrl(charge.getQrCodeUrl())
            .pixCopyPaste(charge.getPixCopyPaste())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .expiresAt(charge.getExpiresAt())
            .build();
    }

    private PixCharge getPixCharge(UUID paymentId) {
        return pixChargeRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("PIX charge not found"));
    }

    private Order fetchAndValidateOrder(UUID orderId, UUID buyerId, UUID tenantId) {
        // Order validation logic
        throw new UnsupportedOperationException("Implement via OrderRepository injection");
    }

    private String fetchSellerPixKey(UUID sellerId) {
        // Fetch from seller settings
        return "seller-pix-key@example.com"; // Replace with real lookup
    }

    private String extractEventId(String gateway, Object payload) {
        // Extract unique event ID from gateway payload
        return gateway + "-" + payload.hashCode(); // Simplified
    }

    private String extractTxId(Object payload) {
        // Extract txId from gateway payload (varies by gateway)
        return payload.toString(); // Simplified
    }

    // Custom exceptions
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String msg) { super(msg); }
    }
}
