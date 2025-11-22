package com.ecommerce.e_commerce.core.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaypalOrderResponse {
    private String paypalOrderId;
    private long orderId;
    private String status;
    private String approvalUrl;
    private BigDecimal amount;
    private String currency;
}
