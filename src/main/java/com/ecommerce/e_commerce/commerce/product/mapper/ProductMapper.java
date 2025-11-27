package com.ecommerce.e_commerce.commerce.product.mapper;

import com.ecommerce.e_commerce.commerce.brand.model.Brand;
import com.ecommerce.e_commerce.commerce.category.model.Category;
import com.ecommerce.e_commerce.common.utils.MapperUtil;
import com.ecommerce.e_commerce.commerce.product.dtos.ProductRequest;
import com.ecommerce.e_commerce.commerce.product.dtos.ProductResponse;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import org.mapstruct.*;


@Mapper(
        componentModel = "spring",
        uses = MapperUtil.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)

public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "productName", source = "request.name")
    @Mapping(target = "image", ignore = true)
    Product toEntity(ProductRequest request, Category category, Brand brand);

    @Mapping(target = "image", source = "image", qualifiedByName = "mapImageUrl")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "name", source = "productName")
    @Mapping(target = "category", source = "category.categoryName")
    @Mapping(target = "brand", source = "brand.brandName")
    ProductResponse toResponse(Product product);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "image", ignore = true)
    void updateProductFromRequest(ProductRequest request, @MappingTarget Product product);
}
