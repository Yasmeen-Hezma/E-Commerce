package com.ecommerce.e_commerce.core.common.exception;

public class PayPalCaptureException extends RuntimeException{
   public PayPalCaptureException(String message){
        super(message);
    }
}
