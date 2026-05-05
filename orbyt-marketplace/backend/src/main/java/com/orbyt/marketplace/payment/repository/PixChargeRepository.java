package com.orbyt.marketplace.payment.repository;

import com.orbyt.marketplace.payment.domain.PixCharge;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PixChargeRepository extends JpaRepository<PixCharge, UUID> {
    Optional<PixCharge> findByTxid(String txid);
    Optional<PixCharge> findByPaymentId(UUID paymentId);
}
