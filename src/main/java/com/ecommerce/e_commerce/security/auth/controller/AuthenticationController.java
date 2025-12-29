package com.ecommerce.e_commerce.security.auth.controller;

import com.ecommerce.e_commerce.security.auth.service.AuthenticationService;
import com.ecommerce.e_commerce.security.token.dto.TokenResponse;
import com.ecommerce.e_commerce.security.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.security.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication Management APIs")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @Operation(summary = "Authenticate user and generate access token")
    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
