package com.ecommerce.e_commerce.commerce.cart.repository;

import com.ecommerce.e_commerce.commerce.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
