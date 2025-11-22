package com.ecommerce.e_commerce.core.cart.dtos;


import com.ecommerce.e_commerce.core.product.dtos.StockWarning;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private List<StockWarning> warnings;
}
