package com.ecommerce.e_commerce.core.review.repository;

import com.ecommerce.e_commerce.core.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct_ProductIdAndDeletedFalse(Long productId, Pageable pageable);

    List<Review> findByProduct_ProductIdAndDeletedFalse(Long productId);

    Optional<Review> findByReviewIdAndDeletedFalse(Long reviewId);

    boolean existsByProduct_ProductIdAndUser_IdAndDeletedFalse(Long productId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId= :productId AND r.deleted=false")
    Optional<BigDecimal> calculateAverageRating(@Param("productId") Long productId);

    Long countByProduct_ProductIdAndDeletedFalse(Long productId);
}
