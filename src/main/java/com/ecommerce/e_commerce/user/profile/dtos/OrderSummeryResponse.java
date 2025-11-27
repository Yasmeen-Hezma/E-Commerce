package com.ecommerce.e_commerce.user.profile.dtos;

import com.ecommerce.e_commerce.commerce.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Setter
@Getter
public class OrderSummeryResponse {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal orderTotal;
    private Integer itemCount;
    private Instant createdAt;
    private Instant updatedAt;
    // shipping info
    private String shippingCity;
    private String shippingGovernorate;
    // payment info
    private String paymentMethod;
    private String paymentStatus;
}
