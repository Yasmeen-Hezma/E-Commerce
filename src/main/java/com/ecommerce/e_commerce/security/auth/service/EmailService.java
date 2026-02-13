package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.commerce.order.model.Order;
import jakarta.mail.MessagingException;

import java.math.BigDecimal;

public interface EmailService {
    void sendOtpEmail(String to, String otp) throws MessagingException;

    void sendPasswordResetEmail(String to, String otp) throws MessagingException;

    void sendOrderConfirmationEmail(String to, Long orderId, BigDecimal orderTotal) throws MessagingException;
}
