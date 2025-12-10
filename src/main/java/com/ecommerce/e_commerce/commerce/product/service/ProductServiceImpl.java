package com.ecommerce.e_commerce.commerce.product.service;

import com.ecommerce.e_commerce.commerce.brand.model.Brand;
import com.ecommerce.e_commerce.commerce.brand.service.BrandService;
import com.ecommerce.e_commerce.commerce.cart.model.CartItem;
import com.ecommerce.e_commerce.commerce.category.model.Category;
import com.ecommerce.e_commerce.commerce.category.service.CategoryService;
import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.InsufficientStockException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.commerce.product.dtos.ProductRequest;
import com.ecommerce.e_commerce.commerce.product.dtos.ProductResponse;
import com.ecommerce.e_commerce.commerce.product.dtos.StockWarning;
import com.ecommerce.e_commerce.commerce.product.enums.ProductStatus;
import com.ecommerce.e_commerce.commerce.product.enums.StockWarningType;
import com.ecommerce.e_commerce.commerce.product.mapper.ProductMapper;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import com.ecommerce.e_commerce.commerce.product.repository.ProductRepository;
import com.ecommerce.e_commerce.common.service.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.PRODUCT_ALREADY_EXISTS;
import static com.ecommerce.e_commerce.common.utils.Constants.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final FileStorageService fileService;


    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        checkDuplicateProduct(request);
        Category category = categoryService.getNonDeletedCategoryById(request.getCategoryId());
        Brand brand = brandService.getNonDeletedBrandById(request.getBrandId());
        String imagePath = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imagePath = fileService.saveImageToFileSystem(request.getImage());
        }
        Product product = productMapper.toEntity(request, category, brand);
        product.setImage(imagePath);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public PaginatedResponse<ProductResponse> searchProducts(Long brandId, Long categoryId, String productName, Integer status, Pageable pageable) {
        ProductStatus productStatus = (status != null) ? ProductStatus.fromValue(status) : null;

        Page<Product> productPage = productRepository.searchProducts(
                brandId, categoryId, productName, productStatus, pageable
        );

        List<ProductResponse> content = productPage
                .map(productMapper::toResponse)
                .getContent();

        return new PaginatedResponse<>(content, productPage.getTotalElements());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository
                .findAllByDeletedFalse()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = getNonDeletedProductById(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = getNonDeletedProductById(id);
        if (isProductIdentityChanged(product, request)) {
            checkDuplicateProduct(request);
        }
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (product.getImage() != null) {
                fileService.deleteImageFromFileSystem(product.getImage());
            }
            String imagePath = fileService.saveImageToFileSystem(request.getImage());
            product.setImage(imagePath);
        }
        productMapper.updateProductFromRequest(request, product);

        if (request.getBrandId() != null && !product.getBrand().getBrandId().equals(request.getBrandId())) {
            Brand brand = brandService.getNonDeletedBrandById(request.getBrandId());
            product.setBrand(brand);
        }
        if (request.getCategoryId() != null && !product.getCategory().getCategoryId().equals(request.getCategoryId())) {
            Category category = categoryService.getNonDeletedCategoryById(request.getCategoryId());
            product.setCategory(category);
        }
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getNonDeletedProductById(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public Product getNonDeletedProductById(Long id) {
        return productRepository
                .findByProductIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(PRODUCT_NOT_FOUND));
    }

    //@Override
    private StockWarningType evaluateStock(Product product, int requested) {
        Integer available = product.getQuantity();
        if (product.getStatus() == ProductStatus.DISCONTINUED) return StockWarningType.DISCONTINUED;
        if (available == 0) return StockWarningType.OUT_OF_STOCK;
        if (available < requested) return StockWarningType.LIMITED_STOCK;
        return null;
    }

    @Override
    public Optional<StockWarning> checkStockAndWarn(Product product, int requested) {
        StockWarningType warningType = evaluateStock(product, requested);
        if (warningType != null) {
            return Optional.of(StockWarning
                    .builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .type(warningType)
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public void checkStockAvailability(List<CartItem> cartItems) {
        List<StockWarning> issues = new ArrayList<>();
        for (CartItem item : cartItems) {
            Optional<StockWarning> warningOpt = checkStockAndWarn(item.getProduct(), item.getQuantity());
            warningOpt.ifPresent(issues::add);
        }
        if (!issues.isEmpty()) {
            throw new InsufficientStockException(issues);
        }
    }

    private void checkDuplicateProduct(ProductRequest request) {
        boolean exists = productRepository.existsByProductNameIgnoreCaseAndBrand_BrandIdAndCategory_CategoryIdAndDeletedFalse(
                request.getName(), request.getBrandId(), request.getCategoryId()
        );
        if (exists) {
            throw new DuplicateItemException(PRODUCT_ALREADY_EXISTS);
        }
    }


    private boolean isProductIdentityChanged(Product product, ProductRequest request) {
        boolean isNameChanged = !product.getProductName().equalsIgnoreCase(request.getName());
        boolean isBrandChanged = !product.getBrand().getBrandId().equals(request.getBrandId());
        boolean isCategoryChanged = !product.getCategory().getCategoryId().equals(request.getCategoryId());
        return isNameChanged || isBrandChanged || isCategoryChanged;
    }

}
