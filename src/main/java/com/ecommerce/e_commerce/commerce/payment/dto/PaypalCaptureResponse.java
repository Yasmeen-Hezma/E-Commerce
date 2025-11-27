package com.ecommerce.e_commerce.commerce.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaypalCaptureResponse {
    private long orderId;
    private String paypalOrderId;
    private String captureId;
    private String status;
    private BigDecimal amount;
}
