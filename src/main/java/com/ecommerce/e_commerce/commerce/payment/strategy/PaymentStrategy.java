package com.ecommerce.e_commerce.commerce.payment.strategy;

import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;

public interface PaymentStrategy {
    PaymentMethod getPaymentMethod();
}
