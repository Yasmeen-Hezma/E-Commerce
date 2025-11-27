package com.ecommerce.e_commerce.common.service;

import com.ecommerce.e_commerce.common.exception.ImageStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.ecommerce.e_commerce.common.utils.Constants.FAILED_TO_SAVE_IMAGE;

@Service
public class FileStorageService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveImageToFileSystem(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String newFilename = UUID.randomUUID() + extension;
            // combine (uploadPath) -> the directory path with the newFilename to get the full path where the file will be saved
            Path targetPath = uploadPath.resolve(newFilename);
            // Saves the file to disk
            file.transferTo(targetPath.toFile());

            return newFilename;
        } catch (IOException e) {
            throw new ImageStorageException(FAILED_TO_SAVE_IMAGE);
        }
    }

    public void deleteImageFromFileSystem(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ImageStorageException(FAILED_TO_SAVE_IMAGE);
        }
    }
}
