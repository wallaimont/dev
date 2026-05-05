package com.nexus.modules.auth.service;

import com.nexus.modules.seller.domain.Tenant;
import com.nexus.modules.seller.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantResolverService {

    private final TenantRepository tenantRepository;

    @Cacheable(value = "tenants", key = "#slug")
    public Tenant resolveBySlug(String slug) {
        return tenantRepository.findBySlug(slug)
            .orElseThrow(() -> new TenantNotFoundException("Tenant not found: " + slug));
    }

    @Cacheable(value = "tenants", key = "'domain:' + #domain")
    public Tenant resolveByDomain(String domain) {
        return tenantRepository.findByDomain(domain)
            .orElseThrow(() -> new TenantNotFoundException("Tenant not found for domain: " + domain));
    }

    public static class TenantNotFoundException extends RuntimeException {
        public TenantNotFoundException(String msg) { super(msg); }
    }
}
