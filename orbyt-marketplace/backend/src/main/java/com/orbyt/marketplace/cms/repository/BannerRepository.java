package com.orbyt.marketplace.cms.repository;

import com.orbyt.marketplace.cms.domain.Banner;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, UUID> {
    List<Banner> findByTenantIdAndIsActiveTrueAndPositionOrderByDisplayOrderAsc(UUID tenantId, String position);
    List<Banner> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
}
