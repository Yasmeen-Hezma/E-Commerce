package com.ecommerce.e_commerce.core.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@Builder
public class PaymentResponse {
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amount;
    private String currency;
    private Instant createdAt;
}
