package com.ecommerce.e_commerce.security.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CustomerRegistrationRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private Integer phoneCode;
}
