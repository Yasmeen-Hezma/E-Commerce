package com.ecommerce.e_commerce.auth.auth.controller;

import com.ecommerce.e_commerce.auth.auth.dto.EmailRequest;
import com.ecommerce.e_commerce.auth.auth.dto.ResetPasswordRequest;
import com.ecommerce.e_commerce.auth.auth.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecommerce.e_commerce.core.common.utils.Constants.IF_AN_ACCOUNT_EXISTS_A_PASSWORD_RESET_OTP_HAS_BEEN_SENT;
import static com.ecommerce.e_commerce.core.common.utils.Constants.PASSWORD_HAS_BEEN_RESET_SUCCESSFULLY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/password")
public class PasswordResetController {
    private final AuthenticationService authenticationService;

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailRequest request) throws MessagingException {
        authenticationService.initiatePasswordReset(request);
        return ResponseEntity.ok(IF_AN_ACCOUNT_EXISTS_A_PASSWORD_RESET_OTP_HAS_BEEN_SENT);
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(PASSWORD_HAS_BEEN_RESET_SUCCESSFULLY);
    }
}
