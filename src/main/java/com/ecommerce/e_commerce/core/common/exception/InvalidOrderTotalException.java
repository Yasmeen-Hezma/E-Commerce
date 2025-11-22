package com.ecommerce.e_commerce.core.common.exception;

public class InvalidOrderTotalException extends RuntimeException {
    public InvalidOrderTotalException(String message) {
        super(message);
    }
}
