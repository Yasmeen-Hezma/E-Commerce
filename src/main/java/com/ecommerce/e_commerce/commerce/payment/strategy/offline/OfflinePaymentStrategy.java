package com.ecommerce.e_commerce.commerce.payment.strategy.offline;

import com.ecommerce.e_commerce.commerce.order.dto.OrderResponse;
import com.ecommerce.e_commerce.commerce.payment.strategy.PaymentStrategy;

public interface OfflinePaymentStrategy extends PaymentStrategy {
    OrderResponse createPayment(Long orderId);

    void completePayment(Long orderId);
}
