package com.ecommerce.e_commerce.security.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerRegistrationRequest {
    private String firstname;
    private String lastname;
    private String phone;
    private Integer phoneCode;
}
