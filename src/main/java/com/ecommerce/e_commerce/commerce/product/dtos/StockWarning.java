package com.ecommerce.e_commerce.commerce.product.dtos;

import com.ecommerce.e_commerce.commerce.product.enums.StockWarningType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockWarning {
    private long productId;
    private String productName;
    @JsonIgnore
    private StockWarningType type;

    @JsonProperty("type")
    public int getTypeValue() {
        return type.getValue();
    }
}
