package com.ecommerce.e_commerce.core.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressRequest {
    private Boolean useDefaultAddress;

    private String governorate;

    private String city;

    private String street;

    @Pattern(regexp = "^01[0-2,5]{1}[0-9]{8}$", message = "Invalid Egyptian phone number")
    private String phone;

    private String floorNumber;

    private String apartmentNumber;

    private String deliveryNotes;
}
