package com.ecommerce.e_commerce.core.category.controller;

import com.ecommerce.e_commerce.core.category.dtos.CategoryRequest;
import com.ecommerce.e_commerce.core.category.dtos.CategoryResponse;
import com.ecommerce.e_commerce.core.category.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category/")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @ModelAttribute CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PatchMapping("{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @ModelAttribute CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(request, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
