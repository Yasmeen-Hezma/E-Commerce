package com.ecommerce.e_commerce.core.product.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private Integer status;
    private String category;
    private String brand;
    private Instant createdAt;
}
