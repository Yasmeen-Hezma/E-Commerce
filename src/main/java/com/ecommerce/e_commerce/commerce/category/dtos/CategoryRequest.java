package com.ecommerce.e_commerce.commerce.category.dtos;

import com.ecommerce.e_commerce.common.validation.ValidFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class CategoryRequest {
    @NotBlank
    @Size(min = 3, message = "Category name must contain at least 3 characters")
    private String name;
    @ValidFile(
            allowedTypes = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE},
            maxSize = 5 * 1024 * 1024,
            notEmpty = false
    )
    private MultipartFile image;
}
