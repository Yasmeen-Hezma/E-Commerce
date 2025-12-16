package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.common.exception.InvalidOtpException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.security.auth.enums.OtpPurpose;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.model.EmailVerification;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.security.auth.repository.EmailVerificationRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {
    @Mock
    private EmailVerificationRepository emailVerificationRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthUserRepository authUserRepository;
    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    private AuthUser authUser;
    private EmailVerification emailVerification;
    private final String email = "user@email.com";
    private final String validOtp = "123456";

    @BeforeEach
    void setUp() {
        authUser = AuthUser
                .builder()
                .email(email)
                .password("encodedPassword")
                .build();

        emailVerification = EmailVerification
                .builder()
                .email(email)
                .otp(validOtp)
                .purpose(OtpPurpose.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .verified(false)
                .build();
    }

    @Test
    void sendOtp_ShouldGenerateAndSendOtp_WhenValidEmail() throws MessagingException {
        // Arrange
        when(emailVerificationRepository.save(any(EmailVerification.class))).thenAnswer(invocation -> {
            EmailVerification verification = invocation.getArgument(0);
            assertThat(verification.getEmail()).isEqualTo(email);
            assertThat(verification.getOtp()).hasSize(6);
            assertThat(verification.getOtp()).matches("\\d{6}"); // 6-digits
            assertThat(verification.getPurpose()).isEqualTo(OtpPurpose.EMAIL_VERIFICATION);
            assertThat(verification.isVerified()).isFalse();
            assertThat(verification.getExpiresAt()).isAfter(Instant.now());
            return verification;
        });
        // Act
        emailVerificationService.sendOtp(email);
        // Assert
        verify(emailVerificationRepository).deleteByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        verify(emailVerificationRepository).save(any(EmailVerification.class));
        verify(emailService).sendOtpEmail(eq(email), anyString());
    }

    @Test
    void sendOtp_ShouldThrowException_WhenEmailServiceFails() throws MessagingException {
        // Arrange
        when(emailVerificationRepository.save(any(EmailVerification.class))).thenReturn(emailVerification);
        doThrow(new MailSendException("SMTP error"))
                .when(emailService).sendOtpEmail(anyString(), anyString());
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.sendOtp(email))
                .isInstanceOf(MailSendException.class)
                .hasMessageContaining("SMTP error");
        verify(emailVerificationRepository).deleteByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        verify(emailVerificationRepository).save(any(EmailVerification.class));
        verify(emailService).sendOtpEmail(eq(email), anyString());
    }

    @Test
    void verifyOtp_ShouldMarkAsVerified_WhenValidOtp() {
        // Arrange
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(emailVerification));
        when(emailVerificationRepository.save(any(EmailVerification.class)))
                .thenAnswer(invocation -> {
                    EmailVerification verification = invocation.getArgument(0);
                    assertThat(verification.isVerified()).isTrue();
                    return verification;
                });
        // Act
        emailVerificationService.verifyOtp(email, validOtp);
        // Assert
        assertThat(emailVerification.isVerified()).isTrue();
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        verify(emailVerificationRepository).save(any(EmailVerification.class));
    }

    @Test
    void verifyOtp_ShouldThrowException_WhenOtpNotFound() {
        // Arrange
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.verifyOtp(email, validOtp))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(OTP_NOT_FOUND_FOR_THIS_EMAIL);
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    @Test
    void verifyOtp_ShouldThrowException_WhenOtpExpired() {
        // Arrange
        emailVerification.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(emailVerification));
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.verifyOtp(email, validOtp))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining(OTP_EXPIRED);
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    @Test
    void verifyOtp_ShouldThrowException_WhenOtpIncorrect() {
        // Arrange
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(emailVerification));
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.verifyOtp(email, "wrong-otp"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining(INVALID_OTP);
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    @Test
    void sendPasswordResetOtp_ShouldSendOtp_WhenUserExists() throws MessagingException {
        // Arrange
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
        when(emailVerificationRepository.save(any(EmailVerification.class))).thenAnswer(invocation -> {
            EmailVerification verification = invocation.getArgument(0);
            assertThat(verification.getEmail()).isEqualTo(email);
            assertThat(verification.getOtp()).hasSize(6);
            assertThat(verification.getOtp()).matches("\\d{6}"); // 6-digits
            assertThat(verification.getPurpose()).isEqualTo(OtpPurpose.PASSWORD_RESET);
            assertThat(verification.isVerified()).isFalse();
            assertThat(verification.getExpiresAt()).isAfter(Instant.now());
            return verification;
        });
        // Act
        emailVerificationService.sendPasswordResetOtp(email);
        // Assert
        verify(authUserRepository).findByEmail(email);
        verify(emailVerificationRepository).deleteByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
        verify(emailVerificationRepository).save(any(EmailVerification.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void sendPasswordResetOtp_ShouldSendOtp_WhenUserNotFound() throws MessagingException {
        // Arrange
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.sendPasswordResetOtp(email))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(NO_USER_FOUND_WITH_THIS_EMAIL);
        verify(authUserRepository).findByEmail(email);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
        verify(emailService, never()).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void verifyPasswordResetOtp_ShouldMarkAsVerified_WhenValidOtp() {
        // Arrange
        emailVerification.setPurpose(OtpPurpose.PASSWORD_RESET);
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.of(emailVerification));
        when(emailVerificationRepository.save(any(EmailVerification.class)))
                .thenAnswer(invocation -> {
                    EmailVerification verification = invocation.getArgument(0);
                    assertThat(verification.isVerified()).isTrue();
                    return verification;
                });
        // Act
        emailVerificationService.verifyPasswordResetOtp(email, validOtp);
        // Assert
        assertThat(emailVerification.isVerified()).isTrue();
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
        verify(emailVerificationRepository).save(any(EmailVerification.class));
    }

    @Test
    void verifyPasswordResetOtp_ShouldThrowException_WhenOtpNotFound() {
        // Arrange
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.verifyPasswordResetOtp(email, validOtp))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(OTP_NOT_FOUND_FOR_THIS_EMAIL);
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    @Test
    void verifyPasswordResetOtp_ShouldThrowException_WhenOtpExpired() {
        // Arrange
        emailVerification.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.of(emailVerification));
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.verifyPasswordResetOtp(email, validOtp))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining(OTP_EXPIRED);
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    @Test
    void verifyPasswordResetOtp_ShouldThrowException_WhenOtpIncorrect() {
        // Arrange
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET))
                .thenReturn(Optional.of(emailVerification));
        // Act & Assert
        assertThatThrownBy(() -> emailVerificationService.verifyPasswordResetOtp(email, "wrong-otp"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining(INVALID_OTP);
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    @Test
    void isEmailVerified_ShouldReturnTrue_WhenEmailVerified() {
        // Arrange
        emailVerification.setVerified(true);
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(emailVerification));
        // Act
        boolean result = emailVerificationService.isEmailVerified(email);
        // Assert
        assertThat(result).isTrue();
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
    }

    @Test
    void isEmailVerified_ShouldReturnFalse_WhenEmailNotVerified() {
        // Arrange
        when(emailVerificationRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(emailVerification));
        // Act
        boolean result = emailVerificationService.isEmailVerified(email);
        // Assert
        assertThat(result).isFalse();
        verify(emailVerificationRepository).findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
    }

    @Test
    void removePasswordResetOtp_ShouldDeleteOtp_WhenCalled() {
        // Act
        emailVerificationService.removePasswordResetOtp(email);
        // Assert
        verify(emailVerificationRepository).deleteByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
    }
}