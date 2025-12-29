package com.ecommerce.e_commerce.commerce.category.controller;

import com.ecommerce.e_commerce.commerce.category.dto.CategoryRequest;
import com.ecommerce.e_commerce.commerce.category.dto.CategoryResponse;
import com.ecommerce.e_commerce.commerce.category.service.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category/")
@Tag(name = "Category", description = "Category Management APIs")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @Operation(summary = "Get All categories")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Get category by id")
    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Create new category")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @ModelAttribute CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @Operation(summary = "Update existing category")
    @PatchMapping("{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @ModelAttribute CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(request, id));
    }

    @Operation(summary = "Delete existing category by id")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
