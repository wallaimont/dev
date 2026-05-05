package com.nexus.modules.catalog.repository;

import com.nexus.modules.catalog.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByTenantIdAndSlugAndDeletedAtIsNull(UUID tenantId, String slug);

    Page<Product> findByTenantIdAndStatusAndDeletedAtIsNull(
        UUID tenantId, Product.ProductStatus status, Pageable pageable);

    Page<Product> findBySellerIdAndTenantIdAndDeletedAtIsNull(
        UUID sellerId, UUID tenantId, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.tenantId = :tenantId
          AND p.status = 'ACTIVE'
          AND p.deletedAt IS NULL
          AND (:categoryId IS NULL OR p.categoryId = :categoryId)
          AND (:brandId    IS NULL OR p.brandId    = :brandId)
          AND (:minPrice   IS NULL OR p.basePrice  >= :minPrice)
          AND (:maxPrice   IS NULL OR p.basePrice  <= :maxPrice)
          AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<Product> search(
        @Param("tenantId")   UUID tenantId,
        @Param("query")      String query,
        @Param("categoryId") UUID categoryId,
        @Param("brandId")    UUID brandId,
        @Param("minPrice")   BigDecimal minPrice,
        @Param("maxPrice")   BigDecimal maxPrice,
        Pageable pageable
    );

    boolean existsByTenantIdAndSlugAndDeletedAtIsNull(UUID tenantId, String slug);
}
