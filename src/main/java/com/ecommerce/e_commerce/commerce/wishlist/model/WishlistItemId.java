package com.ecommerce.e_commerce.commerce.wishlist.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@Embeddable
public class WishlistItemId implements Serializable {
    private Long wishlist;
    private Long product;
}
