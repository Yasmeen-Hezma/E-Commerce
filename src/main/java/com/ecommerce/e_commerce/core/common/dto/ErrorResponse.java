package com.ecommerce.e_commerce.core.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private long timeStamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object details;
}
