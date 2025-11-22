package com.ecommerce.e_commerce.auth.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellerType {
    BUSINESS(0), INDIVIDUAL(1);
    private final int value;
}
