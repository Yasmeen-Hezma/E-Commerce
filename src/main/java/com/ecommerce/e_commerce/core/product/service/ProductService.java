package com.ecommerce.e_commerce.core.product.service;

import com.ecommerce.e_commerce.core.cart.model.CartItem;
import com.ecommerce.e_commerce.core.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.core.product.dtos.ProductRequest;
import com.ecommerce.e_commerce.core.product.dtos.ProductResponse;
import com.ecommerce.e_commerce.core.product.dtos.StockWarning;
import com.ecommerce.e_commerce.core.product.enums.StockWarningType;
import com.ecommerce.e_commerce.core.product.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    PaginatedResponse<ProductResponse> searchProducts(Long brandId, Long categoryId, String productName, Integer status, Pageable pageable);

    Product getNonDeletedProductById(Long id);

    StockWarningType evaluateStock(Product product, int requested);

    Optional<StockWarning> checkStockAndWarn(Product product, int requested);

    void checkStockAvailability(List<CartItem> cartItems);
}
