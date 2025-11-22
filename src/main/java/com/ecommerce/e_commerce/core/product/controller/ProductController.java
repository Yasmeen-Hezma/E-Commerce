package com.ecommerce.e_commerce.core.product.controller;

import com.ecommerce.e_commerce.core.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.core.product.dtos.ProductRequest;
import com.ecommerce.e_commerce.core.product.dtos.ProductResponse;
import com.ecommerce.e_commerce.core.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("search")
    public ResponseEntity<PaginatedResponse<ProductResponse>> searchProducts(@RequestParam(required = false) Long brandId,
                                                                             @RequestParam(required = false) Long categoryId,
                                                                             @RequestParam(required = false) String productName,
                                                                             @RequestParam(required = false) Integer status,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchProducts(brandId, categoryId, productName, status, PageRequest.of(page, size)));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PatchMapping("{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @ModelAttribute ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
