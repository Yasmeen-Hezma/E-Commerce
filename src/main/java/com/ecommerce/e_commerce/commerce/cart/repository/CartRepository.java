package com.ecommerce.e_commerce.commerce.cart.repository;

import com.ecommerce.e_commerce.commerce.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long user_id);
}
