package com.ecommerce.e_commerce.commerce.wishlist.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WishlistRequest {
    private long userId;
    List<WishlistItemRequest> items;
}
