package com.orbyt.marketplace.cms.repository;

import com.orbyt.marketplace.cms.domain.CmsPage;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmsPageRepository extends JpaRepository<CmsPage, UUID> {
    Optional<CmsPage> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<CmsPage> findByTenantIdAndSlugAndLanguage(UUID tenantId, String slug, String language);
}
