package com.ecommerce.e_commerce.core.brand.service;

import com.ecommerce.e_commerce.core.brand.dtos.BrandRequest;
import com.ecommerce.e_commerce.core.brand.dtos.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrands();

    BrandResponse getBrandById(Long brandId);

    BrandResponse createBrand(BrandRequest request);

    BrandResponse updateBrand(Long brandId, BrandRequest request);

    void deleteBrand(Long brandId);
}
