package com.ecommerce.e_commerce.commerce.payment.validation;

import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentStatus;
import com.ecommerce.e_commerce.common.exception.PaymentAlreadyCompletedException;
import org.springframework.stereotype.Component;

import static com.ecommerce.e_commerce.common.utils.Constants.ORDER_ALREADY_COMPLETED;

@Component
public class PaymentAlreadyCompletedValidator extends PaymentOrderValidator {
    @Override
    protected void doValidate(Order order) {
        if (order.getPaymentTransaction() != null && PaymentStatus.COMPLETED.equals(order.getPaymentTransaction().getPaymentStatus())) {
            throw new PaymentAlreadyCompletedException(ORDER_ALREADY_COMPLETED);
        }
    }
}
