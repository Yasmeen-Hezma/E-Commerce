package com.ecommerce.e_commerce.security.auth.validation;

import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.EnumSet;
import java.util.Set;

public class AllowedRoleEnumValidator implements ConstraintValidator<AllowedRoleEnum, RoleEnum> {

    private final Set<RoleEnum> allowedRoles = EnumSet.of(
            RoleEnum.CUSTOMER,
            RoleEnum.SELLER
    );

    @Override
    public boolean isValid(RoleEnum value, ConstraintValidatorContext context) {
        return value != null && allowedRoles.contains(value);
    }
}
