package com.ecommerce.e_commerce.security.auth.dto;

import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.validation.AllowedRoleEnum;
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
