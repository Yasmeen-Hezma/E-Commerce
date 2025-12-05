package com.ecommerce.e_commerce.security.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);

    Long extractUserIdFromToken(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
