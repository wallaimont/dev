package com.nexus.modules.seller.controller;

import com.nexus.modules.seller.dto.*;
import com.nexus.modules.seller.service.SellerService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sellers")
@Tag(name = "Sellers", description = "Seller registration and management")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    @Operation(summary = "Register as seller")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<SellerResponse> register(
            @Valid @RequestBody RegisterSellerRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerService.register(req, principal.getUserId()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my seller profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerDetailResponse> getMyProfile(
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(sellerService.getByUserId(principal.getUserId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my seller profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerDetailResponse> updateProfile(
            @Valid @RequestBody UpdateSellerRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(sellerService.update(req, principal.getUserId()));
    }

    @PostMapping("/me/documents")
    @Operation(summary = "Upload seller document")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> uploadDocument(
            @RequestParam String documentType,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal NexusPrincipal principal) {
        sellerService.uploadDocument(principal.getUserId(), documentType, file);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/store")
    @Operation(summary = "Update store settings")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<StoreResponse> updateStore(
            @Valid @RequestBody UpdateStoreRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(sellerService.updateStore(req, principal.getUserId()));
    }

    @GetMapping("/stores/{storeSlug}")
    @Operation(summary = "Get store public page")
    public ResponseEntity<StorePublicResponse> getStore(@PathVariable String storeSlug) {
        return ResponseEntity.ok(sellerService.getStoreBySlug(storeSlug));
    }

    @GetMapping("/admin")
    @Operation(summary = "Admin - list sellers")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<SellerResponse>> adminList(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(sellerService.adminList(status, pageable));
    }

    @PostMapping("/{sellerId}/approve")
    @Operation(summary = "Approve seller (admin)")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> approve(
            @PathVariable UUID sellerId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        sellerService.approve(sellerId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sellerId}/suspend")
    @Operation(summary = "Suspend seller (admin)")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> suspend(
            @PathVariable UUID sellerId,
            @Valid @RequestBody SuspendRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        sellerService.suspend(sellerId, req.getReason(), principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
