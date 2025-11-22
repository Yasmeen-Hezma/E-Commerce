package com.ecommerce.e_commerce.core.brand.service;

import com.ecommerce.e_commerce.core.brand.dtos.BrandRequest;
import com.ecommerce.e_commerce.core.brand.dtos.BrandResponse;
import com.ecommerce.e_commerce.core.brand.mapper.BrandMapper;
import com.ecommerce.e_commerce.core.brand.model.Brand;
import com.ecommerce.e_commerce.core.brand.repository.BrandRepository;
import com.ecommerce.e_commerce.core.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.core.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.core.common.service.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecommerce.e_commerce.core.common.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final FileStorageService fileService;

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository
                .findAllByDeletedFalse()
                .stream()
                .map(brandMapper::toResponse)
                .toList();
    }

    @Override
    public BrandResponse getBrandById(Long brandId) {
        Brand brand = getNonDeletedBrandById(brandId);
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        checkDuplicateBrandName(request.getName(), null);
        String imagePath = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imagePath = fileService.saveImageToFileSystem(request.getImage());
        }
        Brand brand = brandMapper.toEntity(request);
        brand.setImage(imagePath);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toResponse(savedBrand);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long brandId, BrandRequest request) {
        Brand brand = brandRepository
                .findByBrandIdAndDeletedFalse(brandId)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND));
        checkDuplicateBrandName(request.getName(), brandId);
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (brand.getImage() != null) {
                fileService.deleteImageFromFileSystem(brand.getImage());
            }
            String imagePath = fileService.saveImageToFileSystem(request.getImage());
            brand.setImage(imagePath);
        }
        brand.setBrandName(request.getName());
        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toResponse(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long brandId) {
        Brand brand = getNonDeletedBrandById(brandId);
        brand.setDeleted(true);
        brandRepository.save(brand);
    }

    private void checkDuplicateBrandName(String brandName, Long brandId) {
        if (brandId == null && brandRepository.existsByBrandNameAndDeletedFalse(brandName)) {
            throw new DuplicateItemException(BRAND_ALREADY_EXISTS);
        }
        if (brandId != null && brandRepository.existsByBrandNameAndDeletedFalseAndBrandIdNot(brandName, brandId)) {
            throw new DuplicateItemException(BRAND_ALREADY_EXISTS);
        }
    }

    public Brand getNonDeletedBrandById(Long id) {
        return brandRepository
                .findByBrandIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(BRAND_NOT_FOUND));
    }
}
