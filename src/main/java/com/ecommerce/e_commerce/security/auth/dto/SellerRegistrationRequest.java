package com.ecommerce.e_commerce.security.auth.dto;

import com.ecommerce.e_commerce.security.auth.enums.SellerType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerRegistrationRequest {
    private String businessAddress;
    private String shippingZone;
    private SellerType sellerType;
    private String brandName;
}
