package com.ecommerce.e_commerce.auth.auth.service;

import com.ecommerce.e_commerce.auth.auth.dto.EmailRequest;
import com.ecommerce.e_commerce.auth.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.auth.auth.dto.RegisterRequest;
import com.ecommerce.e_commerce.auth.auth.dto.ResetPasswordRequest;
import com.ecommerce.e_commerce.auth.token.dto.TokenResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse register(RegisterRequest registerRequest);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshToken(HttpServletRequest request);

    void initiatePasswordReset(EmailRequest request) throws MessagingException;

    void resetPassword(ResetPasswordRequest request);
}
