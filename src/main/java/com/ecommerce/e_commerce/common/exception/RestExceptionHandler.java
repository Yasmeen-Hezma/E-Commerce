package com.ecommerce.e_commerce.common.exception;

import com.ecommerce.e_commerce.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.ecommerce.e_commerce.common.utils.Constants.ERROR_HANDLING_REQUEST;

@RestControllerAdvice
public class RestExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // Handle annotation errors
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError fieldError) {
                // fieldError -> a validation annotation fails on a specific field
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                // objectError -> validation fails at the class level
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> itemNotFound(ItemNotFoundException e) {
        return handleException(e.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedException(UnauthorizedException e) {
        return handleException(e.getMessage(), HttpStatus.UNAUTHORIZED, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateItemException.class)
    public ResponseEntity<ErrorResponse> duplicateItem(DuplicateItemException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> insufficientStock(InsufficientStockException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST, e.getStockIssues());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> emptyCart(EmptyCartException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ImageStorageException.class)
    public ResponseEntity<ErrorResponse> imageStorageException(ImageStorageException e) {
        return handleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PaymentAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> paymentAlreadyCompleted(PaymentAlreadyCompletedException e) {
        return handleException(e.getMessage(), HttpStatus.CONFLICT, null);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> invalidOrderStatus(InvalidOrderStatusException e) {
        return handleException(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidOrderTotalException.class)
    public ResponseEntity<ErrorResponse> invalidOrderTotal(InvalidOrderTotalException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> invalidPasswordException(InvalidPasswordException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PayPalOrderMismatchException.class)
    public ResponseEntity<ErrorResponse> PayPalOrderMismatch(PayPalOrderMismatchException e) {
        return handleException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PayPalApprovalUrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> PayPalApprovalUrlNotFound(PayPalApprovalUrlNotFoundException e) {
        return handleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PayPalCaptureException.class)
    public ResponseEntity<ErrorResponse> PayPalCaptureException(PayPalCaptureException e) {
        return handleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> InvalidOperationException(InvalidOperationException e) {
        return handleException(e.getMessage(), HttpStatus.CONFLICT, null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericException(Exception e) {
        return handleException(ERROR_HANDLING_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    private ResponseEntity<ErrorResponse> handleException(String message, HttpStatus httpStatus, Object details) {
        ErrorResponse error = ErrorResponse.builder()
                .status(httpStatus.value())
                .message(message)
                .timeStamp(System.currentTimeMillis())
                .details(details)
                .build();
        return new ResponseEntity<>(error, httpStatus);
    }
}
