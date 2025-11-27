package com.ecommerce.e_commerce.commerce.cart.mapper;

import com.ecommerce.e_commerce.commerce.cart.dtos.CartResponse;
import com.ecommerce.e_commerce.commerce.cart.model.Cart;
import com.ecommerce.e_commerce.commerce.product.dtos.StockWarning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CartItemMapper.class}
)
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(getTotalPrice(cart))")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "id", source = "cartId")
    CartResponse toResponse(Cart cart);

    default BigDecimal getTotalPrice(Cart cart) {
        return cart.getCartItems() == null ? BigDecimal.ZERO : cart
                .getCartItems()
                .stream()
                .map(item -> item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    default CartResponse toResponseWithWarnings(Cart cart, List<StockWarning> warnings) {
        CartResponse response = toResponse(cart);
        response.setWarnings(warnings);
        return response;
    }
}
