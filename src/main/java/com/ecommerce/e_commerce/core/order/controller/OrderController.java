package com.ecommerce.e_commerce.core.order.controller;

import com.ecommerce.e_commerce.core.order.dto.OrderResponse;
import com.ecommerce.e_commerce.core.order.dto.ShippingAddressRequest;
import com.ecommerce.e_commerce.core.order.service.OrderService;
import com.ecommerce.e_commerce.core.payment.dto.PaymentStatusResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaypalCaptureResponse;
import com.ecommerce.e_commerce.core.payment.dto.PaypalOrderResponse;
import com.ecommerce.e_commerce.core.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(HttpServletRequest request) {
        return ResponseEntity.ok(orderService.createOrderFromCart(request));
    }

    @PatchMapping("{orderId}/shipping-address")
    public ResponseEntity<OrderResponse> addShippingAddress(
            @PathVariable Long orderId,
            @Valid @RequestBody ShippingAddressRequest addressRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(orderService.addShippingAddress(orderId, addressRequest, request));
    }

    @PostMapping("{orderId}/payment/paypal/create")
    public ResponseEntity<PaypalOrderResponse> createPayPalPayment(@PathVariable Long orderId) {
        PaypalOrderResponse response = paymentService.createPaypalPayment(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("{orderId}/payment/paypal/capture")
    public ResponseEntity<PaypalCaptureResponse> capturePayPalPayment(@PathVariable Long orderId, @RequestParam String token) {
        PaypalCaptureResponse response = paymentService.capturePayPalPayment(orderId, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{orderId}/payment/paypal/success")
    public ResponseEntity<String> handlePayPalSuccess(@PathVariable Long orderId, @RequestParam String token) {
        return ResponseEntity.ok()
                .header("Location", "/payment/success?orderId=" + orderId + "&token=" + token)
                .body("Redirecting to payment completion...");

    }

    @GetMapping("{orderId}/payment/paypal/cancel")
    public ResponseEntity<String> handlePayPalCancel(@PathVariable Long orderId, @RequestParam String token) {
        paymentService.handlePaymentFailure(orderId);
        return ResponseEntity.ok()
                .header("Location", "/payment/cancelled?orderId=" + orderId)
                .body("Payment was cancelled");
    }

    @GetMapping("{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderResponseById(orderId));
    }

    @GetMapping("{orderId}/payment/status")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(orderId));
    }

    @PostMapping("{orderId}/payment/cod/confirm")
    public ResponseEntity<OrderResponse> confirmCODPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.createCODPayment(orderId));
    }

    @PatchMapping("{orderId}/payment/cod/complete")
    public ResponseEntity<Void> completeCODPayment(@PathVariable Long orderId) {
        paymentService.completeCODPayment(orderId);
        return ResponseEntity.noContent().build();
    }

}
