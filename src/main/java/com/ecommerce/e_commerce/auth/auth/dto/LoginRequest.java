package com.ecommerce.e_commerce.auth.auth.dto;

import lombok.*;

@Setter
@Getter
public class LoginRequest {
    private String email;
    private String password;
}

