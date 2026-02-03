package com.ecommerce.e_commerce.commerce.payment.service;

import com.ecommerce.e_commerce.commerce.order.dto.OrderResponse;
import com.ecommerce.e_commerce.commerce.order.enums.OrderStatus;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.order.repository.OrderRepository;
import com.ecommerce.e_commerce.commerce.order.service.OrderService;
import com.ecommerce.e_commerce.commerce.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.OnlineCaptureResponse;
import com.ecommerce.e_commerce.commerce.payment.dto.OnlinePaymentResponse;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentStatus;
import com.ecommerce.e_commerce.commerce.payment.factory.PaymentStrategyFactory;
import com.ecommerce.e_commerce.commerce.payment.model.PaymentTransaction;
import com.ecommerce.e_commerce.commerce.payment.repository.PaymentTransactionRepository;
import com.ecommerce.e_commerce.commerce.payment.strategy.offline.OfflinePaymentStrategy;
import com.ecommerce.e_commerce.commerce.payment.strategy.online.OnlinePaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderService orderService;
    private final PaymentStrategyFactory paymentStrategyFactory;


    @Override
    public OnlinePaymentResponse createPaypalPayment(Long orderId) {
        OnlinePaymentStrategy strategy = paymentStrategyFactory.getOnlineStrategy(PaymentMethod.PAYPAL);
        return strategy.createPayment(orderId);
    }

    @Override
    public OnlineCaptureResponse capturePayPalPayment(Long orderId, String paypalOrderId) {
        OnlinePaymentStrategy strategy = paymentStrategyFactory.getOnlineStrategy(PaymentMethod.PAYPAL);
        return strategy.capturePayment(orderId, paypalOrderId);
    }

    @Override
    public void handlePaymentFailure(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        PaymentTransaction paymentTransaction = getPaymentTransactionByOrderOrNull(order);
        if (paymentTransaction != null) {
            paymentTransaction.setPaymentStatus(PaymentStatus.CANCELED);
            paymentTransactionRepository.save(paymentTransaction);
        }
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        PaymentTransaction paymentTransaction = getPaymentTransactionByOrderOrNull(order);
        if (paymentTransaction == null) {
            return PaymentStatusResponse
                    .builder()
                    .orderId(orderId)
                    .orderStatus(order.getStatus().toString())
                    .build();
        }
        return buildPaymentStatusResponse(order, paymentTransaction);
    }

    @Override
    public OrderResponse createCODPayment(Long orderId) {
        OfflinePaymentStrategy strategy = paymentStrategyFactory.getOfflineStrategy(PaymentMethod.COD);
        return strategy.createPayment(orderId);
    }

    @Override
    public void completeCODPayment(Long orderId) {
        OfflinePaymentStrategy strategy = paymentStrategyFactory.getOfflineStrategy(PaymentMethod.COD);
        strategy.completePayment(orderId);
    }

    private PaymentTransaction getPaymentTransactionByOrderOrNull(Order order) {
        return paymentTransactionRepository.findByOrder(order)
                .orElse(null);
    }

    private PaymentStatusResponse buildPaymentStatusResponse(Order order, PaymentTransaction paymentTransaction) {
        return PaymentStatusResponse
                .builder()
                .orderId(order.getOrderId())
                .paymentMethod(paymentTransaction.getPaymentMethod())
                .paymentStatus(paymentTransaction.getPaymentStatus())
                .paypalOrderId(paymentTransaction.getPaypalOrderId())
                .captureId(paymentTransaction.getExternalTransactionId())
                .amount(paymentTransaction.getAmount())
                .currency(paymentTransaction.getCurrency())
                .createdAt(paymentTransaction.getCreatedAt())
                .updatedAt(paymentTransaction.getUpdatedAt())
                .orderStatus(order.getStatus().toString())
                .build();
    }
}