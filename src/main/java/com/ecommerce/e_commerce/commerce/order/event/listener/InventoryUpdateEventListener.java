package com.ecommerce.e_commerce.commerce.order.event.listener;

import com.ecommerce.e_commerce.commerce.order.event.OrderCompletedEvent;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.order.service.OrderService;
import com.ecommerce.e_commerce.commerce.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryUpdateEventListener {
    private final ProductService productService;
    private final OrderService orderService;

    @EventListener
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        Order order = orderService.getOrderById(event.orderId());
        order.getOrderItems()
                .forEach(item -> productService.decreaseStock(item.getProduct().getProductId(), item.getQuantity()));
    }
}
