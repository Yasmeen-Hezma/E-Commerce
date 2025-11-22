package com.ecommerce.e_commerce.core.payment.service;

import com.ecommerce.e_commerce.core.order.dto.OrderResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaypalCaptureResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaypalOrderResponse;

public interface PaymentService {
    PaypalOrderResponse createPaypalPayment(Long orderId);

    PaypalCaptureResponse capturePayPalPayment(Long orderId, String paypalOrderId);

    void handlePaymentFailure(Long orderId);

    PaymentStatusResponse getPaymentStatus(Long orderId);

    OrderResponse createCODPayment(Long orderId);

    void completeCODPayment(Long orderId);
}
