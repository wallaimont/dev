package com.nexus.modules.seller.repository;

import com.nexus.modules.seller.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Optional<Seller> findByUserId(UUID userId);
    Optional<Seller> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    default Optional<Seller> findByUserIdAndTenantId(UUID userId, UUID tenantId) {
        return findByTenantIdAndUserId(tenantId, userId);
    }
}
