package com.ecommerce.e_commerce.commerce.product.service;

import com.ecommerce.e_commerce.commerce.brand.model.Brand;
import com.ecommerce.e_commerce.commerce.brand.service.BrandService;
import com.ecommerce.e_commerce.commerce.cart.model.CartItem;
import com.ecommerce.e_commerce.commerce.category.model.Category;
import com.ecommerce.e_commerce.commerce.category.service.CategoryService;
import com.ecommerce.e_commerce.commerce.product.dto.ProductRequest;
import com.ecommerce.e_commerce.commerce.product.dto.ProductResponse;
import com.ecommerce.e_commerce.commerce.product.dto.StockWarning;
import com.ecommerce.e_commerce.commerce.product.enums.ProductStatus;
import com.ecommerce.e_commerce.commerce.product.enums.StockWarningType;
import com.ecommerce.e_commerce.commerce.product.mapper.ProductMapper;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import com.ecommerce.e_commerce.commerce.product.repository.ProductRepository;
import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.InsufficientStockException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BrandService brandService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private FileStorageService fileService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Brand brand;
    private Category category;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        brand = Brand
                .builder()
                .brandId(1L)
                .brandName("Nike")
                .deleted(false)
                .build();

        category = Category
                .builder()
                .categoryId(1L)
                .categoryName("Shoes")
                .deleted(false)
                .build();

        product = Product
                .builder()
                .productId(1L)
                .productName("Running Shoes")
                .description("Running Shoes Desc")
                .brand(brand)
                .category(category)
                .deleted(false)
                .price(BigDecimal.valueOf(99.99))
                .quantity(50)
                .status(ProductStatus.AVAILABLE)
                .orderItems(new ArrayList<>())
                .cartItems(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();

        productRequest = ProductRequest
                .builder()
                .name("Running Shoes")
                .description("Running Shoes Desc")
                .brandId(1L)
                .categoryId(1L)
                .price(BigDecimal.valueOf(99.99))
                .quantity(50)
                .build();

        productResponse = ProductResponse
                .builder()
                .id(1L)
                .name("Running Shoes")
                .price(BigDecimal.valueOf(99.99))
                .quantity(50)
                .brand("Nike")
                .category("Shoes")
                .build();
    }

    @Test
    void createProduct_ShouldCreateProduct_WhenValidRequest() {
        // Arrange
        when(productRepository.existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Running Shoes", 1L, 1L)).thenReturn(false);
        when(categoryService.getNonDeletedCategoryById(1L)).thenReturn(category);
        when(brandService.getNonDeletedBrandById(1L)).thenReturn(brand);
        when(productMapper.toEntity(productRequest, category, brand)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        // Act
        ProductResponse result = productService.createProduct(productRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Running Shoes");
        verify(productRepository).existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Running Shoes", 1L, 1L);
        verify(categoryService).getNonDeletedCategoryById(1L);
        verify(brandService).getNonDeletedBrandById(1L);
        verify(productMapper).toEntity(productRequest, category, brand);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    void createProduct_ShouldThrowException_WhenDuplicateProduct() {
        // Arrange
        when(productRepository.existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Running Shoes", 1L, 1L)).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(productRequest))
                .isInstanceOf(DuplicateItemException.class)
                .hasMessageContaining(PRODUCT_ALREADY_EXISTS);
        verify(productRepository).existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Running Shoes", 1L, 1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldSaveImage_WhenImageProvided() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        productRequest.setImage(mockImage);
        String imageUrl = "http://localhost:8000/uploads/product-123.png";
        when(productRepository.existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Running Shoes", 1L, 1L)).thenReturn(false);
        when(categoryService.getNonDeletedCategoryById(1L)).thenReturn(category);
        when(brandService.getNonDeletedBrandById(1L)).thenReturn(brand);
        when(fileService.saveImageToFileSystem(mockImage)).thenReturn(imageUrl);
        when(productMapper.toEntity(productRequest, category, brand)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            productResponse.setId(savedProduct.getProductId());
            productResponse.setImage(imageUrl);
            productResponse.setName(savedProduct.getProductName());
            return productResponse;
        });
        // Act
        ProductResponse result = productService.createProduct(productRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getImage()).isEqualTo("http://localhost:8000/uploads/product-123.png");
        verify(productRepository).existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Running Shoes", 1L, 1L);
        verify(categoryService).getNonDeletedCategoryById(1L);
        verify(brandService).getNonDeletedBrandById(1L);
        verify(fileService).saveImageToFileSystem(mockImage);
        verify(productMapper).toEntity(productRequest, category, brand);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    void searchProducts_ShouldReturnPaginatedProducts_WhenProductsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.searchProducts(1L, 1L, "Running Shoes", ProductStatus.AVAILABLE, pageable))
                .thenReturn(productPage);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        // Act
        PaginatedResponse<ProductResponse> result = productService
                .searchProducts(1L, 1L, "Running Shoes", ProductStatus.AVAILABLE.getValue(), pageable);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getPayload()).hasSize(1);
        verify(productRepository).searchProducts(1L, 1L, "Running Shoes", ProductStatus.AVAILABLE, pageable);
        verify(productMapper).toResponse(product);
    }

    @Test
    void searchProducts_ShouldReturnEmpty_WhenNoProductsMatch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(productRepository.searchProducts(null, null, "not-exist", null, pageable))
                .thenReturn(emptyPage);
        // Act
        PaginatedResponse<ProductResponse> result = productService
                .searchProducts(null, null, "not-exist", null, pageable);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getPayload()).isEmpty();
        verify(productRepository).searchProducts(null, null, "not-exist", null, pageable);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts_WhenProductsExist() {
        // Arrange
        Product product2 = Product
                .builder()
                .productId(2L)
                .productName("Basketball Shoes")
                .build();

        ProductResponse productResponse2 = ProductResponse
                .builder()
                .id(2L)
                .name("Basketball Shoes")
                .build();

        when(productRepository.findAllByDeletedFalse()).thenReturn(List.of(product, product2));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productMapper.toResponse(product2)).thenReturn(productResponse2);
        // Act
        List<ProductResponse> result = productService.getAllProducts();
        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(productRepository).findAllByDeletedFalse();
        verify(productMapper, times(2)).toResponse(any(Product.class));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findByProductIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        // Act
        ProductResponse result = productService.getProductById(1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Running Shoes");
        verify(productRepository).findByProductIdAndDeletedFalse(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findByProductIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(PRODUCT_NOT_FOUND);
        verify(productRepository).findByProductIdAndDeletedFalse(99L);
    }

    @Test
    void updateProduct_ShouldUpdateProduct_WhenValidRequest() {
        // Arrange
        productRequest.setName("Updated Running Shoes");
        Brand newBrand = Brand
                .builder()
                .brandId(2L)
                .brandName("Adidas")
                .deleted(false)
                .build();
        productRequest.setBrandId(2L);
        when(productRepository.findByProductIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                "Updated Running Shoes", 2L, 1L))
                .thenReturn(false);
        doAnswer(invocation -> {
            ProductRequest productRequest1 = invocation.getArgument(0);
            Product product1 = invocation.getArgument(1);
            product1.setProductName(productRequest1.getName());
            return null;
        }).when(productMapper).updateProductFromRequest(productRequest, product);
        when(brandService.getNonDeletedBrandById(2L)).thenReturn(newBrand);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product))
                .thenAnswer(invocation -> {
                    Product product1 = invocation.getArgument(0);
                    return ProductResponse
                            .builder()
                            .id(product1.getProductId())
                            .name(product1.getProductName())
                            .brand(product1.getBrand().getBrandName())
                            .build();
                });
        // Act
        ProductResponse result = productService.updateProduct(1L, productRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Running Shoes");
        assertThat(result.getBrand()).isEqualTo("Adidas");
        verify(productRepository).findByProductIdAndDeletedFalse(1L);
        verify(productRepository).existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse
                ("Updated Running Shoes", 2L, 1L);
        verify(productMapper).updateProductFromRequest(productRequest, product);
        verify(brandService).getNonDeletedBrandById(2L);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    void updateProduct_ShouldReplaceImage_WhenImageProvided() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        productRequest.setImage(mockImage);
        product.setImage("old-image.png");

        String newImageUrl = "http://localhost:8000/uploads/new-image.png";
        when(productRepository.findByProductIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(fileService.saveImageToFileSystem(mockImage)).thenReturn(newImageUrl);
        doAnswer(invocation -> {
            Product product1 = invocation.getArgument(1);
            product1.setImage(newImageUrl);
            return null;
        }).when(productMapper).updateProductFromRequest(productRequest, product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product))
                .thenAnswer(invocation -> {
                    Product product1 = invocation.getArgument(0);
                    return ProductResponse
                            .builder()
                            .id(product1.getProductId())
                            .image(product1.getImage())
                            .build();
                });
        // Act
        ProductResponse result = productService.updateProduct(1L, productRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getImage()).isEqualTo(newImageUrl);
        verify(productRepository).findByProductIdAndDeletedFalse(1L);
        verify(fileService).deleteImageFromFileSystem("old-image.png");
        verify(fileService).saveImageToFileSystem(mockImage);
        verify(productMapper).updateProductFromRequest(productRequest, product);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    void deleteProduct_ShouldSoftDeleteProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findByProductIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        // Act
        productService.deleteProduct(1L);
        // Assert
        assertThat(product.isDeleted()).isTrue();
        verify(productRepository).findByProductIdAndDeletedFalse(1L);
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findByProductIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(PRODUCT_NOT_FOUND);
        verify(productRepository).findByProductIdAndDeletedFalse(1L);
    }

    @Test
    void checkStockAndWarn_ShouldReturnEmpty_WhenStockSufficient() {
        // Act
        Optional<StockWarning> result = productService.checkStockAndWarn(product, 30);
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void checkStockAndWarn_ShouldReturnOutOfStock_WhenProductOutOfStock() {
        // Arrange
        product.setQuantity(0);
        product.setStatus(ProductStatus.OUT_OF_STOCK);
        // Act
        Optional<StockWarning> result = productService.checkStockAndWarn(product, 30);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(StockWarningType.OUT_OF_STOCK);
        assertThat(result.get().getProductId()).isEqualTo(1L);
        assertThat(result.get().getProductName()).isEqualTo("Running Shoes");
    }

    @Test
    void checkStockAndWarn_ShouldReturnDiscontinued_WhenProductDiscontinued() {
        // Arrange
        product.setQuantity(0);
        product.setStatus(ProductStatus.DISCONTINUED);
        // Act
        Optional<StockWarning> result = productService.checkStockAndWarn(product, 20);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(StockWarningType.DISCONTINUED);
        assertThat(result.get().getProductId()).isEqualTo(1L);
        assertThat(result.get().getProductName()).isEqualTo("Running Shoes");
    }

    @Test
    void checkStockAndWarn_ShouldReturnLimitedStock_WhenRequestedMoreThanAvailable() {
        // Act
        Optional<StockWarning> result = productService.checkStockAndWarn(product, 70); // more than available
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(StockWarningType.LIMITED_STOCK);
        assertThat(result.get().getProductId()).isEqualTo(1L);
        assertThat(result.get().getProductName()).isEqualTo("Running Shoes");
    }

    @Test
    void checkStockAvailability_ShouldThrowException_WhenInsufficientStock() {
        // Arrange
        CartItem cartItem = CartItem.builder()
                .product(product)
                .quantity(60) // more than existing
                .build();

        // Act & Assert
        assertThatThrownBy(() -> productService.checkStockAvailability(List.of(cartItem)))
                .isInstanceOf(InsufficientStockException.class);
    }
}

