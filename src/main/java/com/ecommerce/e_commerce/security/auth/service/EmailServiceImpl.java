package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.common.utils.EmailTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailTemplateBuilder emailTemplateBuilder;

    @Override
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        String text = emailTemplateBuilder.buildOtpEmail("Email Verification",
                "Please use the following OTP to verify your email:",
                otp);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Email Verification - Your OTP Code");
        helper.setText(text, false);
        javaMailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String otp) throws MessagingException {
        String text = emailTemplateBuilder.buildOtpEmail("Password Reset Request",
                "Please use the following OTP to reset your password:",
                otp);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Password Reset - Your OTP Code");
        helper.setText(text, false);
        javaMailSender.send(message);
    }

    @Override
    public void sendOrderConfirmationEmail(String to, Long orderId, BigDecimal orderTotal) throws MessagingException {
        String text = emailTemplateBuilder.buildOrderConfirmationEmail(orderId, orderTotal.toString());
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Order Confirmed - Order #" + orderId);
        helper.setText(text, false);
        javaMailSender.send(message);
    }
}
