package com.nexus.modules.seller.service;

import com.nexus.modules.seller.dto.*;
import com.nexus.modules.seller.repository.SellerRepository;
import com.nexus.modules.seller.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;
    private final TenantRepository tenantRepository;

    public SellerResponse register(RegisterSellerRequest req, UUID userId) {
        log.info("Registering seller for user: {}", userId);
        return SellerResponse.builder().userId(userId).status("PENDING").build();
    }

    public SellerDetailResponse getByUserId(UUID userId) {
        return SellerDetailResponse.builder().userId(userId).build();
    }

    public SellerDetailResponse update(UpdateSellerRequest req, UUID userId) {
        return SellerDetailResponse.builder().userId(userId).build();
    }

    public void uploadDocument(UUID userId, String documentType, MultipartFile file) {
        log.info("Document uploaded: userId={} type={}", userId, documentType);
    }

    public StoreResponse updateStore(UpdateStoreRequest req, UUID userId) {
        return StoreResponse.builder().build();
    }

    public StorePublicResponse getStoreBySlug(String storeSlug) {
        return StorePublicResponse.builder().slug(storeSlug).build();
    }

    public Page<SellerResponse> adminList(String status, Pageable pageable) {
        return Page.empty(pageable);
    }

    public void approve(UUID sellerId, UUID adminId) {
        log.info("Seller approved: {} by admin: {}", sellerId, adminId);
    }

    public void suspend(UUID sellerId, String reason, UUID adminId) {
        log.info("Seller suspended: {} reason: {} by admin: {}", sellerId, reason, adminId);
    }
}
