package com.ecommerce.e_commerce.commerce.payment.dto;

import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusResponse {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paypalOrderId;
    private String captureId;
    private BigDecimal amount;
    private String currency;
    private Instant createdAt;
    private Instant updatedAt;
    private String orderStatus;
}
