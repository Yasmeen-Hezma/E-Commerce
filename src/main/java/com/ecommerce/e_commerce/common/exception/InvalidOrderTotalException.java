package com.ecommerce.e_commerce.common.exception;

public class InvalidOrderTotalException extends RuntimeException {
    public InvalidOrderTotalException(String message) {
        super(message);
    }
}
