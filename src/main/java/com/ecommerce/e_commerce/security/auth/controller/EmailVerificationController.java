package com.ecommerce.e_commerce.security.auth.controller;

import com.ecommerce.e_commerce.security.auth.dto.EmailRequest;
import com.ecommerce.e_commerce.security.auth.dto.OtpVerificationRequest;
import com.ecommerce.e_commerce.security.auth.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/email")
@Tag(name = "Email Verification", description = "Email Verification Management APIs")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "Send OTP to email address")
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody EmailRequest request) throws MessagingException {
        emailVerificationService.sendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    @Operation(summary = "Verify email using OTP")
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) throws MessagingException {
        emailVerificationService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok("Email verified successfully");
    }
}
