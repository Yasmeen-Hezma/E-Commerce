package com.ecommerce.e_commerce.core.common.exception;

public class PayPalOrderMismatchException extends RuntimeException {
    public PayPalOrderMismatchException(String message) {
        super(message);
    }
}
