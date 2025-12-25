package com.ecommerce.e_commerce.commerce.wishlist.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UpdateWishlistItemRequest {
    private List<WishlistItemRequest> wishlistItems;
}
