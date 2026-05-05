package com.orbyt.marketplace.catalog.application;

import com.orbyt.marketplace.catalog.api.dto.UpsertProductRequest;
import com.orbyt.marketplace.catalog.api.dto.ProductResponse;
import com.orbyt.marketplace.catalog.domain.Product;
import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;

    public List<ProductResponse> listFeaturedProducts() {
        UUID tenantId = resolveTenantIdOrFallback();
        List<Product> items = productRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId);
        if (items.isEmpty()) {
            return List.of(
                    new ProductResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Orbyt Pro Dock", "ORB-DOCK-01", BigDecimal.valueOf(399.90), BigDecimal.valueOf(349.90), "BRL", "APPROVED"),
                    new ProductResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Hyper Mesh Backpack", "ORB-BAG-02", BigDecimal.valueOf(249.90), null, "BRL", "APPROVED")
            );
        }
        return items.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ProductResponse findById(UUID id) {
        UUID tenantId = resolveTenantIdOrFallback();
        Product product = productRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(UpsertProductRequest request) {
        UUID tenantId = requireTenantId();
        Product entity = new Product();
        entity.setTenantId(tenantId);
        apply(entity, request);
        entity.setApprovalStatus("APPROVED");
        return toResponse(productRepository.save(entity));
    }

    @Transactional
    public ProductResponse update(UUID id, UpsertProductRequest request) {
        UUID tenantId = requireTenantId();
        Product entity = productRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        apply(entity, request);
        return toResponse(productRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = requireTenantId();
        Product entity = productRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        entity.setStatus("INACTIVE");
        entity.setDeletedAt(java.time.OffsetDateTime.now());
        productRepository.save(entity);
    }

    private void apply(Product entity, UpsertProductRequest request) {
        entity.setStoreId(request.storeId());
        entity.setCategoryId(request.categoryId());
        entity.setSku(request.sku().toUpperCase(Locale.ROOT));
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setPrice(request.price());
        entity.setPromotionalPrice(request.promotionalPrice());
        entity.setCurrencyCode(request.currencyCode());
    }

    private ProductResponse toResponse(Product entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getStoreId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getSku(),
                entity.getPrice(),
                entity.getPromotionalPrice(),
                entity.getCurrencyCode(),
                entity.getApprovalStatus()
        );
    }

    private UUID resolveTenantIdOrFallback() {
        UUID tenantId = TenantContext.get();
        if (tenantId != null) {
            return tenantId;
        }
        return tenantRepository.findBySlugIgnoreCase("orbyt-demo")
                .map(t -> t.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant header is required"));
    }

    private UUID requireTenantId() {
        UUID tenantId = TenantContext.get();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Tenant-Id header is required");
        }
        return tenantId;
    }
}
