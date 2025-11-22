package com.ecommerce.e_commerce.core.order.model;

import com.ecommerce.e_commerce.core.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@Table(name = "order_items", schema = "e-commerce")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    @Column(name = "price", precision = 19, scale = 2, nullable = false)
    @Min(value = 0, message = "price cannot be negative")
    private BigDecimal price;


    @Column(name = "discount", precision = 5, scale = 2)
    @DecimalMax(value = "100.00", message = "Discount can't exceed 100%")
    @DecimalMin(value = "0.00", message = "Discount can't be negative")
    private BigDecimal discount;

}
