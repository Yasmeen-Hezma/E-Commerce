package com.ecommerce.e_commerce.core.wishlist.model;

import com.ecommerce.e_commerce.core.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wishlists_items", schema = "e-commerce")
public class WishlistItem {
    @EmbeddedId
    private WishlistItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("product")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("wishlist")
    @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_snapshot", precision = 19, scale = 2, nullable = false)
    @Min(value = 0, message = "price cannot be negative")
    private BigDecimal priceSnapshot;
}
