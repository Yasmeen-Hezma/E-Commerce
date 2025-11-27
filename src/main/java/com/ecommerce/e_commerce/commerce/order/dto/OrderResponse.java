package com.ecommerce.e_commerce.commerce.order.dto;

import com.ecommerce.e_commerce.commerce.payment.dto.PaymentResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Setter
@Getter
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private String status;
    private BigDecimal totalPrice;
    private Instant createdAt;
    private PaymentResponse payment;
    private UserAddressDto address;
}
