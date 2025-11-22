package com.ecommerce.e_commerce.core.category.mapper;

import com.ecommerce.e_commerce.core.category.dtos.CategoryRequest;
import com.ecommerce.e_commerce.core.category.dtos.CategoryResponse;
import com.ecommerce.e_commerce.core.category.model.Category;
import com.ecommerce.e_commerce.core.common.utils.MapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.logging.FileHandler;

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
