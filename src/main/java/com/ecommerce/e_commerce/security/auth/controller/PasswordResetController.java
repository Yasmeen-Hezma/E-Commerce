package com.ecommerce.e_commerce.security.auth.controller;

import com.ecommerce.e_commerce.security.auth.dto.EmailRequest;
import com.ecommerce.e_commerce.security.auth.dto.ResetPasswordRequest;
import com.ecommerce.e_commerce.security.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecommerce.e_commerce.common.utils.Constants.IF_AN_ACCOUNT_EXISTS_A_PASSWORD_RESET_OTP_HAS_BEEN_SENT;
import static com.ecommerce.e_commerce.common.utils.Constants.PASSWORD_HAS_BEEN_RESET_SUCCESSFULLY;

@RestController
@RequestMapping("/auth/password")
@Tag(name = "Password Reset", description = "Forget And Reset Password APIs")
@RequiredArgsConstructor
public class PasswordResetController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Send password reset OTP to email")
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailRequest request) throws MessagingException {
        authenticationService.initiatePasswordReset(request);
        return ResponseEntity.ok(IF_AN_ACCOUNT_EXISTS_A_PASSWORD_RESET_OTP_HAS_BEEN_SENT);
    }

    @Operation(summary = "Reset password using OTP")
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(PASSWORD_HAS_BEEN_RESET_SUCCESSFULLY);
    }
}
