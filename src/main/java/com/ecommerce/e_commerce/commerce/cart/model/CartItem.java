package com.ecommerce.e_commerce.commerce.cart.model;

import com.ecommerce.e_commerce.commerce.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@Table(name = "cart_items", schema = "e-commerce")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @EmbeddedId
    private CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("product")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cart")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_snapshot", precision = 19, scale = 2, nullable = false)
    @Min(value = 0, message = "price cannot be negative")
    private BigDecimal priceSnapshot;
}
