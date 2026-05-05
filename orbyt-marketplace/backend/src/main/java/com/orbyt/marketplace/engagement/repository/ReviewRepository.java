package com.orbyt.marketplace.engagement.repository;

import com.orbyt.marketplace.engagement.domain.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByTenantIdAndProductId(UUID tenantId, UUID productId, Pageable pageable);
    List<Review> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tenantId = ?1 AND r.productId = ?2")
    Double averageRatingByProduct(UUID tenantId, UUID productId);

    long countByTenantIdAndProductId(UUID tenantId, UUID productId);
}
