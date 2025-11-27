package com.ecommerce.e_commerce.commerce.wishlist.mapper;

import com.ecommerce.e_commerce.commerce.product.dtos.StockWarning;
import com.ecommerce.e_commerce.commerce.wishlist.dtos.WishlistResponse;
import com.ecommerce.e_commerce.commerce.wishlist.model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {WishlistItemMapper.class}
)
public interface WishlistMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "wishlistItems")
    @Mapping(target = "id", source = "wishlistId")
    WishlistResponse toResponse(Wishlist wishlist);

    default WishlistResponse toResponseWithWarnings(Wishlist wishlist, List<StockWarning> warnings) {
        WishlistResponse response = toResponse(wishlist);
        response.setWarnings(warnings);
        return response;
    }
}
