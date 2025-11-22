package com.ecommerce.e_commerce.core.wishlist.repository;

import com.ecommerce.e_commerce.core.wishlist.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}
