package com.ecommerce.e_commerce.commerce.review.service;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewRequest;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewResponse;
import com.ecommerce.e_commerce.commerce.review.model.Review;
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
