package com.ecommerce.e_commerce.common.utils;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {
    public String buildOtpEmail(String title, String message, String otp) {
        return """
                %s

                %s

                OTP Code: %s
                -------------------------
                This code will expire in 10 minutes.
                If you didn’t request this, please ignore this email.

                — E-Commerce Platform
                """.formatted(title, message, otp);
    }
}
