package com.orbyt.marketplace.catalog.api;

import com.orbyt.marketplace.catalog.api.dto.ProductResponse;
import com.orbyt.marketplace.catalog.api.dto.UpsertProductRequest;
import com.orbyt.marketplace.catalog.application.CatalogService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/products")
    public List<ProductResponse> listProducts() {
        return catalogService.listFeaturedProducts();
    }

    @GetMapping("/products/{productId}")
    public ProductResponse findById(@PathVariable UUID productId) {
        return catalogService.findById(productId);
    }

    @PostMapping("/products")
    @PreAuthorize("hasAuthority('catalog.manage')")
    public ProductResponse create(@Valid @RequestBody UpsertProductRequest request) {
        return catalogService.create(request);
    }

    @PatchMapping("/products/{productId}")
    @PreAuthorize("hasAuthority('catalog.manage')")
    public ProductResponse update(@PathVariable UUID productId, @Valid @RequestBody UpsertProductRequest request) {
        return catalogService.update(productId, request);
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasAuthority('catalog.manage')")
    public void delete(@PathVariable UUID productId) {
        catalogService.delete(productId);
    }
}
