package com.ecommerce.e_commerce.core.category.service;

import com.ecommerce.e_commerce.core.category.dtos.CategoryRequest;
import com.ecommerce.e_commerce.core.category.dtos.CategoryResponse;
import com.ecommerce.e_commerce.core.category.mapper.CategoryMapper;
import com.ecommerce.e_commerce.core.category.model.Category;
import com.ecommerce.e_commerce.core.category.repository.CategoryRepository;
import com.ecommerce.e_commerce.core.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.core.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.core.common.service.FileStorageService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecommerce.e_commerce.core.common.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService fileService;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository
                .findAllByDeletedFalse()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = getNonDeletedCategoryById(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        checkDuplicateCategoryName(request.getName(), null);
        String imagePath = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imagePath = fileService.saveImageToFileSystem(request.getImage());
        }
        Category category = categoryMapper.toEntity(request);
        category.setImage(imagePath);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategoryRequest request, Long id) {
        Category category = getNonDeletedCategoryById(id);
        checkDuplicateCategoryName(request.getName(), id);
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (category.getImage() != null) {
                fileService.deleteImageFromFileSystem(category.getImage());
            }
            String imagePath = fileService.saveImageToFileSystem(request.getImage());
            category.setImage(imagePath);
        }
        category.setCategoryName(request.getName());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getNonDeletedCategoryById(id);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private void checkDuplicateCategoryName(String name, Long id) {
        if (id == null && categoryRepository.existsByCategoryName(name)) {
            throw new DuplicateItemException(CATEGORY_ALREADY_EXISTS);
        }
        if (id != null && categoryRepository.existsByCategoryNameAndCategoryIdNot(name, id)) {
            throw new DuplicateItemException(CATEGORY_ALREADY_EXISTS);
        }
    }

    public Category getNonDeletedCategoryById(Long id) {
        return categoryRepository
                .findByCategoryIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CATEGORY_NOT_FOUND));
    }
}
