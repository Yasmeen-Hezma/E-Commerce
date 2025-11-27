package com.ecommerce.e_commerce.common.exception;

public class PayPalOrderMismatchException extends RuntimeException {
    public PayPalOrderMismatchException(String message) {
        super(message);
    }
}
