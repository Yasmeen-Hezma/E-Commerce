package com.ecommerce.e_commerce.commerce.payment.service;

import com.ecommerce.e_commerce.commerce.order.dto.OrderResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.PaypalCaptureResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.PaypalOrderResponse;

public interface PaymentService {
    PaypalOrderResponse createPaypalPayment(Long orderId);

    PaypalCaptureResponse capturePayPalPayment(Long orderId, String paypalOrderId);

    void handlePaymentFailure(Long orderId);

    PaymentStatusResponse getPaymentStatus(Long orderId);

    OrderResponse createCODPayment(Long orderId);

    void completeCODPayment(Long orderId);
}
