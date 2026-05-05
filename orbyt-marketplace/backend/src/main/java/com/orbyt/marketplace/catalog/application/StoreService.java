package com.orbyt.marketplace.catalog.application;

import com.orbyt.marketplace.catalog.api.dto.StoreResponse;
import com.orbyt.marketplace.catalog.api.dto.UpsertStoreRequest;
import com.orbyt.marketplace.catalog.domain.Store;
import com.orbyt.marketplace.catalog.repository.StoreRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
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
public class StoreService {

    private final StoreRepository storeRepository;

    public List<StoreResponse> findAll() {
        UUID tenantId = requireTenantId();
        return storeRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId).stream().map(this::toResponse).toList();
    }

    public StoreResponse findById(UUID id) {
        UUID tenantId = requireTenantId();
        Store store = storeRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
        return toResponse(store);
    }

    @Transactional
    public StoreResponse create(UpsertStoreRequest request) {
        UUID tenantId = requireTenantId();
        Store store = new Store();
        store.setTenantId(tenantId);
        apply(store, request);
        return toResponse(storeRepository.save(store));
    }

    @Transactional
    public StoreResponse update(UUID id, UpsertStoreRequest request) {
        UUID tenantId = requireTenantId();
        Store store = storeRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
        apply(store, request);
        return toResponse(storeRepository.save(store));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = requireTenantId();
        Store store = storeRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
        store.setStatus("INACTIVE");
        store.setDeletedAt(OffsetDateTime.now());
        storeRepository.save(store);
    }

    private void apply(Store store, UpsertStoreRequest request) {
        store.setSellerId(request.sellerId());
        store.setName(request.name());
        store.setSlug(request.slug().toLowerCase(Locale.ROOT));
        store.setDescription(request.description());
        store.setStatus("ACTIVE");
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getTenantId(),
                store.getSellerId(),
                store.getName(),
                store.getSlug(),
                store.getDescription(),
                store.getStatus()
        );
    }

    private UUID requireTenantId() {
        UUID tenantId = TenantContext.get();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Tenant-Id header is required");
        }
        return tenantId;
    }
}
