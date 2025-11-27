package com.ecommerce.e_commerce.user.profile.dtos;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private String governorate;
    private String city;
    private String street;
    private String floorNumber;
    private String apartmentNumber;
}
