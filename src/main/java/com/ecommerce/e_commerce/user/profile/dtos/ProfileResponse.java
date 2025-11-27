package com.ecommerce.e_commerce.user.profile.dtos;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private Integer phoneCode;
    private String email;
    private AddressResponse address;
    private Boolean hasCompleteAddress;
}
