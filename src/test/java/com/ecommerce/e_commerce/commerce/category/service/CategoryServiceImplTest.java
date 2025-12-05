package com.ecommerce.e_commerce.commerce.category.service;

import com.ecommerce.e_commerce.commerce.category.dtos.CategoryRequest;
import com.ecommerce.e_commerce.commerce.category.dtos.CategoryResponse;
import com.ecommerce.e_commerce.commerce.category.mapper.CategoryMapper;
import com.ecommerce.e_commerce.commerce.category.model.Category;
import com.ecommerce.e_commerce.commerce.category.repository.CategoryRepository;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private FileStorageService fileService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category
                .builder()
                .categoryId(1L)
                .categoryName("shoes")
                .image("shoes.png")
                .deleted(false)
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("shoes");

        categoryResponse = CategoryResponse
                .builder()
                .id(1L)
                .name("shoes")
                .image("shoes.png")
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories_WhenCategoriesExist() {
        // Arrange
        Category category2 = Category
                .builder()
                .categoryId(2L)
                .categoryName("clothes")
                .image("clothes.png")
                .deleted(false)
                .build();
        CategoryResponse categoryResponse2 = CategoryResponse
                .builder()
                .id(2L)
                .name("clothes")
                .image("clothes.png")
                .build();
        when(categoryRepository.findAllByDeletedFalse()).thenReturn(List.of(category, category2));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        when(categoryMapper.toResponse(category2)).thenReturn(categoryResponse2);
        // Act
        List<CategoryResponse> result = categoryService.getAllCategories();
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("shoes");
        assertThat(result.get(1).getName()).isEqualTo("clothes");
        verify(categoryRepository).findAllByDeletedFalse();
        verify(categoryMapper, times(2)).toResponse(any(Category.class));
    }

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange
        when(categoryRepository.findAllByDeletedFalse()).thenReturn(List.of());
        // Act
        List<CategoryResponse> result = categoryService.getAllCategories();
        // Assert
        assertThat(result).isEmpty();
        verify(categoryRepository).findAllByDeletedFalse();
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        when(categoryRepository.findByCategoryIdAndDeletedFalse(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        // Act
        CategoryResponse result = categoryService.getCategoryById(1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("shoes");
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(1L);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findByCategoryIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(ItemNotFoundException.class);
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(99L);
    }

    @Test
    void createCategory_ShouldCreateCategory_WhenValidRequest() {
        // Arrange
        when(categoryRepository.existsByCategoryName("shoes")).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        // Act
        CategoryResponse result = categoryService.createCategory(categoryRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("shoes");
        verify(categoryRepository).existsByCategoryName("shoes");
        verify(categoryMapper).toEntity(categoryRequest);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void createCategory_ShouldThrowException_WhenNameAlreadyExist() {
        // Arrange
        when(categoryRepository.existsByCategoryName("shoes")).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(DuplicateItemException.class);
        verify(categoryRepository).existsByCategoryName("shoes");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldSaveImage_WhenImageProvided() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        categoryRequest.setImage(mockImage);

        when(categoryRepository.existsByCategoryName("shoes")).thenReturn(false);
        when(fileService.saveImageToFileSystem(mockImage)).thenReturn("shoes-create-image.png");
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenAnswer(
                invocation -> {
                    Category c = invocation.getArgument(0);
                    return CategoryResponse
                            .builder()
                            .id(c.getCategoryId())
                            .name(c.getCategoryName())
                            .image(c.getImage())
                            .build();
                }
        );
        // Act
        CategoryResponse result = categoryService.createCategory(categoryRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getImage()).isEqualTo("shoes-create-image.png");
        verify(categoryRepository).existsByCategoryName("shoes");
        verify(fileService).saveImageToFileSystem(mockImage);
        verify(categoryMapper).toEntity(categoryRequest);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void updateCategory_ShouldUpdateCategory_WhenValidRequest() {
        // Arrange
        categoryRequest.setName("shoes-update");
        when(categoryRepository.findByCategoryIdAndDeletedFalse(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryNameAndCategoryIdNot("shoes-update", 1L)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenAnswer(
                invocation -> {
                    Category c = invocation.getArgument(0);
                    return CategoryResponse
                            .builder()
                            .id(c.getCategoryId())
                            .name(c.getCategoryName())
                            .image(c.getImage())
                            .build();
                }
        );
        // Act
        CategoryResponse result = categoryService.updateCategory(categoryRequest, 1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("shoes-update");
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(1L);
        verify(categoryRepository).existsByCategoryNameAndCategoryIdNot("shoes-update", 1L);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findByCategoryIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, 99L))
                .isInstanceOf(ItemNotFoundException.class);
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(99L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowException_WhenNameAlreadyExist() {
        // Arrange
        categoryRequest.setName("clothes");
        when(categoryRepository.findByCategoryIdAndDeletedFalse(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryNameAndCategoryIdNot("clothes", 1L)).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, 1L))
                .isInstanceOf(DuplicateItemException.class);
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(1L);
        verify(categoryRepository).existsByCategoryNameAndCategoryIdNot("clothes", 1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateImage_WhenNewImageProvided() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        categoryRequest.setImage(mockImage);

        String oldImageUrl = "shoes.png";
        String newImageUrl = "shoes-update.png";

        when(categoryRepository.findByCategoryIdAndDeletedFalse(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryNameAndCategoryIdNot("shoes", 1L)).thenReturn(false);
        when(fileService.saveImageToFileSystem(mockImage)).thenReturn(newImageUrl);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenAnswer(
                invocation -> {
                    Category c = invocation.getArgument(0);
                    return CategoryResponse
                            .builder()
                            .id(c.getCategoryId())
                            .name(c.getCategoryName())
                            .image(c.getImage())
                            .build();
                }
        );
        // Act
        CategoryResponse result = categoryService.updateCategory(categoryRequest, 1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getImage()).isEqualTo(newImageUrl);
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(1L);
        verify(categoryRepository).existsByCategoryNameAndCategoryIdNot("shoes", 1L);
        verify(fileService).deleteImageFromFileSystem(oldImageUrl);
        verify(fileService).saveImageToFileSystem(mockImage);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void deleteCategory_ShouldSoftDeleteCategory_WhenCategoryExists() {
        // Arrange
        when(categoryRepository.findByCategoryIdAndDeletedFalse(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        // Act
        categoryService.deleteCategory(1L);
        // Assert
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(1L);
        verify(categoryRepository).save(argThat(Category::isDeleted));
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findByCategoryIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(ItemNotFoundException.class);
        verify(categoryRepository).findByCategoryIdAndDeletedFalse(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}