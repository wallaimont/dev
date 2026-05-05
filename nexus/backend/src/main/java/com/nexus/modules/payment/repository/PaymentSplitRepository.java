package com.nexus.modules.payment.repository;

import com.nexus.modules.payment.domain.PaymentSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentSplitRepository extends JpaRepository<PaymentSplit, UUID> {
    List<PaymentSplit> findByPaymentId(UUID paymentId);
}
