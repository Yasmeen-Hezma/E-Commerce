package com.ecommerce.e_commerce.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveImageToFileSystem(MultipartFile file);

    void deleteImageFromFileSystem(String filename);
}
