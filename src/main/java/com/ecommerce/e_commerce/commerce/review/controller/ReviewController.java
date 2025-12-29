package com.ecommerce.e_commerce.commerce.review.controller;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewRequest;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewResponse;
import com.ecommerce.e_commerce.commerce.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Review", description = "Review Management APIs")
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "Create new review")
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable long productId,
            @Valid @RequestBody ReviewRequest reviewRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(productId, reviewRequest, request));
    }

    @Operation(summary = "Update existing review")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable long productId,
            @PathVariable long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(productId, reviewId, reviewRequest, request));
    }

    @Operation(summary = "Delete existing review")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable long productId,
            @PathVariable long reviewId,
            HttpServletRequest request
    ) {
        reviewService.deleteReview(productId, reviewId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get review by id")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable long productId,
            @PathVariable long reviewId
    ) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId, productId));
    }

    @Operation(summary = "Search reviews with sorting and pagination")
    @GetMapping
    public ResponseEntity<PaginatedResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }
}
