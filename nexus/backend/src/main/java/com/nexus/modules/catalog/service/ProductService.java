package com.nexus.modules.catalog.service;

import com.nexus.modules.catalog.domain.*;
import com.nexus.modules.catalog.dto.*;
import com.nexus.modules.catalog.repository.*;
import com.nexus.modules.seller.repository.SellerRepository;
import com.nexus.shared.events.OutboxEventPublisher;
import com.nexus.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository     productRepository;
    private final ProductVariantRepository variantRepository;
    private final StockRepository       stockRepository;
    private final CategoryRepository    categoryRepository;
    private final SellerRepository      sellerRepository;
    private final OutboxEventPublisher  eventPublisher;
    private final SlugService           slugService;

    // ---- Public search -----------------------------------------------

    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> search(ProductSearchRequest req, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return productRepository.search(
            tenantId, req.getQuery(), req.getCategoryId(),
            req.getBrandId(), req.getMinPrice(), req.getMaxPrice(), pageable
        ).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getBySlug(String slug) {
        UUID tenantId = TenantContext.getTenantId();
        Product product = productRepository
            .findByTenantIdAndSlugAndDeletedAtIsNull(tenantId, slug)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + slug));

        if (!product.isVisible()) throw new ProductNotFoundException("Product not available");
        return toDetail(product);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getRelated(UUID productId) {
        UUID tenantId = TenantContext.getTenantId();
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return productRepository.search(
            tenantId, null, product.getCategoryId(), null, null, null,
            Pageable.ofSize(8)
        ).stream()
         .filter(p -> !p.getId().equals(productId))
         .map(this::toSummary)
         .toList();
    }

    // ---- Seller operations -------------------------------------------

    public ProductDetailResponse create(CreateProductRequest req, UUID sellerUserId) {
        UUID tenantId = TenantContext.getTenantId();

        var seller = sellerRepository.findByUserIdAndTenantId(sellerUserId, tenantId)
            .orElseThrow(() -> new SellerNotFoundOrInactiveException("Seller not found"));

        if (!seller.isActive()) throw new SellerNotFoundOrInactiveException("Seller not active");

        String slug = slugService.generateUniqueSlug(req.getName(), tenantId);

        Product product = Product.builder()
            .tenantId(tenantId)
            .sellerId(seller.getId())
            .storeId(seller.getId()) // Store lookup via sellerId in a real impl
            .categoryId(req.getCategoryId())
            .brandId(req.getBrandId())
            .name(req.getName())
            .slug(slug)
            .description(req.getDescription())
            .shortDescription(req.getShortDescription())
            .basePrice(req.getBasePrice())
            .currencyCode(req.getCurrencyCode() != null ? req.getCurrencyCode() : "BRL")
            .condition(req.getCondition() != null ? req.getCondition() : "NEW")
            .weightGrams(req.getWeightGrams())
            .status(needsReview(tenantId) ? Product.ProductStatus.PENDING_REVIEW
                                          : Product.ProductStatus.ACTIVE)
            .build();

        product = productRepository.save(product);

        // Create variants + stock
        if (req.getVariants() != null) {
            for (var varReq : req.getVariants()) {
                createVariantWithStock(product.getId(), tenantId, varReq);
            }
        } else {
            // Default single variant
            createDefaultVariant(product);
        }

        if (product.getStatus() == Product.ProductStatus.PENDING_REVIEW) {
            log.info("Product pending review: {} sellerId={}", product.getId(), seller.getId());
        }

        return toDetail(productRepository.findById(product.getId()).orElseThrow());
    }

    public ProductDetailResponse update(UUID productId, UpdateProductRequest req, UUID sellerUserId) {
        UUID tenantId = TenantContext.getTenantId();
        Product product = getSellerProduct(productId, sellerUserId, tenantId);

        if (req.getName()        != null) product.setName(req.getName());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getBasePrice()   != null) product.setBasePrice(req.getBasePrice());
        if (req.getCategoryId()  != null) product.setCategoryId(req.getCategoryId());

        return toDetail(productRepository.save(product));
    }

    public void delete(UUID productId, UUID sellerUserId) {
        UUID tenantId = TenantContext.getTenantId();
        Product product = getSellerProduct(productId, sellerUserId, tenantId);
        product.setDeletedAt(Instant.now());
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> listBySellerUser(UUID sellerUserId, String status, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        var seller = sellerRepository.findByUserIdAndTenantId(sellerUserId, tenantId)
            .orElseThrow(() -> new SellerNotFoundOrInactiveException("Seller not found"));
        return productRepository.findBySellerIdAndTenantIdAndDeletedAtIsNull(
            seller.getId(), tenantId, pageable
        ).map(this::toSummary);
    }

    // ---- Admin operations -------------------------------------------

    public void approve(UUID productId, UUID adminId) {
        UUID tenantId = TenantContext.getTenantId();
        Product product = productRepository.findById(productId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setStatus(Product.ProductStatus.ACTIVE);
        product.setApprovedAt(Instant.now());
        product.setApprovedBy(adminId);
        productRepository.save(product);

        log.info("Product approved: {} by admin={}", productId, adminId);
    }

    public void reject(UUID productId, String reason, UUID adminId) {
        UUID tenantId = TenantContext.getTenantId();
        Product product = productRepository.findById(productId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setStatus(Product.ProductStatus.REJECTED);
        product.setRejectionReason(reason);
        product.setApprovedBy(adminId);
        productRepository.save(product);
    }

    // ---- Stock service (delegated) ----------------------------------

    public void reserveStock(UUID variantId, int quantity) {
        var stock = stockRepository.findByVariantIdForUpdate(variantId)
            .orElseThrow(() -> new StockNotFoundException("Stock not found for variant: " + variantId));
        stock.reserve(quantity);
        stockRepository.save(stock);
    }

    public void releaseStock(UUID variantId, int quantity) {
        stockRepository.findByVariantId(variantId).ifPresent(stock -> {
            stock.release(quantity);
            stockRepository.save(stock);
        });
    }

    public void deductStock(UUID variantId, int quantity) {
        var stock = stockRepository.findByVariantIdForUpdate(variantId)
            .orElseThrow(() -> new StockNotFoundException("Stock not found"));
        stock.deduct(quantity);
        stockRepository.save(stock);
    }

    // ---- Helpers ----------------------------------------------------

    private boolean needsReview(UUID tenantId) {
        return true; // Read from tenant_settings.auto_approve_products
    }

    private void createVariantWithStock(UUID productId, UUID tenantId, VariantRequest varReq) {
        var variant = ProductVariant.builder()
            .productId(productId)
            .sku(varReq.getSku())
            .name(varReq.getName())
            .price(varReq.getPrice())
            .attributes(varReq.getAttributes())
            .barcode(varReq.getBarcode())
            .build();
        variant = variantRepository.save(variant);

        var stock = StockItem.builder()
            .tenantId(tenantId)
            .variantId(variant.getId())
            .quantity(varReq.getInitialStock() != null ? varReq.getInitialStock() : 0)
            .build();
        stockRepository.save(stock);
    }

    private void createDefaultVariant(Product product) {
        var variant = ProductVariant.builder()
            .productId(product.getId())
            .sku("DEFAULT-" + product.getId().toString().substring(0, 8).toUpperCase())
            .price(product.getBasePrice())
            .build();
        variant = variantRepository.save(variant);

        var stock = StockItem.builder()
            .tenantId(product.getTenantId())
            .variantId(variant.getId())
            .quantity(0)
            .build();
        stockRepository.save(stock);
    }

    private Product getSellerProduct(UUID productId, UUID sellerUserId, UUID tenantId) {
        var seller = sellerRepository.findByUserIdAndTenantId(sellerUserId, tenantId)
            .orElseThrow(() -> new SellerNotFoundOrInactiveException("Seller not found"));
        return productRepository.findById(productId)
            .filter(p -> p.getSellerId().equals(seller.getId()))
            .filter(p -> p.getDeletedAt() == null)
            .orElseThrow(() -> new ProductNotFoundException("Product not found or unauthorized"));
    }

    // ---- Mappers ----------------------------------------------------

    private ProductSummaryResponse toSummary(Product p) {
        var variants = variantRepository.findByProductId(p.getId());
        return ProductSummaryResponse.builder()
            .id(p.getId())
            .name(p.getName())
            .slug(p.getSlug())
            .basePrice(p.getBasePrice())
            .promotionalPrice(p.getPromotionalPrice())
            .effectivePrice(p.getEffectivePrice())
            .isPromoActive(p.isPromoActive())
            .avgRating(p.getAvgRating())
            .totalReviews(p.getTotalReviews())
            .totalSold(p.getTotalSold())
            .status(p.getStatus())
            .currencyCode(p.getCurrencyCode())
            .hasVariants(variants.size() > 1)
            .inStock(variants.stream().anyMatch(ProductVariant::isInStock))
            .build();
    }

    private ProductDetailResponse toDetail(Product p) {
        var variants = variantRepository.findByProductId(p.getId());
        return ProductDetailResponse.builder()
            .id(p.getId())
            .name(p.getName())
            .slug(p.getSlug())
            .description(p.getDescription())
            .shortDescription(p.getShortDescription())
            .basePrice(p.getBasePrice())
            .promotionalPrice(p.getPromotionalPrice())
            .effectivePrice(p.getEffectivePrice())
            .avgRating(p.getAvgRating())
            .totalReviews(p.getTotalReviews())
            .totalSold(p.getTotalSold())
            .status(p.getStatus())
            .currencyCode(p.getCurrencyCode())
            .categoryId(p.getCategoryId())
            .sellerId(p.getSellerId())
            .storeId(p.getStoreId())
            .variants(variants.stream().map(this::toVariantDto).toList())
            .images(p.getImages().stream().map(img ->
                new ProductImageDto(img.getId(), img.getUrl(), img.isMain(), img.getSortOrder())
            ).toList())
            .build();
    }

    private ProductVariantDto toVariantDto(ProductVariant v) {
        return ProductVariantDto.builder()
            .id(v.getId())
            .sku(v.getSku())
            .name(v.getName())
            .price(v.getPrice())
            .promotionalPrice(v.getPromotionalPrice())
            .attributes(v.getAttributes())
            .imageUrl(v.getImageUrl())
            .availableQuantity(v.getAvailableQuantity())
            .inStock(v.isInStock())
            .build();
    }

    // Custom exceptions
    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String msg) { super(msg); }
    }
    public static class SellerNotFoundOrInactiveException extends RuntimeException {
        public SellerNotFoundOrInactiveException(String msg) { super(msg); }
    }
    public static class StockNotFoundException extends RuntimeException {
        public StockNotFoundException(String msg) { super(msg); }
    }
}
