package com.ecommerce.e_commerce.core.validation.role;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedRoleEnumValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedRoleEnum {
    String message() default "Role is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
