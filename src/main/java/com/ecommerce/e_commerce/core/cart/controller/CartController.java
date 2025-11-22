package com.ecommerce.e_commerce.core.cart.controller;

import com.ecommerce.e_commerce.core.cart.dtos.CartItemRequest;
import com.ecommerce.e_commerce.core.cart.dtos.CartItemResponse;
import com.ecommerce.e_commerce.core.cart.dtos.CartResponse;
import com.ecommerce.e_commerce.core.cart.dtos.UpdateCartItemRequest;
import com.ecommerce.e_commerce.core.cart.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart/")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("items")
    public ResponseEntity<CartItemResponse> addItemToCart(HttpServletRequest request,
                                                          @RequestBody @Valid CartItemRequest cartItemRequest) {
        CartItemResponse response = cartService.addItemToCart(request, cartItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("my-cart")
    public ResponseEntity<CartResponse> getMyCart(HttpServletRequest request) {
        return ResponseEntity.ok(cartService.getCartResponseByUser(request));
    }

    @PatchMapping("sync-cart")
    public ResponseEntity<CartResponse> syncMyCart(HttpServletRequest request,
                                                   @RequestBody @Valid UpdateCartItemRequest updateRequest) {
        return ResponseEntity.ok(cartService.syncCartSnapshot(request, updateRequest));
    }

    @DeleteMapping("clear-cart")
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        cartService.clearCart(request);
        return ResponseEntity.noContent().build();
    }
}
