package com.ecommerce.e_commerce.core.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockWarningType {
    OUT_OF_STOCK(0),
    LIMITED_STOCK(1),
    DISCONTINUED(2);
    private final int value;
}
