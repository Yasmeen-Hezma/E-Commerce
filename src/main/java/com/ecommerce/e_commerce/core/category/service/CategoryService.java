package com.ecommerce.e_commerce.core.category.service;

import com.ecommerce.e_commerce.core.category.dtos.CategoryRequest;
import com.ecommerce.e_commerce.core.category.dtos.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(CategoryRequest request, Long id);

    void deleteCategory(Long id);
}
