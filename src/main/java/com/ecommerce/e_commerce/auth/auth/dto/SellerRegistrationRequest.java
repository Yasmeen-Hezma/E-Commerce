package com.ecommerce.e_commerce.auth.auth.dto;

import com.ecommerce.e_commerce.auth.auth.enums.SellerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerRegistrationRequest {
    private String businessAddress;
    private String shippingZone;
    private SellerType sellerType;
    private String brandName;
}
