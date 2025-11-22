package com.ecommerce.e_commerce.core.cart.dtos;

import com.ecommerce.e_commerce.core.cart.model.CartItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemResponse {
    private Long productId;
    private String productName;
    private Integer maxQuantity;
    private String image;
    private Integer quantity;
    private BigDecimal priceSnapshot;
}
