package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.common.utils.EmailTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private EmailTemplateBuilder emailTemplateBuilder;
    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendOtpEmail_ShouldSendOtpEmail_WhenValidRequest() throws MessagingException {
        // Arrange
        String to = "user@email.com";
        String otp = "123456";
        String emailContent = "Email Verification\n\nPlease use the following OTP to verify your email:\n\nOTP Code: 123456";
        when(emailTemplateBuilder.buildOtpEmail("Email Verification",
                "Please use the following OTP to verify your email:",
                otp)).thenReturn(emailContent);
        // Act
        emailService.sendOtpEmail(to, otp);
        // Assert
        verify(emailTemplateBuilder).buildOtpEmail("Email Verification",
                "Please use the following OTP to verify your email:",
                otp);
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sendOtpEmail_ShouldThrowException_WhenMessagingFails() {
        // Arrange
        String to = "user@email.com";
        String otp = "123456";
        String emailContent = "Email content";
        when(emailTemplateBuilder.buildOtpEmail(anyString(), anyString(), anyString()))
                .thenReturn(emailContent);
        doThrow(new MailSendException("SMTP error"))
                .when(javaMailSender).send(any(MimeMessage.class));
        // Act & Assert
        assertThatThrownBy(() -> emailService.sendOtpEmail(to, otp))
                .isInstanceOf(MailSendException.class)
                .hasMessage("SMTP error");
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendOtpEmail_ShouldPasswordResetEmail_WhenValidRequest() throws MessagingException {
        // Arrange
        String to = "user@email.com";
        String otp = "123456";
        String emailContent = "Password Reset Request\n\nPlease use the following OTP to reset your password:\n\nOTP Code: 654321";

        when(emailTemplateBuilder.buildOtpEmail(
                "Password Reset Request",
                "Please use the following OTP to reset your password:",
                otp
        )).thenReturn(emailContent);
        // Act
        emailService.sendPasswordResetEmail(to, otp);
        // Assert
        verify(emailTemplateBuilder).buildOtpEmail(
                "Password Reset Request",
                "Please use the following OTP to reset your password:",
                otp);
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_ShouldThrowException_WhenMessagingFails() {
        // Arrange
        String to = "user@email.com";
        String otp = "123456";
        String emailContent = "Email content";
        when(emailTemplateBuilder.buildOtpEmail(anyString(), anyString(), anyString()))
                .thenReturn(emailContent);
        doThrow(new MailSendException("SMTP error"))
                .when(javaMailSender).send(any(MimeMessage.class));
        // Act & Assert
        assertThatThrownBy(() -> emailService.sendPasswordResetEmail(to, otp))
                .isInstanceOf(MailSendException.class)
                .hasMessage("SMTP error");
        verify(javaMailSender).send(any(MimeMessage.class));
    }
}