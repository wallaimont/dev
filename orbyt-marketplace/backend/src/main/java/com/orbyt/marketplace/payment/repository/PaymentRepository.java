package com.orbyt.marketplace.payment.repository;

import com.orbyt.marketplace.payment.domain.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByTenantIdAndOrderGroupId(UUID tenantId, UUID orderGroupId);
}
