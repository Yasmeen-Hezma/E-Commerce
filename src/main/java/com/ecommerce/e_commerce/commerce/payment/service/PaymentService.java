package com.ecommerce.e_commerce.commerce.payment.service;

import com.ecommerce.e_commerce.commerce.order.dto.OrderResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.OnlineCaptureResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.OnlinePaymentResponse;

public interface PaymentService {
    OnlinePaymentResponse createPaypalPayment(Long orderId);

    OnlineCaptureResponse capturePayPalPayment(Long orderId, String paypalOrderId);

    void handlePaymentFailure(Long orderId);

    PaymentStatusResponse getPaymentStatus(Long orderId);

    OrderResponse createCODPayment(Long orderId);

    void completeCODPayment(Long orderId);
}
