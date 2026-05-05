package com.nexus.modules.catalog.controller;

import com.nexus.modules.catalog.dto.*;
import com.nexus.modules.catalog.service.ProductService;
import com.nexus.shared.security.NexusPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// ============================================================
// PRODUCTS (public + seller + admin)
// ============================================================

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product catalog management")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ---- Public ----
    @GetMapping
    @Operation(summary = "Search/list products (public)")
    public ResponseEntity<Page<ProductSummaryResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sortBy,
            @PageableDefault(size = 24) Pageable pageable) {
        ProductSearchRequest req = ProductSearchRequest.builder()
            .query(q).categoryId(categoryId).brandId(brandId)
            .minPrice(minPrice).maxPrice(maxPrice)
            .minRating(minRating).sortBy(sortBy).build();
        return ResponseEntity.ok(productService.search(req, pageable));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get product detail by slug (public)")
    public ResponseEntity<ProductDetailResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    @GetMapping("/{productId}/related")
    @Operation(summary = "Get related products")
    public ResponseEntity<List<ProductSummaryResponse>> getRelated(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getRelated(productId));
    }

    // ---- Seller ----
    @PostMapping
    @Operation(summary = "Create product (seller)")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDetailResponse> create(
            @Valid @RequestBody CreateProductRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(req, principal.getUserId()));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product (seller)")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDetailResponse> update(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(productService.update(productId, req, principal.getUserId()));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product (seller)")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID productId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        productService.delete(productId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @Operation(summary = "List my products (seller)")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<ProductSummaryResponse>> myProducts(
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                productService.listBySellerUser(principal.getUserId(), status, pageable));
    }

    // ---- Admin ----
    @PostMapping("/{productId}/approve")
    @Operation(summary = "Approve product (admin)")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> approve(
            @PathVariable UUID productId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        productService.approve(productId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/reject")
    @Operation(summary = "Reject product (admin)")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> reject(
            @PathVariable UUID productId,
            @Valid @RequestBody RejectProductRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        productService.reject(productId, req.getReason(), principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
