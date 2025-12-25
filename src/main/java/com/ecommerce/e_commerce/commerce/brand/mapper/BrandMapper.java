package com.ecommerce.e_commerce.commerce.brand.mapper;

import com.ecommerce.e_commerce.commerce.brand.dto.BrandRequest;
import com.ecommerce.e_commerce.commerce.brand.dto.BrandResponse;
import com.ecommerce.e_commerce.commerce.brand.model.Brand;
import com.ecommerce.e_commerce.common.utils.MapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring"
        , uses = MapperUtil.class)
public interface BrandMapper {
    @Mapping(source = "brandId", target = "id")
    @Mapping(source = "brandName", target = "name")
    @Mapping(target = "image", source = "image", qualifiedByName = "mapImageUrl")
    BrandResponse toResponse(Brand brand);

    @Mapping(source = "name", target = "brandName")
    @Mapping(target = "image", ignore = true)
    Brand toEntity(BrandRequest request);
}
