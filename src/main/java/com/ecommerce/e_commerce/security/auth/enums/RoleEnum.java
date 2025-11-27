package com.ecommerce.e_commerce.security.auth.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN(0),
    SELLER(1),
    CUSTOMER(2);
    private final int value;

}