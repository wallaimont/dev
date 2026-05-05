package com.orbyt.marketplace.engagement.api;

import com.orbyt.marketplace.engagement.application.WishlistService;
import com.orbyt.marketplace.engagement.domain.Wishlist;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<Wishlist> getWishlist(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return wishlistService.getUserWishlist(principal.getUserId());
    }

    @PostMapping("/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Wishlist addToWishlist(@AuthenticationPrincipal AuthUserPrincipal principal,
                                  @PathVariable UUID productId) {
        return wishlistService.addToWishlist(principal.getUserId(), productId);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWishlist(@AuthenticationPrincipal AuthUserPrincipal principal,
                                   @PathVariable UUID productId) {
        wishlistService.removeFromWishlist(principal.getUserId(), productId);
    }
}
