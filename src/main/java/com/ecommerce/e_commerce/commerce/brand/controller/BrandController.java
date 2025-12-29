package com.ecommerce.e_commerce.commerce.brand.controller;

import com.ecommerce.e_commerce.commerce.brand.dto.BrandRequest;
import com.ecommerce.e_commerce.commerce.brand.dto.BrandResponse;
import com.ecommerce.e_commerce.commerce.brand.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brand/")
@Tag(name = "Brand", description = "Brand Management APIs")
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "Get All brands")
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @Operation(summary = "Get brand by id")
    @GetMapping("{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @Operation(summary = "Create new brand")
    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @ModelAttribute BrandRequest request) {
        return ResponseEntity.ok(brandService.createBrand(request));
    }

    @Operation(summary = "Update existing brand")
    @PatchMapping("{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable long id,
                                                     @Valid @ModelAttribute BrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @Operation(summary = "Delete existing brand by id")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
