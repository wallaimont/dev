package com.orbyt.marketplace.payment.application;

import com.orbyt.marketplace.payment.api.dto.PixChargeRequest;
import com.orbyt.marketplace.payment.api.dto.PixChargeResponse;
import com.orbyt.marketplace.observability.MarketplaceMetrics;
import com.orbyt.marketplace.payment.domain.Payment;
import com.orbyt.marketplace.payment.domain.PixCharge;
import com.orbyt.marketplace.payment.repository.PaymentRepository;
import com.orbyt.marketplace.payment.repository.PixChargeRepository;
import com.orbyt.marketplace.shared.event.DomainEvent;
import com.orbyt.marketplace.shared.event.DomainEventPublisher;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PixChargeRepository pixChargeRepository;
    private final DomainEventPublisher eventPublisher;
    private final MarketplaceMetrics marketplaceMetrics;

    @Transactional
    public Payment createPayment(UUID orderGroupId, UUID buyerId, String paymentMethod, BigDecimal amount, String currencyCode) {
        UUID tenantId = TenantContext.require();
        Payment payment = new Payment();
        payment.setTenantId(tenantId);
        payment.setOrderGroupId(orderGroupId);
        payment.setBuyerId(buyerId);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setCurrencyCode(currencyCode);
        payment.setStatus("PENDING");
        Payment saved = paymentRepository.save(payment);
        marketplaceMetrics.recordPaymentOutcome("CREATED", paymentMethod, amount);
        return saved;
    }

    @Transactional
    public PixChargeResponse createPixCharge(PixChargeRequest request) {
        UUID tenantId = TenantContext.require();

        Payment payment = createPayment(
                request.orderGroupId(), request.buyerId(),
                "PIX", request.amount(), "BRL");

        String txid = UUID.randomUUID().toString().replace("-", "").substring(0, 25);
        String qrCode = generatePixPayload(txid, request.amount());

        PixCharge pix = new PixCharge();
        pix.setTenantId(tenantId);
        pix.setPaymentId(payment.getId());
        pix.setTxid(txid);
        pix.setQrCode(qrCode);
        pix.setCopyPasteCode(qrCode);
        pix.setAmount(request.amount());
        pix.setExpiresAt(OffsetDateTime.now().plusMinutes(15));
        pix.setStatus("PENDING");
        pixChargeRepository.save(pix);

        log.info("PIX charge created: txid={}, amount={}", txid, request.amount());

        return new PixChargeResponse(
                pix.getId(), payment.getId(),
                qrCode, qrCode,
                pix.getExpiresAt(), "PENDING"
        );
    }

    @Transactional
    public void handlePixWebhook(String txid, String endToEndId) {
        PixCharge pix = pixChargeRepository.findByTxid(txid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PIX charge not found"));

        if ("PAID".equals(pix.getStatus())) {
            log.info("PIX webhook already processed (idempotent): txid={}", txid);
            marketplaceMetrics.recordPaymentWebhook("IDEMPOTENT", "PIX");
            return;
        }

        pix.setStatus("PAID");
        pix.setPaidAt(OffsetDateTime.now());
        pix.setEndToEndId(endToEndId);
        pixChargeRepository.save(pix);

        Payment payment = paymentRepository.findById(pix.getPaymentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        payment.setStatus("APPROVED");
        payment.setPaidAt(OffsetDateTime.now());
        paymentRepository.save(payment);
        marketplaceMetrics.recordPaymentOutcome("APPROVED", "PIX", payment.getAmount());
        marketplaceMetrics.recordPaymentWebhook("PROCESSED", "PIX");

        eventPublisher.publish(new DomainEvent(
                "payment.approved", "Payment", payment.getId(),
                Map.of("orderGroupId", payment.getOrderGroupId(), "amount", payment.getAmount(), "method", "PIX")
        ));

        log.info("PIX payment approved: paymentId={}, amount={}", payment.getId(), payment.getAmount());
    }

    private String generatePixPayload(String txid, BigDecimal amount) {
        return "00020126580014BR.GOV.BCB.PIX0136" + txid +
                "520400005303986540" + amount.toPlainString() +
                "5802BR5921ORBYT MARKETPLACE6009SAO PAULO62070503***6304ABCD";
    }
}
