package com.ecommerce.e_commerce.auth.auth.dto;

import com.ecommerce.e_commerce.core.user.enums.RoleEnum;
import com.ecommerce.e_commerce.core.validation.role.AllowedRoleEnum;
import jakarta.validation.Valid;
import lombok.*;

@Setter
@Getter
public class RegisterRequest {
    @AllowedRoleEnum
    private RoleEnum role;
    private String verifiedEmail;
    private String password;
    @Valid
    private CustomerRegistrationRequest customer;
    @Valid
    private SellerRegistrationRequest seller;
}
