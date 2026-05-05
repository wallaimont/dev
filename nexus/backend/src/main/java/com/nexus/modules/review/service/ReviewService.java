package com.nexus.modules.review.service;

import com.nexus.modules.review.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    public Page<ReviewResponse> listByProduct(UUID productId, Pageable pageable) {
        return Page.empty(pageable);
    }

    public ReviewResponse create(UUID productId, CreateReviewRequest req, UUID userId) {
        log.info("Review created for product: {} by user: {}", productId, userId);
        return ReviewResponse.builder().productId(productId).buyerId(userId).rating(req.getRating()).build();
    }

    public void markHelpful(UUID reviewId, UUID userId) {
        log.info("Review {} marked helpful by {}", reviewId, userId);
    }

    public SellerRatingSummary getSellerRatingSummary(UUID sellerId) {
        return SellerRatingSummary.builder().sellerId(sellerId).avgRating(BigDecimal.ZERO).build();
    }
}
