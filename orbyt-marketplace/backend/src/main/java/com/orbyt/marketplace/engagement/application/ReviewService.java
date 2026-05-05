package com.orbyt.marketplace.engagement.application;

import com.orbyt.marketplace.engagement.domain.Review;
import com.orbyt.marketplace.engagement.repository.ReviewRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<Review> getProductReviews(UUID productId, Pageable pageable) {
        return reviewRepository.findByTenantIdAndProductId(TenantContext.require(), productId, pageable);
    }

    @Transactional
    public Review createReview(UUID userId, UUID productId, UUID orderId, int rating, String title, String comment) {
        UUID tenantId = TenantContext.require();
        if (rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }
        Review review = new Review();
        review.setTenantId(tenantId);
        review.setUserId(userId);
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        review.setVerifiedPurchase(orderId != null);
        review.setStatus("ACTIVE");
        return reviewRepository.save(review);
    }

    @Transactional
    public Review replyToReview(UUID reviewId, UUID sellerId, String reply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        review.setReply(reply);
        review.setRepliedBy(sellerId);
        review.setRepliedAt(java.time.OffsetDateTime.now());
        return reviewRepository.save(review);
    }

    public Double getAverageRating(UUID productId) {
        return reviewRepository.averageRatingByProduct(TenantContext.require(), productId);
    }
}
