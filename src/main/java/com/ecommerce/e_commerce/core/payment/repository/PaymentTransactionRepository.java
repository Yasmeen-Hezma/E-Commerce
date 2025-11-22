package com.ecommerce.e_commerce.core.payment.repository;

import com.ecommerce.e_commerce.core.order.model.Order;
import com.ecommerce.e_commerce.core.payment.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrder(Order order);
}
