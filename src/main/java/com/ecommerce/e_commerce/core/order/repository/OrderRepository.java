package com.ecommerce.e_commerce.core.order.repository;

import com.ecommerce.e_commerce.core.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
