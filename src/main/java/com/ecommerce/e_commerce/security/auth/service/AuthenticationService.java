package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.security.auth.dto.EmailRequest;
import com.ecommerce.e_commerce.security.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.security.auth.dto.RegisterRequest;
import com.ecommerce.e_commerce.security.auth.dto.ResetPasswordRequest;
import com.ecommerce.e_commerce.security.token.dto.TokenResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse register(RegisterRequest registerRequest);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshToken(HttpServletRequest request);

    void initiatePasswordReset(EmailRequest request) throws MessagingException;

    void resetPassword(ResetPasswordRequest request);
}
