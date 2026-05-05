package com.orbyt.marketplace.catalog.repository;

import com.orbyt.marketplace.catalog.domain.Store;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    List<Store> findAllByTenantIdAndDeletedAtIsNull(UUID tenantId);

    Optional<Store> findByIdAndTenantIdAndDeletedAtIsNull(UUID id, UUID tenantId);
}
