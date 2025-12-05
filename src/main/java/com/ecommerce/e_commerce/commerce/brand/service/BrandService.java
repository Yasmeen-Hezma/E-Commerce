package com.ecommerce.e_commerce.commerce.brand.service;

import com.ecommerce.e_commerce.commerce.brand.dtos.BrandRequest;
import com.ecommerce.e_commerce.commerce.brand.dtos.BrandResponse;
import com.ecommerce.e_commerce.commerce.brand.model.Brand;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrands();

    BrandResponse getBrandById(Long brandId);

    BrandResponse createBrand(BrandRequest request);

    BrandResponse updateBrand(Long brandId, BrandRequest request);

    void deleteBrand(Long brandId);

    Brand getNonDeletedBrandById(Long id);
}
