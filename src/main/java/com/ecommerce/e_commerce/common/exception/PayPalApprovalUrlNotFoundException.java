package com.ecommerce.e_commerce.common.exception;

public class PayPalApprovalUrlNotFoundException extends RuntimeException {
    public PayPalApprovalUrlNotFoundException(String message) {
        super(message);
    }
}
