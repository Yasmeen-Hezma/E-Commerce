package com.ecommerce.e_commerce.core.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    public static final String FILE_SIZE_EXCEEDS_THE_MAXIMUM_LIMIT = "File size exceeds the maximum limit";
    public static final String INVALID_FILE_TYPE = "Invalid file type";
    private List<String> allowedTypes;
    private Long maxSize;
    private boolean notEmpty;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.allowedTypes = List.of(constraintAnnotation.allowedTypes());
        this.maxSize = constraintAnnotation.maxSize();
        this.notEmpty = constraintAnnotation.notEmpty();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return !notEmpty;
        }
        return isFileSizeValid(file.getSize(), context) && isFileTypeValid(file.getContentType(), context);
    }

    private boolean isFileSizeValid(Long size, ConstraintValidatorContext context) {
        if (size > maxSize) {
            setValidationMessage(context, FILE_SIZE_EXCEEDS_THE_MAXIMUM_LIMIT);
            return false;
        }
        return true;
    }

    private boolean isFileTypeValid(String contentType, ConstraintValidatorContext context) {
        if (allowedTypes.isEmpty()) {
            return true;
        }
        boolean isValid = allowedTypes.stream().anyMatch(allowed -> allowed.equalsIgnoreCase(contentType));
        if (!isValid) {
            setValidationMessage(context, INVALID_FILE_TYPE);
        }
        return isValid;
    }

    private void setValidationMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
