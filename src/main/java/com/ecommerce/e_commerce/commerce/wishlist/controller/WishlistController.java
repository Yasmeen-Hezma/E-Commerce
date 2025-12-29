package com.ecommerce.e_commerce.commerce.wishlist.controller;

import com.ecommerce.e_commerce.commerce.wishlist.dto.UpdateWishlistItemRequest;
import com.ecommerce.e_commerce.commerce.wishlist.dto.WishlistItemRequest;
import com.ecommerce.e_commerce.commerce.wishlist.dto.WishlistItemResponse;
import com.ecommerce.e_commerce.commerce.wishlist.dto.WishlistResponse;
import com.ecommerce.e_commerce.commerce.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist/")
@Tag(name = "Wishlist", description = "Wishlist Management APIs")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @Operation(summary = "Add new item to my wishlist")
    @PostMapping("items")
    public ResponseEntity<WishlistItemResponse> addItemToWishlist(HttpServletRequest request,
                                                                  @RequestBody @Valid WishlistItemRequest wishlistItemRequest) {
        WishlistItemResponse response = wishlistService.addItemToWishlist(request, wishlistItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get my wishlist")
    @GetMapping("my-wishlist")
    public ResponseEntity<WishlistResponse> getMyWishlist(HttpServletRequest request) {
        return ResponseEntity.ok(wishlistService.getWishlistByUser(request));
    }

    @Operation(summary = "Sync my wishlist")
    @PatchMapping("sync-wishlist")
    public ResponseEntity<WishlistResponse> syncMyWishlist(HttpServletRequest request,
                                                           @RequestBody @Valid UpdateWishlistItemRequest updateRequest) {
        return ResponseEntity.ok(wishlistService.syncWishlistSnapshot(request, updateRequest));
    }

    @Operation(summary = "Clear my wishlist")
    @DeleteMapping("clear-wishlist")
    public ResponseEntity<Void> clearWishlist(HttpServletRequest request) {
        wishlistService.clearWishlist(request);
        return ResponseEntity.noContent().build();
    }
}
