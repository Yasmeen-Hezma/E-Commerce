package com.ecommerce.e_commerce.core.order.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDto {
    private String governorate;
    private String city;
    private String street;
    private String floorNumber;
    private String apartmentNumber;
    private String phone;
    private String deliveryNotes;
}
