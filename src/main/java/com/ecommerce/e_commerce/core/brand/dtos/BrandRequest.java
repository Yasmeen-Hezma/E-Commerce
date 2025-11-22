package com.ecommerce.e_commerce.core.brand.dtos;

import com.ecommerce.e_commerce.core.common.validation.ValidFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class BrandRequest {
    @NotBlank
    @Size(min = 3, message = "Brand name must contain at least 3 characters")
    private String name;
    @ValidFile(
            allowedTypes = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE},
            maxSize = 5 * 1024 * 1024,
            notEmpty = false
    )
    private MultipartFile image;
}
