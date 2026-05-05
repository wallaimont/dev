package com.orbyt.marketplace.fraud.repository;

import com.orbyt.marketplace.fraud.domain.FraudBlacklist;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudBlacklistRepository extends JpaRepository<FraudBlacklist, UUID> {

    boolean existsByTenantIdAndTypeAndValueAndActiveTrue(UUID tenantId,
            FraudBlacklist.BlacklistType type, String value);
}
