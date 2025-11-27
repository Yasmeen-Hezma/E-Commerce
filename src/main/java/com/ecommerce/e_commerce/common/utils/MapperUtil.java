package com.ecommerce.e_commerce.common.utils;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MapperUtil {
    @Value("${app.base-url}")
    private String baseUrl;
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Named("mapImageUrl")  // Must match the qualifier in @Mapping
    public String mapImageUrl(String filename) {
        return filename != null ? baseUrl + uploadDir + filename : null;
    }
}
