package com.ecommerce.e_commerce.core.wishlist.service;

import com.ecommerce.e_commerce.core.wishlist.dtos.UpdateWishlistItemRequest;
import com.ecommerce.e_commerce.core.wishlist.dtos.WishlistItemRequest;
import com.ecommerce.e_commerce.core.wishlist.dtos.WishlistItemResponse;
import com.ecommerce.e_commerce.core.wishlist.dtos.WishlistResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface WishlistService {
    void clearWishlist(HttpServletRequest request);

    WishlistResponse syncWishlistSnapshot(HttpServletRequest request, UpdateWishlistItemRequest updateRequest);

    WishlistResponse getWishlistByUser(HttpServletRequest request);

    WishlistItemResponse addItemToWishlist(HttpServletRequest request, WishlistItemRequest wishlistItemRequest);
}
