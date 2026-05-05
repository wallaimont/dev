package com.nexus.modules.payment.repository;

import com.nexus.modules.payment.domain.PixCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PixChargeRepository extends JpaRepository<PixCharge, UUID> {
    Optional<PixCharge> findByTxid(String txid);
    Optional<PixCharge> findByPaymentId(UUID paymentId);
    boolean existsByProcessedWebhookId(String webhookId);
}
