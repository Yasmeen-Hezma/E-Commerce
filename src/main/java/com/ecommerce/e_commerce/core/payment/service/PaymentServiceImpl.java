package com.ecommerce.e_commerce.core.payment.service;


import com.ecommerce.e_commerce.core.common.exception.*;
import com.ecommerce.e_commerce.core.order.dto.OrderResponse;
import com.ecommerce.e_commerce.core.order.enums.OrderStatus;
import com.ecommerce.e_commerce.core.order.mapper.OrderMapper;
import com.ecommerce.e_commerce.core.order.model.Order;
import com.ecommerce.e_commerce.core.order.repository.OrderRepository;
import com.ecommerce.e_commerce.core.order.service.OrderService;
import com.ecommerce.e_commerce.core.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaypalCaptureResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaypalOrderResponse;
import com.ecommerce.e_commerce.core.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.core.payment.enums.PaymentStatus;
import com.ecommerce.e_commerce.core.payment.model.PaymentTransaction;
import com.ecommerce.e_commerce.core.payment.repository.PaymentTransactionRepository;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.ecommerce.e_commerce.core.common.utils.Constants.*;


@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String APPROVE = "approve";
    public static final String USD = "USD";
    public static final String LIVE = "live";
    public static final String COMPLETED = "COMPLETED";
    public static final String E_COMMERCE = "E-COMMERCE";
    public static final String BILLING = "BILLING";
    public static final String PAY_NOW = "PAY_NOW";
    public static final String CAPTURE = "CAPTURE";
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @Value("${paypal.client-id}")
    private String paypalClientId;
    @Value("${paypal.client-secret}")
    private String paypalClientSecret;
    @Value(("${paypal.mode}"))
    private String paypalMode;
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public PaypalOrderResponse createPaypalPayment(Long orderId) {
        // 1) Get and validate order
        Order order = orderService.getOrderById(orderId);
        validateOrderForPayment(order);
        // 2) Create PayPal client
        PayPalHttpClient client = createPaypalClient();
        // 3) Build PayPal order request
        OrderRequest orderRequest = buildPayPalOrderRequest(order);
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(orderRequest);
        try {
            // 4) Execute PayPal API call
            HttpResponse<com.paypal.orders.Order> response = client.execute(request);
            com.paypal.orders.Order paypalOrder = response.result();
            // 5) Save payment transaction
            PaymentTransaction paymentTransaction = createPaymentTransactionPayPal(order, paypalOrder);
            paymentTransactionRepository.save(paymentTransaction);
            // 6) Update order status
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            return buildPayPalOrderResponse(paypalOrder, order);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PaypalCaptureResponse capturePayPalPayment(Long orderId, String paypalOrderId) {
        // 1) Get order and payment transaction
        Order order = orderService.getOrderById(orderId);
        PaymentTransaction paymentTransaction = getPaymentTransactionByOrder(order);
        // 2) Verify Paypal Order ID matches
        validatePayPalOrderId(paypalOrderId, paymentTransaction);
        // 3) Create PayPal client and capture request
        PayPalHttpClient client = createPaypalClient();
        OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
        request.prefer("return=representation");
        try {
            // 4) Execute capture
            HttpResponse<com.paypal.orders.Order> response = client.execute(request);
            com.paypal.orders.Order capturedOrder = response.result();
            // 5) Verify capture was successful
            if (COMPLETED.equals(capturedOrder.status())) {
                String captureId = extractCaptureId(capturedOrder);

                paymentTransaction.setPaymentStatus(PaymentStatus.COMPLETED);
                paymentTransaction.setExternalTransactionId(captureId);
                paymentTransactionRepository.save(paymentTransaction);

                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                return PaypalCaptureResponse
                        .builder()
                        .orderId(orderId)
                        .paypalOrderId(paypalOrderId)
                        .captureId(captureId)
                        .status(COMPLETED)
                        .amount(paymentTransaction.getAmount())
                        .build();
            } else {
                throw new PayPalCaptureException(PAYPAL_CAPTURE_WAS_NOT_COMPLETED);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        Order order = orderService.getOrderById(orderId);
        validateOrderForPayment(order);

        PaymentTransaction paymentTransaction = createPaymentTransactionCOD(order);
        paymentTransactionRepository.save(paymentTransaction);

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void completeCODPayment(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        PaymentTransaction paymentTransaction = getPaymentTransactionByOrder(order);

        if (PaymentStatus.COMPLETED.equals(paymentTransaction.getPaymentStatus())) {
            throw new PaymentAlreadyCompletedException(ORDER_ALREADY_COMPLETED);
        }

        paymentTransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentTransactionRepository.save(paymentTransaction);

    }

    private PaypalOrderResponse buildPayPalOrderResponse(com.paypal.orders.Order paypalOrder, Order order) {
        String approvalUrl = paypalOrder
                .links()
                .stream()
                .filter(link -> APPROVE.equals(link.rel()))
                .findFirst()
                .map(LinkDescription::href)
                .orElseThrow(() -> new PayPalApprovalUrlNotFoundException(NO_APPROVAL_URL_FOUND_IN_PAYPAL_RESPONSE));
        return PaypalOrderResponse
                .builder()
                .paypalOrderId(paypalOrder.id())
                .orderId(order.getOrderId())
                .status(paypalOrder.status())
                .approvalUrl(approvalUrl)
                .amount(order.getOrderTotal())
                .currency(USD)
                .build();
    }

    private PaymentTransaction createPaymentTransactionPayPal(Order order, com.paypal.orders.Order paypalOrder) {
        return PaymentTransaction
                .builder()
                .order(order)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .paypalOrderId(paypalOrder.id())
                .amount(order.getOrderTotal())
                .currency(USD)
                .build();
    }

    private PaymentTransaction createPaymentTransactionCOD(Order order) {
        return PaymentTransaction
                .builder()
                .order(order)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(order.getOrderTotal())
                .currency(USD)
                .build();
    }

    private OrderRequest buildPayPalOrderRequest(Order order) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent(CAPTURE);

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(baseUrl + "api/v1/order/" + order.getOrderId() + "/payment/paypal/success")
                .cancelUrl(baseUrl + "api/v1/order/" + order.getOrderId() + "/payment/paypal/cancel")
                .brandName(E_COMMERCE)
                .landingPage(BILLING)
                .userAction(PAY_NOW);
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(new PurchaseUnitRequest()
                .referenceId(order.getOrderId().toString())
                .description("Order #" + order.getOrderId())
                .customId(order.getOrderId().toString())
                .softDescriptor(E_COMMERCE)
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode(USD)
                        .value(order.getOrderTotal().toString())));
        orderRequest.purchaseUnits(purchaseUnits);
        return orderRequest;
    }

    private void validateOrderForPayment(Order order) {
        if (order.getPaymentTransaction() != null && PaymentStatus.COMPLETED.equals(order.getPaymentTransaction().getPaymentStatus())) {
            throw new PaymentAlreadyCompletedException(ORDER_ALREADY_COMPLETED);
        }
        if (order.getOrderTotal() == null || order.getOrderTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderTotalException(INVALID_ORDER_TOTAL);
        }
        if (!OrderStatus.PENDING.equals(order.getStatus())
                && !OrderStatus.PAYMENT_FAILED.equals(order.getStatus())) {
            throw new InvalidOrderStatusException(INVALID_ORDER_STATUS);
        }
    }

    private PayPalHttpClient createPaypalClient() {
        PayPalEnvironment environment;
        if (LIVE.equalsIgnoreCase(paypalMode)) {
            environment = new PayPalEnvironment.Live(paypalClientId, paypalClientSecret);
        } else {
            environment = new PayPalEnvironment.Sandbox(paypalClientId, paypalClientSecret);
        }
        return new PayPalHttpClient(environment);
    }

    private PaymentTransaction getPaymentTransactionByOrder(Order order) {
        return paymentTransactionRepository.findByOrder(order)
                .orElseThrow(() -> new ItemNotFoundException(PAYMENT_NOT_FOUND));
    }

    private PaymentTransaction getPaymentTransactionByOrderOrNull(Order order) {
        return paymentTransactionRepository.findByOrder(order)
                .orElse(null);
    }


    private void validatePayPalOrderId(String paypalOrderId, PaymentTransaction paymentTransaction) {
        if (!paypalOrderId.equals(paymentTransaction.getPaypalOrderId())) {
            throw new PayPalOrderMismatchException(PAYPAL_ORDER_ID_MISMATCH);
        }
    }

    private String extractCaptureId(com.paypal.orders.Order capturedOrder) {
        return capturedOrder.purchaseUnits().getFirst().payments().captures().getFirst().id();
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