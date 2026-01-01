package com.ecommerce.e_commerce.commerce.review.mapper;

import com.ecommerce.e_commerce.commerce.product.model.Product;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewRequest;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewResponse;
import com.ecommerce.e_commerce.commerce.review.model.Review;
import com.ecommerce.e_commerce.user.profile.model.User;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReviewMapper {
    @Mapping(target = "product", source = "product")
    @Mapping(target = "user", source = "user")
    Review toEntity(ReviewRequest request, Product product, User user);

    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userName", expression = "java(review.getUser().getFirstName()+\" \"+review.getUser().getLastName())")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
        // @MappingTarget -> update an existing object instead of creating a new one
    void updateReviewFromRequest(ReviewRequest reviewRequest, @MappingTarget Review review);
}
