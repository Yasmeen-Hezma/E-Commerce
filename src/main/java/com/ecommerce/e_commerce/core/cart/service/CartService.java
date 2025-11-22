package com.ecommerce.e_commerce.core.cart.service;

import com.ecommerce.e_commerce.core.cart.dtos.CartItemRequest;
import com.ecommerce.e_commerce.core.cart.dtos.CartItemResponse;
import com.ecommerce.e_commerce.core.cart.dtos.CartResponse;
import com.ecommerce.e_commerce.core.cart.dtos.UpdateCartItemRequest;
import com.ecommerce.e_commerce.core.cart.model.Cart;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    CartItemResponse addItemToCart(HttpServletRequest request, CartItemRequest cartItemRequest);

    CartResponse getCartResponseByUser(HttpServletRequest request);

    Cart getCartByUser(HttpServletRequest request);

    CartResponse syncCartSnapshot(HttpServletRequest request, UpdateCartItemRequest updateRequest);

    void clearCart(HttpServletRequest request);

    void checkCartExisting(Cart cart);
}
