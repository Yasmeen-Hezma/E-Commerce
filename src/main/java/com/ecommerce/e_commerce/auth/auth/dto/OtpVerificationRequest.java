package com.ecommerce.e_commerce.auth.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpVerificationRequest {
    private String email;
    private String otp;
}
