package com.ecommerce.e_commerce.security.auth.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOtpEmail(String to, String otp) throws MessagingException;

    void sendPasswordResetEmail(String to, String otp) throws MessagingException;
}
