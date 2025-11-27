package com.ecommerce.e_commerce.user.profile.mapper;

import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.dtos.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dtos.UpdateProfileRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProfileMapper {
    @Mapping(target = "email", source = "authUser.email")
    @Mapping(target = "address.governorate", source = "governorate")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.floorNumber", source = "floorNumber")
    @Mapping(target = "address.apartmentNumber", source = "apartmentNumber")
    @Mapping(target = "hasCompleteAddress", expression = "java(user.hasCompleteShippingAddress())")
    ProfileResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authUser", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "brandName", ignore = true)
    @Mapping(target = "sellerType", ignore = true)
    @Mapping(target = "shippingZone", ignore = true)
    @Mapping(target = "businessAddress", ignore = true)
    void updateUserFromRequest(UpdateProfileRequest request, @MappingTarget User user);

}
