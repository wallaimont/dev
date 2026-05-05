package com.orbyt.marketplace.catalog.repository;

import com.orbyt.marketplace.catalog.domain.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByTenantIdAndDeletedAtIsNull(UUID tenantId);
    Optional<Category> findByIdAndTenantIdAndDeletedAtIsNull(UUID id, UUID tenantId);
}
