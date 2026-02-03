package com.ecommerce.e_commerce.commerce.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineCaptureResponse {
    private long orderId;
    private String externalPaymentId;
    private String captureId;
    private String status;
    private BigDecimal amount;
}
