package com.ecommerce.e_commerce.commerce.cart.controller;

import com.ecommerce.e_commerce.commerce.cart.dto.CartItemRequest;
import com.ecommerce.e_commerce.commerce.cart.dto.CartItemResponse;
import com.ecommerce.e_commerce.commerce.cart.dto.CartResponse;
import com.ecommerce.e_commerce.commerce.cart.dto.UpdateCartItemRequest;
import com.ecommerce.e_commerce.commerce.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart/")
@Tag(name = "Cart", description = "Cart Management APIs")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Add new item to my cart")
    @PostMapping("items")
    public ResponseEntity<CartItemResponse> addItemToCart(HttpServletRequest request,
                                                          @RequestBody @Valid CartItemRequest cartItemRequest) {
        CartItemResponse response = cartService.addItemToCart(request, cartItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get my cart")
    @GetMapping("my-cart")
    public ResponseEntity<CartResponse> getMyCart(HttpServletRequest request) {
        return ResponseEntity.ok(cartService.getCartResponseByUser(request));
    }

    @Operation(summary = "Sync my cart")
    @PatchMapping("sync-cart")
    public ResponseEntity<CartResponse> syncMyCart(HttpServletRequest request,
                                                   @RequestBody @Valid UpdateCartItemRequest updateRequest) {
        return ResponseEntity.ok(cartService.syncCartSnapshot(request, updateRequest));
    }

    @Operation(summary = "clear my cart")
    @DeleteMapping("clear-cart")
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        cartService.clearCart(request);
        return ResponseEntity.noContent().build();
    }
}
