package com.ecommerce.e_commerce.commerce.wishlist.dtos;

import com.ecommerce.e_commerce.commerce.product.dtos.StockWarning;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WishlistResponse {
    private Long id;
    private Long userId;
    private List<WishlistItemResponse> items;
  //  private BigDecimal totalPrice;
    private List<StockWarning> warnings;
}
