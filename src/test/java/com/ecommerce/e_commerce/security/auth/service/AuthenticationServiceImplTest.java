package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.security.auth.dto.*;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.security.token.dto.TokenResponse;
import com.ecommerce.e_commerce.security.token.service.TokenService;
import com.ecommerce.e_commerce.user.profile.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private TokenService tokenService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthUser authUser;
    private EmailRequest emailRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private final String email = "user@email.com";
    private final String accessToken = "access.token.jwt";
    private final String refreshToken = "refresh.token.jwt";

    @BeforeEach
    void setUp() {
        Role customerRole = Role
                .builder()
                .roleId(1L)
                .roleEnum(RoleEnum.CUSTOMER)
                .build();

        authUser = AuthUser
                .builder()
                .authUserId(1L)
                .email(email)
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .build();

        CustomerRegistrationRequest customer = CustomerRegistrationRequest
                .builder()
                .firstName("Yousef")
                .lastName("Mohamed")
                .phone("1234567890")
                .phoneCode(20)
                .build();

        registerRequest = RegisterRequest
                .builder()
                .verifiedEmail(email)
                .password("password123")
                .customer(customer)
                .role(RoleEnum.CUSTOMER)
                .build();

        loginRequest = LoginRequest
                .builder()
                .email(email)
                .password("password123")
                .build();

        emailRequest = EmailRequest
                .builder()
                .email(email)
                .build();

        resetPasswordRequest = ResetPasswordRequest
                .builder()
                .otp("123456")
                .email(email)
                .newPassword("newPassword123")
                .build();
    }

    @Test
    void register_ShouldReturnTokens_WhenValidRequest() {
        // Arrange
        when(userService.createUser(registerRequest)).thenReturn(authUser);
        when(tokenService.generateAccessToken(email, RoleEnum.CUSTOMER)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(email, RoleEnum.CUSTOMER)).thenReturn(refreshToken);
        // Act
        TokenResponse result = authenticationService.register(registerRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        verify(userService).createUser(registerRequest);
        verify(tokenService).generateAccessToken(email, RoleEnum.CUSTOMER);
        verify(tokenService).generateRefreshToken(email, RoleEnum.CUSTOMER);
        verify(tokenService).revokeAllUserTokens(authUser);
        verify(tokenService).saveToken(accessToken, authUser);
    }

    @Test
    void register_ShouldThrowException_WhenEmailNotVerified() {
        // Arrange
        doThrow(new UnauthorizedException(EMAIL_IS_NOT_VERIFIED))
                .when(userService).createUser(registerRequest);
        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(EMAIL_IS_NOT_VERIFIED);
        verify(userService).createUser(registerRequest);
        verify(tokenService, never()).generateAccessToken(email, RoleEnum.CUSTOMER);
        verify(tokenService, never()).generateRefreshToken(email, RoleEnum.CUSTOMER);
        verify(tokenService, never()).saveToken(accessToken, authUser);
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        doThrow(new DuplicateItemException(EMAIL_ALREADY_EXISTS))
                .when(userService).createUser(registerRequest);
        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(DuplicateItemException.class)
                .hasMessageContaining(EMAIL_ALREADY_EXISTS);
        verify(userService).createUser(registerRequest);
        verify(tokenService, never()).generateAccessToken(email, RoleEnum.CUSTOMER);
        verify(tokenService, never()).generateRefreshToken(email, RoleEnum.CUSTOMER);
        verify(tokenService, never()).saveToken(accessToken, authUser);
    }

    @Test
    void login_ShouldReturnTokens_WhenValidCredentials() {
        // Arrange
        when(userService.loginUser(loginRequest)).thenReturn(authUser);
        when(tokenService.generateAccessToken(email, RoleEnum.CUSTOMER)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(email, RoleEnum.CUSTOMER)).thenReturn(refreshToken);
        // Act
        TokenResponse result = authenticationService.login(loginRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        verify(userService).validateAuthUserByEmailAndPassword(email, "password123");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).loginUser(loginRequest);
        verify(tokenService).generateAccessToken(email, RoleEnum.CUSTOMER);
        verify(tokenService).generateRefreshToken(email, RoleEnum.CUSTOMER);
        verify(tokenService).revokeAllUserTokens(authUser);
        verify(tokenService).saveToken(accessToken, authUser);
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsInValid() {
        // Arrange
        doThrow(new ItemNotFoundException(USER_NOT_FOUND))
                .when(userService).validateAuthUserByEmailAndPassword(email, "wrongPassword");
        LoginRequest invalidLogin = LoginRequest
                .builder()
                .email(email)
                .password("wrongPassword")
                .build();
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.login(invalidLogin))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(userService).validateAuthUserByEmailAndPassword(email, "wrongPassword");
        verify(tokenService, never()).generateAccessToken(email, RoleEnum.CUSTOMER);
        verify(tokenService, never()).generateRefreshToken(email, RoleEnum.CUSTOMER);
        verify(tokenService, never()).saveToken(accessToken, authUser);
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenRefreshTokenValid() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(email);
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
        when(jwtService.isTokenValid(refreshToken, authUser)).thenReturn(true);
        TokenResponse newTokenResponse = new TokenResponse("new.access.token", refreshToken);
        when(tokenService.generateNewAccessToken(authUser, refreshToken)).thenReturn(newTokenResponse);
        // Act
        TokenResponse result = authenticationService.refreshToken(httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new.access.token");
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUsername(refreshToken);
        verify(authUserRepository).findByEmail(email);
        verify(jwtService).isTokenValid(refreshToken, authUser);
        verify(tokenService).generateNewAccessToken(authUser, refreshToken);
    }

    @Test
    void refreshToken_ShouldThrowException_WhenEmailNotExtracted() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(null);
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(httpRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUsername(refreshToken);
        verify(tokenService, never()).generateNewAccessToken(authUser, refreshToken);
    }

    @Test
    void refreshToken_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(email);
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(httpRequest))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(EMAIL_NOT_FOUND);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUsername(refreshToken);
        verify(authUserRepository).findByEmail(email);
        verify(tokenService, never()).generateNewAccessToken(authUser, refreshToken);
    }

    @Test
    void refreshToken_ShouldThrowException_WhenRefreshTokenInvalid() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(email);
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
        when(jwtService.isTokenValid(refreshToken, authUser)).thenReturn(false);
        when(tokenService.isTokenValidInDB(refreshToken)).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(httpRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUsername(refreshToken);
        verify(authUserRepository).findByEmail(email);
        verify(jwtService).isTokenValid(refreshToken, authUser);
        verify(tokenService).isTokenValidInDB(refreshToken);
        verify(tokenService, never()).generateNewAccessToken(authUser, refreshToken);
    }

    @Test
    void initiatePasswordReset_ShouldSendOtp_WhenValidEmail() throws MessagingException {
        // Act
        authenticationService.initiatePasswordReset(emailRequest);
        // Assert
        verify(emailVerificationService).sendPasswordResetOtp(email);
    }

    @Test
    void initiatePasswordReset_ShouldThrowException_WhenEmailServiceFails() throws MessagingException {
        // Arrange
        doThrow(new MailSendException("SMTP error"))
                .when(emailVerificationService).sendPasswordResetOtp(email);
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.initiatePasswordReset(emailRequest))
                .isInstanceOf(MailSendException.class)
                .hasMessageContaining("SMTP error");
        verify(emailVerificationService).sendPasswordResetOtp(email);
    }

    @Test
    void resetPassword_ShouldResetPassword_WhenValidOtp() {
        // Arrange
        when(userService.getAuthUserByEmail(email)).thenReturn(authUser);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(authUserRepository.save(authUser)).thenAnswer(invocation -> {
            AuthUser savedUser = invocation.getArgument(0);
            assertThat(savedUser.getPassword()).isEqualTo("encodedNewPassword");
            return savedUser;
        });
        // Act
        authenticationService.resetPassword(resetPasswordRequest);
        // Assert
        assertThat(authUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(emailVerificationService).verifyPasswordResetOtp(email, "123456");
        verify(userService).getAuthUserByEmail(email);
        verify(passwordEncoder).encode("newPassword123");
        verify(authUserRepository).save(authUser);
        verify(tokenService).revokeAllUserTokens(authUser);
        verify(emailVerificationService).removePasswordResetOtp(email);
    }
}