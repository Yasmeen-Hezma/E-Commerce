package com.ecommerce.e_commerce.core.cart.repository;

import com.ecommerce.e_commerce.core.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
