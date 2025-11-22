package com.ecommerce.e_commerce.core.brand.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BrandResponse {
    private long id;
    private String name;
    private String image;
}
