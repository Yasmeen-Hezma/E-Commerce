package com.ecommerce.e_commerce.commerce.order.event.listener;


import com.ecommerce.e_commerce.commerce.order.event.OrderCompletedEvent;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.order.model.OrderItem;
import com.ecommerce.e_commerce.commerce.order.service.OrderService;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import com.ecommerce.e_commerce.commerce.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryUpdateEventListenerTest {
    @Mock
    private ProductService productService;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private InventoryUpdateEventListener listener;

    @Test
    void handleOrderCompleted_ShouldDecreaseStock_WhenOrderCompleted() {
        // Arrange
        Product product1 = Product
                .builder()
                .productId(1L)
                .build();
        Product product2 = Product
                .builder()
                .productId(2L)
                .build();
        OrderItem item1 = OrderItem
                .builder()
                .product(product1)
                .quantity(2)
                .build();
        OrderItem item2 = OrderItem
                .builder()
                .product(product2)
                .quantity(3)
                .build();
        Order order = Order.builder()
                .orderId(1L)
                .orderItems(List.of(item1, item2))
                .build();

        OrderCompletedEvent event =
                new OrderCompletedEvent(1L, BigDecimal.valueOf(100), "test@email.com");

        when(orderService.getOrderById(1L)).thenReturn(order);
        // Act
        listener.handleOrderCompleted(event);
        // Assert
        verify(orderService).getOrderById(1L);
        verify(productService).decreaseStock(1L, 2);
        verify(productService).decreaseStock(2L, 3);
        verifyNoMoreInteractions(productService);
    }
}