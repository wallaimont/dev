package com.orbyt.marketplace.engagement.application;

import com.orbyt.marketplace.engagement.domain.Wishlist;
import com.orbyt.marketplace.engagement.repository.WishlistRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    @Transactional(readOnly = true)
    public List<Wishlist> getUserWishlist(UUID userId) {
        return wishlistRepository.findByTenantIdAndUserId(TenantContext.require(), userId);
    }

    @Transactional
    public Wishlist addToWishlist(UUID userId, UUID productId) {
        UUID tenantId = TenantContext.require();
        return wishlistRepository.findByTenantIdAndUserIdAndProductId(tenantId, userId, productId)
                .orElseGet(() -> {
                    Wishlist w = new Wishlist();
                    w.setTenantId(tenantId);
                    w.setUserId(userId);
                    w.setProductId(productId);
                    return wishlistRepository.save(w);
                });
    }

    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        wishlistRepository.deleteByTenantIdAndUserIdAndProductId(TenantContext.require(), userId, productId);
    }
}
