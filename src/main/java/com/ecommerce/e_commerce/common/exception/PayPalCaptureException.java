package com.ecommerce.e_commerce.common.exception;

public class PayPalCaptureException extends RuntimeException{
   public PayPalCaptureException(String message){
        super(message);
    }
}
