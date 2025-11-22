package com.ecommerce.e_commerce.auth.token.service;

import com.ecommerce.e_commerce.auth.auth.model.Role;
import com.ecommerce.e_commerce.auth.auth.repository.RoleRepository;
import com.ecommerce.e_commerce.auth.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import com.ecommerce.e_commerce.auth.token.dto.TokenResponse;
import com.ecommerce.e_commerce.auth.token.model.Token;
import com.ecommerce.e_commerce.auth.token.model.TokenType;
import com.ecommerce.e_commerce.auth.token.repository.TokenRepository;
import com.ecommerce.e_commerce.core.user.enums.RoleEnum;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    @Value("${jwt.token.expiration}")
    private long jwtExpiration;
    @Value("${jwt.refresh-token.expiration}")
    private long jwtRefreshExpiration;

    public String generateToken(String email, RoleEnum roleId, long expiration) {
        Role role = roleRepository.findByRoleEnum(roleId).orElseThrow();
        return Jwts.builder()
                .subject(email)
                .claim("role", role.getRoleEnum().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(JwtUtils.getSigningKey())
                .compact();
    }

    public String generateAccessToken(String email, RoleEnum roleId) {
        return generateToken(email, roleId, jwtExpiration);
    }

    public String generateRefreshToken(String email, RoleEnum roleId) {
        return generateToken(email, roleId, jwtRefreshExpiration);
    }

    public TokenResponse generateNewAccessToken(AuthUser user, String refreshToken) {
        RoleEnum roleId = user.getRoles().iterator().next().getRoleEnum();
        String newAccessToken = generateAccessToken(user.getEmail(), roleId);
        revokeAllUserTokens(user);
        saveToken(newAccessToken, user);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    public void saveToken(String tokenStr, AuthUser authUser) {
        Token token = Token.builder()
                .token(tokenStr)
                .authUser(authUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(AuthUser user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public boolean isTokenValidInDB(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }
}
