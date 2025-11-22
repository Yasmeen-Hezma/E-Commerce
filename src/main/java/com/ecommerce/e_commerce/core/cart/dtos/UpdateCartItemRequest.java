package com.ecommerce.e_commerce.core.cart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UpdateCartItemRequest {
    private List<CartItemRequest> cartItems;
}
