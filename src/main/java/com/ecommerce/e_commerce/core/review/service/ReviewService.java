package com.ecommerce.e_commerce.core.review.service;

import com.ecommerce.e_commerce.core.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.core.review.dto.ReviewRequest;
import com.ecommerce.e_commerce.core.review.dto.ReviewResponse;
import com.ecommerce.e_commerce.core.review.model.Review;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(Long productId, ReviewRequest reviewRequest, HttpServletRequest request);

    ReviewResponse updateReview(Long productId, Long reviewId, ReviewRequest reviewRequest, HttpServletRequest request);

    void deleteReview(Long productId, Long reviewId, HttpServletRequest request);

    Review getNonDeletedReviewById(Long id);

    ReviewResponse getReviewById(Long reviewId, Long productId);

    PaginatedResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable);
}
