package com.nexus.modules.review.controller;

import com.nexus.modules.review.dto.*;
import com.nexus.modules.review.service.ReviewService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Reviews", description = "Product reviews and ratings")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    @Operation(summary = "List product reviews")
    public ResponseEntity<Page<ReviewResponse>> listProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reviewService.listByProduct(productId, pageable));
    }

    @PostMapping("/products/{productId}/reviews")
    @Operation(summary = "Submit product review")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> submitReview(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateReviewRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(productId, req, principal.getUserId()));
    }

    @PostMapping("/reviews/{reviewId}/helpful")
    @Operation(summary = "Mark review as helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        reviewService.markHelpful(reviewId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sellers/{sellerId}/ratings")
    @Operation(summary = "Get seller ratings summary")
    public ResponseEntity<SellerRatingSummary> getSellerRatings(@PathVariable UUID sellerId) {
        return ResponseEntity.ok(reviewService.getSellerRatingSummary(sellerId));
    }
}
