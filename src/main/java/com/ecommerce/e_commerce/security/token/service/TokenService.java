package com.ecommerce.e_commerce.security.token.service;

import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.token.dto.TokenResponse;

public interface TokenService {
    String generateAccessToken(String email, RoleEnum roleId);

    String generateRefreshToken(String email, RoleEnum roleId);

    TokenResponse generateNewAccessToken(AuthUser user, String refreshToken);

    void saveToken(String tokenStr, AuthUser authUser);

    void revokeAllUserTokens(AuthUser user);

    boolean isTokenValidInDB(String token);
}
