package com.ecommerce.e_commerce.commerce.wishlist.repository;

import com.ecommerce.e_commerce.commerce.wishlist.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}
