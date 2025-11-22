package com.ecommerce.e_commerce.core.product.model;

import com.ecommerce.e_commerce.core.brand.model.Brand;
import com.ecommerce.e_commerce.core.cart.model.CartItem;
import com.ecommerce.e_commerce.core.category.model.Category;
import com.ecommerce.e_commerce.core.order.model.OrderItem;
import com.ecommerce.e_commerce.core.product.enums.ProductStatus;
import com.ecommerce.e_commerce.core.review.model.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "products", schema = "e-commerce")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    @NotBlank
    @Size(min = 3, message = "product name must contains at least 3 characters")
    private String productName;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 6, message = "description must contains at least 6 characters")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "quantity")
    @Min(value = 0, message = "quantity cannot be negative")
    private Integer quantity;

    @Column(name = "price", precision = 19, scale = 2)
    @Min(value = 0, message = "price cannot be negative")
    private BigDecimal price;

    @Column(name = "discount", precision = 19, scale = 2)
    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    private BigDecimal discount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder.Default
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;


    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;
}
