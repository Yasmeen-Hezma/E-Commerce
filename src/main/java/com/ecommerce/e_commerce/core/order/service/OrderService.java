package com.ecommerce.e_commerce.core.order.service;

import com.ecommerce.e_commerce.core.order.dto.OrderResponse;
import com.ecommerce.e_commerce.core.order.dto.ShippingAddressRequest;
import com.ecommerce.e_commerce.core.order.model.Order;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    OrderResponse createOrderFromCart(HttpServletRequest request);

    OrderResponse addShippingAddress(Long orderId, ShippingAddressRequest addressRequest, HttpServletRequest request);

    Order getOrderById(Long orderId);

    OrderResponse getOrderResponseById(Long orderId);

}
