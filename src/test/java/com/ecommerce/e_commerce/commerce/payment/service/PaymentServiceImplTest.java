package com.ecommerce.e_commerce.commerce.payment.service;

import com.ecommerce.e_commerce.commerce.order.dto.OrderResponse;
import com.ecommerce.e_commerce.commerce.order.enums.OrderStatus;
import com.ecommerce.e_commerce.commerce.order.mapper.OrderMapper;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.order.repository.OrderRepository;
import com.ecommerce.e_commerce.commerce.order.service.OrderService;
import com.ecommerce.e_commerce.commerce.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentStatus;
import com.ecommerce.e_commerce.commerce.payment.model.PaymentTransaction;
import com.ecommerce.e_commerce.commerce.payment.repository.PaymentTransactionRepository;
import com.ecommerce.e_commerce.commerce.payment.validation.PaymentOrderValidationChain;
import com.ecommerce.e_commerce.commerce.payment.validation.PaymentOrderValidator;
import com.ecommerce.e_commerce.common.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentOrderValidationChain paymentOrderValidationChain;
    @Mock
    private PaymentOrderValidator paymentOrderValidator;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private PaymentTransaction paymentTransaction;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "paypalClientId", "test-client-id");
        ReflectionTestUtils.setField(paymentService, "paypalClientSecret", "test-client-secret");
        ReflectionTestUtils.setField(paymentService, "paypalMode", "sandbox");
        ReflectionTestUtils.setField(paymentService, "baseUrl", "http://localhost:8080/");

        order = Order
                .builder()
                .orderId(1L)
                .orderTotal(BigDecimal.valueOf(100.00))
                .status(OrderStatus.PENDING)
                .build();
        paymentTransaction = PaymentTransaction
                .builder()
                .paymentId(1L)
                .order(order)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .paypalOrderId("PAYPAL-ORDER-123")
                .amount(BigDecimal.valueOf(100.00))
                .currency("USD")
                .build();
    }

    @Test
    void createPaypalPayment_ShouldThrowException_WhenOrderAlreadyCompleted() {
        // Arrange
        paymentTransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setPaymentTransaction(paymentTransaction);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        doThrow(new PaymentAlreadyCompletedException(ORDER_ALREADY_COMPLETED))
                .when(paymentOrderValidator)
                .validate(any(Order.class));
        // Act & Assert
        assertThatThrownBy(() -> paymentService.createPaypalPayment(1L))
                .isInstanceOf(PaymentAlreadyCompletedException.class)
                .hasMessageContaining(ORDER_ALREADY_COMPLETED);
        verify(orderService).getOrderById(1L);
        verify(paymentOrderValidationChain).build();
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }

    @Test
    void createPaypalPayment_ShouldThrowException_WhenInvalidOrderTotal() {
        // Arrange
        order.setOrderTotal(BigDecimal.ZERO);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        doThrow(new InvalidOrderTotalException(INVALID_ORDER_TOTAL))
                .when(paymentOrderValidator)
                .validate(any(Order.class));
        // Act & Assert
        assertThatThrownBy(() -> paymentService.createPaypalPayment(1L))
                .isInstanceOf(InvalidOrderTotalException.class)
                .hasMessageContaining(INVALID_ORDER_TOTAL);
        verify(orderService).getOrderById(1L);
        verify(paymentOrderValidationChain).build();
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }

    @Test
    void createPaypalPayment_ShouldThrowException_WhenInvalidOrderStatus() {
        // Arrange
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        doThrow(new InvalidOrderStatusException(INVALID_ORDER_STATUS))
                .when(paymentOrderValidator)
                .validate(any(Order.class));
        // Act & Assert
        assertThatThrownBy(() -> paymentService.createPaypalPayment(1L))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining(INVALID_ORDER_STATUS);
        verify(orderService).getOrderById(1L);
        verify(paymentOrderValidationChain).build();
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }

    @Test
    void capturePayPalPayment_ShouldThrowException_WhenPayPalOrderMisMatch() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.of(paymentTransaction));
        // Act & Assert
        assertThatThrownBy(() -> paymentService.capturePayPalPayment(1L, "WRONG-ORDER-ID"))
                .isInstanceOf(PayPalOrderMismatchException.class)
                .hasMessageContaining(PAYPAL_ORDER_ID_MISMATCH);
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void capturePayPalPayment_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> paymentService.capturePayPalPayment(1L, "PAYPAL-ORDER-123"))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(PAYMENT_NOT_FOUND);
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void handlePaymentFailure_ShouldUpdatePaymentStatus_WhenPaymentExists() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.of(paymentTransaction));
        // Act
        paymentService.handlePaymentFailure(1L);
        // Assert
        verify(paymentTransactionRepository).save(argThat(pt -> pt.getPaymentStatus() == PaymentStatus.CANCELED));
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.PAYMENT_FAILED));
    }

    @Test
    void handlePaymentFailure_ShouldUpdateOrderStatusOnly_WhenPaymentNotExist() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.empty());
        // Act
        paymentService.handlePaymentFailure(1L);
        // Assert
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.PAYMENT_FAILED));
    }

    @Test
    void getPaymentStatus_ShouldReturnCompleteStatus_WhenTransactionFound() {
        // Arrange
        paymentTransaction.setCreatedAt(Instant.now());
        paymentTransaction.setUpdatedAt(Instant.now());
        paymentTransaction.setExternalTransactionId("CAPTURE-123");
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.of(paymentTransaction));
        // Act
        PaymentStatusResponse result = paymentService.getPaymentStatus(1L);
        // Assert
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.getCaptureId()).isEqualTo("CAPTURE-123");
        assertThat(result.getPaypalOrderId()).isEqualTo("PAYPAL-ORDER-123");
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(100.00));
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void getPaymentStatus_ShouldReturnBasicStatus_WhenTransactionNotFound() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.empty());
        // Act
        PaymentStatusResponse result = paymentService.getPaymentStatus(1L);
        // Assert
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PENDING.toString());
        assertThat(result.getPaymentStatus()).isNull();
        assertThat(result.getPaymentMethod()).isNull();
    }

    @Test
    void createCODPayment_ShouldCreateCODPayment_WhenValidRequest() {
        // Arrange
        OrderResponse orderResponse = OrderResponse
                .builder()
                .id(1L)
                .build();
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);
        // Act
        OrderResponse result = paymentService.createCODPayment(1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).save(argThat(pt -> pt.getPaymentStatus() == PaymentStatus.PENDING && pt.getPaymentMethod() == PaymentMethod.COD));
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.CONFIRMED));
        verify(orderMapper).toOrderResponse(order);
    }

    @Test
    void createCODPayment_ShouldThrowException_WhenOrderAlreadyCompleted() {
        // Arrange
        paymentTransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setPaymentTransaction(paymentTransaction);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        doThrow(new PaymentAlreadyCompletedException(ORDER_ALREADY_COMPLETED))
                .when(paymentOrderValidator)
                .validate(any(Order.class));
        // Assert & Act
        assertThatThrownBy(() -> paymentService.createCODPayment(1L))
                .isInstanceOf(PaymentAlreadyCompletedException.class)
                .hasMessageContaining(ORDER_ALREADY_COMPLETED);
        verify(orderService).getOrderById(1L);
        verify(paymentOrderValidationChain).build();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createCODPayment_ShouldThrowException_WhenInValidOrderTotal() {
        // Arrange
        order.setOrderTotal(BigDecimal.ZERO);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        doThrow(new InvalidOrderTotalException(INVALID_ORDER_TOTAL))
                .when(paymentOrderValidator)
                .validate(any(Order.class));
        // Assert & Act
        assertThatThrownBy(() -> paymentService.createCODPayment(1L))
                .isInstanceOf(InvalidOrderTotalException.class)
                .hasMessageContaining(INVALID_ORDER_TOTAL);
        verify(orderService).getOrderById(1L);
        verify(paymentOrderValidationChain).build();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createCODPayment_ShouldThrowException_WhenInValidOrderStatus() {
        // Arrange
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentOrderValidationChain.build()).thenReturn(paymentOrderValidator);
        doThrow(new InvalidOrderStatusException(INVALID_ORDER_STATUS))
                .when(paymentOrderValidator)
                .validate(any(Order.class));
        // Assert & Act
        assertThatThrownBy(() -> paymentService.createCODPayment(1L))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining(INVALID_ORDER_STATUS);
        verify(orderService).getOrderById(1L);
        verify(paymentOrderValidationChain).build();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void completeCODPayment_ShouldCompleteCODPayment_WhenValidRequest() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.of(paymentTransaction));
        // Act
        paymentService.completeCODPayment(1L);
        // Assert
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        verify(paymentTransactionRepository).save(argThat(pt -> paymentTransaction.getPaymentStatus() == PaymentStatus.COMPLETED));
    }

    @Test
    void completeCODPayment_ShouldThrowException_WhenOrderAlreadyCompleted() {
        // Arrange
        paymentTransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.of(paymentTransaction));
        // Act & Assert
        assertThatThrownBy(() -> paymentService.completeCODPayment(1L))
                .isInstanceOf(PaymentAlreadyCompletedException.class)
                .hasMessageContaining(ORDER_ALREADY_COMPLETED);
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }

    @Test
    void completeCODPayment_ShouldThrowException_WhenTransactionNotFound() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> paymentService.completeCODPayment(1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(PAYMENT_NOT_FOUND);
        verify(orderService).getOrderById(1L);
        verify(paymentTransactionRepository).findByOrder(order);
        verify(paymentTransactionRepository, never()).save(any(PaymentTransaction.class));
    }
}