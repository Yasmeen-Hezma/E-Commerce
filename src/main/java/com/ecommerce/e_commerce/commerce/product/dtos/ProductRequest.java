package com.ecommerce.e_commerce.commerce.product.dtos;

import com.ecommerce.e_commerce.common.validation.ValidFile;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Setter
@Getter
public class ProductRequest {
    @NotBlank
    @Size(min = 3, message = "Product name must contain at least 3 characters")
    private String name;

    @NotBlank
    @Size(min = 3, message = "Description name must contain at least 6 characters")
    private String description;
    @ValidFile(
            allowedTypes = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE},
            maxSize = 5 * 1024 * 1024,
            notEmpty = false
    )
    private MultipartFile image;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;

    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    private BigDecimal discount;

    @Min(0)
    @Max(2)
    private Integer status;

    private Long categoryId;

    private Long brandId;
}
