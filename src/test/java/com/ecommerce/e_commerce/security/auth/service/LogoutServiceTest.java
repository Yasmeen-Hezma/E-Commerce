package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.token.model.Token;
import com.ecommerce.e_commerce.security.token.repository.TokenRepository;
import com.ecommerce.e_commerce.security.token.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private LogoutService logoutService;

    private AuthUser authUser;
    private Token token;

    @BeforeEach
    void setUp() {
        authUser = AuthUser
                .builder()
                .authUserId(1L)
                .email("test@email.com")
                .password("encodedPassword")
                .build();

        token = Token
                .builder()
                .id(1L)
                .authUser(authUser)
                .token("valid.jwt.token")
                .expired(false)
                .revoked(false)
                .build();
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void logout_ShouldRevokeTokens_WhenValidToken() {
        // Arrange
        when(httpRequest.getHeader("Authorization"))
                .thenReturn("Bearer valid.jwt.token");
        when(tokenRepository.findByToken("valid.jwt.token"))
                .thenReturn(Optional.of(token));
        // Act
        logoutService.logout(httpRequest, httpResponse, authentication);
        // Assert
        verify(tokenService).revokeAllUserTokens(authUser);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(httpRequest).getHeader("Authorization");
        verify(tokenRepository).findByToken("valid.jwt.token");
    }

    @Test
    void logout_ShouldNotRevokeTokens_WhenInValidToken() {
        // Arrange
        when(httpRequest.getHeader("Authorization"))
                .thenReturn("Bearer invalid.token");
        when(tokenRepository.findByToken("invalid.token"))
                .thenReturn(Optional.empty());
        // Act
        logoutService.logout(httpRequest, httpResponse, authentication);
        // Assert
        verify(tokenService, never()).revokeAllUserTokens(authUser);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(httpRequest).getHeader("Authorization");
        verify(tokenRepository).findByToken("invalid.token");
    }
}