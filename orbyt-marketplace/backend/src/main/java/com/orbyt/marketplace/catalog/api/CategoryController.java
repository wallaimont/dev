package com.orbyt.marketplace.catalog.api;

import com.orbyt.marketplace.catalog.domain.Category;
import com.orbyt.marketplace.catalog.repository.CategoryRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> list() {
        UUID tenantId = TenantContext.require();
        return categoryRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId);
    }

    @GetMapping("/{id}")
    public Category findById(@PathVariable UUID id) {
        UUID tenantId = TenantContext.require();
        return categoryRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Category not found"));
    }
}
