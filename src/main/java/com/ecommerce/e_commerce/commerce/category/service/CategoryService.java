package com.ecommerce.e_commerce.commerce.category.service;

import com.ecommerce.e_commerce.commerce.category.dto.CategoryRequest;
import com.ecommerce.e_commerce.commerce.category.dto.CategoryResponse;
import com.ecommerce.e_commerce.commerce.category.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(CategoryRequest request, Long id);

    void deleteCategory(Long id);

    Category getNonDeletedCategoryById(Long id);
}
