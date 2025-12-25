package com.ecommerce.e_commerce.commerce.category.mapper;

import com.ecommerce.e_commerce.commerce.category.dto.CategoryRequest;
import com.ecommerce.e_commerce.commerce.category.dto.CategoryResponse;
import com.ecommerce.e_commerce.commerce.category.model.Category;
import com.ecommerce.e_commerce.common.utils.MapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring"
        , uses = MapperUtil.class)
public interface CategoryMapper {
    @Mapping(source = "categoryId", target = "id")
    @Mapping(source = "categoryName", target = "name")
    @Mapping(target = "image", source = "image", qualifiedByName = "mapImageUrl")
    CategoryResponse toResponse(Category category);

    @Mapping(source = "name", target = "categoryName")
    @Mapping(target = "image", ignore = true)
    Category toEntity(CategoryRequest request);
}
