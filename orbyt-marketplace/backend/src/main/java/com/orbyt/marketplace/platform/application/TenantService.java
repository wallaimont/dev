package com.orbyt.marketplace.platform.application;

import com.orbyt.marketplace.platform.api.dto.TenantResponse;
import com.orbyt.marketplace.platform.api.dto.TenantUpsertRequest;
import com.orbyt.marketplace.platform.domain.Tenant;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<TenantResponse> findAll() {
        return tenantRepository.findAll().stream().map(this::toResponse).toList();
    }

    public TenantResponse findById(UUID id) {
        return toResponse(tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found")));
    }

    @Transactional
    public TenantResponse create(TenantUpsertRequest request) {
        tenantRepository.findBySlugIgnoreCase(request.slug())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant slug already exists");
                });
        Tenant tenant = new Tenant();
        apply(tenant, request);
        tenant.setStatus("ACTIVE");
        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantResponse update(UUID id, TenantUpsertRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        tenantRepository.findBySlugIgnoreCase(request.slug())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant slug already exists");
                });
        apply(tenant, request);
        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional
    public void deactivate(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        tenant.setStatus("INACTIVE");
        tenant.setDeletedAt(OffsetDateTime.now());
        tenantRepository.save(tenant);
    }

    private void apply(Tenant tenant, TenantUpsertRequest request) {
        tenant.setSlug(request.slug().toLowerCase(Locale.ROOT));
        tenant.setName(request.name());
        tenant.setDefaultLocale(request.defaultLocale());
        tenant.setCurrencyCode(request.currencyCode().toUpperCase(Locale.ROOT));
        tenant.setPrimaryDomain(request.primaryDomain());
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getSlug(),
                tenant.getName(),
                tenant.getStatus(),
                tenant.getDefaultLocale(),
                tenant.getCurrencyCode(),
                tenant.getPrimaryDomain()
        );
    }
}
