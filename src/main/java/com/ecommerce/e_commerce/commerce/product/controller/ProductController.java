package com.ecommerce.e_commerce.commerce.product.controller;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.commerce.product.dto.ProductRequest;
import com.ecommerce.e_commerce.commerce.product.dto.ProductResponse;
import com.ecommerce.e_commerce.commerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/")
@Tag(name = "Product", description = "Product Management APIs")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Search products by filters and pagination")
    @GetMapping("search")
    public ResponseEntity<PaginatedResponse<ProductResponse>> searchProducts(@RequestParam(required = false) Long brandId,
                                                                             @RequestParam(required = false) Long categoryId,
                                                                             @RequestParam(required = false) String productName,
                                                                             @RequestParam(required = false) Integer status,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchProducts(brandId, categoryId, productName, status, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get product by id")
    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Create new product")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Operation(summary = "Update existing product")
    @PatchMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @ModelAttribute ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @Operation(summary = "Delete existing product by id")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
