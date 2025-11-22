package com.ecommerce.e_commerce.auth.auth.service;

import com.ecommerce.e_commerce.auth.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.auth.token.repository.TokenRepository;
import com.ecommerce.e_commerce.auth.token.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String jwt = JwtUtils.extractTokenFromHeader(request);
        tokenRepository.findByToken(jwt).ifPresent(token -> {
            tokenService.revokeAllUserTokens(token.getAuthUser());
            SecurityContextHolder.clearContext();
        });

    }
}
