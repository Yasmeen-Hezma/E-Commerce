package com.ecommerce.e_commerce.auth.auth.service;

import jakarta.mail.MessagingException;

public interface EmailVerificationService {
    void sendOtp(String email) throws MessagingException;

    void verifyOtp(String email, String otp);

    void sendPasswordResetOtp(String email) throws MessagingException;

    void verifyPasswordResetOtp(String email, String otp);

    boolean isEmailVerified(String email);

    void removePasswordResetOtp(String email);
}
