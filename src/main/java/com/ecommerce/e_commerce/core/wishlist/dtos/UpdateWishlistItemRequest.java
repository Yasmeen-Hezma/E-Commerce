package com.ecommerce.e_commerce.core.wishlist.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UpdateWishlistItemRequest {
    private List<WishlistItemRequest> wishlistItems;
}
