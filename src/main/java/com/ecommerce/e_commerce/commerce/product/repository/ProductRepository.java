package com.ecommerce.e_commerce.commerce.product.repository;

import com.ecommerce.e_commerce.commerce.product.enums.ProductStatus;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalse();

    Optional<Product> findByProductIdAndDeletedFalse(Long id);

    boolean existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(String productName, Long brandId, Long categoryId);

    @Query("""
                SELECT p FROM Product p
                WHERE p.deleted = false
                AND (:brandId IS NULL OR p.brand.brandId = :brandId)
                AND (:categoryId IS NULL OR p.category.categoryId = :categoryId)
                AND (:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')))
                AND (:status IS NULL OR p.status= :status)
            """)
    Page<Product> searchProducts(Long brandId, Long categoryId, String productName, ProductStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.averageRating= :avgRating, p.reviewCount= :count WHERE p.productId= :productId")
    void updateReviewStats(@Param("productId") Long productId,
                           @Param("avgRating") BigDecimal avgRating,
                           @Param("count") Long count);
}
