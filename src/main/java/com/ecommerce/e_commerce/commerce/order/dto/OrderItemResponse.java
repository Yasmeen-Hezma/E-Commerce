package com.ecommerce.e_commerce.commerce.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String image;
    private Integer quantity;
    private BigDecimal price;
}
