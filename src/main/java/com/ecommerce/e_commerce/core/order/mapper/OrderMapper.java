package com.ecommerce.e_commerce.core.order.mapper;

import com.ecommerce.e_commerce.core.order.dto.OrderItemResponse;
import com.ecommerce.e_commerce.core.order.dto.OrderResponse;
import com.ecommerce.e_commerce.core.order.model.Order;
import com.ecommerce.e_commerce.core.order.model.OrderItem;
import com.ecommerce.e_commerce.core.payment.dto.PaymentResponse;
import com.ecommerce.e_commerce.core.payment.model.PaymentTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "items")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "orderTotal", target = "totalPrice")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "paymentTransaction", target = "payment")
    // Address mapping
    @Mapping(source = "shippingGovernorate",target = "address.governorate")
    @Mapping(source = "shippingCity",target = "address.city")
    @Mapping(source = "shippingStreet",target = "address.street")
    @Mapping(source = "shippingFloorNumber",target = "address.floorNumber")
    @Mapping(source = "shippingApartmentNumber",target = "address.apartmentNumber")
    @Mapping(source = "shippingPhone",target = "address.phone")
    @Mapping(source = "deliveryNotes",target = "address.deliveryNotes")
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.image", target = "image")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    PaymentResponse toPaymentResponse(PaymentTransaction paymentTransaction);
}
