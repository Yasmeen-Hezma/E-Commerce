package com.ecommerce.e_commerce.commerce.payment.validation;

import com.ecommerce.e_commerce.commerce.order.enums.OrderStatus;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.common.exception.InvalidOrderStatusException;
import org.springframework.stereotype.Component;

import static com.ecommerce.e_commerce.common.utils.Constants.INVALID_ORDER_STATUS;
@Component
public class OrderStatusValidator extends PaymentOrderValidator {
    @Override
    protected void doValidate(Order order) {
        if (!OrderStatus.PENDING.equals(order.getStatus())
                && !OrderStatus.PAYMENT_FAILED.equals(order.getStatus())) {
            throw new InvalidOrderStatusException(INVALID_ORDER_STATUS);
        }
    }
}
