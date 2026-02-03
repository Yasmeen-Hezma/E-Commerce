package com.ecommerce.e_commerce.commerce.payment.strategy.online;

import com.ecommerce.e_commerce.commerce.payment.dto.OnlineCaptureResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.OnlinePaymentResponse;
import com.ecommerce.e_commerce.commerce.payment.strategy.PaymentStrategy;

public interface OnlinePaymentStrategy extends PaymentStrategy {
    OnlinePaymentResponse createPayment(Long orderId);
    OnlineCaptureResponse capturePayment(Long orderId, String externalPaymentId);
}
