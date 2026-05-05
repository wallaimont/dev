package com.orbyt.marketplace.fraud.repository;

import com.orbyt.marketplace.fraud.domain.FraudAnalysis;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAnalysisRepository extends JpaRepository<FraudAnalysis, UUID> {

    Optional<FraudAnalysis> findByOrderId(UUID orderId);
}
