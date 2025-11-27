package com.ecommerce.e_commerce.commerce.brand.controller;

import com.ecommerce.e_commerce.commerce.brand.dtos.BrandRequest;
import com.ecommerce.e_commerce.commerce.brand.dtos.BrandResponse;
import com.ecommerce.e_commerce.commerce.brand.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brand/")
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @ModelAttribute BrandRequest request) {
        return ResponseEntity.ok(brandService.createBrand(request));
    }

    @PatchMapping("{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable long id,
                                                     @Valid @ModelAttribute BrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
