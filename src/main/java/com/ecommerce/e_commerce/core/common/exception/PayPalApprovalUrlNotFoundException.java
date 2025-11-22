package com.ecommerce.e_commerce.core.common.exception;

public class PayPalApprovalUrlNotFoundException extends RuntimeException {
    public PayPalApprovalUrlNotFoundException(String message) {
        super(message);
    }
}
