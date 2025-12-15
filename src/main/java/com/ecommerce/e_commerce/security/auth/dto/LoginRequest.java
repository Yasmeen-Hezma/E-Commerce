package com.ecommerce.e_commerce.security.auth.dto;

import lombok.*;

@Setter
@Getter
@Builder
public class LoginRequest {
    private String email;
    private String password;
}

