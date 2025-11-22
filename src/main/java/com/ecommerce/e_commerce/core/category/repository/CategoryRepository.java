package com.ecommerce.e_commerce.core.category.repository;

import com.ecommerce.e_commerce.core.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryNameAndCategoryIdNot(String name, Long id);

    boolean existsByCategoryName(String name);

    List<Category> findAllByDeletedFalse();

    Optional<Category> findByCategoryIdAndDeletedFalse(Long id);
}
