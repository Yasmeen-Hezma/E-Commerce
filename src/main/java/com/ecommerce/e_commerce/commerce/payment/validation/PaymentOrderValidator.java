package com.ecommerce.e_commerce.commerce.payment.validation;

import com.ecommerce.e_commerce.commerce.order.model.Order;

public abstract class PaymentOrderValidator {
    private PaymentOrderValidator next;

    public PaymentOrderValidator linkWith(PaymentOrderValidator next) {
        this.next = next;
        return next;
    }

    public void validate(Order order) {
        doValidate(order);
        if (next != null) {
            next.validate(order);
        }
    }

    protected abstract void doValidate(Order order);
}
