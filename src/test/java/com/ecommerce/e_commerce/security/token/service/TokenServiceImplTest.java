package com.ecommerce.e_commerce.security.token.service;

import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.auth.repository.RoleRepository;
import com.ecommerce.e_commerce.security.token.dto.TokenResponse;
import com.ecommerce.e_commerce.security.token.model.Token;
import com.ecommerce.e_commerce.security.token.enums.TokenType;
import com.ecommerce.e_commerce.security.token.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private AuthUser authUser;
    private Token token;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        // 24 hours
        long jwtExpiration = 86400000L;
        ReflectionTestUtils.setField(tokenService, "jwtExpiration", jwtExpiration);
        // 7 days
        long jwtRefreshExpiration = 604800000L;
        ReflectionTestUtils.setField(tokenService, "jwtRefreshExpiration", jwtRefreshExpiration);
        customerRole = Role
                .builder()
                .roleId(1L)
                .roleEnum(RoleEnum.CUSTOMER)
                .build();
        authUser = AuthUser
                .builder()
                .authUserId(1L)
                .email("Yousef@email.com")
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .lastLogin(Instant.now())
                .build();
        token = Token.builder()
                .token("sample.jwt.token")
                .authUser(authUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    @Test
    void generateAccessToken_ShouldReturnValidJWT_WhenValidCredentials() {
        // Arrange
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.of(customerRole));
        // Act
        String result = tokenService.generateAccessToken("Yousef@email.com", RoleEnum.CUSTOMER);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).contains(".");
        assertThat(result.split("\\.")).hasSize(3);
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
    }

    @Test
    void generateAccessToken_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> tokenService.generateAccessToken("Yousef@email.com", RoleEnum.CUSTOMER))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(ROLE_NOT_FOUND);
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
    }

    @Test
    void generateRefreshToken_ShouldReturnValidJWT_WhenValidCredentials() {
        // Arrange
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.of(customerRole));
        // Act
        String result = tokenService.generateRefreshToken("Yousef@email.com", RoleEnum.CUSTOMER);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).contains(".");
        assertThat(result.split("\\.")).hasSize(3);
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
    }

    @Test
    void generateRefreshToken_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> tokenService.generateRefreshToken("Yousef@email.com", RoleEnum.CUSTOMER))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(ROLE_NOT_FOUND);
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
    }

    @Test
    void generateNewAccessToken_ShouldReturnTokenResponse_WhenValidRefreshToken() {
        // Arrange
        String oldRefreshToken = "old.refresh.token";
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of(token));
        when(tokenRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token savedToken = invocation.getArgument(0);
            savedToken.setId(2L);
            return savedToken;
        });
        // Act
        TokenResponse result = tokenService.generateNewAccessToken(authUser, oldRefreshToken);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getAccessToken()).isNotEmpty();
        assertThat(result.getRefreshToken()).isEqualTo(oldRefreshToken);
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
        verify(tokenRepository).findAllValidTokensByUser(1L);
        verify(tokenRepository).saveAll(anyList());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void generateNewAccessToken_ShouldRevokeOldTokens_WhenGeneratingNew() {
        // Arrange
        String oldRefreshToken = "old.refresh.token";
        Token oldToken = Token.builder()
                .token("old.jwt.token")
                .authUser(authUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of(oldToken));
        when(tokenRepository.saveAll(anyList())).thenAnswer(invocation ->
        {
            List<Token> tokens = invocation.getArgument(0);
            tokens.forEach(t -> {
                assertThat(t.isRevoked()).isTrue();
                assertThat(t.isExpired()).isTrue();
            });
            return tokens;
        });
        when(tokenRepository.save(any(Token.class))).thenReturn(token);
        // Act
        TokenResponse result = tokenService.generateNewAccessToken(authUser, oldRefreshToken);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getAccessToken()).isNotEmpty();
        assertThat(result.getRefreshToken()).isEqualTo(oldRefreshToken);
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
        verify(tokenRepository).findAllValidTokensByUser(1L);
        verify(tokenRepository).saveAll(anyList());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void saveToken_ShouldSaveTokenToDB_WhenValidTokenAndUser() {
        // Arrange
        String tokenStr = "new.jwt.token";
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token savedToken = invocation.getArgument(0);
            assertThat(savedToken.getToken()).isEqualTo(tokenStr);
            assertThat(savedToken.getAuthUser()).isEqualTo(authUser);
            assertThat(savedToken.getTokenType()).isEqualTo(TokenType.BEARER);
            assertThat(savedToken.isExpired()).isFalse();
            assertThat(savedToken.isRevoked()).isFalse();
            savedToken.setId(1L);
            return savedToken;
        });
        // Act
        tokenService.saveToken(tokenStr, authUser);
        // Assert
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void revokeAllUserTokens_ShouldRevokeAllTokens_WhenValidTokensExist() {
        // Arrange
        Token token1 = Token.builder()
                .token("sample1.jwt.token")
                .authUser(authUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        Token token2 = Token.builder()
                .token("sample2.jwt.token")
                .authUser(authUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        when(tokenRepository.findAllValidTokensByUser(1L)).thenReturn(List.of(token1, token2));
        when(tokenRepository.saveAll(anyList())).thenAnswer(invocation ->
        {
            List<Token> tokens = invocation.getArgument(0);
            tokens.forEach(t -> {
                assertThat(t.isRevoked()).isTrue();
                assertThat(t.isExpired()).isTrue();
            });
            return tokens;
        });
        // Act
        tokenService.revokeAllUserTokens(authUser);
        // Assert
        assertThat(token1.isRevoked()).isTrue();
        assertThat(token1.isExpired()).isTrue();
        assertThat(token2.isRevoked()).isTrue();
        assertThat(token2.isExpired()).isTrue();
        verify(tokenRepository).findAllValidTokensByUser(1L);
        verify(tokenRepository).saveAll(anyList());
    }

    @Test
    void isTokenValidInDB_ShouldReturnTrue_WhenTokenValidInDB() {
        // Arrange
        when(tokenRepository.findByToken("sample.jwt.token")).thenReturn(Optional.of(token));
        // Act
        boolean result = tokenService.isTokenValidInDB("sample.jwt.token");
        // Assert
        assertThat(result).isTrue();
        verify(tokenRepository).findByToken("sample.jwt.token");
    }

    @Test
    void isTokenValidInDB_ShouldReturnFalse_WhenTokenExpired() {
        // Arrange
        token.setExpired(true);
        when(tokenRepository.findByToken("sample.jwt.token")).thenReturn(Optional.of(token));
        // Act
        boolean result = tokenService.isTokenValidInDB("sample.jwt.token");
        // Assert
        assertThat(result).isFalse();
        verify(tokenRepository).findByToken("sample.jwt.token");
    }
    @Test
    void isTokenValidInDB_ShouldReturnFalse_WhenTokenRevoked() {
        // Arrange
        token.setRevoked(true);
        when(tokenRepository.findByToken("sample.jwt.token")).thenReturn(Optional.of(token));
        // Act
        boolean result = tokenService.isTokenValidInDB("sample.jwt.token");
        // Assert
        assertThat(result).isFalse();
        verify(tokenRepository).findByToken("sample.jwt.token");
    }
    @Test
    void isTokenValidInDB_ShouldReturnFalse_WhenTokenNotFound() {
        // Arrange
        when(tokenRepository.findByToken("not-found.jwt.token")).thenReturn(Optional.empty());
        // Act
        boolean result = tokenService.isTokenValidInDB("not-found.jwt.token");
        // Assert
        assertThat(result).isFalse();
        verify(tokenRepository).findByToken("not-found.jwt.token");
    }
}