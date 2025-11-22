package com.ecommerce.e_commerce.core.user.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN(0),
    SELLER(1),
    CUSTOMER(2);
    private final int value;

}