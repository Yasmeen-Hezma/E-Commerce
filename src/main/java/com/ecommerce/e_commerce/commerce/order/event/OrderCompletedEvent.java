package com.ecommerce.e_commerce.commerce.order.event;

import java.math.BigDecimal;

public record OrderCompletedEvent(Long orderId, BigDecimal orderTotal, String customerEmail) {
}
