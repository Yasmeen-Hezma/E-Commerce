package com.ecommerce.e_commerce.commerce.order.repository;

import com.ecommerce.e_commerce.commerce.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
