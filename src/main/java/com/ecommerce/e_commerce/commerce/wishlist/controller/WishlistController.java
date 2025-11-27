package com.ecommerce.e_commerce.commerce.wishlist.controller;

import com.ecommerce.e_commerce.commerce.wishlist.dtos.UpdateWishlistItemRequest;
import com.ecommerce.e_commerce.commerce.wishlist.dtos.WishlistItemRequest;
import com.ecommerce.e_commerce.commerce.wishlist.dtos.WishlistItemResponse;
import com.ecommerce.e_commerce.commerce.wishlist.dtos.WishlistResponse;
import com.ecommerce.e_commerce.commerce.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist/")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("items")
    public ResponseEntity<WishlistItemResponse> addItemToWishlist(HttpServletRequest request,
                                                                  @RequestBody @Valid WishlistItemRequest wishlistItemRequest) {
        WishlistItemResponse response = wishlistService.addItemToWishlist(request, wishlistItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("my-wishlist")
    public ResponseEntity<WishlistResponse> getMyWishlist(HttpServletRequest request) {
        return ResponseEntity.ok(wishlistService.getWishlistByUser(request));
    }

    @PatchMapping("sync-wishlist")
    public ResponseEntity<WishlistResponse> syncMyWishlist(HttpServletRequest request,
                                                           @RequestBody @Valid UpdateWishlistItemRequest updateRequest) {
        return ResponseEntity.ok(wishlistService.syncWishlistSnapshot(request, updateRequest));
    }

    @DeleteMapping("clear-wishlist")
    public ResponseEntity<Void> clearWishlist(HttpServletRequest request) {
        wishlistService.clearWishlist(request);
        return ResponseEntity.noContent().build();
    }
}
