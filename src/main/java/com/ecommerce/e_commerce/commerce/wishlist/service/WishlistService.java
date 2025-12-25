package com.ecommerce.e_commerce.commerce.wishlist.service;

import com.ecommerce.e_commerce.commerce.wishlist.dto.UpdateWishlistItemRequest;
import com.ecommerce.e_commerce.commerce.wishlist.dto.WishlistItemRequest;
import com.ecommerce.e_commerce.commerce.wishlist.dto.WishlistItemResponse;
import com.ecommerce.e_commerce.commerce.wishlist.dto.WishlistResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface WishlistService {
    void clearWishlist(HttpServletRequest request);

    WishlistResponse syncWishlistSnapshot(HttpServletRequest request, UpdateWishlistItemRequest updateRequest);

    WishlistResponse getWishlistByUser(HttpServletRequest request);

    WishlistItemResponse addItemToWishlist(HttpServletRequest request, WishlistItemRequest wishlistItemRequest);
}
