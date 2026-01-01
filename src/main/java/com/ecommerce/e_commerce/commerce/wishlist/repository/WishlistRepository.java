package com.ecommerce.e_commerce.commerce.wishlist.repository;

import com.ecommerce.e_commerce.commerce.wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser_UserId(Long user_id);
}
