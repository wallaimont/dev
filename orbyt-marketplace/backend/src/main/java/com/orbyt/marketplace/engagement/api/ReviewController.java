package com.orbyt.marketplace.engagement.api;

import com.orbyt.marketplace.engagement.api.dto.CreateReviewRequest;
import com.orbyt.marketplace.engagement.application.ReviewService;
import com.orbyt.marketplace.engagement.domain.Review;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}")
    public Page<Review> getProductReviews(@PathVariable UUID productId, Pageable pageable) {
        return reviewService.getProductReviews(productId, pageable);
    }

    @GetMapping("/products/{productId}/average")
    public Double getAverageRating(@PathVariable UUID productId) {
        return reviewService.getAverageRating(productId);
    }

    @PostMapping
    public Review createReview(@AuthenticationPrincipal AuthUserPrincipal principal,
                               @Valid @RequestBody CreateReviewRequest request) {
        return reviewService.createReview(principal.getUserId(), request.productId(),
                request.orderId(), request.rating(), request.title(), request.comment());
    }

    @PostMapping("/{reviewId}/reply")
    public Review replyToReview(@AuthenticationPrincipal AuthUserPrincipal principal,
                                @PathVariable UUID reviewId,
                                @RequestBody String reply) {
        return reviewService.replyToReview(reviewId, principal.getUserId(), reply);
    }
}
