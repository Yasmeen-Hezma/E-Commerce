package com.ecommerce.e_commerce.commerce.order.event.listener;


import com.ecommerce.e_commerce.commerce.order.event.OrderCompletedEvent;
import com.ecommerce.e_commerce.security.auth.service.EmailService;
import jakarta.mail.MessagingException;
import org.hibernate.sql.ast.tree.expression.Over;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderConfirmationEmailEventListenerTest {
    @Mock
    private EmailService emailService;
    @InjectMocks
    private OrderConfirmationEmailEventListener listener;

    @Test
    void handleOrderCompleted_ShouldSendOrderConfirmationEmail_WhenOrderCompleted() throws MessagingException {
        // Arrange
        OrderCompletedEvent event =
                new OrderCompletedEvent(1L, BigDecimal.valueOf(100.00), "customer@email.com");
        // Act
        listener.handleOrderCompleted(event);
        // Assert
        verify(emailService).sendOrderConfirmationEmail("customer@email.com", 1L, BigDecimal.valueOf(100.00));
    }

}