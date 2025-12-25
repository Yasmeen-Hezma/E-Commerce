package com.ecommerce.e_commerce.commerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    @NotNull(message = "Product Id is required")
    private Long productId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
