package com.ecommerce.e_commerce.common.utils;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {
    public String buildOtpEmail(String title, String message, String otp) {
        return """
                %s

                %s

                OTP Code: %s
                -------------------------
                This code will expire in 10 minutes.
                If you didn’t request this, please ignore this email.

                — E-Commerce Platform
                """.formatted(title, message, otp);
    }

    public String buildOrderConfirmationEmail(Long orderId, String totalAmount) {
        return """
                Order Confirmation

                Your order has been successfully placed.

                Order ID: %s
                Total Amount: %s
                -------------------------
                We will notify you once your order is shipped.

                — E-Commerce Platform
                """.formatted(orderId, totalAmount);
    }
}
