package com.ecommerce.e_commerce.security.token.service;

import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.auth.repository.RoleRepository;
import com.ecommerce.e_commerce.security.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.token.dto.TokenResponse;
import com.ecommerce.e_commerce.security.token.model.Token;
import com.ecommerce.e_commerce.security.token.enums.TokenType;
import com.ecommerce.e_commerce.security.token.repository.TokenRepository;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.ecommerce.e_commerce.common.utils.Constants.ROLE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    @Value("${jwt.token.expiration}")
    private long jwtExpiration;
    @Value("${jwt.refresh-token.expiration}")
    private long jwtRefreshExpiration;

    @Override
    public String generateAccessToken(String email, RoleEnum roleId) {
        return generateToken(email, roleId, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(String email, RoleEnum roleId) {
        return generateToken(email, roleId, jwtRefreshExpiration);
    }

    @Override
    public TokenResponse generateNewAccessToken(AuthUser user, String refreshToken) {
        RoleEnum roleId = user.getRoles().iterator().next().getRoleEnum();
        String newAccessToken = generateAccessToken(user.getEmail(), roleId);
        revokeAllUserTokens(user);
        saveToken(newAccessToken, user);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Override
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

    @Override
    public void revokeAllUserTokens(AuthUser user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUserId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public boolean isTokenValidInDB(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    private String generateToken(String email, RoleEnum roleId, long expiration) {
        Role role = roleRepository.findByRoleEnum(roleId)
                .orElseThrow(() -> new ItemNotFoundException(ROLE_NOT_FOUND));
        return Jwts.builder()
                .subject(email)
                .claim("role", role.getRoleEnum().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(JwtUtils.getSigningKey())
                .compact();
    }

}
