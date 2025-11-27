package com.ecommerce.e_commerce.commerce.brand.repository;

import com.ecommerce.e_commerce.commerce.brand.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByBrandNameAndDeletedFalseAndBrandIdNot(String brandName, Long brandId);

    boolean existsByBrandNameAndDeletedFalse(String brandName);

    List<Brand> findAllByDeletedFalse();

    Optional<Brand> findByBrandIdAndDeletedFalse(Long id);
}
