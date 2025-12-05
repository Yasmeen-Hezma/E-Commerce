package com.ecommerce.e_commerce.commerce.brand.service;

import com.ecommerce.e_commerce.commerce.brand.dtos.BrandRequest;
import com.ecommerce.e_commerce.commerce.brand.dtos.BrandResponse;
import com.ecommerce.e_commerce.commerce.brand.mapper.BrandMapper;
import com.ecommerce.e_commerce.commerce.brand.model.Brand;
import com.ecommerce.e_commerce.commerce.brand.repository.BrandRepository;
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
class BrandServiceImplTest {
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private BrandMapper brandMapper;
    @Mock
    private FileStorageService fileService;

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;
    private BrandRequest brandRequest;
    private BrandResponse brandResponse;

    @BeforeEach
    void setUp() {
        brand = Brand.builder()
                .brandId(1L)
                .brandName("Nike")
                .image("nike.png")
                .deleted(false)
                .build();

        brandRequest = new BrandRequest();
        brandRequest.setName("Nike");

        brandResponse = BrandResponse.builder()
                .id(1L)
                .name("Nike")
                .image("nike.png")
                .build();
    }

    @Test
    void getAllBrands_ShouldReturnListOfBrands_WhenBrandsExist() {
        // Arrange
        Brand brand2 = Brand.builder()
                .brandId(2L)
                .brandName("Adidas")
                .image("adidas.png")
                .deleted(false)
                .build();
        BrandResponse brandResponse2 = BrandResponse.builder()
                .id(2L)
                .name("Adidas")
                .image("adidas.png")
                .build();
        List<Brand> brands = List.of(brand, brand2);
        when(brandRepository.findAllByDeletedFalse()).thenReturn(brands);
        when(brandMapper.toResponse(brand)).thenReturn(brandResponse);
        when(brandMapper.toResponse(brand2)).thenReturn(brandResponse2);
        // Act
        List<BrandResponse> result = brandService.getAllBrands();
        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Nike");
        assertThat(result.get(1).getName()).isEqualTo("Adidas");
        verify(brandRepository).findAllByDeletedFalse();
        verify(brandMapper, times(2)).toResponse(any(Brand.class));
    }

    @Test
    void getAllBrands_ShouldReturnEmptyList_WhenNoBrandsExist() {
        // Arrange
        when(brandRepository.findAllByDeletedFalse()).thenReturn(List.of());
        // Act
        List<BrandResponse> result = brandService.getAllBrands();
        // Assert
        assertThat(result).isEmpty();
        verify(brandRepository).findAllByDeletedFalse();
    }

    @Test
    void getById_ShouldReturnBrand_WhenBrandExists() {
        // Arrange
        when(brandRepository.findByBrandIdAndDeletedFalse(1L)).thenReturn(Optional.of(brand));
        when(brandMapper.toResponse(brand)).thenReturn(brandResponse);
        // Act
        BrandResponse result = brandService.getBrandById(1L);
        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Nike");
        verify(brandRepository).findByBrandIdAndDeletedFalse(1L);
        verify(brandMapper).toResponse(brand);
    }

    @Test
    void getById_ShouldTrowException_WhenBrandNotFound() {
        // Arrange
        when(brandRepository.findByBrandIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> brandService.getBrandById(99L))
                .isInstanceOf(ItemNotFoundException.class);
        verify(brandRepository).findByBrandIdAndDeletedFalse(99L);
    }

    @Test
    void createBrand_ShouldCreateBrand_WhenValidRequest() {
        // Arrange
        when(brandRepository.existsByBrandNameAndDeletedFalse("Nike")).thenReturn(false);
        when(brandMapper.toEntity(brandRequest)).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(brandMapper.toResponse(brand)).thenReturn(brandResponse);
        // Act
        BrandResponse result = brandService.createBrand(brandRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Nike");
        verify(brandRepository).existsByBrandNameAndDeletedFalse("Nike");
        verify(brandMapper).toEntity(brandRequest);
        verify(brandRepository).save(any(Brand.class));
        verify(brandMapper).toResponse(brand);
    }

    @Test
    void createBrand_ShouldThrowException_WhenNameAlreadyExists() {
        // Arrange
        when(brandRepository.existsByBrandNameAndDeletedFalse("Nike")).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> brandService.createBrand(brandRequest))
                .isInstanceOf(DuplicateItemException.class);
        verify(brandRepository).existsByBrandNameAndDeletedFalse("Nike");
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void createBrand_ShouldSaveImage_WhenImageProvided() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        brandRequest.setImage(mockImage);
        String expectedImageUrl = "nike-create-image.png";
        when(brandRepository.existsByBrandNameAndDeletedFalse("Nike")).thenReturn(false);
        when(fileService.saveImageToFileSystem(mockImage)).thenReturn(expectedImageUrl);
        when(brandMapper.toEntity(brandRequest)).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(brandMapper.toResponse(any(Brand.class))).thenAnswer(invocation -> {
            Brand b = invocation.getArgument(0);
            return BrandResponse
                    .builder()
                    .id(b.getBrandId())
                    .name(b.getBrandName())
                    .image(b.getImage())
                    .build();
        });
        // Act
        BrandResponse result = brandService.createBrand(brandRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Nike");
        verify(fileService).saveImageToFileSystem(mockImage);
        verify(brandRepository).existsByBrandNameAndDeletedFalse("Nike");
        verify(brandMapper).toEntity(brandRequest);
        verify(brandRepository).save(any(Brand.class));
        verify(brandMapper).toResponse(brand);
    }

    @Test
    void updateBrand_ShouldUpdateBrand_WhenValidRequest() {
        // Arrange
        brandRequest.setName("Nike Update");
        when(brandRepository.findByBrandIdAndDeletedFalse(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandNameAndDeletedFalseAndBrandIdNot("Nike Update", 1L))
                .thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(brandMapper.toResponse(any(Brand.class))).thenAnswer(invocation -> {
            Brand b = invocation.getArgument(0);
            return BrandResponse
                    .builder()
                    .id(b.getBrandId())
                    .name(b.getBrandName())
                    .image(b.getImage())
                    .build();
        });
        // Act
        BrandResponse result = brandService.updateBrand(1L, brandRequest);
        // Assert
        assertThat(result).isNotNull();
        verify(brandRepository).findByBrandIdAndDeletedFalse(1L);
        verify(brandRepository).existsByBrandNameAndDeletedFalseAndBrandIdNot("Nike Update", 1L);
        verify(brandRepository).save(any(Brand.class));
        verify(brandMapper).toResponse(brand);
    }

    @Test
    void updateBrand_ShouldThrowException_WhenBrandNotFound() {
        // Arrange
        when(brandRepository.findByBrandIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> brandService.updateBrand(99L, brandRequest))
                .isInstanceOf(ItemNotFoundException.class);
        verify(brandRepository).findByBrandIdAndDeletedFalse(99L);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void updateBrand_ShouldThrowException_WhenBrandNameExists() {
        // Arrange
        brandRequest.setName("Adidas");
        when(brandRepository.findByBrandIdAndDeletedFalse(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandNameAndDeletedFalseAndBrandIdNot("Adidas", 1L)).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> brandService.updateBrand(1L, brandRequest))
                .isInstanceOf(DuplicateItemException.class);
        verify(brandRepository).findByBrandIdAndDeletedFalse(1L);
        verify(brandRepository).existsByBrandNameAndDeletedFalseAndBrandIdNot("Adidas", 1L);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void updateBrand_ShouldReplaceImage_WhenNewImageProvided() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        brandRequest.setImage(mockImage);

        String oldImageUrl = "nike.png";
        String newImageUrl = "nike-updated.png";
        when(brandRepository.findByBrandIdAndDeletedFalse(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandNameAndDeletedFalseAndBrandIdNot("Nike", 1L)).thenReturn(false);
        when(fileService.saveImageToFileSystem(mockImage)).thenReturn(newImageUrl);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(brandMapper.toResponse(any(Brand.class))).thenAnswer(invocation -> {
            Brand b = invocation.getArgument(0);
            return BrandResponse
                    .builder()
                    .id(b.getBrandId())
                    .name(b.getBrandName())
                    .image(b.getImage())
                    .build();
        });
        // Act
        BrandResponse result = brandService.updateBrand(1L, brandRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(brand.getImage()).isEqualTo(newImageUrl);
        verify(brandRepository).findByBrandIdAndDeletedFalse(1L);
        verify(brandRepository).existsByBrandNameAndDeletedFalseAndBrandIdNot("Nike", 1L);
        verify(fileService).deleteImageFromFileSystem(oldImageUrl);
        verify(fileService).saveImageToFileSystem(mockImage);
        verify(brandRepository).save(any(Brand.class));
        verify(brandMapper).toResponse(brand);
    }

    @Test
    void deleteBrand_ShouldSoftDeleteBrand_WhenBrandExists() {
        // Arrange
        when(brandRepository.findByBrandIdAndDeletedFalse(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        // Act
        brandService.deleteBrand(1L);
        // Assert
        verify(brandRepository).findByBrandIdAndDeletedFalse(1L);
        verify(brandRepository).save(argThat(Brand::isDeleted));
    }

    @Test
    void deleteBrand_ShouldThrowException_WhenBrandNotFound() {
        // Arrange
        when(brandRepository.findByBrandIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> brandService.deleteBrand(1L))
                .isInstanceOf(ItemNotFoundException.class);
        verify(brandRepository).findByBrandIdAndDeletedFalse(1L);
        verify(brandRepository, never()).save(any(Brand.class));
    }
}

