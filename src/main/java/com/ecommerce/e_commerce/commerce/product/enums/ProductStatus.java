package com.ecommerce.e_commerce.commerce.product.enums;

import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.ecommerce.e_commerce.common.utils.Constants.PRODUCT_STATUS_NOT_FOUND;

@AllArgsConstructor
@Getter
public enum ProductStatus {
    OUT_OF_STOCK(0),
    AVAILABLE(1),
    DISCONTINUED(2);
    private final Integer value;

    public static ProductStatus fromValue(int value) {
        for (ProductStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new ItemNotFoundException(PRODUCT_STATUS_NOT_FOUND);
    }
}
