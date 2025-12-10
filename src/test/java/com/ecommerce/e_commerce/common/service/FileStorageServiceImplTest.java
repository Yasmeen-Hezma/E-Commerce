package com.ecommerce.e_commerce.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Integration-style tests for FileStorageService
 * Uses a temporary directory to test actual file operations
 */
class FileStorageServiceImplTest {
    private FileStorageServiceImpl fileService;
    @TempDir
    Path tempDir; // JUnit creates and cleans up automatically

    @BeforeEach
    void setUp() {
        fileService = new FileStorageServiceImpl();
        // Set the upload directory to our temp directory
        ReflectionTestUtils.setField(fileService, "uploadDir", tempDir.toString());
    }

    @Test
    void saveImageToFileSystem_ShouldSaveFile_WhenValidImageProvided() throws IOException {
        // Arrange
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test-image.png",
                "img/png",
                content
        );
        // Act
        String savedFileName = fileService.saveImageToFileSystem(file);
        // Assert
        assertThat(savedFileName).isNotNull();
        assertThat(savedFileName).endsWith(".png");
        assertThat(savedFileName).hasSizeGreaterThan(10); // UUID + extension

        Path savedPath = tempDir.resolve(savedFileName);
        assertThat(Files.exists(savedPath)).isTrue();

        byte[] savedContent = Files.readAllBytes(savedPath);
        assertThat(savedContent).isEqualTo(content);
    }

    @Test
    void saveImageToFileSystem_ShouldThrowException_WhenFileHasNoExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test",
                "image/png",
                "content".getBytes()
        );
        // Act & Assert
        assertThatThrownBy(() -> fileService.saveImageToFileSystem(file))
                .isInstanceOf(StringIndexOutOfBoundsException.class);
    }

    @Test
    void deleteImageFromFileSystem_ShouldDeleteFile_WhenFileExists() throws IOException {
        // Arrange - First create a file
        Path testFile = tempDir.resolve("test-image.png");
        Files.write(testFile, "test content".getBytes());
        assertThat(Files.exists(testFile)).isTrue();
        // Act
        fileService.deleteImageFromFileSystem("test-image.png");
        // Assert
        assertThat(Files.exists(testFile)).isFalse();
    }
}