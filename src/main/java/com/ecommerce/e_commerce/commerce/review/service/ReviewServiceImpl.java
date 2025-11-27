package com.ecommerce.e_commerce.commerce.review.service;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import com.ecommerce.e_commerce.commerce.product.repository.ProductRepository;
import com.ecommerce.e_commerce.commerce.product.service.ProductService;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewRequest;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewResponse;
import com.ecommerce.e_commerce.commerce.review.mapper.ReviewMapper;
import com.ecommerce.e_commerce.commerce.review.model.Review;
import com.ecommerce.e_commerce.commerce.review.repository.ReviewRepository;
import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.ecommerce.e_commerce.common.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ProductService productService;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(Long productId, ReviewRequest reviewRequest, HttpServletRequest request) {
        productService.getNonDeletedProductById(productId);
        User user = userService.getUserByRequest(request);
        Product product = productService.getNonDeletedProductById(productId);

        if (reviewRepository.existsByProduct_ProductIdAndUser_IdAndDeletedFalse(productId, user.getId())) {
            throw new DuplicateItemException(YOU_HAVE_ALREADY_REVIEWED_THIS_PRODUCT);
        }

        Review review = reviewMapper.toEntity(reviewRequest, product, user);
        Review savedReview = reviewRepository.save(review);

        updateProductReviewStats(productId);

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long productId, Long reviewId, ReviewRequest reviewRequest, HttpServletRequest request) {
        productService.getNonDeletedProductById(productId);
        Review review = getNonDeletedReviewById(reviewId);
        Long userId = userService.getUserId(request);

        validateReviewBelongsToProduct(review, productId);
        validateReviewOwnership(review, userId);

        reviewMapper.updateReviewFromRequest(reviewRequest, review);
        Review updatedReview = reviewRepository.save(review);

        updateProductReviewStats(productId);

        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long productId, Long reviewId, HttpServletRequest request) {
        productService.getNonDeletedProductById(productId);
        Review review = getNonDeletedReviewById(reviewId);
        Long userId = userService.getUserId(request);

        validateReviewBelongsToProduct(review, productId);
        validateReviewOwnership(review, userId);

        review.setDeleted(true);
        reviewRepository.save(review);

        updateProductReviewStats(productId);
    }

    @Override
    public Review getNonDeletedReviewById(Long id) {
        return reviewRepository.findByReviewIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(REVIEW_NOT_FOUND));
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId, Long productId) {
        productService.getNonDeletedProductById(productId);
        Review review = getNonDeletedReviewById(reviewId);

        validateReviewBelongsToProduct(review, productId);

        return reviewMapper.toResponse(review);
    }

    @Override
    public PaginatedResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByProduct_ProductIdAndDeletedFalse(productId, pageable);
        List<ReviewResponse> content = reviewPage.map(reviewMapper::toResponse)
                .getContent();
        return new PaginatedResponse<>(content, reviewPage.getTotalElements());
    }

    @Transactional
    public void updateProductReviewStats(Long productId) {
        BigDecimal averageRating = reviewRepository.calculateAverageRating(productId)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        Long reviewCount = reviewRepository.countByProduct_ProductIdAndDeletedFalse(productId);
        productRepository.updateReviewStats(productId, averageRating, reviewCount);
    }

    private void validateReviewBelongsToProduct(Review review, Long productId) {
        if (!review.getProduct().getProductId().equals(productId)) {
            throw new ItemNotFoundException(REVIEW_NOT_FOUND_FOR_THIS_PRODUCT);
        }
    }

    private void validateReviewOwnership(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException(YOU_CAN_ONLY_ACCESS_YOUR_OWN_REVIEWS);
        }
    }
}
