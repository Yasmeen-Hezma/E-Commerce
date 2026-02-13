package com.ecommerce.e_commerce.commerce.order.event.listener;

import com.ecommerce.e_commerce.commerce.order.event.OrderCompletedEvent;
import com.ecommerce.e_commerce.security.auth.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderConfirmationEmailEventListener {
    private final EmailService emailService;

    @Async
    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) throws MessagingException {
        Long orderId = event.orderId();
        BigDecimal orderTotal=event.orderTotal();
        String email = event.customerEmail();
        emailService.sendOrderConfirmationEmail(email, orderId,orderTotal);
    }
}
